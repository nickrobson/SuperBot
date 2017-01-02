package xyz.nickr.superbot.cmd.util;

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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ColourCommand implements Command {

    public static final Pattern PATTERN_COLOUR = Pattern.compile("(?:[A-F0-9]{2,3})|(?:[A-F0-9]{5,6})");
    public static final int PHOTO_WIDTH = 128, PHOTO_HEIGHT = 128;
    public static final Font FONT = new Font("Impact", Font.PLAIN, 20);

    @Override
    public String[] names() {
        return new String[] {"colour", "color"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[hex]", "get an image of the colour"};
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length > 0) {
            String colour = args[0].toUpperCase();
            while (colour.startsWith("#")) {
                colour = colour.substring(1);
            }
            List<String> colours = new LinkedList<>();
            if (PATTERN_COLOUR.matcher(colour).matches()) {
                String hex = "0123456789ABCDEF";
                if (group.supportsMultiplePhotos() && colour.length() == 2) {
                    for (char z : hex.toCharArray()) {
                        String co = colour + z;
                        String c = "";
                        for (int x = 0; x < 6; x++) {
                            c += co.charAt(x / 2);
                        }
                        colours.add(c);
                    }
                } else if (group.supportsMultiplePhotos() && colour.length() == 5) {
                    for (char c : hex.toCharArray()) {
                        colours.add(colour + c);
                    }
                } else if (colour.length() == 3 || colour.length() == 6) {
                    String c = colour.length() == 3 ? "" : colour;
                    if (colour.length() == 3) {
                        for (int i = 0; i < 6; i++) {
                            c += colour.charAt(i / 2);
                        }
                    }
                    colours.add(c);
                } else {
                    group.sendMessage(sys.message().escaped("Invalid colour."));
                }
                for (String co : colours) {
                    try {
                        File f = new File("web/photo/" + co + ".png");
                        if (!f.exists()) {
                            BufferedImage img = new BufferedImage(PHOTO_WIDTH, PHOTO_HEIGHT, BufferedImage.TYPE_INT_RGB);
                            int rgb = Integer.parseInt(co, 16);
                            Graphics2D g = img.createGraphics();
                            g.setColor(new Color(rgb));
                            g.fillRect(0, 0, PHOTO_WIDTH, PHOTO_HEIGHT);
                            g.setColor(Color.WHITE);
                            drawCenteredString(g, "#" + co, new Rectangle(0, 0, PHOTO_WIDTH, PHOTO_HEIGHT), FONT);
                            f.getParentFile().mkdirs();
                            f.createNewFile();
                            ImageIO.write(img, "png", f);
                        }
                        group.sendPhoto(f, true, true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        group.sendMessage(sys.message().escaped("Something went wrong!"));
                    }
                }
            } else {
                group.sendMessage(sys.message().escaped("Not hexadecimal: '%s'", colour));
            }
        } else {
            this.sendUsage(sys, user, group);
        }
    }

    public static void drawCenteredString(Graphics2D g, String text, Rectangle r, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (r.width - metrics.stringWidth(text)) / 2;
        int y = r.height / 2 - metrics.getDescent() / 2;
        Font old = g.getFont();
        g.setFont(font);

        TextLayout txt = new TextLayout(text, font, g.getFontRenderContext());
        Shape shape = txt.getOutline(null);

        Graphics2D g2 = (Graphics2D) g.create();
        AffineTransform affineTransform = g2.getTransform();
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

}
