package org.anymetrics.support.filter.JSON;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.FiltersConfig;
import org.anymetrics.core.rule.filter.RuleFilter;
import org.anymetrics.core.task.PipelineTaskContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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

            Map<String, String> fetchDataVariable = fetchData.getFetchDataVariable() == null ? new HashMap<>() : fetchData.getFetchDataVariable();

            // 把JSON.key作为变量名称， JSON.value作为变量值
            initFetchDataVariable(fetchDataVariable, jsonObject);

            fetchData.setFetchDataVariable(fetchDataVariable);
            // 同时把变量写入 Spring EL Context
            fetchData.setELContext(initELVariable(fetchData, fetchDataVariable));

            if(!fetchDataVariable.isEmpty()) {
                context.getLog().trace("JSONRuleFilter - variable : " + JSON.toJSONString(fetchDataVariable));
            }

            splitFetchDatas.add(fetchData);
        }

        // split 一条数据 到 多条数据
        context.setFetchCallbackData(splitFetchDatas);

    }

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


    private void initFetchDataVariable(Map<String, String> fetchDataVariable, JSONObject jsonObject) {
        for(String key : jsonObject.keySet()) {
            fetchDataVariable.put(key, jsonObject.getString(key));
        }
    }

    private StandardEvaluationContext initELVariable(FetchData fetchData, Map<String, String> fetchDataVariable) {
        StandardEvaluationContext context = fetchData.getELContext() == null ? new StandardEvaluationContext() : fetchData.getELContext();
        for(String index : fetchDataVariable.keySet()) {
            String value = fetchDataVariable.get(index);
            try {
                context.setVariable(index, Long.parseLong(value));
            } catch (Exception e) {
                context.setVariable(index, value);
            }
        }
        return context;
    }
}
