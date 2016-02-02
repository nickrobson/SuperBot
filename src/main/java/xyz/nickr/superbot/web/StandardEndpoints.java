package xyz.nickr.superbot.web;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class StandardEndpoints {

    public static final Pattern PATTERN_HEXCOLOUR = Pattern.compile("(?i)(?:[a-f0-9]{3})|(?:[a-f0-9]{6})");
    public static final int PHOTO_WIDTH = 128, PHOTO_HEIGHT = 128;

    public static final Endpoint PHOTO = (session, routes) -> {
        if (routes.length > 1) {
            String colour = routes[1];
            if (PATTERN_HEXCOLOUR.matcher(colour).matches()) {
                if (colour.length() == 3) {
                    String c = "";
                    for (int i = 0; i < 6; i++)
                        c += colour.charAt(i/2);
                    colour = c;
                }
                if (colour.length() == 6) {
                    File cache = new File("web/photo/" + colour);
                    if (!cache.exists()) {
                        BufferedImage img = new BufferedImage(PHOTO_WIDTH, PHOTO_HEIGHT, BufferedImage.TYPE_INT_RGB);
                        int rgb = Integer.parseInt(colour, 16);
                        Graphics2D g = img.createGraphics();
                        g.setColor(new Color(rgb));
                        g.fillRect(0, 0, PHOTO_WIDTH, PHOTO_HEIGHT);
                        cache.getParentFile().mkdirs();
                        cache.createNewFile();
                        ImageIO.write(img, "png", cache);
                    }
                    return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "image/png", new FileInputStream(cache), cache.length());
                }
            }
        }
        return null;
    };

    static void register() {
        SuperBotServer.registerEndpoint(PHOTO, "photo");
    }

}
