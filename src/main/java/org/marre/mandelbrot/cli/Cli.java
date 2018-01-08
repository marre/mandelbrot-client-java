package org.marre.mandelbrot.cli;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.UnicastSubject;
import org.marre.mandelbrot.Complex;
import org.marre.mandelbrot.Dimension;
import org.marre.mandelbrot.MandelbrotPart;
import org.marre.mandelbrot.api.MandelbrotClient;
import org.marre.mandelbrot.api.MandelbrotResult;
import org.marre.mandelbrot.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.awt.image.BufferedImage;
import java.util.List;

public final class Cli {
    private static final Logger LOG = LoggerFactory.getLogger(Cli.class);

    public static void main(String ... args) {
        CliOptions params = parseArguments(args);

        long start = System.currentTimeMillis();

        Dimension canvasSize = new Dimension(params.getWidth(), params.getHeight());
        MandelbrotPart mandelbrotPartComplete = MandelbrotPart.create(
                canvasSize,
                new Complex(params.getMincre(), params.getMincim()),
                new Complex(params.getMaxcre(), params.getMaxcim()),
                params.getSteps());
        int partSize = Math.max(1, params.getDivisions());
        int maxConcurrency = Math.max(1, params.getConcurrent());
        String mandelbrotServerBaseUrl = params.getUrl().toASCIIString();

        // Split into parts
        List<MandelbrotPart> mandelbrotParts = mandelbrotPartComplete.split(partSize);

        BufferedImage canvas = new BufferedImage(canvasSize.getWidth(), canvasSize.getHeight(), BufferedImage.TYPE_INT_ARGB);

        UnicastSubject<MandelbrotResult> mandelbrotResults = UnicastSubject.create();

        mandelbrotResults
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.single())
                .subscribe(
                        response -> {
                            // A new response has been received. Add pixels to the canvas
                            MandelbrotPart part = response.getMandelbrotPart();

                            Dimension size = part.getSize();
                            Position offset = part.getOffset();

                            int[] pixelIterations = response.getPixels();
                            int[] packedRgbPixels = colourize(pixelIterations);

                            // Remove the part from the mandelbrotParts list
                            mandelbrotParts.remove(part);

                            LOG.info("Received : {} {} {} pixels, {} parts left", offset, size, packedRgbPixels.length, mandelbrotParts.size());

                            canvas.setRGB(
                                    offset.getX(),
                                    offset.getY(),
                                    size.getWidth(),
                                    size.getHeight(),
                                    packedRgbPixels,
                                    0,
                                    size.getWidth());

                            // Is this the final part?
                            if (mandelbrotParts.isEmpty()) {
                                mandelbrotResults.onComplete();
                            }
                        },
                        err -> {
                            // Something went spectacularly wrong
                            LOG.error("Failed to retrieve mandelbrot from server", err);
                            System.exit(1);
                        },
                        () -> {
                            // All responses received. Write out the canvas as a png file to stdout
                            PngUtil.toPng(canvas, System.out);
                            System.out.flush();

                            long duration = System.currentTimeMillis() - start;

                            LOG.info("Done in {} ms!", duration);

                            System.exit(0);
                        });

        MandelbrotClient mandelbrotClient = MandelbrotClient.create(mandelbrotServerBaseUrl, maxConcurrency);

        // Just send all requests to retrofit and let it send it as efficient as possible
        for (MandelbrotPart part : mandelbrotParts) {
            LOG.info("Enqueing request for: {}", part);
            mandelbrotClient.mandelbrot(part)
                    .toObservable()
                    .subscribe(mandelbrotResults::onNext, mandelbrotResults::onError);
        }

        // Just to keep this thread from terminating
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            // Ok. Fine interrupted...
        }
    }

    private static CliOptions parseArguments(String ... args) {
        try {
            return CommandLine.populateCommand(new CliOptions(), args);
        } catch (CommandLine.ParameterException pe) {
            System.err.println(pe.getMessage());
            CommandLine.usage(new CliOptions(), System.err);
            System.exit(1);
            return null;
        }
    }

    private static int[] colourize(int[] pixelIterations) {
        int[] rgbPixels = new int[pixelIterations.length];

        for(int i=0; i < pixelIterations.length; i++) {
            int pixel = pixelIterations[i];
            int grey = pixel % 256;
            rgbPixels[i] =  packRgb(grey, grey, grey);
        }

        return rgbPixels;
    }

    private static int packRgb(int r, int g, int b) {
        return  (0xff000000) |
                ((r & 0xff) << 16) |
                ((g & 0xff) << 8)  |
                ((b & 0xff));
    }
}
