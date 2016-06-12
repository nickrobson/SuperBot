package xyz.nickr.superbot;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import com.google.gson.JsonObject;

public class Imgur {

    public static String IMGUR_CLIENT_ID;

    public static URL upload(File f) {
        try {
            HttpPost post = new HttpPost("https://api.imgur.com/3/image");
            post.setHeader("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
            FileBody file = new FileBody(f);
            HttpEntity entity = MultipartEntityBuilder.create().addPart("image", file).build();
            post.setEntity(entity);
            CloseableHttpResponse res = SuperBotController.HTTP.execute(post);
            URL url = null;
            try (InputStreamReader isr = new InputStreamReader(res.getEntity().getContent())) {
                JsonObject obj = SuperBotController.GSON.fromJson(isr, JsonObject.class);
                if (obj.get("success").getAsBoolean()) {
                    url = new URL(obj.getAsJsonObject("data").get("link").getAsString());
                }
            }
            return url;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
