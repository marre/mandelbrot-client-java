package org.marre.mandelbrot;

import lombok.Value;

@Value
public final class Dimension {
    private final int width;
    private final int height;

    public Dimension(int width, int height) {
        if (width < 0) {
            throw new IllegalArgumentException("negative width [" + width + "]");
        }

        if (height < 0) {
            throw new IllegalArgumentException("negative height [" + height + "]");
        }

        this.width = width;
        this.height = height;
    }
}
