package net.sinofool.wechat.mp;

public interface WeChatMPAccessTokenStorage {
    void setAccessToken(String token, int expire);

    String getAccessToken();

    void setWebpageAccessToken(String openId, String scope, String token, int expire);

    String getWebpageAccessToken(String openId, String scope);

    void setWebpageRefreshToken(String openId, String scope, String token);

    String getWebpageRefreshToken(String openId, String scope);
}
