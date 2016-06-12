package xyz.nickr.superbot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import com.google.gson.JsonObject;

public class Imgur {

    public static String IMGUR_CLIENT_ID;

    private static final Map<String, String> cache = new HashMap<>();

    static {
        try {
            FileReader reader = new FileReader("imgurCache.json");
            JsonObject o = SuperBotController.GSON.fromJson(reader, JsonObject.class);
            o.entrySet().forEach(e -> cache.put(e.getKey(), e.getValue().getAsString()));
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                new File("imgurCache.json").delete();
                FileWriter writer = new FileWriter("imgurCache.json");
                JsonObject o = new JsonObject();
                cache.forEach(o::addProperty);
                SuperBotController.GSON.toJson(o, writer);
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }));
    }

    public static URL upload(File f, boolean cache) {
        try {
            if (cache && Imgur.cache.containsKey(f.getAbsolutePath())) {
                return new URL(Imgur.cache.get(f.getAbsolutePath()));
            }
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
                    if (cache) {
                        Imgur.cache.put(f.getAbsolutePath(), url.toString());
                    }
                }
            }
            return url;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
