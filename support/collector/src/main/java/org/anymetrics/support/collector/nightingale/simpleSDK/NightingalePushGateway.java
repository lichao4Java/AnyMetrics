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

    public void push(NightingaleCollectorRegistry registry) {

        PipelineTaskContext context = PipelineTaskContext.getContext();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(JSON.toJSONString(registry.getRegisterMetrics()), Charset.forName("UTF-8")));

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
