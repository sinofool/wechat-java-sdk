package net.sinofool.wechat.mp.msg;

import java.io.StringWriter;

import net.sinofool.wechat.thirdparty.org.json.JSONWriter;

public class OutgoingTextMessage extends TextMessage implements ReplyXMLFormat, PushJSONFormat {

    @Override
    public String toReplyXMLString() {
        OneLevelOnlyXML xml = new OneLevelOnlyXML();
        xml.createRootElement("xml");
        xml.createChild("ToUserName", getToUserName());
        xml.createChild("FromUserName", getFromUserName());
        xml.createChild("CreateTime", getCreateTime());
        xml.createChild("MsgType", getMsgType());
        xml.createChild("Content", getContent());
        return xml.toReplyXMLString();
    }

    @Override
    public String toPushJSONString() {
        StringWriter w = new StringWriter();
        JSONWriter json = new JSONWriter(w);
        json.object().key("touser").value(getToUserName());
        json.key("msgtype").value(getMsgType());
        json.key("text").object();
        json.key("content").value(getContent());
        json.endObject().endObject();
        return w.toString();
    }

}
