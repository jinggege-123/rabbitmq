package com.liangjing.receive;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hewei
 * @date 2022/7/6 16:29
 */
@Component
public class DeadReceive {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "dead_queue",durable = "true"),
            exchange = @Exchange(value = "dead_exchange",durable = "true"),
            key = "dead"
    ))
    public void delay(Map map, Message msg, Channel channel){
        System.out.println("死信队列："+map.toString());
    }
}
