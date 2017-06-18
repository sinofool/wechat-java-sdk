package net.sinofool.wechat.miniapp;

public class WeChatMiniAppEncryptedUserInfo {
    private String base64EncryptedData;
    private String base64Iv;

    public String getBase64EncryptedData() {
        return base64EncryptedData;
    }

    public void setBase64EncryptedData(String base64EncryptedData) {
        this.base64EncryptedData = base64EncryptedData;
    }

    public String getBase64Iv() {
        return base64Iv;
    }

    public void setBase64Iv(String base64Iv) {
        this.base64Iv = base64Iv;
    }
}
