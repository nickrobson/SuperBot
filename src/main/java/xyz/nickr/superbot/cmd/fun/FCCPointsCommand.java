package xyz.nickr.superbot.cmd.fun;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class FCCPointsCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"fccpoints"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[alltime/recent]", "get alltime or recent leaders points"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0 || !(args[0].equalsIgnoreCase("alltime") || args[0].equalsIgnoreCase("recent"))) {
            this.sendUsage(sys, user, group);
            return;
        }
        try {
            JSONArray arr = Unirest.get("http://fcctop100.herokuapp.com/api/fccusers/top/" + args[0].toLowerCase()).asJson().getBody().getArray();
            MessageBuilder mb = sys.message();
            mb.escaped("| Username | All Time | Recent |").newLine();
            mb.escaped("| -------- | -------- | ------ |").newLine();
            for (int i = 0, j = Math.min(10, arr.length()); i < j; i++) {
                JSONObject obj = arr.getJSONObject(i);
                String username = obj.getString("username");
                int alltime = obj.getInt("alltime");
                int recent = obj.getInt("recent");
                mb.escaped("| " + username + " | " + alltime + " | " + recent + " |").newLine();
            }
            if (arr.length() > 0) {
                group.sendMessage(mb);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean useEverythingOn() {
        return false;
    }

}
