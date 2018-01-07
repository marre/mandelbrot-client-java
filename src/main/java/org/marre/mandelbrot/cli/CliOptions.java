package org.marre.mandelbrot.cli;

import lombok.Data;
import lombok.NonNull;
import picocli.CommandLine;

import java.net.URI;

@Data
@CommandLine.Command(showDefaultValues = true, description = "Mandelbrot client")
final class CliOptions {
    @CommandLine.Option(names = "-u", description = "the mandelbrot server url")
    @NonNull
    private URI url = URI.create("https://qfvdee5mse.execute-api.us-east-1.amazonaws.com/dev/");

    @CommandLine.Option(names = "-c", description = "the number of concurrent requests [>0]")
    private int concurrent = 1;

    @CommandLine.Option(names = "-d", description = "the size of each sub part [>100]")
    private int divisions = 1000;

    @CommandLine.Option(names = "-s", description = "max steps per pixel [>0]")
    private int steps = 256*4;

    @CommandLine.Parameters(index = "0", paramLabel = "min_c_re", arity = "1", description = "min_c_re [-2.0 ... 2.0]")
    private double mincre;

    @CommandLine.Parameters(index = "1", paramLabel = "min_c_im", arity = "1", description = "min_c_im [-2.0 ... 2.0]")
    private double mincim;

    @CommandLine.Parameters(index = "2", paramLabel = "max_c_re", arity = "1", description = "max_c_re [-2.0 ... 2.0]")
    private double maxcre;

    @CommandLine.Parameters(index = "3", paramLabel = "max_c_im", arity = "1", description = "max_c_im [-2.0 ... 2.0]")
    private double maxcim;

    @CommandLine.Parameters(index = "4", paramLabel = "x", arity = "1", description = "canvas width in pixels [>0]")
    private int width;

    @CommandLine.Parameters(index = "5", paramLabel = "y", arity = "1", description = "canvas height in pixels [>0]")
    private int height;
}
