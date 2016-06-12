package xyz.nickr.superbot.cmd.util;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class DistanceCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"distance"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[from] [to]", "get the distance between the two locations"};
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length >= 2) {
            String from = args[0], to = args[1];
            try {
                JSONObject res = Unirest.get(String.format("http://www.distance24.org/route.json?stops=%s", URLEncoder.encode(from + "|" + to, "UTF-8"))).asJson().getBody().getObject();
                JSONArray arr = res.getJSONArray("stops");
                boolean fromValid = true, toValid = true;
                if (arr.getJSONObject(0).getString("type").equalsIgnoreCase("Invalid")) {
                    fromValid = false;
                } else {
                    from = arr.getJSONObject(0).getString("city") + (arr.getJSONObject(0).has("region") ? ", " + arr.getJSONObject(0).getString("region") : "");
                }
                if (arr.getJSONObject(1).getString("type").equalsIgnoreCase("Invalid")) {
                    toValid = false;
                } else {
                    to = arr.getJSONObject(1).getString("city") + ", " + (arr.getJSONObject(1).has("region") ? ", " + arr.getJSONObject(1).getString("region") : "");
                }
                if (fromValid && toValid) {
                    group.sendMessage(sys.message().escaped("Distance (%s => %s): %s km", from, to, res.getDouble("distance")));
                } else {
                    String disp = "";
                    if (!fromValid) {
                        disp += from;
                    }
                    if (!toValid) {
                        disp += (disp.isEmpty() ? "" : ", ") + to;
                    }
                    group.sendMessage(sys.message().escaped("[Distance] Invalid city name(s): " + disp));
                }
            } catch (IOException | UnirestException e) {
                e.printStackTrace();
            }
        } else {
            this.sendUsage(sys, user, group);
        }
    }

}
