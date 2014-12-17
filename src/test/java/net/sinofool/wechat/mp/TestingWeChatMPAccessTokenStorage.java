package net.sinofool.wechat.mp;

import java.util.HashMap;
import java.util.Map;

final class TestingWeChatMPAccessTokenStorage implements WeChatMPAccessTokenStorage {
    private String token;
    private int expire;

    @Override
    public void setAccessToken(String token, int expire) {
        this.token = token;
        this.expire = WeChatUtils.now() + expire;
    }

    @Override
    public String getAccessToken() {
        if (this.expire < WeChatUtils.now()) {
            return null;
        }
        return token;
    }

    private Map<String, Map<String, String>> webpageAccessTokens;
    private Map<String, Map<String, Integer>> webpageExpires;

    @Override
    public void setWebpageAccessToken(String openId, String scope, String token, int expire) {
        webpageAccessTokens.put(openId, new HashMap<String, String>());
        webpageExpires.put(openId, new HashMap<String, Integer>());

        webpageAccessTokens.get(openId).put(scope, token);
        webpageExpires.get(openId).put(scope, WeChatUtils.now() + expire);
    }

    @Override
    public String getWebpageAccessToken(String openId, String scope) {
        if (webpageExpires.get(openId) == null) {
            return null;
        }
        if (webpageExpires.get(openId).get(scope) == null) {
            return null;
        }
        int expire = webpageExpires.get(openId).get(scope);
        if (expire < WeChatUtils.now()) {
            return null;
        }

        if (webpageAccessTokens.get(openId) == null) {
            return null;
        }
        if (webpageAccessTokens.get(openId).get(scope) == null) {
            return null;
        }
        return webpageAccessTokens.get(openId).get(scope);
    }

    private Map<String, Map<String, String>> webpageRefreshTokens;

    @Override
    public void setWebpageRefreshToken(String openId, String scope, String token) {
        webpageRefreshTokens.put(openId, new HashMap<String, String>());
        webpageRefreshTokens.get(openId).put(scope, token);
    }

    @Override
    public String getWebpageRefreshToken(String openId, String scope) {
        if (webpageRefreshTokens.get(openId) == null) {
            return null;
        }
        return webpageRefreshTokens.get(openId).get(scope);
    }
}