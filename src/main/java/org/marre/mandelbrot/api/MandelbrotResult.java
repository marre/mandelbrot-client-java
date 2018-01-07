package org.marre.mandelbrot.api;

import lombok.Value;
import org.marre.mandelbrot.MandelbrotPart;

@Value
public final class MandelbrotResult {
    private final MandelbrotPart mandelbrotPart;

    private final int[] pixels;
}
