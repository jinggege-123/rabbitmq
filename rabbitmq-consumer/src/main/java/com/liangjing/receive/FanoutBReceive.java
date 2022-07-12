package com.liangjing.receive;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hewei
 * @date 2022/7/5 14:39
 */
@Component
//@RabbitListener(queues = "queue.B")
public class FanoutBReceive {

//    @RabbitHandler
//不需要指定key
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.B"),
            exchange = @Exchange(value = "fanoutExchange", type = "fanout")

    ))
    public void receiveA(Map map){
        System.out.println("queueB***:"+map.toString());
    }
}
