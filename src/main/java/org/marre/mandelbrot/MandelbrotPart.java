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
     * Size of the canvas where this part will be painted.
     */
    private final Dimension canvasSize;

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
                size,
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

        int partWidth = size.getWidth() / partSize;
        int width = partWidth * partSize;
        double sizeRe = maxc.getRe() - minc.getRe();
        double pixelSizeRe = sizeRe / width;

        int partHeight = size.getHeight() / partSize;
        int height = partHeight * partSize;
        double sizeIm = maxc.getIm() - minc.getIm();
        double pixelSizeIm = sizeIm / height;

        for (int y=0; y < height; y = y + partSize) {
            for (int x=0; x < width; x = x + partSize) {
                MandelbrotPart part = new MandelbrotPart(
                        new Dimension(partSize, partSize),
                        new Complex(minc.getRe() + x * pixelSizeRe, minc.getIm() + y * pixelSizeIm),
                        new Complex(minc.getRe() + (x + partSize) * pixelSizeRe, minc.getIm() + (y + partSize) * pixelSizeIm),
                        maxSteps,
                        canvasSize,
                        new Position(x, y));
                mandelbrotParts.add(part);
            }
        }

        return mandelbrotParts;
    }
}
