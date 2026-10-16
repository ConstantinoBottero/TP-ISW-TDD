package ar.edu.utn.ecoharmony;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GeneradorQR {

    private static final String FORMATO_IMAGEN = "PNG";
    private static final int LADO_EN_PIXELES = 300;

    public byte[] generar(String contenido) {
        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException("El contenido del QR no puede estar vacío.");
        }
        try {
            var matriz = new QRCodeWriter()
                    .encode(contenido, BarcodeFormat.QR_CODE, LADO_EN_PIXELES, LADO_EN_PIXELES);
            var salida = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matriz, FORMATO_IMAGEN, salida);
            return salida.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("No se pudo generar el QR.", e);
        }
    }
}
