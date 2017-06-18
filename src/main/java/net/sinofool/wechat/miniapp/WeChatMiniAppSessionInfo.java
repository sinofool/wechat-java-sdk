package net.sinofool.wechat.miniapp;

public class WeChatMiniAppSessionInfo {
    private String openid;
    private String sessionKey;

    public String getOpenid() {

        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}
