package com.liangjing.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

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
            if(Integer.parseInt(map.get("id"))%2 !=0){
                //          System.out.println("body****:"+body);
                System.out.println("***奇数消息发送"+map.toString());

                channel.basicAck(id,true);
            }else{
                System.out.println("***奇数消息回退");
                channel.basicReject(id,true);
            }


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("***奇数消息回退");
            channel.basicReject(id,true);
        }

    }
}
