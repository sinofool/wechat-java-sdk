package net.sinofool.wechat.mp;

import net.sinofool.wechat.mp.msg.*;
import net.sinofool.wechat.pay.WeChatPay;

public interface WeChatMPEventHandler {
    void setWeChatMP(WeChatMP mpSDK);
    void setWeChatPay(WeChatPay paySDK);

    ReplyXMLFormat handle(final IncomingTextMessage incoming);

    ReplyXMLFormat handle(final IncomingSubscribeEventMessage incoming);

    ReplyXMLFormat handle(final IncomingSubscribeWithScanEventMessage incoming);

    ReplyXMLFormat handle(final IncomingScanEventMessage incoming);

    ReplyXMLFormat handle(final IncomingLocationEventMessage incoming);

    ReplyXMLFormat handle(final IncomingClickEventMessage incoming);

    ReplyXMLFormat handle(final IncomingViewEventMessage incoming);
}
