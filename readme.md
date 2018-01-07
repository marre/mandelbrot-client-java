# Mandelbrot client

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

✗ java -jar target/mandelbrot-client-java-0.0.1-SNAPSHOT.jar -- -2.0 -2.0 2.0 2.0 1000 1000 > mandelbrot.png
Done!

```

# TODO

* Allow any size for division. Right now division controls the resulting canvas size.
* CLI parameter validation
* Limit parallelism
* 'HTTP 502 Bad Gateway' errors -- Most likely caused by a too large result. A lambda body can only be 6Mb large. A workaround is to save it to s3 and redirect to that resource.
