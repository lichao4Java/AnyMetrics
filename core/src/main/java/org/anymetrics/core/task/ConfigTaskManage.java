package org.anymetrics.core.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.anymetrics.core.collector.CollectorConfig;
import org.anymetrics.core.datasource.DataSourceConfig;
import org.anymetrics.core.rule.RuleConfig;
import org.anymetrics.core.util.SPIUtil;

import java.util.*;

public class ConfigTaskManage {

    private static ConfigService configService;

    public static final String defaultDataId = "AnyMetricsConfig";

    public static final String defaultGroup = "DEFAULT";

    private static String dataId;

    private static String group;

    static {
        Properties properties = new Properties();
        String nacos = System.getProperties().getProperty("nacos.address");
        dataId = System.getProperties().getProperty("nacos.config.dataId");
        group = System.getProperties().getProperty("nacos.config.group");

        if(nacos == null) {
            throw new IllegalArgumentException("缺少Nacos地址，请添加启动参数 -Dnacos.address=x.x.x.x");
        }
        if(dataId == null) {
            dataId = defaultDataId;
        }
        if(group == null) {
            group = defaultGroup;
        }
        properties.put("serverAddr", nacos);
        try {
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public static String getConfigsStr() {
        try {
            return configService.getConfig(dataId, group, 5000);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateConfigStr(String newConfigStr) {
        try {
            return configService.publishConfig(dataId, group, newConfigStr);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static List<ConfigTask> configs;

    /**
     * 加载所有任务配置
     * @return
     */
    public static List<ConfigTask> loadConfigTasks() {

        synchronized (ConfigTask.class) {
            if(configs != null) {
                return configs;
            }
            configs = new ArrayList<>();
            String source = getConfigsStr();
            if(source == null) {
                return configs;
            }
            JSONArray jsonArray = JSONArray.parseArray(source);
            for(int i = 0; i < jsonArray.size(); i ++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                loadConfigTask(jsonObject);
            }
            return configs;
        }
    }

    private static ConfigTask removeConfigIfExists(String id) {
        Iterator<ConfigTask> iterator = configs.iterator();
        while (iterator.hasNext()) {
            ConfigTask next = iterator.next();
            if(id.equalsIgnoreCase(next.getId())) {
                iterator.remove();
                return next;
            }
        }
        return null;
    }

    public static ConfigTask loadConfigTask(String jsonConfigStr) {
        JSONObject jsonConfigObject = JSON.parseObject(jsonConfigStr);
        return loadConfigTask(jsonConfigObject);
    }

    private static ConfigTask loadConfigTask(JSONObject jsonConfigObject) {
        Set<String> configKeys = jsonConfigObject.keySet();

        String id = jsonConfigObject.getString("id");
        if(id == null) {
            throw new IllegalArgumentException("missing id");
        }

        // 修改配置
        removeConfigIfExists(id);

        ConfigTask config = new ConfigTask();
        for(String configKey : configKeys) {
            if("dataSource".equalsIgnoreCase(configKey)) {
                config.setDataSource(loadDataSourceConfig(jsonConfigObject.getJSONObject(configKey)));
            }
            else if("rule".equalsIgnoreCase(configKey)) {
                config.setRule(loadRuleConfig(jsonConfigObject.getJSONObject(configKey)));
            }
            else if("name".equalsIgnoreCase(configKey)) {
                config.setName(jsonConfigObject.getString(configKey));
            }
            else if("id".equalsIgnoreCase(configKey)) {
                config.setId(jsonConfigObject.getString(configKey));
            }
            else if("iframe".equalsIgnoreCase(configKey)) {
                config.setIframe(jsonConfigObject.getString(configKey));
            }
            else if("collector".equalsIgnoreCase(configKey)) {
                config.setCollector(loadCollectorConfig(jsonConfigObject.getJSONObject(configKey)));
            }
        }

        configs.add(config);
        return config;
    }


    private static RuleConfig loadRuleConfig(JSONObject jsonObject) {
        String kind = jsonObject.getString("kind");
        Class ruleConfigClass = SPIUtil.loadConfigClass(RuleConfig.class, kind);
        Object ruleConfig = jsonObject.toJavaObject(ruleConfigClass);

        try {
            return (RuleConfig) ruleConfig;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static DataSourceConfig loadDataSourceConfig(JSONObject jsonObject) {
        String type = jsonObject.getString("type");
        Class dataSourceConfigClass = SPIUtil.loadConfigClass(DataSourceConfig.class, type);
        Object dataSourceConfig = jsonObject.toJavaObject(dataSourceConfigClass);
        try {
            return (DataSourceConfig) dataSourceConfig;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static CollectorConfig loadCollectorConfig(JSONObject jsonObject) {
        String type = jsonObject.getString("type");
        Class collectorConfigClass = SPIUtil.loadConfigClass(CollectorConfig.class, type);
        Object collectorConfig = jsonObject.toJavaObject(collectorConfigClass);
        try {
            return (CollectorConfig) collectorConfig;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
