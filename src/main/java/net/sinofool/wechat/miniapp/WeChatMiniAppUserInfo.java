package net.sinofool.wechat.miniapp;

import net.sinofool.wechat.mp.WeChatUtils;
import net.sinofool.wechat.thirdparty.org.json.JSONObject;

public class WeChatMiniAppUserInfo {
    public static WeChatMiniAppUserInfo valueOf(String ret) {
        WeChatMiniAppUserInfo user = new WeChatMiniAppUserInfo();
        JSONObject json = new JSONObject(ret);
        user.setOpenId(WeChatUtils.getJSONString(json, "openId"));
        user.setNickName(WeChatUtils.getJSONString(json, "nickName"));
        user.setGender(WeChatUtils.getJSONInt(json, "gender"));
        user.setCity(WeChatUtils.getJSONString(json, "city"));
        user.setProvince(WeChatUtils.getJSONString(json, "province"));
        user.setCountry(WeChatUtils.getJSONString(json, "country"));
        user.setAvatarUrl(WeChatUtils.getJSONString(json, "avatarUrl"));
        user.setUnionId(WeChatUtils.getJSONString(json, "unionId"));
        return user;
    }

    public enum GENDER {
        NONE(0), MALE(1), FEMALE(2);
        private int i;

        GENDER(int i) {
            this.i = i;
        }

        public static GENDER valueOf(int i) {
            if (i == 1) {
                return MALE;
            } else if (i == 2) {
                return FEMALE;
            } else {
                return NONE;
            }
        };

        public int getValue() {
            return this.i;
        }
    }

    private String openId;
    private String nickName;
    private GENDER gender;
    private String city;
    private String province;
    private String country;
    private String avatarUrl;
    private String unionId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public GENDER getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = GENDER.valueOf(gender);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatarUrl(int size) {
        switch (size) {
            case 0:
            case 46:
            case 64:
            case 96:
            case 132:
                return replaceAvatarSize(size);
        }
        return null;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    private String replaceAvatarSize(int size) {
        if (avatarUrl == null) {
            return null;
        }
        int pos = avatarUrl.lastIndexOf('/');
        if (pos == -1) {
            return null;
        }
        return avatarUrl.substring(0, pos + 1) + size;
    }

}
