package com.yunli.mq.common;

/**
 * 配置文件属性名 常量池
 *
 * @author zhouchao
 * @date 2019-01-16 16:47
 */
public class MqConstant {

    /**
     * 配置文件根路径
     */
    public static final String DEFAULT_ROOT_DIR = "mq";

    /**
     * 默认生产者配置文件名
     */
    public static final String DEFAULT_PRODUCER_FILE = "unitop_mq_producer.properties";

    /**
     * 默认消费者配置文件名
     */
    public static final String DEFAULT_CONSUMER_FILE = "unitop_mq_consumer.properties";
    /**
     * 配置文件默认前缀
     */
    public static final String DEFAULT_MQ_PREFIX     = "default";

    /**
     * MQ默认分隔符，RocketMQ中服务地址分割为“;”
     */
    public static final String DEFAULT_MQ_SEPARATOR = ",";

    /*********************************【生产者默认配置】******************************/

    /**
     * -服务地址：
     * -客户端实例名称：客户端创建的多个Producer、 Consumer实际是共用一个内部实例（这个实例包含网络连接、线程资源等）
     * -消息队列数：Number of queues to create per default topic.
     * -超时时间：发送消息超时.默认是3000毫秒
     * -集群名称：
     */
    public static final String MQ_PRODUCER_NAMESERVER        = ".mq.producer.nameServer";
    public static final String MQ_PRODUCER_INSTANCENAME      = ".mq.producer.instanceName";
    public static final String MQ_PRODUCER_TOPICQUEUENUMS    = ".mq.producer.topicQueueNums";
    public static final String MQ_PRODUCER_SENDTIMEOUTMILLIS = ".mq.producer.sendTimeOutMillis";
    public static final String MQ_PRODUCER_GROUPNAME         = ".mq.producer.groupName";

    /*********************************【消费者默认配置】***************************** */

    /**
     * 消费者实现接口
     */
    public static final String CONSUMER_INTERFACE_CLASSNAME = "com.unitop.mq.customer.handler.IMqHandler";

    /**
     * 消费者：
     * -需要启动的消费者配置
     * -服务地址
     * -消费内容
     * -客户端实例名称：客户端创建的多个Producer、 Consumer实际是共用一个内部实例（这个实例包含网络连接、线程资源等）
     * -阻塞时间：消息可能阻止使用线程的最长时间（以分钟为单位,默认：15）。
     * -批量消息大小
     * -最小线程
     * -最大线程
     * -是否广播
     * -是否顺序
     * -监听类路径
     */
    public static final String MQ_START_CONSUMER            = "mq.start.consumer";
    public static final String MQ_CONSUMER_NAMESERVER       = ".consumer.nameServer";
    public static final String MQ_CONSUMER_SUBSCRIBE        = ".consumer.subscribe";
    public static final String MQ_CONSUMER_INSTANCENAME     = ".consumer.instanceName";
    public static final String MQ_CONSUMER_TIMEOUT          = ".consumer.consumeTimeout";
    public static final String MQ_CONSUMER_MESSAGE_SIZE     = ".consumer.consumeMessageBatchMaxSize";
    public static final String MQ_CONSUMER_THREAD_MIN       = ".consumer.ThreadCountMin";
    public static final String MQ_CONSUMER_THREAD_MAX       = ".consumer.ThreadCountMax";
    public static final String MQ_CONSUMER_BROADCAST        = ".consumer.broadcast";
    public static final String MQ_CONSUMER_ORDERLY          = ".consumer.orderly";
    public static final String MQ_CONSUMER_LISTENER_HANDLER = ".consumer.listener";
}
