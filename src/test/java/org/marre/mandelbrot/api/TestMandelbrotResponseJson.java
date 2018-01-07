package org.marre.mandelbrot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestMandelbrotResponseJson {
    @Test
    public void testApa() throws Exception {
        String json =
                "{\n" +
                "  \"pixels\": [1,2,3,4,5,6,7,8]\n" +
                "}\n";


        MandelbrotResponseJson response = fromJson(json.getBytes(StandardCharsets.UTF_8));

        Assert.assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, response.getPixels());
    }

    private static MandelbrotResponseJson fromJson(byte[] json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.readerFor(MandelbrotResponseJson.class);
        return objectReader.readValue(json);
    }
}
