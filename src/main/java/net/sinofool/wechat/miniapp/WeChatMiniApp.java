package net.sinofool.wechat.miniapp;

import net.sinofool.wechat.thirdparty.org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class WeChatMiniApp {
    private static final Logger LOG = LoggerFactory.getLogger(WeChatMiniApp.class);

    private final WeChatMiniAppConfig config;

    private final WeChatMiniAppHttpClient httpClient;

    public WeChatMiniApp(final WeChatMiniAppConfig config, final WeChatMiniAppHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    public boolean getSessionInfo(final String code, final WeChatMiniAppSessionInfo session) {
        String ret = httpClient.get(
                "api.weixin.qq.com",
                443,
                "https",
                "/sns/jscode2session?appid=" + config.getAppId() + "&secret=" + config.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code");
        JSONObject json = new JSONObject(ret);
        if (json.has("openid") && json.has("session_key")) {
            session.setOpenid(json.getString("openid"));
            session.setSessionKey(json.getString("session_key"));
            return true;
        } else {
            return false;
        }
    }

    public WeChatMiniAppUserInfo decryptUserInfo(final WeChatMiniAppSessionInfo session, final WeChatMiniAppEncryptedUserInfo encryptedUserInfo) {
        try {
            IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(encryptedUserInfo.getBase64Iv()));
            SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(session.getSessionKey()), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            byte[] dec = cipher.doFinal(Base64.getDecoder().decode(encryptedUserInfo.getBase64EncryptedData()));

            return WeChatMiniAppUserInfo.valueOf(new String(dec, Charset.forName("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            LOG.warn("Error decrypt Wechat MiniApp user info", e);
        } catch (InvalidKeyException e) {
            LOG.warn("Error decrypt Wechat MiniApp user info", e);
        } catch (InvalidAlgorithmParameterException e) {
            LOG.warn("Error decrypt Wechat MiniApp user info", e);
        } catch (NoSuchPaddingException e) {
            LOG.warn("Error decrypt Wechat MiniApp user info", e);
        } catch (BadPaddingException e) {
            LOG.warn("Error decrypt Wechat MiniApp user info", e);
        } catch (IllegalBlockSizeException e) {
            LOG.warn("Error decrypt Wechat MiniApp user info", e);
        }
        return null;
    }
}
