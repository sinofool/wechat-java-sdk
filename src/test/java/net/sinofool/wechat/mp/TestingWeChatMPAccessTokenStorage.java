package net.sinofool.wechat.mp;

final class TestingWeChatMPAccessTokenStorage implements WeChatMPAccessTokenStorage {
    private String token;
    private int expire;

    @Override
    public void setAccessToken(String token, int expire) {
        this.token = token;
        this.expire = WeChatUtils.now() + expire;
    }

    @Override
    public String getAccessToken() {
        if (this.expire < WeChatUtils.now()) {
            return null;
        }
        return token;
    }
}