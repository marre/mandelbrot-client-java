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
        LOG.info("Starting");

        CliOptions params = parseArguments(args);

        // Split into parts

        MandelbrotPart mandelbrotPartComplete = MandelbrotPart.create(
                new Dimension(params.getWidth(), params.getHeight()),
                new Complex(params.getMincre(), params.getMincim()),
                new Complex(params.getMaxcre(), params.getMaxcim()),
                params.getSteps());

        int partSize = Math.max(1, params.getDivisions());
        List<MandelbrotPart> mandelbrotParts = mandelbrotPartComplete.split(partSize);

        // BUG: After splitting, the canvas size is a multiple of params.getDivisions()
        //      so we have to recalculate the canvas size
        int partWidth = params.getWidth() / partSize;
        int canvasWidth = partWidth * partSize;
        int partHeight = params.getHeight() / partSize;
        int canvasHeight = partHeight * partSize;

        BufferedImage canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);

        UnicastSubject<MandelbrotResult> mandelbrotResults = UnicastSubject.create();

        mandelbrotResults
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.single())
                .subscribe(
                        response -> {
                            MandelbrotPart part = response.getMandelbrotPart();
                            LOG.info("Received : {}", part.getOffset());

                            mandelbrotParts.remove(part);

                            // A new response has been received. Add pixel to the canvas

                            int[] pixelIterations = response.getPixels();
                            Dimension dimension = part.getSize();
                            Position offset = part.getOffset();

                            int[] packedRgbPixels = colourize(pixelIterations);

                            canvas.setRGB(offset.getX(), offset.getY(), dimension.getWidth(), dimension.getHeight(), packedRgbPixels, 0, dimension.getWidth());

                            // Signal that we are done
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

                            LOG.info("Done!");

                            System.exit(0);
                        });

        String mandelbrotServerBaseUrl = params.getUrl().toASCIIString();
        MandelbrotClient mandelbrotClient = MandelbrotClient.create(mandelbrotServerBaseUrl);

        int concurrentRequests = 1;
        if (mandelbrotParts.size() > 1) {
            // Need more than one part
            concurrentRequests = params.getConcurrent();
        }

        // TODO: Limit parallelism to params.getParallel()
        //       Right now we just send all requests at once
        for (MandelbrotPart part : mandelbrotParts) {
            LOG.info("Sending : {}", part);
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
