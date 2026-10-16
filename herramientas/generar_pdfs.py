"""Genera los PDF documentales desde Markdown; no ejecuta tests del producto."""

from pathlib import Path
from html import escape
import argparse
import re

from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, KeepTogether,
    Preformatted, CondPageBreak,
)
from pypdf import PdfReader

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'prototipo/dist/assets'
OUT = ROOT / 'documentacion/pdf'
OUT.mkdir(parents=True, exist_ok=True)
pdfmetrics.registerFont(TTFont('Lato', str(ASSETS / 'lato-regular.ttf')))
pdfmetrics.registerFont(TTFont('LatoBold', str(ASSETS / 'lato-bold.ttf')))
pdfmetrics.registerFontFamily('Lato', normal='Lato', bold='LatoBold', italic='Lato', boldItalic='LatoBold')

FOREST = colors.HexColor('#386641')
CREAM = colors.HexColor('#f2e8cf')
LEAF = colors.HexColor('#6a994e')
LINE = colors.HexColor('#b9c3ac')
WIDTH = A4[0] - 84
styles = {
    'body': ParagraphStyle('body', fontName='Lato', fontSize=10, leading=14,
                           textColor=FOREST, spaceAfter=6, allowWidows=0, allowOrphans=0),
    'title': ParagraphStyle('title', fontName='LatoBold', fontSize=26, leading=30,
                            textColor=FOREST, spaceAfter=16, keepWithNext=True),
    'h2': ParagraphStyle('h2', fontName='LatoBold', fontSize=16, leading=20,
                         textColor=FOREST, spaceBefore=15, spaceAfter=9, keepWithNext=True),
    'h3': ParagraphStyle('h3', fontName='LatoBold', fontSize=12, leading=16,
                         textColor=FOREST, spaceBefore=12, spaceAfter=6, keepWithNext=True),
    'h4': ParagraphStyle('h4', fontName='LatoBold', fontSize=10.5, leading=14,
                         textColor=FOREST, spaceBefore=8, spaceAfter=5, keepWithNext=True),
    'cell': ParagraphStyle('cell', fontName='Lato', fontSize=8.8, leading=12,
                           textColor=FOREST, spaceAfter=0),
    'thead': ParagraphStyle('thead', fontName='LatoBold', fontSize=8.8, leading=12,
                            textColor=CREAM, spaceAfter=0),
    'bullet': ParagraphStyle('bullet', fontName='Lato', fontSize=9.7, leading=13.5,
                             textColor=FOREST, leftIndent=10, firstLineIndent=-8, spaceAfter=4),
    'code': ParagraphStyle('code', fontName='Courier', fontSize=8, leading=10,
                           textColor=FOREST, spaceBefore=5, spaceAfter=8, leftIndent=8),
}


def normalize(text):
    for ch in ['\u2014', '\u2013', '\u2011', '\u2010', '\u2212']:
        text = text.replace(ch, '-')
    return text.replace('→', ' > ').replace('\ufeff', '')


def inline(text):
    text = normalize(text)
    text = re.sub(r'\[([^\]]+)\]\((#[^)]+)\)', r'\1', text)
    # Los enlaces de referencia se dejan visibles; los PDF son autosuficientes.
    text = re.sub(r'\[([^\]]+)\]\((https?://[^)]+)\)', r'\1 (\2)', text)
    text = escape(text)
    text = re.sub(r'`([^`]+)`', r'<font name="LatoBold">\1</font>', text)
    text = re.sub(r'\*\*([^*]+)\*\*', r'<b>\1</b>', text)
    return text


def para(text, style='body'):
    return Paragraph(inline(text), styles[style])


def table_widths(rows):
    header = rows[0]
    n = len(header)
    if n == 4 and header[0] == 'ID' and header[1] == 'Origen':
        return [35, 49, 195, WIDTH - 279]
    if n == 4 and header[0] == 'Actividad':
        return [90, 90, 114, WIDTH - 294]
    if n == 3 and header[0] == 'ID':
        return [35, 310, WIDTH - 345]
    if n == 3 and header[0] in ['Elemento', 'Componente']:
        return [125, 160, WIDTH - 285]
    if n == 3 and header[0] in ['Dato', 'Rol']:
        return [117, 115, WIDTH - 232]
    if n == 3 and header[0] in ['Combinación', 'Tema', 'Referencia anterior']:
        return [170, 120, WIDTH - 290] if header[0] == 'Combinación' else [100, 285, WIDTH - 385]
    if n == 2:
        return [150, WIDTH - 150]
    if n == 3:
        return [WIDTH * .31, WIDTH * .32, WIDTH * .37]
    return [WIDTH / n] * n


def make_table(rows):
    content = [[para(cell, 'thead' if i == 0 else 'cell') for cell in row]
               for i, row in enumerate(rows)]
    table = Table(content, colWidths=table_widths(rows), repeatRows=1, hAlign='LEFT')
    table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), FOREST),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
        ('LEFTPADDING', (0, 0), (-1, -1), 7),
        ('RIGHTPADDING', (0, 0), (-1, -1), 7),
        ('TOPPADDING', (0, 0), (-1, -1), 7),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 7),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, CREAM]),
        ('LINEBELOW', (0, 0), (-1, 0), .7, FOREST),
        ('LINEBELOW', (0, 1), (-1, -1), .25, LINE),
    ]))
    return table


def markdown_story(text):
    lines = text.splitlines()
    scenario_doc = '#### U001' in text
    story = []
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        if not line or line.startswith('<a '):
            i += 1
            continue
        if line.startswith('```'):
            block = []
            i += 1
            while i < len(lines) and not lines[i].strip().startswith('```'):
                block.append(normalize(lines[i]))
                i += 1
            story.append(Preformatted('\n'.join(block), styles['code'], maxLineLength=95))
            i += 1
            continue
        if line.startswith('|'):
            rows = []
            while i < len(lines) and lines[i].strip().startswith('|'):
                cells = [v.strip() for v in lines[i].strip().strip('|').split('|')]
                if not all(re.fullmatch(r':?-+:?', c) for c in cells):
                    rows.append(cells)
                i += 1
            if len({len(r) for r in rows}) != 1:
                raise ValueError(f'Tabla inválida: {rows[:2]}')
            story.append(make_table(rows))
            story.append(Spacer(1, 8))
            continue
        heading = re.match(r'^(#{1,4}) (.*)', line)
        if heading:
            level = len(heading.group(1))
            title = heading.group(2)
            if level == 4 and re.match(r'[UI]\d{3}\b', title):
                # Cada escenario se mantiene completo: título y ocho campos.
                block = [para(title, 'h4')]
                i += 1
                while i < len(lines) and not re.match(r'^#{1,4} ', lines[i]):
                    item = lines[i].strip()
                    if item.startswith('- '):
                        block.append(para(item[2:], 'bullet'))
                    elif item and not item.startswith('<a '):
                        block.append(para(item))
                    i += 1
                story.append(KeepTogether(block))
                story.append(Spacer(1, 5))
                continue
            if level == 2:
                story.append(CondPageBreak(220 if scenario_doc else 100))
            previous_is_heading = bool(story and isinstance(story[-1], Paragraph)
                                       and story[-1].style.name in ('h2', 'title'))
            if level == 3 and scenario_doc and not previous_is_heading:
                story.append(CondPageBreak(240))
            story.append(para(title, {1: 'title', 2: 'h2', 3: 'h3', 4: 'h4'}[level]))
            i += 1
            continue
        if line.startswith('- '):
            story.append(para('- ' + line[2:], 'bullet'))
        else:
            story.append(para(line))
        i += 1
    return story


def page_decor(canvas, doc, label):
    canvas.saveState()
    canvas.setStrokeColor(LEAF)
    canvas.setLineWidth(.6)
    canvas.line(42, A4[1] - 30, A4[0] - 42, A4[1] - 30)
    canvas.setFillColor(FOREST)
    canvas.setFont('Lato', 8)
    canvas.drawString(42, A4[1] - 23, 'EcoHarmony Park | TP6 | Documentación actualizada 16/10/2026')
    canvas.drawString(42, 24, label)
    canvas.drawRightString(A4[0] - 42, 24, f'16/10/2026 | Página {doc.page}')
    canvas.restoreState()


DOCUMENTS = [
    ('PLAN_CLASES_METODOS_Y_ESCENARIOS.md', 'TP6_Plan_clases_metodos_y_escenarios.pdf', 'Plan de clases, métodos y escenarios'),
    ('CATALOGO_PRUEBAS.md', 'TP6_Catalogo_pruebas.pdf', 'Catálogo de escenarios - sin implementación'),
    ('CONVENCION_ESTILOS.md', 'TP6_Convencion_estilos.pdf', 'Convención de estilos y calidad'),
    ('CONSULTAS_Y_VALIDACION_PO.md', 'TP6_Consultas_y_validacion_PO.pdf', 'Consultas y validación con el PO'),
]

parser = argparse.ArgumentParser(description='Generar PDF desde los documentos editables vigentes.')
parser.add_argument('--solo', choices=['plan', 'catalogo', 'estilos', 'po'],
                    help='Regenerar un único documento; por defecto se generan los cuatro.')
args = parser.parse_args()
selection = {'plan': 0, 'catalogo': 1, 'estilos': 2, 'po': 3}
documents = [DOCUMENTS[selection[args.solo]]] if args.solo else DOCUMENTS

for source, filename, label in documents:
    text = (ROOT / 'documentacion/editables' / source).read_text(encoding='utf-8')
    dest = OUT / filename
    doc = SimpleDocTemplate(str(dest), pagesize=A4, rightMargin=42, leftMargin=42,
                            topMargin=47, bottomMargin=43,
                            title=label, author='EcoHarmony Park - Equipo TP6',
                            pageCompression=1)
    doc.build(markdown_story(text),
              onFirstPage=lambda c, d, l=label: page_decor(c, d, l),
              onLaterPages=lambda c, d, l=label: page_decor(c, d, l))
    print(filename, len(PdfReader(dest).pages), 'páginas')
