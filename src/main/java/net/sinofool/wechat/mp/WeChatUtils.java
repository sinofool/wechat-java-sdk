package net.sinofool.wechat.mp;

import net.sinofool.wechat.WeChatException;
import net.sinofool.wechat.thirdparty.org.json.JSONArray;
import net.sinofool.wechat.thirdparty.org.json.JSONException;
import net.sinofool.wechat.thirdparty.org.json.JSONObject;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class WeChatUtils {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WeChatUtils.class);

    // Random shared instance
    public static final Random RAND = new Random(System.currentTimeMillis());

    // time in seconds
    public static int now() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    // Digest
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F' };

    private static String hex(byte[] input, boolean upperCase) {
        StringBuffer buf = new StringBuffer();
        for (int j = 0; j < input.length; j++) {
            if (upperCase) {
                buf.append(DIGITS_UPPER[(input[j] >> 4) & 0x0f]);
                buf.append(DIGITS_UPPER[input[j] & 0x0f]);
            } else {
                buf.append(DIGITS_LOWER[(input[j] >> 4) & 0x0f]);
                buf.append(DIGITS_LOWER[input[j] & 0x0f]);
            }
        }
        return buf.toString();
    }

    /**
     * Lower case sha1
     * 
     * @param input
     * @return
     */
    public static String sha1hex(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA1").digest(input.getBytes(Charset.forName("utf-8")));
            return hex(digest, false);
        } catch (NoSuchAlgorithmException e) {
            LOG.warn("Cannot find SHA1 digest algorithm", e);
            throw new WeChatException(e);
        }
    }

    /**
     * Lower case md5
     * 
     * @param input
     * @return
     */
    public static String md5hex(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(input.getBytes(Charset.forName("utf-8")));
            return hex(digest, false);
        } catch (NoSuchAlgorithmException e) {
            LOG.warn("Cannot find MD5 digest algorithm", e);
            throw new WeChatException(e);
        }
    }

    /**
     * Upper case md5
     * 
     * @param input
     * @return
     */
    public static String md5HEX(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(input.getBytes(Charset.forName("utf-8")));
            return hex(digest, true);
        } catch (NoSuchAlgorithmException e) {
            LOG.warn("Cannot find MD5 digest algorithm", e);
            throw new WeChatException(e);
        }
    }

    // Nonce generation
    private static final String NONCE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String nonce() {
        int length = WeChatUtils.RAND.nextInt(5) + 5;
        char[] ret = new char[length];
        for (int i = 0; i < length; ++i) {
            ret[i] = NONCE.charAt(WeChatUtils.RAND.nextInt(NONCE.length()));
        }
        return new String(ret);
    }

    // JSON
    public static String getJSONString(JSONObject obj, String key) {
        try {
            return obj.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public static int getJSONInt(JSONObject obj, String key) {
        try {
            return obj.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    public static JSONArray getJSONArray(JSONObject obj, String key) {
        try {
            return obj.getJSONArray(key);
        } catch (JSONException e) {
            return null;
        }
    }
}
