package com.liangjing.receive;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hewei
 * @date 2022/7/4 17:01
 */
@Component
//@RabbitListener(queues = "topic.man")
public class TopicReceive {

//    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "topic.man"),
            exchange = @Exchange(value = "topicExchange",type = "topic"),
            key = "topic.man"
    ))
    public void man(Map map){
        System.out.println("man@@@@@@:"+map.toString());
    }
}
