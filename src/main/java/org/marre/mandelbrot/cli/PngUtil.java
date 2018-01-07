package org.marre.mandelbrot.cli;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public final class PngUtil {
    private PngUtil() {}

    public static void toPng(BufferedImage bi, OutputStream os) {
        try {
            ImageIO.write(bi, "PNG", os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
