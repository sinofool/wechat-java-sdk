package net.sinofool.wechat.mp.msg;

import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OneLevelOnlyXML implements ReplyXMLFormat {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(OneLevelOnlyXML.class);

    private Document doc;
    private Element root;

    public void createRootElement(String tag) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            root = doc.createElement(tag);
            doc.appendChild(root);
        } catch (Exception e) {
            LOG.warn("Cannot create XML document", e);
        }
    }

    public void createChild(String tag, String value) {
        if (doc == null || root == null) {
            return;
        }
        Element encrypt = doc.createElement(tag);
        encrypt.appendChild(doc.createTextNode(value));
        root.appendChild(encrypt);
    }

    public void createChild(String tag, int value) {
        createChild(tag, String.valueOf(value));
    }

    @Override
    public String toReplyXMLString() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(doc), new StreamResult(out));
            return out.toString("utf-8");
        } catch (Exception e) {
            LOG.warn("Cannot serialize to XML", e);
        }
        return null;
    }
}
