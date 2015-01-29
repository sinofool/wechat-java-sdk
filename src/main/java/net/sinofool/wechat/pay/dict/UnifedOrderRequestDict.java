package net.sinofool.wechat.pay.dict;

public class UnifedOrderRequestDict {
    public static class REQUIRED {
        public static final String APPID = "appid";
        public static final String MCH_ID = "mch_id";
        public static final String NONCE_STR = "nonce_str";
        public static final String SIGN = "sign";
        public static final String BODY = "body";
        public static final String OUT_TRADE_NO = "out_trade_no";
        public static final String TOTAL_FEE = "total_fee";
        public static final String SPBILL_CREATE_IP = "spbill_create_ip";
        public static final String NOTIFY_URL = "notify_url";
        public static final String TRADE_TYPE = "trade_type";
    }

    public static class OPTIONAL {
        public static final String DEVICE_INFO = "device_info";
        public static final String DETAIL = "detail";
        public static final String ATTACH = "attach";
        public static final String FEE_TYPE = "fee_type";
        public static final String TIME_START = "time_start";
        public static final String TIME_EXPIRE = "time_expire";
        public static final String GOODS_TAG = "goods_tag";
        public static final String PRODUCT_ID = "product_id";
        public static final String OPEN_ID = "openid";
    }
}
