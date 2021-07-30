package org.anymetrics.support.filter.JSON;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.FiltersConfig;
import org.anymetrics.core.rule.filter.RuleFilter;
import org.anymetrics.core.task.PipelineTaskContext;

import java.util.*;

/**
 * JSON Filter
 */
public class JSONRuleFilter extends RuleFilter {

    public JSONRuleFilter(FiltersConfig filtersConfig, RuleFilter next) {
        super(filtersConfig, next);
    }

    @Override
    public void filter() {

        PipelineTaskContext context = PipelineTaskContext.getContext();

        List<FetchData> splitFetchDatas = new ArrayList<>();

        // split JSONArray to List<JSONObject>
        List<SplitJSONFetchData> splitJSONFetchDatas = splitJSONFetchData(context);

        for(SplitJSONFetchData splitJSONFetchData : splitJSONFetchDatas) {

            // split 之前的原始 FetchData
            FetchData fetchData = splitJSONFetchData.getRefFetchData();

            // JSON 目标数据
            JSONObject jsonObject = splitJSONFetchData.getJsonObject();

            Map<String, String> fetchDataVariable = initFetchDataVariable(fetchData);

            // 把JSON.key作为变量名称， JSON.value作为变量值
            initFetchDataVariable(fetchDataVariable, null, jsonObject);

            appendSpELVariable(fetchData);

            if(!fetchDataVariable.isEmpty()) {
                context.getLog().trace("JSONRuleFilter - variable : " + JSON.toJSONString(fetchDataVariable));
            }

            splitFetchDatas.add(fetchData);
        }

        context.setFetchCallbackData(splitFetchDatas);

    }

    /**
     *  将每条 FetchData JSONArray 转成 List<JSONObject>
     *  如 FetchData = [{},{}] 对象转成 List<FetchData>
     * @param context
     * @return
     */
    private List<SplitJSONFetchData> splitJSONFetchData(PipelineTaskContext context) {

        List<SplitJSONFetchData> splitJSONFetchDatas = new ArrayList<>();

        Iterator<FetchData> oldFetchCallbackData = context.getFetchCallbackData().iterator();

        while(oldFetchCallbackData.hasNext()) {
            FetchData fetchData = oldFetchCallbackData.next();

            Object fetchDataStr = fetchData.getFetchData();
            Object fetchDataObj = JSON.parse(String.valueOf(fetchDataStr));

            if (fetchDataObj instanceof JSONObject) {
                SplitJSONFetchData splitJSONFetchData = new SplitJSONFetchData();
                splitJSONFetchData.setJsonObject((JSONObject)fetchDataObj);
                splitJSONFetchData.setRefFetchData(fetchData);
                splitJSONFetchDatas.add(splitJSONFetchData);

            }
            else if(fetchDataObj instanceof JSONArray) {

                JSONArray jsonArray = (JSONArray)fetchDataObj;

                for(int i = 0;i < jsonArray.size(); i ++) {

                    SplitJSONFetchData splitJSONFetchData = new SplitJSONFetchData();
                    splitJSONFetchData.setJsonObject(jsonArray.getJSONObject(i));
                    splitJSONFetchData.setRefFetchData(fetchData);
                    splitJSONFetchDatas.add(splitJSONFetchData);

                }
            }
            else {
                context.getLog().trace("JSONRuleFilter - only support JSONObject type, " + fetchDataObj.getClass());
                // do not match
                oldFetchCallbackData.remove();
            }
        }
        return splitJSONFetchDatas;
    }


    /**
     * 将JSON串转换成Map
     *  1 当属性的value为JSONObject类型时，key = 属性名_JSONObject属性名
     *  2 当属性的value为JSONArray类型时，key = 属性名_下标_JSONObject属性名
     *
     * 如以下JSON串：
     * {
     *     "a":1,
     *     "b":{
     *         "c":2
     *     },
     *     "d":[
     *         {
     *             "e":3,
     *             "f":"f"
     *         }
     *     ],
     *     "g":"g"
     * }
     * 转化成Map后的结果为:
     * {
     *     "a":"1",
     *     "b.c":"2",
     *     "d.0.e":"3",
     *     "d.0.f":"f",
     *     "g":"g"
     * }
     *
     * @param fetchDataVariable
     * @param parentKey
     * @param jsonObject
     */
    private void initFetchDataVariable(Map<String, String> fetchDataVariable, String parentKey, JSONObject jsonObject) {

        for(String key : jsonObject.keySet()) {

            String nodeKey = parentKey == null ? key : parentKey + "." + key;

            Object o = jsonObject.get(key);
            if(o instanceof JSONObject) {
                initFetchDataVariable(fetchDataVariable, key, (JSONObject) o);
            }
            else if(o instanceof JSONArray) {
                JSONArray a = (JSONArray) o;
                for(int i = 0; i < a.size(); i ++) {
                    if(a.get(i) instanceof JSONObject) {
                        initFetchDataVariable(fetchDataVariable, key + '.' + i , a.getJSONObject(i));
                    } else {
                        fetchDataVariable.put(nodeKey + "." + i , a.getString(i));
                    }
                }
            }
            else {
                fetchDataVariable.put(nodeKey, jsonObject.getString(key));
            }
        }
    }

}
