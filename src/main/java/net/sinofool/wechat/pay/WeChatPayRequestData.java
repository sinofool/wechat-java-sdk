package net.sinofool.wechat.pay;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sinofool.wechat.base.StringPair;

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
