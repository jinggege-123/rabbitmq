package com.liangjing.receive;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hewei
 * @date 2022/7/4 16:29
 */
@Component
//@RabbitListener(queues = "directQueue")
public class DirectAReceive {

//    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "directQueue",durable = "true"),
            exchange = @Exchange(value = "directExchange", durable = "true"),
            key = "routing"
    ))
    public void receive(Map map) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        System.out.println("direct-A-*****"+map.toString());
    }
}
