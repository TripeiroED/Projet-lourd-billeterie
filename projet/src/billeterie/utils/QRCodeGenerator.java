package billeterie.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.nio.file.Path;

public class QRCodeGenerator {

    public static String generateQRCode(String text, String filePath) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.QR_CODE, 200, 200);

            Path path = new File(filePath).toPath();
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
