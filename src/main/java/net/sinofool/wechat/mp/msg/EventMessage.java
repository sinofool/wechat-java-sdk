package net.sinofool.wechat.mp.msg;

public abstract class EventMessage implements Message {
    private String toUserName;
    private String fromUserName;
    private int createTime;

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getMsgType() {
        return "event";
    }

    public abstract String getEvent();
}
