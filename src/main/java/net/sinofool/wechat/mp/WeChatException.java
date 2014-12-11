package net.sinofool.wechat.mp;

public class WeChatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WeChatException(String reason) {
        super(reason);
    }
}
