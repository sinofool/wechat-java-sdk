package net.sinofool.wechat.mp.msg;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.sinofool.wechat.WeChatException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Messages {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Messages.class);

    public static Message parseIncoming(final String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(Charset.forName("utf-8"))));
            Element root = doc.getDocumentElement();
            String type = root.getElementsByTagName("MsgType").item(0).getTextContent();
            if ("text".equals(type)) {
                return handleText(root);
            } else if ("event".equals(type)) {
                return handleEvent(root);
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            LOG.warn("Failed to parse incoming message:", e);
            throw new WeChatException(e);
        } catch (Exception e) {
            LOG.warn("Failed to parse incoming message", e);
            throw new WeChatException(e);
        }
    }

    private static Message handleEvent(Element root) {
        String event = e(root, "Event");
        switch (event) {
        case "subscribe":
            if (root.getElementsByTagName("EventKey").getLength() == 0) {
                IncomingSubscribeWithScanEventMessage msg = new IncomingSubscribeWithScanEventMessage();
                msg.setFromUserName(e(root, "FromUserName"));
                msg.setToUserName(e(root, "ToUserName"));
                msg.setCreateTime(Integer.parseInt(e(root, "CreateTime")));
                msg.setEvent(event);
                msg.setEventKey(e(root, "EventKey"));
                msg.setTicket(e(root, "Ticket"));
                return msg;
            }
        case "unsubscribe": {
            IncomingSubscribeEventMessage msg = new IncomingSubscribeEventMessage();
            msg.setFromUserName(e(root, "FromUserName"));
            msg.setToUserName(e(root, "ToUserName"));
            msg.setCreateTime(Integer.parseInt(e(root, "CreateTime")));
            msg.setEvent(event);
            return msg;
        }
        case "SCAN": {
            IncomingScanEventMessage msg = new IncomingSubscribeWithScanEventMessage();
            msg.setFromUserName(e(root, "FromUserName"));
            msg.setToUserName(e(root, "ToUserName"));
            msg.setCreateTime(Integer.parseInt(e(root, "CreateTime")));
            msg.setEvent(event);
            msg.setEventKey(e(root, "EventKey"));
            msg.setTicket(e(root, "Ticket"));
            return msg;
        }
        case "LOCATION": {
            IncomingLocationEventMessage msg = new IncomingLocationEventMessage();
            msg.setFromUserName(e(root, "FromUserName"));
            msg.setToUserName(e(root, "ToUserName"));
            msg.setCreateTime(Integer.parseInt(e(root, "CreateTime")));
            msg.setEvent(event);
            msg.setLatitude(Double.parseDouble(e(root, "Latitude")));
            msg.setLongitude(Double.parseDouble(e(root, "Longitude")));
            msg.setPrecision(Double.parseDouble(e(root, "Precision")));
            return msg;
        }
        case "CLICK": {
            IncomingClickEventMessage msg = new IncomingClickEventMessage();
            msg.setFromUserName(e(root, "FromUserName"));
            msg.setToUserName(e(root, "ToUserName"));
            msg.setCreateTime(Integer.parseInt(e(root, "CreateTime")));
            msg.setEvent(event);
            msg.setEventKey(e(root, "EventKey"));
            return msg;
        }
        case "VIEW": {
            IncomingViewEventMessage msg = new IncomingViewEventMessage();
            msg.setFromUserName(e(root, "FromUserName"));
            msg.setToUserName(e(root, "ToUserName"));
            msg.setCreateTime(Integer.parseInt(e(root, "CreateTime")));
            msg.setEvent(event);
            msg.setEventKey(e(root, "EventKey"));
            return msg;
        }
        }
        return null;
    }

    private static String e(Element root, String element) {
        return root.getElementsByTagName(element).item(0).getTextContent();
    }

    private static Message handleText(Element root) throws ParserConfigurationException, TransformerException {
        IncomingTextMessage msg = new IncomingTextMessage();
        msg.setFromUserName(e(root, "FromUserName"));
        msg.setToUserName(e(root, "ToUserName"));
        msg.setCreateTime(Integer.parseInt(e(root, "CreateTime")));
        msg.setContent(e(root, "Content"));
        msg.setMsgId(Long.parseLong(e(root, "MsgId")));
        return msg;
    }
}
