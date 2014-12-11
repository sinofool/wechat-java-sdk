package net.sinofool.wechat.mp;

import static org.junit.Assert.*;
import net.sinofool.wechat.mp.msg.IncomingTextMessage;
import net.sinofool.wechat.mp.msg.OutgoingTextMessage;
import net.sinofool.wechat.mp.msg.ReplyXMLFormat;

import org.junit.Before;
import org.junit.Test;

public class WeChatMPTest {
    private WeChatMP sdk;
    private OutgoingTextMessage msg;

    @Before
    public void setUp() throws Exception {
        sdk = new WeChatMP("fakeappid", "faketoken", "fake43byteaeskey1fake43byteaeskey2fake43byt", "fakeappsource",
                new WeChatMPEventHandler() {
                    @Override
                    public ReplyXMLFormat handleText(IncomingTextMessage incoming) {
                        return null;
                    }
                });

        msg = new OutgoingTextMessage();
        msg.setFromUserName("fakefrom");
        msg.setToUserName("faketo");
        msg.setCreateTime(WeChatUtils.now());
        msg.setContent("HelloWorld");
    }

    @Test
    public void testSha1() {
        String inHouse = WeChatUtils.sha1Hex("Hello");
        String expected = "f7ff9e8b7bb2e09b70935a5d785e0cc5d9d0abf0";
        assertEquals("SHA1 in WeChatUtils not correct", expected, inHouse);
    }

    @Test
    public void testEncrypt() {
        String xml = msg.toReplyXMLString();
        String encMessage = sdk.encryptMPMessage(xml);
        String decMessage = sdk.decryptMPMessage(encMessage);
        assertEquals("Encrypt/Decrypt pair not match", xml, decMessage);
    }

}
