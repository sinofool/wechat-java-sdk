package net.sinofool.wechat.mp;

/**
 * Obtain these values from WeChat open platform. <br />
 * If you didn't get verified yet, you can apply for a sample account
 *
 */
final class TestingWeChatMPConfig implements WeChatMPConfig {
    @Override
    public String getToken() {
        return "ecfa5957f27b28184cfd03f53be9de06";
    }

    @Override
    public String getOriginID() {
        return "gh_8152dae8f15d";
    }

    @Override
    public String getAppSecret() {
        return "8fb89e9f5524bc53c5080efe6834cf4b";
    }

    @Override
    public String getAppId() {
        return "wxef6d62224771736f";
    }

    @Override
    public String getAESKey() {
        return "NiUE2JQsCALCtW4DYeor1CGWDCLqeAykGN7sP6WINjH";
    }
}