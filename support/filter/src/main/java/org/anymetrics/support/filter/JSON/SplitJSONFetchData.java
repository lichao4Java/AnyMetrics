package org.anymetrics.support.filter.JSON;

import com.alibaba.fastjson.JSONObject;
import org.anymetrics.core.datasource.callback.FetchData;


public class SplitJSONFetchData {

    private FetchData refFetchData;

    private JSONObject jsonObject;

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }


    public void setRefFetchData(FetchData refFetchData) {
        this.refFetchData = refFetchData;
    }

    public FetchData getRefFetchData() {
        return refFetchData;
    }
}
