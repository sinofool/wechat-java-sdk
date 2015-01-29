package net.sinofool.wechat.pay;

public interface WeChatPayHttpClient {
    String get(String host, int port, String schema, String uri);

    String post(String host, int port, String schema, String uri, String body);
}
