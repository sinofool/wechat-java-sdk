package net.sinofool.wechat.mp.msg;

public abstract class EventMessage implements Message {

    @Override
    public String getType() {
        return "event";
    }

    public abstract String getEvent();
}
