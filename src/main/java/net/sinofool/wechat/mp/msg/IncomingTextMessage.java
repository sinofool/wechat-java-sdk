package net.sinofool.wechat.mp.msg;

public class IncomingTextMessage extends TextMessage {

    private long msgId;

    public long getMsgId() {
        return this.msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }
}
