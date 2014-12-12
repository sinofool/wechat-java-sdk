package net.sinofool.wechat.mp;

public interface WeChatMPAccessTokenStorage {
    void setAccessToken(String token, int expire);
    String getAccessToken();
}
