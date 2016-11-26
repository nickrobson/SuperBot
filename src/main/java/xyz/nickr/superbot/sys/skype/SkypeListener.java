package xyz.nickr.superbot.sys.skype;

import java.util.Scanner;

import in.kyle.ezskypeezlife.Chat;
import in.kyle.ezskypeezlife.api.conversation.SkypeConversation;
import in.kyle.ezskypeezlife.api.conversation.SkypeConversationType;
import in.kyle.ezskypeezlife.api.errors.SkypeCaptcha;
import in.kyle.ezskypeezlife.api.errors.SkypeErrorHandler;
import in.kyle.ezskypeezlife.events.conversation.SkypeConversationUserJoinEvent;
import in.kyle.ezskypeezlife.events.conversation.message.SkypeMessageEditedEvent;
import in.kyle.ezskypeezlife.events.conversation.message.SkypeMessageReceivedEvent;
import in.kyle.ezskypeezlife.events.user.SkypeContactRequestEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.link.LinkCommand;
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
        in.kyle.ezskypeezlife.api.user.SkypeUser user = event.getUser();
        SkypeConversation convo = event.getConversation();
        GroupConfiguration cfg = SuperBotController.getGroupConfiguration(this.sys.wrap(convo));
        if (cfg != null && cfg.isShowJoinMessage()) {
            String welcome = String.format(SuperBotController.WELCOME_MESSAGE_JOIN, user.getUsername(), convo.getTopic());
            String help = "You can access my help menu by typing `" + this.sys.prefix() + "help`";
            String message = Chat.bold(HtmlMessageBuilder.html_escape(welcome)) + "\n" + HtmlMessageBuilder.html_escape(help);
            convo.sendMessage(message);
        }
    }

    public void contactRequest(SkypeContactRequestEvent event) {
        event.getUser().setContact(true);
    }

    public synchronized void chat(SkypeMessageReceivedEvent event) {
        this.cmd(event.getMessage());
    }

    public synchronized void chat(SkypeMessageEditedEvent event) {
        SkypeConversation convo = event.getMessage().getConversation();
        GroupConfiguration conf = SuperBotController.getGroupConfiguration(this.sys.wrap(convo), false);
        boolean isGroup = convo.getConversationType() == SkypeConversationType.GROUP;
        if (isGroup && conf != null && conf.isShowEditedMessages()) {
            MessageBuilder mb = this.sys.message();
            mb.bold(true).escaped(event.getUser().getUsername()).bold(false);
            mb.escaped(" edited their message:").newLine();
            mb.raw(Sys.START_OF_LINE.matcher(event.getContentOld()).replaceAll("&gt; ")).newLine().newLine();
            mb.raw(Sys.START_OF_LINE.matcher(event.getContentNew()).replaceAll("&gt; "));
            convo.sendMessage(mb.build());
        }
        this.cmd(event.getMessage());
    }

    public synchronized void cmd(in.kyle.ezskypeezlife.api.conversation.message.SkypeMessage message) {
        in.kyle.ezskypeezlife.api.user.SkypeUser user = message.getSender();
        SkypeConversation group = message.getConversation();

        Group g = this.sys.wrap(group);
        User u = this.sys.wrap(user);

        SuperBotCommands.exec(this.sys, g, u, this.sys.wrap(message));
        LinkCommand.propagate(this.sys, g, u, this.sys.wrap(message));
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

    @Override
    public void handleException(Exception ex) {
        ex.printStackTrace();
    }

}
