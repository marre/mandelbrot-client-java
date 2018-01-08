package org.marre.mandelbrot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the mandelbrot parameters for a part of the mandelbrot image.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class MandelbrotPart {
    /**
     * Size of this part in pixels.
     */
    private final Dimension size;

    /**
     * Min C
     */
    private final Complex minc;

    /**
     * Max C
     */
    private final Complex maxc;

    /**
     * Max number of steps per pixel
     */
    private final int maxSteps;

    /**
     * Offset in a possible larger canvas where the result should be drawn.
     */
    private final Position offset;

    public static MandelbrotPart create(
            Dimension size,
            Complex minc,
            Complex maxc,
            int maxSteps) {

        if (minc.getRe() >= maxc.getRe()) {
            throw new IllegalArgumentException("mincre is larger than maxcre [mincre=" + minc.getRe() + ", maxcre=" + maxc.getRe() + "]");
        }

        if (minc.getIm() >= maxc.getIm()) {
            throw new IllegalArgumentException("mincim is larger than maxcim [mincim=" + minc.getIm() + ", maxcim=" + maxc.getIm() + "]");
        }

        if (size.getWidth() == 0) {
            throw new IllegalArgumentException("width is 0");
        }

        if (size.getHeight() == 0) {
            throw new IllegalArgumentException("height is 0");
        }

        return new MandelbrotPart(
                size,
                minc,
                maxc,
                maxSteps,
                new Position(0, 0));
    }

    public List<MandelbrotPart> split(int partSize) {
        if (partSize <= 0) {
            throw new IllegalArgumentException("partSize must be positive integer [" + partSize + "]");
        }

        if ( (offset.getX() != 0) || (offset.getY() != 0) ) {
            // LIMITATION: Can only split of offset is 0
            throw new IllegalArgumentException("offset must be zero [" + offset + "]");
        }

        List<MandelbrotPart> mandelbrotParts = new LinkedList<>();

        if ( (partSize >= size.getWidth()) && (partSize >= size.getHeight()) ) {
            // No need to split this
            mandelbrotParts.add(this);
            return mandelbrotParts;
        }

        double sizeRe = maxc.getRe() - minc.getRe();
        double pixelSizeRe = sizeRe / size.getWidth();
        double sizeIm = maxc.getIm() - minc.getIm();
        double pixelSizeIm = sizeIm / size.getHeight();

        int tilesX = size.getWidth() / partSize;
        int newCanvasWidth = tilesX * partSize;
        int tilesY = size.getHeight() / partSize;
        int newCanvasHeight = tilesY * partSize;

        for (int y=0; y < newCanvasHeight; y = y + partSize) {
            for (int x=0; x < newCanvasWidth; x = x + partSize) {
                mandelbrotParts.add(getMandelbrotPart(x, y, partSize, partSize, pixelSizeRe, pixelSizeIm));
            }
        }

        int xRest = size.getWidth() % partSize;
        int yRest = size.getHeight() % partSize;

        if (xRest > 0) {
            for (int y=0; y < newCanvasHeight; y = y + partSize) {
                mandelbrotParts.add(getMandelbrotPart(newCanvasWidth, y, xRest, partSize, pixelSizeRe, pixelSizeIm));
            }
        }

        if (yRest > 0) {
            for (int x=0; x < newCanvasWidth; x = x + partSize) {
                mandelbrotParts.add(getMandelbrotPart(x, newCanvasHeight, partSize, yRest, pixelSizeRe, pixelSizeIm));
            }
        }

        if ((xRest > 0) && (yRest > 0)) {
            mandelbrotParts.add(getMandelbrotPart(newCanvasWidth, newCanvasHeight, xRest, yRest, pixelSizeRe, pixelSizeIm));
        }

        return mandelbrotParts;
    }

    private MandelbrotPart getMandelbrotPart(int x, int y, int width, int height, double rePixelSize, double imPixelSize) {
        return new MandelbrotPart(
                            new Dimension(width, height),
                            new Complex(minc.getRe() + x * rePixelSize, minc.getIm() + y * imPixelSize),
                            new Complex(minc.getRe() + (x + width) * rePixelSize, minc.getIm() + (y + height) * imPixelSize),
                            maxSteps,
                            new Position(x, y));
    }
}
