package net.sinofool.wechat.mp;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sinofool.wechat.WeChatException;
import net.sinofool.wechat.WeChatJSAPIConfig;
import net.sinofool.wechat.WeChatUserInfo;
import net.sinofool.wechat.base.OneLevelOnlyXML;
import net.sinofool.wechat.mp.msg.IncomingClickEventMessage;
import net.sinofool.wechat.mp.msg.IncomingLocationEventMessage;
import net.sinofool.wechat.mp.msg.IncomingScanEventMessage;
import net.sinofool.wechat.mp.msg.IncomingSubscribeEventMessage;
import net.sinofool.wechat.mp.msg.IncomingSubscribeWithScanEventMessage;
import net.sinofool.wechat.mp.msg.IncomingTextMessage;
import net.sinofool.wechat.mp.msg.IncomingViewEventMessage;
import net.sinofool.wechat.mp.msg.Message;
import net.sinofool.wechat.mp.msg.Messages;
import net.sinofool.wechat.mp.msg.PushJSONFormat;
import net.sinofool.wechat.mp.msg.ReplyXMLFormat;
import net.sinofool.wechat.thirdparty.org.json.JSONArray;
import net.sinofool.wechat.thirdparty.org.json.JSONObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WeChatMP {
    public static final String WECHAT_MP_WEB_SCOPE_BASE = "snsapi_base";
    public static final String WECHAT_MP_WEB_SCOPE_USERINFO = "snsapi_userinfo";

    public static final String WECHAT_MP_WEB_LANG_ZHCN = "zh_CN";
    public static final String WECHAT_MP_WEB_LANG_ZHTW = "zh_TW";
    public static final String WECHAT_MP_WEB_LANG_EN = "en";

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WeChatMP.class);
    private final WeChatMPConfig config;
    private final WeChatMPEventHandler eventHandler;
    private final WeChatMPHttpClient httpClient;
    private final WeChatMPAccessTokenStorage atStorage;
    private final byte[] appIdBytes;
    private final byte[] aesKeyBytes;

    public WeChatMP(WeChatMPConfig config, WeChatMPEventHandler eventHandler, WeChatMPHttpClient httpClient,
            WeChatMPAccessTokenStorage atStorage) {
        this.config = config;
        this.eventHandler = eventHandler;
        this.httpClient = httpClient;
        this.atStorage = atStorage;

        this.appIdBytes = config.getAppId().getBytes(Charset.forName("utf-8"));
        if (config.getAESKey() != null) {
            this.aesKeyBytes = DatatypeConverter.parseBase64Binary(config.getAESKey() + "=");
        } else {
            this.aesKeyBytes = null;
        }
        if (null != eventHandler) {
            eventHandler.setWeChatMP(this);
        }
    }

    public boolean isEncrypted() {
        return this.aesKeyBytes != null;
    }

    /**
     * Call this method when you have incoming validate request.<br>
     * It is usually GET request for your endpoint.
     * 
     * @param signature
     * @param echostr
     * @param timestamp
     * @param nonce
     * @return
     */
    public String validate(final String signature, final String echostr, int timestamp, final String nonce) {
        return verify(signature, timestamp, nonce) ? echostr : "";
    }

    /**
     * Call this method when you have a incoming request.
     * 
     * @param signature
     *            From request query string 'signature'
     * @param timestamp
     *            From request query string 'timestamp'
     * @param nonce
     *            From request query string 'nonce'
     * @param encryptType
     *            From request query string 'encrypt_type'
     * @param msgSignature
     *            From request query string 'msg_signature'
     * @param body
     *            From request body
     * @return null if nothing to reply or something wrong. <br>
     *         Application should always return empty page.
     * @throws WeChatException
     *             When there is underlying exception thrown, <br>
     *             Application should response error if wants WeChat platform
     *             retry, or response empty page to ignore.
     */
    public String incomingMessage(final String signature, final int timestamp, final String nonce,
            final String encryptType, final String msgSignature, final String body) {
        if (!verify(signature, timestamp, nonce)) {
            LOG.warn("Failed while verify signature of request query");
            return null;
        }

        String encMessage;
        if (isEncrypted()) {
            if (!encryptType.equals("aes")) {
                LOG.warn("Supoort only encrypted account, please contact support for migration");
                return null;
            }

            encMessage = decryptMPMessage(verifyAndExtractEncryptedEnvelope(timestamp, nonce, msgSignature, body));
            if (encMessage == null) {
                LOG.warn("Failed to extract encrypted envelope");
                return null;
            }
        } else {
            encMessage = body;
        }

        Message dec = Messages.parseIncoming(encMessage);
        if (dec == null) {
            LOG.warn("Failed to decrypt message");
            return null;
        }
        ReplyXMLFormat rpl = dispatch(dec);
        if (rpl == null) {
            // This is normal situation, handler want.
            return null;
        }
        if (isEncrypted()) {
            String enc = encryptMPMessage(rpl.toReplyXMLString());
            if (enc == null) {
                LOG.warn("Failed to encrypt message");
                return null;
            }
            return packAndSignEncryptedEnvelope(enc, WeChatUtils.now(), WeChatUtils.nonce());
        } else {
            return rpl.toReplyXMLString();
        }
    }

    private ReplyXMLFormat dispatch(Message dec) {
        if (dec instanceof IncomingTextMessage) {
            return eventHandler.handle((IncomingTextMessage) dec);
        } else if (dec instanceof IncomingSubscribeEventMessage) {
            return eventHandler.handle((IncomingSubscribeEventMessage) dec);
        } else if (dec instanceof IncomingSubscribeWithScanEventMessage) {
            return eventHandler.handle((IncomingSubscribeWithScanEventMessage) dec);
        } else if (dec instanceof IncomingScanEventMessage) {
            return eventHandler.handle((IncomingScanEventMessage) dec);
        } else if (dec instanceof IncomingLocationEventMessage) {
            return eventHandler.handle((IncomingLocationEventMessage) dec);
        } else if (dec instanceof IncomingClickEventMessage) {
            return eventHandler.handle((IncomingClickEventMessage) dec);
        } else if (dec instanceof IncomingViewEventMessage) {
            return eventHandler.handle((IncomingViewEventMessage) dec);
        } else {
            return null;
        }
    }

    private String packAndSignEncryptedEnvelope(String enc, int createTime, String nonce) {
        OneLevelOnlyXML xml = new OneLevelOnlyXML();
        xml.createRootElement("xml");
        xml.createChild("Encrypt", enc);
        xml.createChild("MsgSignature", sign(createTime, nonce, enc));
        xml.createChild("TimeStamp", createTime);
        xml.createChild("Nonce", nonce);
        return xml.toXMLString();
    }

    private String verifyAndExtractEncryptedEnvelope(final int timestamp, final String nonce,
            final String msgSignature, final String body) {
        String encMessage = null;
        String toUserName = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document root = builder.parse(new ByteArrayInputStream(body.getBytes(Charset.forName("utf-8"))));
            Element doc = root.getDocumentElement();
            encMessage = doc.getElementsByTagName("Encrypt").item(0).getTextContent();
            toUserName = doc.getElementsByTagName("ToUserName").item(0).getTextContent();
        } catch (RuntimeException e) {
            LOG.warn("Failed to parse XML:", e);
            throw new WeChatException(e);
        } catch (Exception e) {
            LOG.warn("Failed to parse XML", e);
            throw new WeChatException(e);
        }

        if (!config.getOriginID().equals(toUserName)) {
            LOG.warn("Failed to parse encrypted envelope, ToUserName expected={} not {}", config.getOriginID(),
                    toUserName);
            return null;
        }

        if (!verify(msgSignature, timestamp, nonce, encMessage)) {
            LOG.warn("Failed to verify encrypted envelope message signature.");
            return null;
        }

        return encMessage;
    }

    public String getAccessToken() {
        String token = atStorage.getAccessToken();
        if (token == null) {
            String ret = httpClient.get(
                    "api.weixin.qq.com",
                    443,
                    "https",
                    "/cgi-bin/token?grant_type=client_credential&appid=" + config.getAppId() + "&secret="
                            + config.getAppSecret());
            JSONObject json = new JSONObject(ret);
            token = json.getString("access_token");
            atStorage.setAccessToken(token, json.getInt("expires_in"));
        }
        return token;
    }

    public WeChatUserInfo getUserInfo(final String openid) {
        String ret = httpClient.get("api.weixin.qq.com", 443, "https", "/cgi-bin/user/info?access_token="
                + getAccessToken() + "&openid=" + openid);
        return parseWeChatUser(ret);
    }

    /**
     * Push message to WeChat platform.
     * 
     * @param message
     */
    public <T extends PushJSONFormat> void pushMessage(T message) {
        String ret = httpClient.post("api.weixin.qq.com", 443, "https", "/cgi-bin/message/custom/send?access_token="
                + getAccessToken(), message.toPushJSONString());
        JSONObject json = new JSONObject(ret);
        if (json.getInt("errcode") != 0) {
            throw new WeChatException(json.getInt("errcode") + ":" + json.getString("errmsg"));
        }
    }

    private boolean verify(final String signature, int timestamp, final String nonce) {
        String[] verify = new String[] { config.getToken(), String.valueOf(timestamp), nonce };
        Arrays.sort(verify);
        return signature.equals(WeChatUtils.sha1hex(verify[0] + verify[1] + verify[2]));
    }

    private boolean verify(String signature, int timestamp, String nonce, String msg) {
        return signature.equals(sign(timestamp, nonce, msg));
    }

    private String sign(int timestamp, String nonce, String msg) {
        String[] verify = new String[] { config.getToken(), String.valueOf(timestamp), nonce, msg };
        Arrays.sort(verify);
        return WeChatUtils.sha1hex(verify[0] + verify[1] + verify[2] + verify[3]);
    }

    final String decryptMPMessage(final String encMessage) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKeyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKeyBytes, 0, 16);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] aesMsg = DatatypeConverter.parseBase64Binary(encMessage);
            byte[] msg = cipher.doFinal(aesMsg);
            int length = ((msg[16] & 0xFF) << 24) | ((msg[17] & 0xFF) << 16) | ((msg[18] & 0xFF) << 8)
                    | (msg[19] & 0xFF);
            if (20 + length + appIdBytes.length + msg[msg.length - 1] != msg.length) {
                LOG.warn("decrypt message length not match length={}, msg.length={}", length, msg.length);
                return null;
            }
            for (int i = 0; i < appIdBytes.length; ++i) {
                if (appIdBytes[i] != msg[20 + length + i]) {
                    LOG.warn("decrypt message appid not match {} expected but {} in message", config.getAppId(),
                            new String(msg, 20 + length, appIdBytes.length, Charset.forName("utf-8")));
                    return null;
                }
            }
            return new String(msg, 20, length, Charset.forName("utf-8"));
        } catch (RuntimeException e) {
            LOG.warn("Failed to decrypt message:", e);
            throw new WeChatException(e);
        } catch (Exception e) {
            LOG.warn("Failed to decrypt message", e);
            throw new WeChatException(e);
        }
    }

    final String encryptMPMessage(final String rpl) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKeyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKeyBytes, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] messageBytes = rpl.getBytes(Charset.forName("utf-8"));
            int usefulLength = 20 + messageBytes.length + appIdBytes.length;
            int padLength = (usefulLength % 32 == 0) ? 32 : 32 - usefulLength % 32;

            byte[] buff = new byte[usefulLength + padLength];

            byte[] rand = new byte[16];
            WeChatUtils.RAND.nextBytes(rand);
            for (int i = 0; i < 16; ++i) {
                buff[i] = rand[i];
            }

            buff[19] = (byte) (messageBytes.length & 0xFF);
            buff[18] = (byte) ((messageBytes.length >> 8) & 0xFF);
            buff[17] = (byte) ((messageBytes.length >> 16) & 0xFF);
            buff[16] = (byte) ((messageBytes.length >> 24) & 0xFF);

            for (int i = 0; i < messageBytes.length; ++i) {
                buff[i + 20] = messageBytes[i];
            }

            for (int i = 0; i < appIdBytes.length; ++i) {
                buff[i + 20 + messageBytes.length] = appIdBytes[i];
            }

            for (int i = 0; i < padLength; ++i) {
                buff[i + usefulLength] = (byte) padLength;
            }

            byte[] msg = cipher.doFinal(buff);
            String enc = DatatypeConverter.printBase64Binary(msg);
            return enc;
        } catch (RuntimeException e) {
            LOG.warn("Failed to decrypt message:", e);
            throw new WeChatException(e);
        } catch (Exception e) {
            LOG.warn("Failed to encrypt message", e);
            throw new WeChatException(e);
        }
    }

    /**
     * 微信公众号网页授权
     * http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html
     * 
     * @param redirectURI
     *            需要确保域名已经加入到信任列表
     * @param scope
     *            请从WECHAT_MP_WEB_SCOPE_BASE和WECHAT_MP_WEB_SCOPE_USERINFO中选择
     * @param state
     * @return 需要引导用户跳转的地址
     * @throws UnsupportedEncodingException
     */
    public String webpageAuthorize(final String redirectURI, final String scope, final String state)
            throws UnsupportedEncodingException {
        StringBuffer redirect = new StringBuffer();
        redirect.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=");
        redirect.append(config.getAppId());
        redirect.append("&redirect_uri=");
        redirect.append(URLEncoder.encode(redirectURI, "utf-8"));
        redirect.append("&response_type=code&scope=");
        redirect.append(scope);
        redirect.append("&state=");
        redirect.append(state);
        redirect.append("#wechat_redirect");
        return redirect.toString();
    }

    /**
     * 
     * @param code
     * @param state
     * @return OpenID for this user;
     */
    public String webpageProcessCallback(final String code, final String state) {
        String ret = httpClient.get("api.weixin.qq.com", 443, "https",
                "/sns/oauth2/access_token?appid=" + config.getAppId() + "&secret=" + config.getAppSecret() + "&code="
                        + code + "&grant_type=authorization_code");

        JSONObject json = new JSONObject(ret);
        String accessToken = json.getString("access_token");
        int expire = json.getInt("expires_in");
        String refreshToken = json.getString("refresh_token");
        String openId = json.getString("openid");
        String scope = json.getString("scope");
        atStorage.setWebpageAccessToken(openId, scope, accessToken, expire);
        atStorage.setWebpageRefreshToken(openId, scope, refreshToken);

        return openId;
    }

    public WeChatUserInfo webpageUserInfo(final String openId, final String lang) {
        String accessToken = atStorage.getWebpageAccessToken(openId, WECHAT_MP_WEB_SCOPE_USERINFO);
        // TODO try refresh once if expired.

        String ret = httpClient.get("api.weixin.qq.com", 443, "https", "/sns/userinfo?access_token=" + accessToken
                + "&openid=" + openId + "&lang=" + lang);

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

    public String getJSAPITicket() {
        String ticket = atStorage.getJSAPITicket();
        if (ticket == null) {
            String ret = httpClient.get("api.weixin.qq.com", 443, "https", "/cgi-bin/ticket/getticket?access_token="
                    + getAccessToken() + "&type=jsapi");
            JSONObject json = new JSONObject(ret);
            ticket = json.getString("ticket");
            atStorage.setJSAPITicket(ticket, json.getInt("expires_in"));
        }
        return ticket;
    }

    public WeChatJSAPIConfig getJSAPIConfig(final String url) {
        String ticket = getJSAPITicket();
        String nonce = WeChatUtils.nonce();
        int timestamp = WeChatUtils.now();
        String signature = WeChatUtils.sha1hex("jsapi_ticket=" + ticket + "&noncestr=" + nonce + "&timestamp="
                + timestamp + "&url=" + url);

        WeChatJSAPIConfig ret = new WeChatJSAPIConfig();
        ret.setAppId(config.getAppId());
        ret.setNonce(nonce);
        ret.setTimestamp(timestamp);
        ret.setSignature(signature);
        return ret;
    }
}
