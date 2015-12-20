package net.sinofool.wechat.mp.msg;

import net.sinofool.wechat.base.OneLevelOnlyXML;
import net.sinofool.wechat.thirdparty.org.json.JSONWriter;

import java.io.StringWriter;

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
        return xml.toXMLString();
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
