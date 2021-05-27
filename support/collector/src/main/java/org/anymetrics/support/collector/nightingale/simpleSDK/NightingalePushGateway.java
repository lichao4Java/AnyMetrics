package org.anymetrics.support.collector.nightingale.simpleSDK;

import com.alibaba.fastjson.JSON;
import org.anymetrics.core.task.PipelineTaskContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NightingalePushGateway {

    private String url;

    private int maxConnPerRoute;

    private int maxConnTotal;

    private HttpClientBuilder httpClientBuilder;

    public NightingalePushGateway(String url) {
        this.url = url;
        this.maxConnPerRoute = Integer.parseInt(System.getProperty("nightingaleMaxConnPerRoute", "10"));
        this.maxConnTotal = Integer.parseInt(System.getProperty("nightingaleMaxConnTotal", "10"));
        httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
        httpClientBuilder.setMaxConnTotal(maxConnTotal);
    }


    /**
     *
     * fomart as:
     *
     * [
     *     {
     *         "metric": "disk.io.util",
     *         "endpoint": "10.86.12.13",
     *         "tags": "device=sda",
     *         "value": 15.4,
     *         "timestamp": 1554455574,
     *         "step": 20
     *     },
     *     {
     *         "metric": "api.latency",
     *         "endpoint": "10.86.12.13",
     *         "tags": "api=/api/v1/auth/login,srv=n9e,mod=monapi,idc=bj",
     *         "value": 5.4,
     *         "timestamp": 1554455574,
     *         "step": 20
     *     }
     * ]
     */
    public String format(NightingaleCollectorRegistry registry) {
        List<Map<String, Object>> metricsMap = new ArrayList<>();
        List<NightingaleMetrics> registerMetrics = registry.getRegisterMetrics();
        for(NightingaleMetrics metrics : registerMetrics) {
            Map<List<String>, NightingaleMetrics.Child> childrens = metrics.getChildren();
            for(List<String> key : childrens.keySet()) {
                Map<String, Object> metric = new HashMap<>();
                metric.put("metric", metrics.getMetric());
                metric.put("endpoint", metrics.getMetric());
                metric.put("timestamp", metrics.getTimestamp());
                metric.put("step", metrics.getStep());
                metric.put("nid", metrics.getNid());

                StringBuffer tags = new StringBuffer();
                NightingaleMetrics.Child child = childrens.get(key);
                List<String> keyNames = child.getKeyNames();
                for(int i = 0; i < keyNames.size(); i ++) {
                    tags.append(",").append(keyNames.get(i)).append("=").append(child.getKeyValues().get(i));
                }
                metric.put("tags", tags.toString().replaceFirst(",", ""));
                metric.put("value", child.getValue());
                metricsMap.add(metric);
            }
        }
        return JSON.toJSONString(metricsMap);
    }

    public void push(NightingaleCollectorRegistry registry) {

        PipelineTaskContext context = PipelineTaskContext.getContext();

        String content = format(registry);

        context.getLog().trace("nightingaleCollector push content : " + content);

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(content, Charset.forName("UTF-8")));

        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = httpClientBuilder.build();
            response = httpClient.execute(httpPost);
            context.getLog().trace("NightingalePushGateway push result " + response.getStatusLine());
        } catch (Exception e) {
            e.printStackTrace();
            context.getLog().trace("NightingalePushGateway push faild " + e.getMessage());
        }finally {
            try {
                if(httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
