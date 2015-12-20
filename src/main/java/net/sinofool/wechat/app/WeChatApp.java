package net.sinofool.wechat.app;

import net.sinofool.wechat.WeChatException;
import net.sinofool.wechat.WeChatJSAPIConfig;
import net.sinofool.wechat.WeChatUserInfo;
import net.sinofool.wechat.base.OneLevelOnlyXML;
import net.sinofool.wechat.mp.*;
import net.sinofool.wechat.mp.msg.*;
import net.sinofool.wechat.thirdparty.org.json.JSONArray;
import net.sinofool.wechat.thirdparty.org.json.JSONObject;
import net.sinofool.wechat.thirdparty.org.json.JSONWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;

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
        WeChatUserInfo user = new WeChatUserInfo();
        JSONObject json = new JSONObject(ret);
        user.setOpenId(WeChatUtils.getJSONString(json, "openid"));
        user.setNickname(WeChatUtils.getJSONString(json, "nickname"));
        user.setSex(WeChatUtils.getJSONInt(json, "sex"));
        user.setProvince(WeChatUtils.getJSONString(json, "province"));
        user.setCity(WeChatUtils.getJSONString(json, "city"));
        user.setCountry(WeChatUtils.getJSONString(json, "country"));
        user.setHeadimgurl(WeChatUtils.getJSONString(json, "headimgurl"));
        JSONArray privs = WeChatUtils.getJSONArray(json, "privilege");
        if (privs != null) {
            for (int i = 0; i < privs.length(); ++i) {
                user.addPrivilege(privs.getString(i));
            }
        }
        user.setUnionid(WeChatUtils.getJSONString(json, "unionid"));
        return user;
    }
}
