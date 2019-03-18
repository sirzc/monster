package com.yunli.monster.modular.mq;

import com.unitop.mq.common.MessageData;
import com.unitop.mq.exception.MqBusinessException;
import com.unitop.mq.producer.MqProducerFactory;
import com.unitop.mq.producer.UnitopMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouchao
 * @date 2019-03-05 14:26
 */
@RestController
@RequestMapping("/producer")
public class ProducerController {

    private UnitopMQProducer producer = MqProducerFactory.getProducer("unitop");

    @RequestMapping("/send/{num}")
    public List<SendResult> send(@PathVariable int num) {
        List<SendResult> list = new ArrayList<>();
        try {
            for (int i = 0; i < num; i++) {
                producer.send("B-001-1", new MessageData("触发测试:" + num), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        list.add(sendResult);
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });
            }
            return list;
        } catch (MqBusinessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/sendResult/{num}")
    public List<SendResult> sendResults(@PathVariable int num) {
        List<SendResult> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            try {
                SendResult result = producer.send("B-001-1", new MessageData("触发测试:" + num));
                list.add(result);
            } catch (MqBusinessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
