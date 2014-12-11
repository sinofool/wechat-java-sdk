package net.sinofool.wechat.mp.msg;

public class OutgoingTextMessage extends TextMessage implements ReplyXMLFormat, PushJSONFormat {

    @Override
    public String toReplyXMLString() {
        OneLevelOnlyXML xml = new OneLevelOnlyXML();
        xml.createRootElement("xml");
        xml.createChild("ToUserName", getToUserName());
        xml.createChild("FromUserName", getFromUserName());
        xml.createChild("CreateTime", getCreateTime());
        xml.createChild("MsgType", getType());
        xml.createChild("Content", getContent());
        return xml.toReplyXMLString();
    }

    @Override
    public String toPushJSONString() {
        // TODO Auto-generated method stub
        return null;
    }

}
