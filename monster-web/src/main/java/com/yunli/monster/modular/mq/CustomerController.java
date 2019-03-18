package com.yunli.monster.modular.mq;

import com.unitop.mq.common.MessageData;
import com.unitop.mq.customer.MqConsumerManager;
import com.unitop.mq.customer.handler.IMqHandler;
import com.unitop.mq.customer.pull.UnitopMQPullConsumer;
import com.unitop.mq.exception.MqBusinessException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouchao
 * @date 2019-03-05 15:18
 */
@RestController
@RequestMapping("/customer")
public class CustomerController implements IMqHandler {
    private  UnitopMQPullConsumer consumer = MqConsumerManager.getPullConsumer("unitop");

    @Override
    public void handle(List<MessageData> list) throws Exception {
        System.err.println(list);
    }

    @RequestMapping("/pull/{num}")
    public List<MessageData> pull(@PathVariable int num) {
        List<MessageData> result = new ArrayList<>();
        try {
            consumer.pull(list -> {
                result.addAll(list);
            }, num, null);
        } catch (MqBusinessException e) {
            e.printStackTrace();
        }
        return result;
    }
}
