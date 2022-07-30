import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    private static final String NASA_URI = "https://api.nasa.gov/planetary/apod?api_key=HBMOrNR6ICVoBR0oqfInagCjFpoOICT2Z9mG8EwP";
    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().setUserAgent("NASA API connection").setDefaultRequestConfig(RequestConfig.DEFAULT).build();

        HttpGet request = new HttpGet(NASA_URI);
        CloseableHttpResponse response = httpClient.execute(request);

        Info info = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
        });
        System.out.printf("%s\n", info.getDate());
        System.out.printf("%s\n", info.getExplanation());

        response.close();

        request = new HttpGet(info.getUrl());
        response = httpClient.execute(request);

        File file = new File(info.getUrl().trim());

        try (FileOutputStream fos = new FileOutputStream(file.getName())) {
            byte[] picture = response.getEntity().getContent().readAllBytes();
            fos.write(picture, 0, picture.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        httpClient.close();
        response.close();
        System.out.println("Картинка сохранена в " + file.getName());
    }
}
