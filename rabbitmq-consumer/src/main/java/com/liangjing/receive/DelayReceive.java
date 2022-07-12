package com.liangjing.receive;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hewei
 * @date 2022/7/6 16:23
 */
@Component
public class DelayReceive {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "normal_queue",durable = "true",autoDelete = "true"
                    ,arguments = {
                    @Argument(name = "x-dead-letter-exchange",value = "dead_exchange"),
                    @Argument(name = "x-dead-letter-routing-key",value = "dead"),
                    @Argument(name = "x-message-ttl",value = "3000"),
                    @Argument(name = "x-max-length",value = "10")
                }
            ),
            exchange = @Exchange(value = "normal_exchange",durable = "true"),
            key = "normal"
    ),ackMode = "MANUAL")
    public void delay(Map map, Message msg, Channel channel) throws InterruptedException, IOException {
        long id = msg.getMessageProperties().getDeliveryTag();
        try {
            System.out.println("延迟队列："+map.toString());
            TimeUnit.SECONDS.sleep(10);
            System.out.println("延迟队列处理反馈");
//            拒绝后会进入死信队列
            channel.basicReject(id,false);
        }catch (Exception e){
            e.printStackTrace();
            channel.basicReject(id,false);
        }
    }
}
