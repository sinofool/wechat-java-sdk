package net.sinofool.wechat.miniapp;

public interface WeChatMiniAppHttpClient {
    String get(String host, int port, String schema, String uri);

    String post(String host, int port, String schema, String uri, String body);
}
