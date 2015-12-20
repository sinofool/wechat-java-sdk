package net.sinofool.wechat.app;

import net.sinofool.wechat.WeChatUserInfo;
import net.sinofool.wechat.mp.WeChatUtils;
import net.sinofool.wechat.thirdparty.org.json.JSONArray;
import net.sinofool.wechat.thirdparty.org.json.JSONObject;

public class WeChatApp {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WeChatApp.class);
    private final WeChatAppConfig config;

    private final WeChatAppHttpClient httpClient;

    public WeChatApp(WeChatAppConfig config, WeChatAppHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    public boolean getAccessInfo(final String code, final WeChatAppAccessInfo access) {
        String ret = httpClient.get(
                "api.weixin.qq.com",
                443,
                "https",
                "/sns/oauth2/access_token?appid=" + config.getAppId() + "&secret="
                        + config.getAppSecret() + "&code=" + code + "&grant_type=authorization_code");
        JSONObject json = new JSONObject(ret);
        access.setAccessToken(json.getString("access_token"));
        access.setExpiresIn(json.getInt("expires_in"));
        access.setRefreshToken(json.getString("refresh_token"));
        access.setOpenId(json.getString("openid"));
        access.setScope(json.getString("scope"));
        return true;
    }

    public WeChatUserInfo getUserInfo(final String accessToken, final String openid) {
        String ret = httpClient.get("api.weixin.qq.com", 443, "https", "/sns/userinfo?access_token="
                + accessToken + "&openid=" + openid);
        return parseWeChatUser(ret);
    }

    private WeChatUserInfo parseWeChatUser(String ret) {
        LOG.debug("Parsing WeChatUser {}", ret);
        return WeChatUserInfo.valueOf(ret);
    }
}
