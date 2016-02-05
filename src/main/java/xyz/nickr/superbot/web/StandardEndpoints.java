package xyz.nickr.superbot.web;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class StandardEndpoints {

    public static final Pattern PATTERN_HEXCOLOUR = Pattern.compile("(?:[A-F0-9]{3})|(?:[A-F0-9]{6})");
    public static final int PHOTO_WIDTH = 128, PHOTO_HEIGHT = 128;

    public static final Endpoint PHOTO = (session, routes) -> {
        if (routes.length > 1) {
            routes[1] = routes[1].toUpperCase();
            if (routes[1].contains("."))
                routes[1] = routes[1].split("\\.")[0];
            String colour = routes[1];
            if (PATTERN_HEXCOLOUR.matcher(colour).matches()) {
                if (colour.length() == 3) {
                    String c = "";
                    for (int i = 0; i < 6; i++)
                        c += colour.charAt(i/2);
                    colour = c;
                }
                if (colour.length() == 6) {
                    File cache = new File("web/photo/" + routes[1]);
                    if (!cache.exists()) {
                        BufferedImage img = new BufferedImage(PHOTO_WIDTH, PHOTO_HEIGHT, BufferedImage.TYPE_INT_RGB);
                        int rgb = Integer.parseInt(colour, 16);
                        Graphics2D g = img.createGraphics();
                        g.setColor(new Color(rgb));
                        g.fillRect(0, 0, PHOTO_WIDTH, PHOTO_HEIGHT);
                        g.setColor(Color.WHITE);
                        StandardEndpoints.drawCenteredString(g, "#" + routes[1], new Rectangle(0, 0, PHOTO_WIDTH, PHOTO_HEIGHT), new Font(Font.MONOSPACED, Font.BOLD, 16));
                        cache.getParentFile().mkdirs();
                        cache.createNewFile();
                        ImageIO.write(img, "jpg", cache);
                    }
                    return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "image/jpeg", new FileInputStream(cache), cache.length());
                }
            }
        }
        return null;
    };

    public static void drawCenteredString(Graphics2D g, String text, Rectangle r, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (r.width - metrics.stringWidth(text)) / 2;
        int y = r.height / 2 - metrics.getDescent() / 2;
        Font old = g.getFont();
        g.setFont(font);

        TextLayout txt = new TextLayout(text, font, g.getFontRenderContext());

        Shape shape = txt.getOutline(null);

        Graphics2D g2 = (Graphics2D) g.create();
        AffineTransform affineTransform = new AffineTransform();
        affineTransform = g2.getTransform();
        affineTransform.translate(1.2, 1.2);
        g2.transform(affineTransform);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(4));
        g2.translate(x, y);
        g2.draw(shape);
        g2.setClip(shape);

        g.setColor(Color.WHITE);
        g.drawString(text, x, y);

        g.setFont(old);
    }

    static void register() {
        SuperBotServer.registerEndpoint(PHOTO, "photo");
    }

}
