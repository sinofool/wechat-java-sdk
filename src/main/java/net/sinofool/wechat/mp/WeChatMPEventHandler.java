package net.sinofool.wechat.mp;

import net.sinofool.wechat.mp.msg.IncomingTextMessage;
import net.sinofool.wechat.mp.msg.ReplyXMLFormat;

public interface WeChatMPEventHandler {
    ReplyXMLFormat handleText(final IncomingTextMessage incoming);
}
