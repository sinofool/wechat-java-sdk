package net.sinofool.wechat.mp;

public interface WeChatMPHttpClient {
    String get(String host, int port, String schema, String uri);

    String post(String host, int port, String schema, String uri, String body);
}
