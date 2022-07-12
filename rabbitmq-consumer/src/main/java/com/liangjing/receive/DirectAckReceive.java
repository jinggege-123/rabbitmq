package com.liangjing.receive;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hewei
 * @date 2022/7/6 9:34
 */
@Component
public class DirectAckReceive {

    @RabbitListener(bindings=@QueueBinding(
            value =@Queue(value = "acksQueue",durable = "false"),
            exchange = @Exchange(value = "acksExchange" ,type = "direct",durable = "false"),
            key = "acksing"
    ),ackMode = "MANUAL")
    public void ackReceive(Map<String,String> map, Message msg, Channel channel) throws IOException {
        long id = msg.getMessageProperties().getDeliveryTag();
        try {
            if(Integer.parseInt(map.get("id"))%2==0){
                //          System.out.println("body****:"+body);
            TimeUnit.SECONDS.sleep(3);
                System.out.println("***偶数消息发送"+map.toString());

                channel.basicAck(id,true);
            }else{
                System.out.println("***偶数消息回退");
                channel.basicReject(id,true);
            }


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("***消息回退");
            channel.basicReject(id,true);
        }

    }
}
