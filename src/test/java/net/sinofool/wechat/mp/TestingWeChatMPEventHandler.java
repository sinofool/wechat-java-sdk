package net.sinofool.wechat.mp;

import net.sinofool.wechat.mp.msg.IncomingClickEventMessage;
import net.sinofool.wechat.mp.msg.IncomingLocationEventMessage;
import net.sinofool.wechat.mp.msg.IncomingScanEventMessage;
import net.sinofool.wechat.mp.msg.IncomingSubscribeEventMessage;
import net.sinofool.wechat.mp.msg.IncomingSubscribeWithScanEventMessage;
import net.sinofool.wechat.mp.msg.IncomingTextMessage;
import net.sinofool.wechat.mp.msg.IncomingViewEventMessage;
import net.sinofool.wechat.mp.msg.ReplyXMLFormat;
import net.sinofool.wechat.pay.WeChatPay;

final class TestingWeChatMPEventHandler implements WeChatMPEventHandler {
    @Override
    public ReplyXMLFormat handle(IncomingTextMessage incoming) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReplyXMLFormat handle(IncomingSubscribeEventMessage incoming) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReplyXMLFormat handle(IncomingSubscribeWithScanEventMessage incoming) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReplyXMLFormat handle(IncomingScanEventMessage incoming) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReplyXMLFormat handle(IncomingLocationEventMessage incoming) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReplyXMLFormat handle(IncomingClickEventMessage incoming) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReplyXMLFormat handle(IncomingViewEventMessage incoming) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setWeChatMP(WeChatMP mpSDK) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setWeChatPay(WeChatPay paySDK) {
        // TODO Auto-generated method stub
        
    }
}