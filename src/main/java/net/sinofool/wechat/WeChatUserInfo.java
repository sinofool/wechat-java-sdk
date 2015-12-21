package net.sinofool.wechat;

import net.sinofool.wechat.mp.WeChatUtils;
import net.sinofool.wechat.thirdparty.org.json.JSONArray;
import net.sinofool.wechat.thirdparty.org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeChatUserInfo {
    public static WeChatUserInfo valueOf(String ret) {
        WeChatUserInfo user = new WeChatUserInfo();
        JSONObject json = new JSONObject(ret);
        user.setOpenId(WeChatUtils.getJSONString(json, "openid"));
        user.setNickname(WeChatUtils.getJSONString(json, "nickname"));
        user.setSex(WeChatUtils.getJSONInt(json, "sex"));
        user.setProvince(WeChatUtils.getJSONString(json, "province"));
        user.setCity(WeChatUtils.getJSONString(json, "city"));
        user.setCountry(WeChatUtils.getJSONString(json, "country"));
        user.setHeadimgurl(WeChatUtils.getJSONString(json, "headimgurl"));
        JSONArray privs = WeChatUtils.getJSONArray(json, "privilege");
        if (privs != null) {
            for (int i = 0; i < privs.length(); ++i) {
                user.addPrivilege(privs.getString(i));
            }
        }
        user.setUnionid(WeChatUtils.getJSONString(json, "unionid"));
        return user;
    }

    public static enum SEX {
        NONE(0), MALE(1), FEMALE(2);
        private int i;

        SEX(int i) {
            this.i = i;
        }

        public static SEX valueOf(int i) {
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
    private String nickname;
    private SEX sex;
    private String province;
    private String city;
    private String country;
    private String headimgurl;
    private List<String> privilege = new ArrayList<String>();
    private String unionid;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public SEX getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = SEX.valueOf(sex);
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String replaceHeadimgSize(int size) {
        if (headimgurl == null) {
            return null;
        }
        int pos = headimgurl.lastIndexOf('/');
        if (pos == -1) {
            return null;
        }
        return headimgurl.substring(0, pos + 1) + size;
    }

    public String getHeadimgurl() {
        return getHeadimgurl(0);
    }

    public String getHeadimgurl(int size) {
        switch (size) {
        case 0:
        case 46:
        case 64:
        case 96:
        case 132:
            return replaceHeadimgSize(size);
        }
        return null;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public List<String> getPrivilege() {
        return privilege;
    }

    public void addPrivilege(String privilege) {
        this.privilege.add(privilege);
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

}
