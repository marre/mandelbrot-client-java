package org.marre.mandelbrot.api;

import io.reactivex.Single;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.marre.mandelbrot.Complex;
import org.marre.mandelbrot.Dimension;
import org.marre.mandelbrot.MandelbrotPart;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class MandelbrotClient {
    @NonNull
    private final MandelbrotApi mandelbrotApi;

    public static MandelbrotClient create(String mandelbrotServerBaseUrl, int maxConcurrency) {
        if (maxConcurrency < 1) {
            throw new IllegalArgumentException("maxConcurrency must be a positive integer. [" + maxConcurrency + "]");
        }

        // Retrofit complains if the base URI doesn't end with '/'
        if (!mandelbrotServerBaseUrl.endsWith("/")) {
            mandelbrotServerBaseUrl += "/";
        }

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(maxConcurrency);
        dispatcher.setMaxRequestsPerHost(maxConcurrency);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .readTimeout(45, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(mandelbrotServerBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build();

        MandelbrotApi mandelbrotApi = retrofit.create(MandelbrotApi.class);

        return new MandelbrotClient(mandelbrotApi);
    }

    /**
     * Sends a request to the mandelbrot server.
     *
     */
    public Single<MandelbrotResult> mandelbrot(MandelbrotPart mandelbrotPart) {
        Dimension size = mandelbrotPart.getSize();
        Complex minc = mandelbrotPart.getMinc();
        Complex maxc = mandelbrotPart.getMaxc();
        int maxSteps = mandelbrotPart.getMaxSteps();

        return mandelbrotApi.mandelbrot(minc.getRe(), minc.getIm(), maxc.getRe(), maxc.getIm(), size.getWidth(), size.getHeight(), maxSteps)
                .map(response -> {
                    if (response.isSuccessful()) {
                        return response.body();
                    }
                    throw new HttpException(response);
                })
                .map(response -> new MandelbrotResult(mandelbrotPart, response.getPixels()));
    }

    /**
     * The mandelbrot HTTP API for retrofit.
     */
    interface MandelbrotApi {
        @GET("mandelbrot/{mincre}/{mincim}/{maxcre}/{maxcim}/{xres}/{yres}/{infn}")
        Single<Response<MandelbrotResponseJson>> mandelbrot(
                @Path("mincre") double mincre,
                @Path("mincim") double mincim,
                @Path("maxcre") double maxcre,
                @Path("maxcim") double maxcim,
                @Path("xres") int xres,
                @Path("yres") int yres,
                @Path("infn") int infn);
    }
}
