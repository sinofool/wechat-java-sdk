package net.sinofool.wechat.mp;

import net.sinofool.wechat.mp.msg.OutgoingTextMessage;

public class IntegrationTestSample {
    public static void main(String[] args) {
        TestingWeChatMPConfig config = new TestingWeChatMPConfig();
        TestingWeChatMPEventHandler handler = new TestingWeChatMPEventHandler();
        TestingWeChatMPHttpClient http = new TestingWeChatMPHttpClient();
        TestingWeChatMPAccessTokenStorage store = new TestingWeChatMPAccessTokenStorage();
        WeChatMP sdk = new WeChatMP(config, handler, http, store);

        OutgoingTextMessage msg = new OutgoingTextMessage();
        msg.setFromUserName(config.getOriginID());
        msg.setToUserName("oEFDisnPNlnZEDYOf28EXSd_7_dk");
        msg.setCreateTime(WeChatUtils.now());
        msg.setContent("Hello\"World");
        sdk.pushMessage(msg);
    }
}
