package ar.edu.utn.ecoharmony;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeneradorQRTest {
    private static final String CONTENIDO_PASE = "INSCRIPCION-1";

    private GeneradorQR generador;

    @BeforeEach
    void setUp() {
        generador = new GeneradorQR();
    }

    @Test
    void debeDecodificarAlContenidoOriginalElCodigoGenerado() {
        byte[] imagen = generador.generar(CONTENIDO_PASE);

        String decodificado = decodificar(imagen);

        assertEquals(CONTENIDO_PASE, decodificado);
    }

    @Test
    void debeGenerarImagenPngValidaParaAdjuntarAlCorreo() {
        byte[] imagen = generador.generar(CONTENIDO_PASE);

        BufferedImage leida = leerPng(imagen);

        assertNotNull(leida, "los bytes generados no son una imagen PNG legible");
    }

    @Test
    void debeRechazarContenidoVacioCuandoNoHayNadaQueCodificar() {
        assertThrows(IllegalArgumentException.class, () -> generador.generar(""));
    }

   
    private String decodificar(byte[] imagen) {
        BufferedImage leida = leerPng(imagen);
        if (leida == null) {
            return null;
        }
        var bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(leida)));
        try {
            return new QRCodeReader().decode(bitmap).getText();
        } catch (Exception e) {
            return null;
        }
    }

   
    private BufferedImage leerPng(byte[] imagen) {
        try {
            return ImageIO.read(new ByteArrayInputStream(imagen));
        } catch (IOException e) {
            return null;
        }
    }
}
