package net.sinofool.wechat.mp;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sinofool.wechat.mp.msg.IncomingTextMessage;
import net.sinofool.wechat.mp.msg.Message;
import net.sinofool.wechat.mp.msg.Messages;
import net.sinofool.wechat.mp.msg.OneLevelOnlyXML;
import net.sinofool.wechat.mp.msg.PushJSONFormat;
import net.sinofool.wechat.mp.msg.ReplyXMLFormat;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WeChatMP {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WeChatMP.class);
    private static final Random RAND = new Random(System.currentTimeMillis());

    private final String appId;
    private final String token;
    // private final String aesKey;
    private final String account;
    private final WeChatMPEventHandler eventHandler;

    private final byte[] appIdBytes;
    private final byte[] aesKeyBytes;

    public WeChatMP(String appId, String token, String aesKey, String account, WeChatMPEventHandler eventHandler) {
        this.appId = appId;
        this.token = token;
        // this.aesKey = aesKey;
        this.account = account;
        this.eventHandler = eventHandler;

        this.appIdBytes = appId.getBytes(Charset.forName("utf-8"));
        this.aesKeyBytes = Base64.decodeBase64(aesKey + "=");
    }

    /**
     * Call this method when you have incoming validate request.<br />
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
     * @return null if nothing to reply or something wrong. Application should
     *         always return empty page.
     */
    public ReplyXMLFormat incomingMessage(final String signature, final int timestamp, final String nonce,
            final String encryptType, final String msgSignature, final String body) {
        if (!verify(signature, timestamp, nonce)) {
            LOG.warn("Failed while verify signature of request query");
            return null;
        }

        if (!encryptType.equals("aes")) {
            LOG.warn("Supoort only encrypted account, please contact support for migration");
            return null;
        }

        String encMessage = verifyAndExtractEncryptedEnvelope(timestamp, nonce, msgSignature, body);
        if (encMessage == null) {
            LOG.warn("Failed to extract encrypted envelope");
            return null;
        }

        Message dec = decryptMPMessage(encMessage);
        if (dec == null) {
            LOG.warn("Failed to decrypt message");
            return null;
        }
        ReplyXMLFormat rpl = dispatch(dec);
        if (rpl == null) {
            // This is normal situation, handler want.
            return null;
        }
        String enc = encryptMPMessage(rpl);
        if (enc == null) {
            LOG.warn("Failed to encrypt message");
            return null;
        }
        int createTime = (int) (System.currentTimeMillis() / 1000L);
        return packEncryptedEnvelope(enc, createTime, nonce());
    }

    private ReplyXMLFormat dispatch(Message dec) {
        if (dec instanceof IncomingTextMessage) {
            return eventHandler.handleText((IncomingTextMessage) dec);
        } else {
            return null;
        }
    }

    private ReplyXMLFormat packEncryptedEnvelope(String enc, int createTime, String newNonce) {
        OneLevelOnlyXML xml = new OneLevelOnlyXML();
        xml.createRootElement("xml");
        xml.createChild("Encrypt", enc);
        xml.createChild("MsgSignature", sign(createTime, newNonce, enc));
        xml.createChild("TimeStamp", createTime);
        xml.createChild("Nonce", newNonce);
        return xml;
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
        } catch (Exception e) {
            LOG.warn("Failed to parse XML", e);
            throw new WeChatException(e.getMessage());
        }

        if (!account.equals(toUserName)) {
            LOG.warn("Failed to parse encrypted envelope, ToUserName expected={} not {}", account, toUserName);
            return null;
        }

        if (!verify(msgSignature, timestamp, nonce, encMessage)) {
            LOG.warn("Failed to verify encrypted envelope message signature.");
            return null;
        }

        return encMessage;
    }

    /**
     * Push message to WeChat platform.
     * 
     * @param message
     */
    public <T extends PushJSONFormat> void pushMessage(T message) {
        // TODO
    }

    private boolean verify(final String signature, int timestamp, final String nonce) {
        String[] verify = new String[] { token, String.valueOf(timestamp), nonce };
        Arrays.sort(verify);
        return signature.equals(DigestUtils.sha1Hex(verify[0] + verify[1] + verify[2]));
    }

    private boolean verify(String signature, int timestamp, String nonce, String msg) {
        return signature.equals(sign(timestamp, nonce, msg));
    }

    private String sign(int timestamp, String nonce, String msg) {
        String[] verify = new String[] { token, String.valueOf(timestamp), nonce, msg };
        Arrays.sort(verify);
        return DigestUtils.sha1Hex(verify[0] + verify[1] + verify[2] + verify[3]);
    }

    private static final String NONCE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private String nonce() {
        int length = RAND.nextInt(5) + 5;
        char[] ret = new char[length];
        for (int i = 0; i < length; ++i) {
            ret[i] = NONCE.charAt(RAND.nextInt(NONCE.length()));
        }
        return new String(ret);
    }

    public Message decryptMPMessage(final String encMessage) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKeyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKeyBytes, 0, 16);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] aesMsg = Base64.decodeBase64(encMessage);
            byte[] msg = cipher.doFinal(aesMsg);
            int length = (msg[16] << 24) + (msg[17] << 16) + (msg[18] << 8) + msg[19];
            if (20 + length + appIdBytes.length + msg[msg.length - 1] != msg.length) {
                LOG.warn("decrypt message length not match length={}, msg.length={}", length, msg.length);
                return null;
            }
            for (int i = 0; i < appIdBytes.length; ++i) {
                if (appIdBytes[i] != msg[20 + length + i]) {
                    LOG.warn("decrypt message appid not match {} expected but {} in message", appId, new String(msg,
                            20 + length, appIdBytes.length, Charset.forName("utf-8")));
                    return null;
                }
            }
            String xml = new String(msg, 20, length, Charset.forName("utf-8"));
            return Messages.parseIncoming(xml);
        } catch (Exception e) {
            LOG.warn("Failed to decrypt message", e);
            throw new WeChatException(e.getMessage());
        }
    }

    private void fillIntToByte(final byte[] buff, int pos, int value) {
        buff[pos + 3] = (byte) (value & 0xFF);
        buff[pos + 2] = (byte) ((value >> 8) & 0xFF);
        buff[pos + 1] = (byte) ((value >> 16) & 0xFF);
        buff[pos + 0] = (byte) ((value >> 24) & 0xFF);
    }

    public final String encryptMPMessage(final ReplyXMLFormat rpl) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKeyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKeyBytes, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] messageBytes = rpl.toReplyXMLString().getBytes(Charset.forName("utf-8"));
            int usefulLength = 20 + messageBytes.length + appIdBytes.length;
            int padLength = (usefulLength % 32 == 0) ? 32 : 32 - usefulLength % 32;
            byte[] buff = new byte[usefulLength + padLength];
            byte[] rand = new byte[16];
            RAND.nextBytes(rand);
            for (int i = 0; i < 16; ++i) {
                buff[i] = rand[i];
            }
            fillIntToByte(buff, 16, messageBytes.length);
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
            String enc = Base64.encodeBase64String(msg);
            return enc;
        } catch (Exception e) {
            LOG.warn("Failed to encrypt message", e);
            throw new WeChatException(e.getMessage());
        }
    }
}
