package org.anymetrics.support.datasource.http;

import com.alibaba.fastjson.JSONObject;
import org.anymetrics.core.datasource.bounded.ScheduleBoundedDataSource;
import org.anymetrics.core.datasource.callback.FetchCallback;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.BoundedRuleConfig;
import org.anymetrics.core.task.PipelineTaskContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  įŽåį HTTP DataSource
 * @author lichao
 */
public class SimpleHTTPDataSource extends ScheduleBoundedDataSource<SimpleHTTPDataSourceConfig, BoundedRuleConfig> {


    @Override
    public void scheduleFetch(PipelineTaskContext context, FetchCallback dataCallback) {

        String url = getDataSourceConfig(context).getUrl();

        String content = null;
        int httpCode = 0;
        try {
            CloseableHttpResponse closeableHttpResponse = sendHTTP(url);
            httpCode = closeableHttpResponse.getStatusLine().getStatusCode();
            content = EntityUtils.toString(closeableHttpResponse.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            httpCode = 500;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("httpCode", httpCode);
        jsonObject.put("content", content);

        String fetchDataStr = jsonObject.toJSONString();
        context.getLog().trace(fetchDataStr);
        List<FetchData> httpDatas = new ArrayList<>();
        FetchData fetchData = new FetchData();
        fetchData.setFetchData(fetchDataStr);
        httpDatas.add(fetchData);

        context.setFetchCallbackData(httpDatas);
        try {
            dataCallback.callback(context);
        } catch (Exception e) {
            e.printStackTrace();
            context.getLog().trace(e.toString());
        }
    }

    private static RequestConfig requestConfig = RequestConfig.custom()
            // čŽžįŊŽčŋæĨčļæļæļé´(åäŊæ¯Ģį§)
            .setConnectTimeout(5000)
            // čŽžįŊŽč¯ˇæąčļæļæļé´(åäŊæ¯Ģį§)
            .setConnectionRequestTimeout(5000)
            // socketč¯ģåčļæļæļé´(åäŊæ¯Ģį§)
            .setSocketTimeout(5000)
            // čŽžįŊŽæ¯åĻåčŽ¸éåŽå(éģčŽ¤ä¸ētrue)
            .setRedirectsEnabled(true).build();

    private static CloseableHttpResponse sendHTTP(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = null;
        try {
            // įąåŽĸæˇįĢ¯æ§čĄ(åé)Getč¯ˇæą
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // éæžčĩæē
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }


    @Override
    public void connect() {

    }
}
