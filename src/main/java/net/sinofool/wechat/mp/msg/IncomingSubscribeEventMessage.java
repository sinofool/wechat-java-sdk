package net.sinofool.wechat.mp.msg;

public class IncomingSubscribeEventMessage extends EventMessage {

    private String event;

    @Override
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

}
