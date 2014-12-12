package net.sinofool.wechat.mp;

import static org.junit.Assert.*;
import net.sinofool.wechat.mp.msg.OutgoingTextMessage;

import org.junit.Before;
import org.junit.Test;

public class WeChatMPTest {
    WeChatMP sdk;
    OutgoingTextMessage msg;

    TestingWeChatMPConfig config = new TestingWeChatMPConfig();
    TestingWeChatMPEventHandler handler = new TestingWeChatMPEventHandler();
    TestingWeChatMPHttpClient http = new TestingWeChatMPHttpClient();
    TestingWeChatMPAccessTokenStorage store = new TestingWeChatMPAccessTokenStorage();

    @Before
    public void setUp() throws Exception {
        sdk = new WeChatMP(config, handler, http, store);

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
