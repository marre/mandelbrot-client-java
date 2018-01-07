# Mandelbrot client

CLI to the ["serverless" mandelbrot server](https://github.com/marre/mandelbrot-serverless-js).

Dependencies:
 - picocli for command line parsing
 - retrofit2 for http client
 - rxjava for managing concurrency
 - lombok for value objects

```
✗ mvn clean install

[...]

✗ java -jar target/mandelbrot-client-java-0.0.1-SNAPSHOT.jar
Missing required parameters: min_c_re, min_c_im, max_c_re, max_c_im, x, y
Usage: <main class> [-d=<divisions>] [-p=<parallel>] [-s=<steps>] [-u=<url>]
                    min_c_re min_c_im max_c_re max_c_im x y
Mandelbrot client
      min_c_re                min_c_re [-2.0 ... 2.0]
      min_c_im                min_c_im [-2.0 ... 2.0]
      max_c_re                max_c_re [-2.0 ... 2.0]
      max_c_im                max_c_im [-2.0 ... 2.0]
      x                       canvas width in pixels [>0]
      y                       canvas height in pixels [>0]
  -d= <divisions>             the size of each sub part [>0]
                                Default: 1000
  -p= <parallel>              the number of parallel requests [>0]
                                Default: 1
  -s= <steps>                 max steps per pixel [>0]
                                Default: 1024
  -u= <url>                   the mandelbrot server url
                                Default: https://qfvdee5mse.execute-api.
                                us-east-1.amazonaws.com/dev/

[...]

✗ $ java -jar target/mandelbrot-client-java-0.0.1-SNAPSHOT.jar -d 500 -- -1.0 0 1.0 1.0 1000 500 > mandelbrot.png
2018-01-05 20:56:03,476 [main] - INFO o.m.m.cli.Cli - Sending : MandelbrotPart(size=Dimension(width=500, height=500), minc=Complex(re=-1.0, im=0.0), maxc=Complex(re=0.0, im=1.0), maxSteps=1024, canvasSize=Dimension(width=1000, height=500), offset=Position(x=0, y=0))
2018-01-05 20:56:03,796 [main] - INFO o.m.m.cli.Cli - Sending : MandelbrotPart(size=Dimension(width=500, height=500), minc=Complex(re=0.0, im=0.0), maxc=Complex(re=1.0, im=1.0), maxSteps=1024, canvasSize=Dimension(width=1000, height=500), offset=Position(x=500, y=0))
2018-01-05 20:56:08,519 [RxSingleScheduler-1] - INFO o.m.m.cli.Cli - Received : Position(x=500, y=0)
2018-01-05 20:56:08,885 [RxSingleScheduler-1] - INFO o.m.m.cli.Cli - Received : Position(x=0, y=0)
2018-01-05 20:56:09,504 [RxSingleScheduler-1] - INFO o.m.m.cli.Cli - Done!
```

# TODO

* Allow any size for division. Right now division controls the resulting 
  canvas size.
* Actually get the parallelism working:
    * The retrofit client seems to send them in parallel, but the server 
      processes them sequentially.
    * Also limit the concurrency. Right now it sends a request for all parts
      at once.
* Better handling of 'HTTP 502 Bad Gateway' errors
  * Most likely caused by a too large result. A lambda body can only be 
    6Mb large. A workaround is to save it to s3 and redirect to that resource.
  * Max lambda execution time is 30 seconds. Allow the client to split a part
    into smaller chunks if it encounters a timeout.
