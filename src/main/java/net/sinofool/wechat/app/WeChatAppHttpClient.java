package net.sinofool.wechat.app;

public interface WeChatAppHttpClient {
    String get(String host, int port, String schema, String uri);

    String post(String host, int port, String schema, String uri, String body);
}
