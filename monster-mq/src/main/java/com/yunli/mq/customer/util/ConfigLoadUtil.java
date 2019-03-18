package com.yunli.mq.customer.util;

import com.yunli.mq.common.MqConstant;
import com.yunli.mq.common.PropertiesUtil;
import com.yunli.mq.customer.config.ConsumerBuildConfig;
import com.yunli.mq.customer.handler.IMqHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 消费者配置加载工具类
 *
 * @author zhouchao
 * @date 2019-02-27 15:35
 */
public class ConfigLoadUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoadUtil.class);

    private static Properties properties = PropertiesUtil.getConsumerProperties();

    /**
     * 获取需要配置的
     *
     * @return
     */
    public static List<String> listPrefix() {
        String start = properties.getProperty(MqConstant.MQ_START_CONSUMER, MqConstant.DEFAULT_MQ_PREFIX);
        String[] configs = start.split(MqConstant.DEFAULT_MQ_SEPARATOR);
        List<String> prefixList = new ArrayList();
        for (String prefix : configs) {
            if (!prefixList.contains(prefix)) {
                prefixList.add(prefix);
            }
        }
        return prefixList;
    }

    /**
     * 检查常规配置有效性
     *
     * @param prefix
     * @return
     */
    public static boolean checkCommon(String prefix) {
        // 校验服务是否配置
        if (StringUtils.isEmpty(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_NAMESERVER))) {
            return false;
        }
        // 校验是否配置订阅
        if (StringUtils.isEmpty(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_SUBSCRIBE))) {
            return false;
        }
        return true;
    }

    /**
     * 校验Push配置有效性
     *
     * @param prefix 获取的配置
     * @return
     */
    public static boolean checkValid(String prefix) {
        checkCommon(prefix);
        if (StringUtils.isEmpty(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_LISTENER_HANDLER))) {
            return false;
        }
        return true;
    }

    /**
     * 组装配置
     *
     * @param prefix 获取的配置
     * @return
     */
    public static ConsumerBuildConfig getBuildConfig(String prefix) {
        ConsumerBuildConfig config = new ConsumerBuildConfig();
        config.setNameServer(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_NAMESERVER));
        config.setGroupName(prefix);
        String[] subscribe = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_SUBSCRIBE)
                .split(MqConstant.DEFAULT_MQ_SEPARATOR);
        config.setTopic(subscribe[0]);
        if (subscribe.length > 1) {
            config.setSubExpression(subscribe[1]);
        }
        String instanceName = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_INSTANCENAME);
        if (!StringUtils.isEmpty(instanceName)) {
            config.setInstanceName(instanceName);
        }
        String timeout = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_TIMEOUT);
        if (!StringUtils.isEmpty(properties.getProperty(prefix + MqConstant.MQ_CONSUMER_TIMEOUT))) {
            config.setConsumeTimeout(Long.parseLong(timeout));
        }
        String messageSize = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_MESSAGE_SIZE);
        if (!StringUtils.isEmpty(messageSize)) {
            config.setConsumeMessageBatchMaxSize(Integer.parseInt(messageSize));
        }
        String threadMin = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_THREAD_MIN);
        if (!StringUtils.isEmpty(threadMin)) {
            config.setThreadCountMin(Integer.parseInt(threadMin));
        }
        String threadMax = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_THREAD_MAX);
        if (!StringUtils.isEmpty(threadMax)) {
            config.setThreadCountMax(Integer.parseInt(threadMax));
        }
        String isBroadcast = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_BROADCAST);
        if (!StringUtils.isEmpty(isBroadcast)) {
            config.setBroadcast(Boolean.parseBoolean(isBroadcast));
        }
        String isOrderly = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_ORDERLY);
        if (!StringUtils.isEmpty(isOrderly)) {
            config.setOrderly(Boolean.parseBoolean(isOrderly));
        }
        return config;
    }

    /**
     * 注册topic处理器
     *
     * @param topic 主题
     * @param prefix 获取的配置
     * @return
     */
    public static Map<String, List<IMqHandler>> registerTopicHandler(String topic, String prefix) {
        // 获取消息处理器
        String iHandler = properties.getProperty(prefix + MqConstant.MQ_CONSUMER_LISTENER_HANDLER);
        // 注册主题对应的监听器
        Map<String, List<IMqHandler>> topicHandler = new HashMap<>(5);
        List<IMqHandler> iMqHandlers = new ArrayList<>();
        String[] callbackAddress = iHandler.split(MqConstant.DEFAULT_MQ_SEPARATOR);
        for (String callbackName : callbackAddress) {
            try {
                // callbackName完整的类名称，即待回调的类
                Class<?> cc = Class.forName(callbackName);
                // 默认的接口名称
                Class<?> interfaceClass = Class.forName(MqConstant.CONSUMER_INTERFACE_CLASSNAME);
                // 检查当前类是否继承了 默认的接口
                boolean rightStatus = Arrays.asList(cc.getInterfaces()).contains(interfaceClass);
                if (!rightStatus) {
                    logger.error("com.yunli.mq.mq consumer register error. class:{} not implement {}", callbackName,
                            MqConstant.CONSUMER_INTERFACE_CLASSNAME);
                    continue;
                }
                // newInstance:相当于先加载到类即（classs.forName）,再实例化
                IMqHandler mqHandler = (IMqHandler) cc.newInstance();
                iMqHandlers.add(mqHandler);
            } catch (Exception e) {
                logger.error("com.yunli.mq.mq consumer register error. analysis class:{}", callbackName, e);
            }
        }
        topicHandler.put(topic, iMqHandlers);
        return topicHandler;
    }
}
