package com.yunli.mq.common;

import com.google.common.collect.Sets;
import com.yunli.mq.exception.PropertiesLoadException;
import com.yunli.mq.producer.MqProducerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * 配置读取工具类
 *
 * @author zhouchao
 * @date 2019-01-18 11:21
 */
public class PropertiesUtil {

    /**
     * 获取消费者需要扫描的包路径
     *
     * @param prefix 配置属性的前缀
     * @return
     */
    public static String getScanPackages(String... prefix) {
        String scanPackages = MqConstant.CONSUMER_DEFAULT_SCAN_PACKAGE;
        Set<String> packages = Sets.newHashSet();
        for (String str : prefix) {
            packages.add(getConsumerProperties().getProperty(str + MqConstant.MQ_CONSUMER_SCAN_PACKAGE));
        }
        if (packages.size() > 0) {
            scanPackages = String.join(",", packages);
        }
        return scanPackages;
    }

    /**
     * 获取生产者配置
     * @return producer properties
     */
    public static Properties getProducerProperties() {
        String filePath = null;
        if (MqConstant.DEFAULT_ROOT_DIR.trim().length() > 0) {
            filePath = MqConstant.DEFAULT_ROOT_DIR + "/" + MqConstant.DEFAULT_PRODUCER_FILE;
        } else {
            filePath = MqConstant.DEFAULT_PRODUCER_FILE;
        }
        return getProperties(filePath);
    }

    /**
     * 获取消费者配置
     * @return consumer properties
     */
    public static Properties getConsumerProperties() {
        String filePath = null;
        if (MqConstant.DEFAULT_ROOT_DIR.trim().length() > 0) {
            filePath = MqConstant.DEFAULT_ROOT_DIR + "/" + MqConstant.DEFAULT_CONSUMER_FILE;
        } else {
            filePath = MqConstant.DEFAULT_CONSUMER_FILE;
        }
        return getProperties(filePath);
    }

    /**
     * 读取配置文件
     *
     * @param path 文件路径
     * @return
     */
    public static Properties getProperties(String path) {
        Properties props = new Properties();
        try {
            InputStream in = MqProducerFactory.class.getClassLoader().getResourceAsStream(path);
            props.load(in);
        } catch (IOException e) {
            throw new PropertiesLoadException("无法加载到配置文件，异常路径：" + path, e);
        }
        return props;
    }
}
