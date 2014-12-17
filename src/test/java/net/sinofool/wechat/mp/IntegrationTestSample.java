package net.sinofool.wechat.mp;

import net.sinofool.wechat.mp.msg.OutgoingTextMessage;

public class IntegrationTestSample {

    private static TestingWeChatMPConfig config = new TestingWeChatMPConfig();
    private static TestingWeChatMPEventHandler handler = new TestingWeChatMPEventHandler();
    private static TestingWeChatMPHttpClient http = new TestingWeChatMPHttpClient();
    private static TestingWeChatMPAccessTokenStorage store = new TestingWeChatMPAccessTokenStorage();
    private static WeChatMP sdk = new WeChatMP(config, handler, http, store);

    public static void sendMsg(String[] args) {
        OutgoingTextMessage msg = new OutgoingTextMessage();
        msg.setFromUserName(config.getOriginID());
        msg.setToUserName("oEFDisnPNlnZEDYOf28EXSd_7_dk");
        msg.setCreateTime(WeChatUtils.now());
        msg.setContent("Hello\"World");
        sdk.pushMessage(msg);
    }

    public static void main(String[] args) throws Exception {
        sendMsg(args);
    }
}
