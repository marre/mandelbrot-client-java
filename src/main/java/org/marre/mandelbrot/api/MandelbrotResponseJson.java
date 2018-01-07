package org.marre.mandelbrot.api;

import lombok.Value;

/**
 * Represents the response from the mandelbrot server.
 */
@Value
final class MandelbrotResponseJson {
    /**
     * Each int represents the number of iterations performed for a pixel.
     */
    private final int[] pixels;
}
