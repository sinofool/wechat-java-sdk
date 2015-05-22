package net.sinofool.wechat.mp.msg;

import net.sinofool.wechat.base.OneLevelOnlyXML;

public class TransferCustomerMessage implements ReplyXMLFormat, Message {
    private String toUserName;
    private String fromUserName;
    private int createTime;

    @Override
    public String getMsgType() {
        return "transfer_customer_service";
    }

    public String getToUserName() {
        return this.toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return this.fromUserName;
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
    public String toReplyXMLString() {
        OneLevelOnlyXML xml = new OneLevelOnlyXML();
        xml.createRootElement("xml");
        xml.createChild("ToUserName", getToUserName());
        xml.createChild("FromUserName", getFromUserName());
        xml.createChild("CreateTime", getCreateTime());
        xml.createChild("MsgType", getMsgType());
        return xml.toXMLString();
    }

}
