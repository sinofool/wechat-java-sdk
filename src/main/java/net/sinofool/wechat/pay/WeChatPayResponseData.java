package net.sinofool.wechat.pay;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sinofool.wechat.base.GroupStringPair;
import net.sinofool.wechat.base.StringPair;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WeChatPayResponseData {

    public static WeChatPayResponseData parse(String text) throws ParserConfigurationException, SAXException,
            IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(text)));
        NodeList childNodes = doc.getDocumentElement().getChildNodes();
        WeChatPayResponseData ret = new WeChatPayResponseData();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            if (childNodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            ret.data.add(childNodes.item(i).getNodeName(), childNodes.item(i).getTextContent());
        }
        return ret;
    }

    private GroupStringPair data = new GroupStringPair();

    public GroupStringPair getAllData() {
        return data;
    }

    public String getString(final String key) {
        return data.get(key);
    };

    public Date getDate(final String key) throws ParseException {
        String v = getString(key);
        if (v == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(v);
    }

    public Boolean getBoolean(final String key) {
        String v = getString(key);
        if (v == null) {
            return null;
        }
        if (v.equalsIgnoreCase("success")) {
            return true;
        }
        if (v.equalsIgnoreCase("N")) {
            return false;
        }
        if (v.equalsIgnoreCase("T")) {
            return true;
        }
        return null;
    }

    public List<StringPair> getSortedParameters(final String... skipKeys) {
        return data.getSorted(skipKeys);
    }

    public List<StringPair> getOrderedParameters(final String... skipKeys) {
        return data.getOrdered(skipKeys);
    }
}
