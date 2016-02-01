package xyz.nickr.superbot.sys.skype;

import java.util.Scanner;

import in.kyle.ezskypeezlife.Chat;
import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.captcha.SkypeCaptcha;
import in.kyle.ezskypeezlife.api.captcha.SkypeErrorHandler;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import in.kyle.ezskypeezlife.events.conversation.SkypeConversationUserJoinEvent;
import in.kyle.ezskypeezlife.events.conversation.SkypeMessageEditedEvent;
import in.kyle.ezskypeezlife.events.conversation.SkypeMessageReceivedEvent;
import in.kyle.ezskypeezlife.events.user.SkypeContactRequestEvent;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeListener implements SkypeErrorHandler {

    private SkypeSys sys;

    public SkypeListener(SkypeSys sys) {
        this.sys = sys;
    }

    public void join(SkypeConversationUserJoinEvent event) {
        SkypeUser user = event.getUser();
        SkypeConversation convo = event.getConversation();
        GroupConfiguration cfg = SuperBotController.getGroupConfiguration(sys.wrap(convo));
        if (cfg != null && cfg.isShowJoinMessage()) {
            String welcome = String.format(SuperBotController.WELCOME_MESSAGE_JOIN, user.getUsername(), convo.getTopic());
            String help = "You can access my help menu by typing `" + sys.prefix() + "help`";
            String message = Chat.bold(HtmlMessageBuilder.html_escape(welcome)) + "\n" + HtmlMessageBuilder.html_escape(help);
            convo.sendMessage(message);
        }
    }

    public void contactRequest(SkypeContactRequestEvent event) {
        event.getUser().setContact(true);
    }

    public synchronized void chat(SkypeMessageReceivedEvent event) {
        cmd(event.getMessage());
    }

    public synchronized void chat(SkypeMessageEditedEvent event) {
        SkypeConversation convo = event.getMessage().getConversation();
        GroupConfiguration conf = SuperBotController.getGroupConfiguration(sys.wrap(convo), false);
        boolean isGroup = convo.getConversationType() == SkypeConversationType.GROUP;
        if (isGroup && conf != null && conf.isShowEditedMessages()) {
            MessageBuilder<?> mb = sys.message();
            mb.bold(true).text(event.getUser().getUsername()).bold(false);
            mb.text(" edited their message:").newLine();
            mb.html(Sys.START_OF_LINE.matcher(event.getContentOld()).replaceAll("&gt; ")).newLine().newLine();
            mb.html(Sys.START_OF_LINE.matcher(event.getContentNew()).replaceAll("&gt; "));
            convo.sendMessage(mb.build());
        }
        cmd(event.getMessage());
    }

    public synchronized void cmd(SkypeMessage message) {
        SkypeUser user = message.getSender();
        SkypeConversation group = message.getConversation();

        Group g = sys.wrap(group);
        User u = sys.wrap(user);

        SuperBotCommands.exec(sys, g, u, sys.wrap(message));
    }

    @Override
    public String setNewPassword() {
        System.out.println("You need to set a new password!!");
        return null;
    }

    @Override
    public String solve(SkypeCaptcha captcha) {
        System.out.println("Enter captcha ( " + captcha.getUrl() + " )");
        try (Scanner sc = new Scanner(System.in)) {
            return sc.nextLine();
        } catch (Exception ex) {
            return null;
        }
    }

}
