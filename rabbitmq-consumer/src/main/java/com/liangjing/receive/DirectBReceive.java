package com.liangjing.receive;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hewei
 * @date 2022/7/4 16:29
 */
@Component
//@RabbitListener(queues = "directQueue")
public class DirectBReceive {

//    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "directQueue",durable = "true"),
            exchange = @Exchange(value = "directExchange", durable = "true"),
            key = "routing"
    ))
    public void receive(Map map){
        System.out.println("direct-B-*****"+map.toString());
    }
}
