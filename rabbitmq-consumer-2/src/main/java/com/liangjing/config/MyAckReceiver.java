package com.liangjing.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author hewei
 * @date 2022/7/5 15:32
 */
@Configuration
public class MyAckReceiver implements ChannelAwareMessageListener {


    @Value("${server.port}")
    private String port;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {

            channel.basicQos(1);
            String s = message.toString();
            System.out.println(port+s);

            TimeUnit.SECONDS.sleep(3);
            channel.basicAck(deliveryTag,true);
        }catch (Exception e){
            channel.basicReject(deliveryTag,false);
        }


    }
}
