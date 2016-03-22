package xyz.nickr.superbot.sys.telegram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.inline.send.InlineQueryResponse;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResult;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultArticle;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultPhoto;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineResultChosenEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.util.ConvertCommand;
import xyz.nickr.superbot.cmd.util.ConvertCommand.Conversion;
import xyz.nickr.superbot.cmd.util.CurrencyCommand;
import xyz.nickr.superbot.cmd.util.MathsCommand;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.User;
import xyz.nickr.superbot.sys.gitter.MarkdownMessageBuilder;
import xyz.nickr.superbot.web.StandardEndpoints;

/**
 * Created by bo0tzz
 */
public class TelegramListener implements Listener {

    public static final Pattern PATTERN_HEXCOLOUR_FREE = Pattern.compile("(?:[A-F0-9]{2,3})|(?:[A-F0-9]{5,6})");

    private final TelegramBot bot;
    private final TelegramSys sys;
    private final Properties uids = new Properties();

    public TelegramListener(TelegramBot bot, TelegramSys sys) {
        this.bot = bot;
        this.sys = sys;

        try {
            uids.load(new FileInputStream(new File("tguids.cache")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        Group g = sys.wrap(event.getChat());
        User u = sys.wrap(event.getMessage().getSender());
        String msg = sys.prefix() + event.getCommand().trim() + " " + event.getArgsString().trim();
        SuperBotCommands.exec(sys, g, u, sys.wrap(msg, event.getMessage()));
    }

    @Override
    public void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
        User user = sys.wrap(event.getParticipant());
        Group convo = sys.wrap(event.getChat());
        GroupConfiguration cfg = SuperBotController.getGroupConfiguration(convo);
        if (cfg != null && cfg.isShowJoinMessage()) {
            String welcome = String.format(SuperBotController.WELCOME_MESSAGE_JOIN, user.getUsername(), convo.getDisplayName());
            String help = "You can access my help menu by typing `" + sys.prefix() + "help`";
            String message = sys.message()
                    .bold(true)
                    .escaped(TelegramMessageBuilder.markdown_escape(welcome, false) + "\n" + TelegramMessageBuilder.markdown_escape(help, false))
                    .bold(false)
                    .build();
            convo.sendMessage(message);
        }
    }

    private InlineQueryResultArticle res(String title, String desc, String text, boolean web) {
        return InlineQueryResultArticle.builder()
            .parseMode(ParseMode.MARKDOWN)
            .title(title)
            .description(desc)
            .disableWebPagePreview(!web)
            .messageText(text)
            .build();
    }

    @Override
    public void onInlineQueryReceived(InlineQueryReceivedEvent event) {
        String q = event.getQuery().getQuery().trim();
        //System.out.println("INLINE QUERY RECEIVED: " + q);
        //System.out.println("FROM: " + (event.getQuery().getSender().getUsername() != null ? event.getQuery().getSender().getUsername() : String.valueOf(event.getQuery().getSender().getId())));
        String[] words = q.split("\\s+");
        List<InlineQueryResult> results = new LinkedList<>();
        boolean is_personal = false;
        int cache_time = 0;
        if (words.length >= 1) {
            if (words[0].startsWith("#")) {
                String colour = words[0].substring(1).toUpperCase();
                List<String> colours = new LinkedList<>();
                if (PATTERN_HEXCOLOUR_FREE.matcher(colour).matches()) {
                    String hex = "0123456789ABCDEF";
                    if (colour.length() == 2) {
                        for (int i = 0; i < hex.length(); i++) {
                            String co = colour + hex.charAt(i);
                            String c = "";
                            for (int x = 0; x < 6; x++)
                                c += co.charAt(x/2);
                            colours.add(c);
                        }
                    } else if (colour.length() == 5) {
                        for (int i = 0; i < hex.length(); i++) {
                            colours.add(colour + hex.charAt(i));
                        }
                    } else if (colour.length() == 3 || colour.length() == 6) {
                        String c = colour.length() == 3 ? "" : colour;
                        if (colour.length() == 3) {
                            for (int i = 0; i < 6; i++)
                                c += colour.charAt(i/2);
                        }
                        colours.add(c);
                    }
                }
                for (String co : colours) {
                    URL url;
                    try {
                        url = new URL("http://nickr.xyz/photo/" + co + ".jpg");
                        results.add(InlineQueryResultPhoto.builder()
                                        .parseMode(ParseMode.NONE)
                                        .title("#" + co)
                                        .caption("")
                                        .thumbUrl(url)
                                        .photoUrl(url)
                                        .photoWidth(StandardEndpoints.PHOTO_WIDTH)
                                        .photoHeight(StandardEndpoints.PHOTO_HEIGHT)
                                        .build());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                cache_time = 30;
            } else if (words[0].equalsIgnoreCase("distance")) {
                if (words.length >= 3) {
                    String from = words[1], to = words[2];
                    try {
                        JSONObject res = Unirest.get(String.format("http://www.distance24.org/route.json?stops=%s", URLEncoder.encode(from + "|" + to, "UTF-8")))
                                        .asJson().getBody().getObject();
                        JSONArray arr = res.getJSONArray("stops");
                        boolean fromValid = true, toValid = true;
                        if (arr.getJSONObject(0).getString("type").equalsIgnoreCase("Invalid"))
                            fromValid = false;
                        else
                            from = arr.getJSONObject(0).getString("city") + (arr.getJSONObject(0).has("region") ? ", " + arr.getJSONObject(0).getString("region") : "");
                        if (arr.getJSONObject(1).getString("type").equalsIgnoreCase("Invalid"))
                            toValid = false;
                        else
                            to = arr.getJSONObject(1).getString("city") + ", " + (arr.getJSONObject(1).has("region") ? ", " + arr.getJSONObject(1).getString("region") : "");
                        if (fromValid && toValid) {
                            double dist = res.getDouble("distance");
                            String text = "\\[Distance] " + " " + from + " -> " + to + " = " + dist + "km";
                            results.add(res(from + " => " + to, "Distance: " + dist + "km", text, false));
                        } else {
                            String disp = "";
                            if (!fromValid)
                                disp += from;
                            if (!toValid)
                                disp += (disp.isEmpty() ? "" : ", ") + to;
                            results.add(res("Invalid city name(s): " + disp, "Try another query", "\\[Distance] Invalid city name(s): " + disp, false));
                        }
                    } catch (IOException | UnirestException e) {
                        e.printStackTrace();
                    }
                }
            } else if (words[0].equalsIgnoreCase("flip")) {
                if (words.length > 1) {
                    String[] args = Arrays.copyOfRange(words, 1, words.length);
                    String text = Joiner.join(" ", args);
                    results.add(res("flip text", flip(text), flip(text), false));
                }
            } else if (words[0].equalsIgnoreCase("math") || words[0].equalsIgnoreCase("maths")) {
                try {
                    String[] args = Arrays.copyOfRange(words, 1, words.length);
                    List<String> ags = new ArrayList<>(Arrays.asList(args));
                    Map<String, Matcher> vars = ags.stream().collect(Collectors.toMap(a -> a, a -> MathsCommand.VARIABLE_ARG.matcher(a)));
                    vars.entrySet().removeIf(e -> !e.getValue().matches());
                    ags.removeIf(a -> vars.containsKey(a));
                    String input = Joiner.join("", ags);
                    List<Map.Entry<String, String>> vs = vars.entrySet().stream()
                                                                .map(e -> e.getValue())
                                                                .map(m -> new AbstractMap.SimpleEntry<>(m.group(1), m.group(2)))
                                                                .collect(Collectors.toList());
                    Expression e = new ExpressionBuilder(input)
                            .variables(vs.stream().map(z -> z.getKey()).collect(Collectors.toSet()))
                            .build();
                    for (Map.Entry<String, String> ent : vs) {
                        e.setVariable(ent.getKey(), Double.parseDouble(ent.getValue()));
                    }
                    double result = e.evaluate();
                    results.add(res("Result:", String.valueOf(result), MarkdownMessageBuilder.markdown_escape(input + " = " + result, false), false));
                } catch (Exception ignored) {
                    results.add(res("Invalid maths", ":(", "Invalid maths!", false));
                }
            } else if (words[0].equalsIgnoreCase("convert")) {
                // convert [from] [to] [quantity]
                Map<String, Map<String, Conversion>> convs = ConvertCommand.conversions;
                if (words.length >= 4) {
                    String from = words[1], to = words[2], quant = words[3];
                    Map<String, Conversion> map = convs.get(from);
                    if (convs.containsKey(from) && map.containsKey(to)) {
                        String out = map.get(to).apply(quant);
                        String text = "\\[Convert] " + quant + " " + from + " => " + out + " " + to;
                        results.add(res(from + " => " + to, quant + " => " + out, text, false));
                    }
                } else {
                    String un = bot.getBotUsername();
                    for (Entry<String, Map<String, Conversion>> e : convs.entrySet()) {
                        for (Entry<String, Conversion> f : e.getValue().entrySet()) {
                            String title = "@" + un + " convert " + e.getKey() + " " + f.getKey() + " [quantity]";
                            String desc = "Convert " + f.getValue().from + " to " + f.getValue().to;
                            String text = "/convert@" + un.replace("_", "\\_") + " " + e.getKey() + " " + f.getKey() + " 100";
                            results.add(res(title, desc, text, false));
                        }
                    }
                }
            } else if (words[0].equalsIgnoreCase("currency")) {
                // currency [from] [to] [quantity]
                if (words.length >= 4) {
                    String from = words[1].toUpperCase(), to = words[2].toUpperCase(), quant = words[3];
                    String queryURL = String.format("https://www.google.co.uk/finance/converter?from=%s&to=%s&a=%s", from, to, quant);
                    try {
                        URL url = new URL(queryURL);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        String line;
                        while ((line = reader.readLine()) != null && results.isEmpty()) {
                            Matcher matcher = CurrencyCommand.CONVERSION_PATTERN.matcher(line);
                            if (matcher.find()) {
                                String out = "\\[Currency] " + quant + " " + from + " => " + matcher.group(1);
                                results.add(res(from + " => " + to, quant + " => " + matcher.group(1).split(" ")[0], out, false));
                            }
                        }
                    } catch (IOException e) {}
                }
            }
        }
        if (results.isEmpty()) {
            String un = bot.getBotUsername();
            results.add(res("Flip", "@" + un + " flip [text]", "@" + un.replace("_", "\\_") + " flip \\[text]", false));
            results.add(res("Colour", "@" + un + " #[colour]", "@" + un.replace("_", "\\_") + " #\\[colour]", false));
            results.add(res("Distance", "@" + un + " distance [from] [to]", "@" + un.replace("_", "\\_") + " distance \\[from] \\[to]", false));
            results.add(res("Maths", "@" + un + " maths [query]", "/math@" + un.replace("_", "\\_"), false));
            results.add(res("Convert", "@" + un + " convert [from] [to] [quantity]", "/convert@" + un.replace("_", "\\_"), false));
            results.add(res("Currency", "@" + un + " currency [from] [to] [quantity]", "/currency@" + un.replace("_", "\\_"), false));
        }
        InlineQueryResponse res = InlineQueryResponse.builder()
                                        .is_personal(is_personal)
                                        .results(results)
                                        .cache_time(cache_time)
                                        .build();
        event.getQuery().answer(bot, res);
    }

    String alphabet = "abcdefghijklmnopqrstuvwxyz',\\/`?!";
    String flippedalph = "ɐqɔpǝɟbɥıظʞןɯuodbɹsʇnʌʍxʎz,'/\\,¿¡";

    private String flip(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            int idx;
            char r = Character.isAlphabetic(c) ? Character.toLowerCase(c) : c;
            if ((idx = alphabet.indexOf(r)) >= 0)
                c = flippedalph.charAt(idx);
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public void onInlineResultChosen(InlineResultChosenEvent event) {
    }

}
