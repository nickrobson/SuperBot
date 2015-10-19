package me.nickrobson.skype.superchat;

import xyz.gghost.jskype.message.FormatUtils;

public class MessageBuilder {
	
	private String msg = "";
	private boolean link = false, bold = false, italic = false, underline = false, code = false, blink = false, size = false, strikethrough = false; 
	
	public MessageBuilder() {}
	
	public MessageBuilder(String initial) {
		msg = initial;
	}
	
	public String build() {
		link(null).strikethrough(false)
			.italic(false).blink(false)
			.underline(false).code(false)
			.size(0).bold(false);
		return msg;
	}
	
	public MessageBuilder newLine() {
		msg += "\n";
		return this;
	}
	
	public MessageBuilder text(String text) {
		msg += FormatUtils.encodeRawText(text);
		return this;
	}
	
	public MessageBuilder html(String text) {
		msg += text;
		return this;
	}
	
	public MessageBuilder link(String url) {
		boolean on = url != null;
		if (link != on) {
			link = on;
			msg += on ? "<a href=\"" + url + "\">" : "</a>";
		}
		return this;
	}
	
	public MessageBuilder bold(boolean on) {
		if (bold != on) {
			bold = on;
			msg += on ? "<b>" : "</b>";
		}
		return this;
	}
	
	public MessageBuilder italic(boolean on) {
		if (italic != on) {
			italic = on;
			msg += on ? "<i>" : "</i>";
		}
		return this;
	}
	
	public MessageBuilder underline(boolean on) {
		if (underline != on) {
			underline = on;
			msg += on ? "<u>" : "</u>";
		}
		return this;
	}
	
	public MessageBuilder strikethrough(boolean on) {
		if (strikethrough != on) {
			strikethrough = on;
			msg += on ? "<s>" : "</s>";
		}
		return this;
	}
	
	public MessageBuilder code(boolean on) {
		if (code != on) {
			code = on;
			msg += on ? "<pre>" : "</pre>";
		}
		return this;
	}
	
	public MessageBuilder blink(boolean on) {
		if (blink != on) {
			blink = on;
			msg += on ? "<blink>" : "</blink>";
		}
		return this;
	}
	
	public MessageBuilder size(int s) {
		boolean on = s > 0;
		if (size != on) {
			size = on;
			msg += on ? "<font size=\"" + size + "\">" : "</font>";
		}
		return this;
	}

}
