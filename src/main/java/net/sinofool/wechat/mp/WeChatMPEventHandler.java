package net.sinofool.wechat.mp;

import net.sinofool.wechat.mp.msg.IncomingClickEventMessage;
import net.sinofool.wechat.mp.msg.IncomingLocationEventMessage;
import net.sinofool.wechat.mp.msg.IncomingScanEventMessage;
import net.sinofool.wechat.mp.msg.IncomingSubscribeEventMessage;
import net.sinofool.wechat.mp.msg.IncomingSubscribeWithScanEventMessage;
import net.sinofool.wechat.mp.msg.IncomingTextMessage;
import net.sinofool.wechat.mp.msg.IncomingViewEventMessage;
import net.sinofool.wechat.mp.msg.ReplyXMLFormat;

public interface WeChatMPEventHandler {
    ReplyXMLFormat handle(final IncomingTextMessage incoming);

    ReplyXMLFormat handle(final IncomingSubscribeEventMessage incoming);

    ReplyXMLFormat handle(final IncomingSubscribeWithScanEventMessage incoming);

    ReplyXMLFormat handle(final IncomingScanEventMessage incoming);

    ReplyXMLFormat handle(final IncomingLocationEventMessage incoming);

    ReplyXMLFormat handle(final IncomingClickEventMessage incoming);

    ReplyXMLFormat handle(final IncomingViewEventMessage incoming);
}
