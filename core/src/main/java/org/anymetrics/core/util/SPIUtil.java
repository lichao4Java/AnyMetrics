package org.anymetrics.core.util;

import org.anymetrics.core.task.ConfigTaskManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SPIUtil {

    private static Logger logger = LoggerFactory.getLogger("SPIUtil");

    private static final String internel = "META-INF/anymetrics";

    public static Properties loadConfig(Class clazz) {
        String path = internel + "/" + clazz.getName();
        return loadProperties(path);
    }

    public static Class loadConfigClass(Class clazz, String config) {
        Properties configProperties = loadConfig(clazz);
        String className = configProperties.getProperty(config);
        if(className != null) {
            try {
                return SPIUtil.class.getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Properties loadProperties(String path) {
        Properties properties = new Properties();
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = ConfigTaskManage.class.getClassLoader().getResourceAsStream(path);
            properties.load(resourceAsStream);
            logger.info("loaded {} \n {}", path, properties.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }
}
