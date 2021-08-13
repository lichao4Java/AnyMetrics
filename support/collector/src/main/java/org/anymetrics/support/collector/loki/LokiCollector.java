package org.anymetrics.support.collector.loki;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import org.anymetrics.core.collector.Collector;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.task.PipelineTaskContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Loki Collector
 * Use HTTP /loki/api/v1/push send log entries to Loki
 */
public class LokiCollector extends Collector<LokiCollectorConfig> {

    private HttpClientBuilder httpClientBuilder;

    private final static String pushAddr = "http://%s/loki/api/v1/push";

    private String addr;

    @Override
    public void connect() {

        int maxConnPerRoute = Integer.parseInt(System.getProperty("lokiMaxConnPerRoute", "10"));
        int maxConnTotal = Integer.parseInt(System.getProperty("lokiMaxConnTotal", "10"));
        httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
        httpClientBuilder.setMaxConnTotal(maxConnTotal);

        LokiCollectorConfig collectorConfig = getCollectorConfig();
        this.addr = String.format(pushAddr, collectorConfig.getServerAddr());
    }

    @Override
    public void destory() {

    }

    @Override
    public void collect() throws Exception {

        /**
         * content such as:
         *
         * {
         *   "streams": [
         *     {
         *       "stream": {
         *         "label1": "value",
         *         "label2": "value"
         *       },
         *       "values": [
         *           [ "<unix epoch in nanoseconds>", "<log line>" ],
         *           [ "<unix epoch in nanoseconds>", "<log line>" ]
         *       ]
         *     },
         *     {
         *        "stream": {
         *           "label1": "value",
         *           "label2": "value"
         *        },
         *         "values": [
         *              [ "<unix epoch in nanoseconds>", "<log line>" ],
         *              [ "<unix epoch in nanoseconds>", "<log line>" ]
         *          ]
         *     }
         *   ]
         * }
         */
        JSONObject rootNode = new JSONObject();
        JSONArray streamsNode = new JSONArray();
        rootNode.put("streams", streamsNode);

        PipelineTaskContext context = PipelineTaskContext.getContext();
        List<FetchData> fetchDatas = context.getFetchCallbackData();
        LokiStreamConfig streamConfig = getCollectorConfig().getStreams();

        Map<String, Object> streamConfigkv = streamConfig.getStream();


        for (FetchData fetchData : fetchDatas) {
            JSONObject streamNode = new JSONObject();
            JSONObject stream = new JSONObject();

            for(String label : streamConfigkv.keySet()) {
                String key = label;
                Object value = formatVariable(String.valueOf(streamConfigkv.get(label)), fetchData);
                stream.put(key, value);
            }

            String[][] values = new String[][]{
                new String[]{
                        // unix epoch in nanoseconds
                        // such as 1570818238000000000
                        String.format("%d000000", System.currentTimeMillis()),
                        StringUtils.isEmpty(streamConfig.getValue()) ? String.valueOf(fetchData.getFetchData()) : formatVariable(streamConfig.getValue(), fetchData)
                }
            };

            streamNode.put("stream", stream);
            streamNode.put("values", values);
            streamsNode.add(streamNode);
        }

        push(rootNode.toJSONString());
    }

    public void push(String content) {

        PipelineTaskContext context = PipelineTaskContext.getContext();

        context.getLog().trace("LokiCollector push content : " + content);
        HttpPost httpPost = new HttpPost(this.addr);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(content, Charset.forName("UTF-8")));

        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = httpClientBuilder.build();
            response = httpClient.execute(httpPost);
            context.getLog().trace("LokiCollector push result " + response.getStatusLine());
        } catch (Exception e) {
            e.printStackTrace();
            context.getLog().trace("LokiCollector push faild " + e.getMessage());
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

    private String formatVariable(String source, FetchData fetchData) {
        if(source == null) {
            return null;
        }
        Map<String, String> fetchDataVariable = fetchData.getFetchDataVariable();
        if(fetchDataVariable != null) {
            for(String index : fetchDataVariable.keySet()) {
                source = source.replace(index, fetchDataVariable.get(index));
            }
        }
        return source;
    }

}
