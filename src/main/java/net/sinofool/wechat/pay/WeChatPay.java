package net.sinofool.wechat.pay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import net.sinofool.wechat.WeChatException;
import net.sinofool.wechat.base.OneLevelOnlyXML;
import net.sinofool.wechat.base.StringPair;
import net.sinofool.wechat.mp.WeChatMPEventHandler;
import net.sinofool.wechat.mp.WeChatUtils;
import net.sinofool.wechat.pay.dict.UnifedOrderRequestDict;

import org.xml.sax.SAXException;

public class WeChatPay {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WeChatPay.class);

    private final WeChatPayConfig config;
    private final WeChatPayHttpClient http;

    public WeChatPay(final WeChatPayConfig config, WeChatMPEventHandler eventHandler, final WeChatPayHttpClient http) {
        this.config = config;
        this.http = http;
        if (null != eventHandler) {
            eventHandler.setWeChatPay(this);
        }
    }

    public WeChatPayRequestData makeJSAPIPayment(int timestamp, String nonce, String prepayId) {
        WeChatPayRequestData request = new WeChatPayRequestData();
        request.setString("appId", config.getAppId());
        request.setString("timeStamp", String.valueOf(timestamp));
        request.setString("nonceStr", nonce);
        request.setString("package", "prepay_id=" + prepayId);
        request.setString("signType", "MD5");
        return request;
    }

    public String signJSAPIPayment(WeChatPayRequestData request) {
        return signMD5(request.getSortedParameters());
    }

    public WeChatPayRequestData makeOrder(String tradeNo, String name, double fee, String ip, String notify,
            String openId) {
        WeChatPayRequestData request = new WeChatPayRequestData();
        request.setString(UnifedOrderRequestDict.REQUIRED.APPID, config.getAppId());
        request.setString(UnifedOrderRequestDict.REQUIRED.MCH_ID, config.getMchId());
        request.setString(UnifedOrderRequestDict.REQUIRED.NONCE_STR, WeChatUtils.nonce());
        request.setString(UnifedOrderRequestDict.REQUIRED.OUT_TRADE_NO, tradeNo);
        request.setString(UnifedOrderRequestDict.REQUIRED.SPBILL_CREATE_IP, ip);
        request.setString(UnifedOrderRequestDict.REQUIRED.TOTAL_FEE, String.valueOf(fee * 100));
        request.setString(UnifedOrderRequestDict.REQUIRED.BODY, name);
        request.setString(UnifedOrderRequestDict.REQUIRED.NOTIFY_URL, notify);
        request.setString(UnifedOrderRequestDict.REQUIRED.TRADE_TYPE, "JSAPI");
        request.setString(UnifedOrderRequestDict.OPTIONAL.OPEN_ID, openId);
        return request;
    }

    public String placeOrder(final WeChatPayRequestData request) {
        OneLevelOnlyXML xml = new OneLevelOnlyXML();
        xml.createRootElement("xml");
        for (StringPair param : sign(request.getSortedParameters())) {
            xml.createChild(param.getFirst(), param.getSecond());
        }
        String str = xml.toXMLString();
        LOG.info("Order request: {}", str);
        String ret = http.post("api.mch.weixin.qq.com", 443, "https", "/pay/unifiedorder", str);
        LOG.info("Order response: {}", ret);
        try {
            WeChatPayResponseData response = WeChatPayResponseData.parse(ret);
            String sign = signMD5(response.getAllData().getSorted("sign"));
            if (!sign.equalsIgnoreCase(response.getString("sign"))) {
                LOG.warn("Failed to verify sign");
                return null;
            }
            if (!"SUCCESS".equalsIgnoreCase(response.getString("return_code"))
                    || !"SUCCESS".equalsIgnoreCase(response.getString("result_code"))) {
                LOG.warn("Error returned {}", response.getString("return_msg"));
                return null;
            }
            return response.getString("prepay_id");
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Cannot parse result", e);
            return null;
        } catch (ParserConfigurationException e) {
            LOG.warn("Cannot parse result", e);
            return null;
        } catch (SAXException e) {
            LOG.warn("Cannot parse result", e);
            return null;
        } catch (IOException e) {
            LOG.warn("Cannot parse result", e);
            return null;
        }
    }

    private List<StringPair> sign(final List<StringPair> p) {
        String sign = signMD5(p);
        p.add(new StringPair("sign", sign));
        return p;
    }

    private String signMD5(final List<StringPair> p) {
        String param = join(p, false);
        String sign = WeChatUtils.md5Hex(param + "&key=" + config.getPayKey()).toUpperCase(Locale.ENGLISH);
        LOG.trace("Signing {}", param);
        return sign;
    }

    protected String join(final List<StringPair> p, boolean encode) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < p.size(); ++i) {
            if (i != 0) {
                buff.append("&");
            }
            buff.append(p.get(i).getFirst()).append("=");
            if (encode) {
                try {
                    buff.append(URLEncoder.encode(p.get(i).getSecond(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    LOG.warn("Cannot encode {}", p.get(i).getSecond());
                    throw new WeChatException(e);
                }
            } else {
                buff.append(p.get(i).getSecond());
            }
        }
        return buff.toString();
    }

}
