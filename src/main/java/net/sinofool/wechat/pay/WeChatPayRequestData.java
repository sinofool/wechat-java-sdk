package net.sinofool.wechat.pay;

import net.sinofool.wechat.base.StringPair;

import java.util.*;
import java.util.Map.Entry;

public class WeChatPayRequestData {
    private Map<String, String> data = new HashMap<String, String>();

    public void setString(final String key, final String value) {
        data.put(key, value);
    }

    public List<StringPair> getSortedParameters() {
        List<StringPair> ret = new LinkedList<StringPair>();
        for (Entry<String, String> param : data.entrySet()) {
            ret.add(new StringPair(param.getKey(), param.getValue()));
        }
        Collections.sort(ret);
        return ret;
    }
}
