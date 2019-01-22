package com.yunli.mq.customer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunli.mq.common.MqConstant;
import com.yunli.mq.common.PropertiesUtil;
import com.yunli.mq.common.ScanPackageUtil;
import com.yunli.mq.customer.annotation.MqConsumerMeta;
import com.yunli.mq.customer.config.ConsumerBuildConfig;
import com.yunli.mq.customer.handler.IMqHandler;
import com.yunli.mq.exception.MqConsumerConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 消费者管理类
 *
 * @author zhouchao
 * @date 2019-01-18 16:19
 */
public class MqConsumerManager {

    private static final Logger logger = LoggerFactory.getLogger(MqConsumerManager.class);

    /**
     * 初始化默认配置
     */
    public static void init() {
        init(MqConstant.DEFAULT_MQ_PREFIX);
    }

    /**
     * 初始化多个消费集群
     *
     * @param prefix
     */
    public static void init(String... prefix) {
        String packages = PropertiesUtil.getScanPackages(prefix);
        // 获取所有需要注册的消费者
        List<ConsumerBuildConfig> consumerBuildConfigs = getConsumerBuildConfigs(packages);
        // 循环注册消费者
        for (ConsumerBuildConfig buildConfig : consumerBuildConfigs) {
            initConsumerGroup(buildConfig);
        }
    }

    /**
     * 初始化消费者组
     */
    private static void initConsumerGroup(ConsumerBuildConfig buildConfig) {
        // 获取选定的配置
        String prefix = buildConfig.getPrefix();
        // 通过消费者工厂获取消费者实例
        FastMqPushConsumer consumer = MqConsumerFactory.getFactory().getPushConsumer(prefix);
        try {
            // 加载消费者消费配置
            consumer.loadBuildConfig(buildConfig);
            consumer.start();
            logger.info("mq start. a consumer started. use config:{} ,use buildConfig:{}", prefix, buildConfig);
        } catch (Exception e) {
            String warn = "mq error. load consumer error. use config: " + prefix + " buildConfig:" + buildConfig;
            logger.error(warn, e);
            throw new MqConsumerConfigException(warn, e);
        }
    }

    /**
     * 获取消费者动态配置
     *
     * @param packages 扫描包路径
     * @return
     */
    private static List<ConsumerBuildConfig> getConsumerBuildConfigs(String packages) {
        // 存放组装结果
        Map<String, ConsumerBuildConfig> consumerConfigs = Maps.newHashMap();
        Set<String> consumerCallbackNames = ScanPackageUtil.findAnnotationClass(packages, MqConsumerMeta.class);
        // 存放指定类的完整的路径名称
        ArrayList<String> consumerCallbackNamesList = new ArrayList<>(consumerCallbackNames);
        Collections.sort(consumerCallbackNamesList);
        for (String callbackName : consumerCallbackNamesList) {
            try {
                // callbackName完整的类名称，即待回调的类
                Class<?> cc = Class.forName(callbackName);
                // 默认的接口名称
                Class<?> interfaceClass = Class.forName(MqConstant.CONSUMER_INTERFACE_CLASSNAME);
                // 检查当前类是否继承了 默认的接口
                boolean rightStatus = Arrays.asList(cc.getInterfaces()).contains(interfaceClass);
                if (!rightStatus) {
                    logger.error("consumer register error. class:{} not implement {}", callbackName,
                            MqConstant.CONSUMER_INTERFACE_CLASSNAME);
                    continue;
                }
                // 获取当前类中注解实体
                MqConsumerMeta meta = cc.getAnnotation(MqConsumerMeta.class);
                if (meta == null) {
                    logger.error("consumer register error. class:{} annotation analysis wrong.", callbackName);
                    continue;
                }
                // newInstance:相当于先加载到类即（classs.forName）,再实例化
                IMqHandler mqHandler = (IMqHandler) cc.newInstance();
                //  每组一个配置,返回map的键为groupName
                consumerConfigs = getConsumerConfigList(consumerConfigs, meta, mqHandler);
            } catch (Exception e) {
                logger.error("consumer register error. analysis class:{}", callbackName, e);
            }
        }
        return new ArrayList<>(consumerConfigs.values());

    }

    /**
     * 组装消费者
     *
     * @param consumerConfigs
     * @param meta
     * @param mqHandler
     * @return
     */
    private static Map<String, ConsumerBuildConfig> getConsumerConfigList(
            Map<String, ConsumerBuildConfig> consumerConfigs, MqConsumerMeta meta, IMqHandler mqHandler) {
        if (consumerConfigs == null) {
            consumerConfigs = Maps.newHashMap();
        }
        // 获取注解中的值
        String topic = meta.topic();
        String group = meta.group();
        String subExpression = meta.subExpression();
        String prefix = meta.prefix();
        boolean orderly = meta.isOrderly();
        boolean broadcast = meta.isBroadcast();
        int consumerThreadCountMin = meta.consumerThreadCountMin();
        int consumerThreadCountMax = meta.consumerThreadCountMax();
        int consumeMessageBatchMaxSize = meta.consumeMessageBatchMaxSize();

        // 格式化组名称
        String groupName = formatGroupName(group, orderly, broadcast);
        // 检查配置是否存在
        if (!consumerConfigs.containsKey(groupName)) {
            //该组配置不存在，生成
            ConsumerBuildConfig buildConfig = new ConsumerBuildConfig();
            buildConfig.setGroupName(groupName);
            buildConfig.setConsumerThreadCountMin(consumerThreadCountMin);
            buildConfig.setConsumerThreadCountMax(consumerThreadCountMax);
            buildConfig.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
            buildConfig.setSubExpression(subExpression);
            buildConfig.setGroup(group);
            buildConfig.setOrderly(orderly);
            buildConfig.setBroadcast(broadcast);
            buildConfig.setPrefix(prefix);
            // 建立topic与实现类的关系
            Map<String, List<IMqHandler>> topicHandler = Maps.newHashMap();
            List<IMqHandler> handlers = Lists.newArrayList();
            handlers.add(mqHandler);
            topicHandler.put(topic, handlers);

            buildConfig.setTopicHandler(topicHandler);
            consumerConfigs.put(groupName, buildConfig);
        } else {
            // 该group配置存在，则更新
            ConsumerBuildConfig buildConfig = consumerConfigs.get(groupName);
            // 检查已存在消费主题
            Map<String, List<IMqHandler>> topicHandler = buildConfig.getTopicHandler();
            if (topicHandler == null) {
                String error = "mq wrong. topic handler is null. topic:" + topic;
                logger.error(error);
                throw new MqConsumerConfigException(error);
            }
            // 一个group中有多个topic，一个topic可能存在多个实例(线程)
            if (topicHandler.containsKey(topic)) {
                List<IMqHandler> handlers = topicHandler.get(topic);
                handlers.add(mqHandler);
            } else {
                List<IMqHandler> handlers = Lists.newArrayList();
                handlers.add(mqHandler);
                topicHandler.put(topic, handlers);
            }
            // 更新最小线程数
            if (consumerThreadCountMin > buildConfig.getConsumerThreadCountMin()) {
                buildConfig.setConsumerThreadCountMin(consumerThreadCountMin);
            }
            // 更新最大线程数
            if (consumerThreadCountMax > buildConfig.getConsumerThreadCountMax()) {
                buildConfig.setConsumerThreadCountMax(consumerThreadCountMin);
            }
            // 更新最大消费数据
            if (consumeMessageBatchMaxSize > buildConfig.getConsumeMessageBatchMaxSize()) {
                buildConfig.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
            }
            // 更新消费对象 子表达式
            buildConfig.setSubExpression(subExpression);
            // 更新消费对象对应的静态配置
            buildConfig.setPrefix(prefix);
            // 更新组中的主题信息
            consumerConfigs.replace(groupName, buildConfig);
        }
        return consumerConfigs;
    }

    /**
     * 格式化组名称
     *
     * @param group     组名称
     * @param orderly   是否顺序
     * @param broadcast 是否广播
     * @return
     */
    private static String formatGroupName(String group, boolean orderly, boolean broadcast) {
        String groupName = group;
        if (orderly) {
            groupName = groupName + MqConstant.CONSUMER_GROUPNAME_SEP + MqConstant.CONSUMER_ORDERLY_TRUE;
        }
        if (broadcast) {
            groupName = groupName + MqConstant.CONSUMER_GROUPNAME_SEP + MqConstant.CONSUMER_BROADCAST_TRUE;
        }
        return groupName;
    }
}
