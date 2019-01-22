package com.yunli.mq.customer.handler;

import com.yunli.mq.common.MessageData;

import java.util.List;

/**
 * unitop-mq消费者开放接口
 *
 * @author zhouchao
 * @date 2019-01-18 15:09
 */
public interface IMqHandler {
    /**
     * 消息消费
     * @param msg
     * @throws Exception
     */
    void handle(List<MessageData> msg) throws Exception;
}
