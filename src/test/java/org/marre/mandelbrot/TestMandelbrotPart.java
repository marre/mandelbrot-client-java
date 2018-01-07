package org.marre.mandelbrot;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestMandelbrotPart {
    @Test
    public void testSplitTo1() {
        MandelbrotPart mandelbrotPart = MandelbrotPart.create(
                new Dimension(1000, 1000),
                new Complex(-2, -2),
                new Complex(2, 2),
                100);

        List<MandelbrotPart> parts = mandelbrotPart.split(1000);

        Assert.assertEquals(1, parts.size());
        Assert.assertEquals(mandelbrotPart, parts.get(0));

        Assert.assertEquals(part(1000, 1000, 0, 0, 1000, 1000, -2, -2, 2, 2), parts.get(0));
    }

    @Test
    public void testSplitTo2() {
        MandelbrotPart mandelbrotPart = MandelbrotPart.create(
                new Dimension(1000, 2000),
                new Complex(-2, -2),
                new Complex(2, 2),
                100);

        List<MandelbrotPart> parts = mandelbrotPart.split(1000);
        Assert.assertEquals(2, parts.size());

        Assert.assertEquals(part(1000, 2000, 0, 0, 1000, 1000, -2, -2, 2, 0), parts.get(0));
        Assert.assertEquals(part(1000, 2000, 0, 1000, 1000, 1000, -2, 0, 2, 2), parts.get(1));
    }

    @Test
    public void testSplitTo4() {
        MandelbrotPart mandelbrotPart = MandelbrotPart.create(
                new Dimension(2000, 2000),
                new Complex(-2, -2),
                new Complex(2, 2),
                100);

        List<MandelbrotPart> parts = mandelbrotPart.split(1000);
        Assert.assertEquals(4, parts.size());

        Assert.assertEquals(part(2000, 2000, 0, 0, 1000, 1000, -2, -2, 0, 0), parts.get(0));
        Assert.assertEquals(part(2000, 2000, 1000, 0, 1000, 1000, 0, -2, 2, 0), parts.get(1));
        Assert.assertEquals(part(2000, 2000, 0, 1000, 1000, 1000, -2, 0, 0, 2), parts.get(2));
        Assert.assertEquals(part(2000, 2000, 1000, 1000, 1000, 1000, 0, 0, 2, 2), parts.get(3));
    }

    @Test
    public void testSplitTo4Wide() {
        MandelbrotPart mandelbrotPart = MandelbrotPart.create(
                new Dimension(4000, 1000),
                new Complex(-2, -2),
                new Complex(2, 2),
                100);

        List<MandelbrotPart> parts = mandelbrotPart.split(1000);
        Assert.assertEquals(4, parts.size());

        Assert.assertEquals(part(4000, 1000, 0, 0, 1000, 1000, -2, -2, -1, 2), parts.get(0));
        Assert.assertEquals(part(4000, 1000, 1000, 0, 1000, 1000, -1, -2, 0, 2), parts.get(1));
        Assert.assertEquals(part(4000, 1000, 2000, 0, 1000, 1000, 0, -2, 1, 2), parts.get(2));
        Assert.assertEquals(part(4000, 1000, 3000, 0, 1000, 1000, 1, -2, 2, 2), parts.get(3));
    }

    private static MandelbrotPart part(int canvasWidth, int canvasHeight, int x, int y, int width, int height, int mincre, int mincim, int maxcre, int maxcim) {
        return new MandelbrotPart(
                new Dimension(width, height),
                new Complex(mincre, mincim),
                new Complex(maxcre, maxcim),
                100,
                new Dimension(canvasWidth, canvasHeight),
                new Position(x, y));
    }
}
