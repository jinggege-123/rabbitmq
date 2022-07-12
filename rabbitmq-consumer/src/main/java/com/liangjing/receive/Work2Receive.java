package com.liangjing.receive;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hewei
 * @date 2022/7/5 15:10
 */
@Component
@RabbitListener(queues = "work")
public class Work2Receive {

    @RabbitHandler
    public void work(Map map){
        System.out.println("work222******:"+map);
    }
}
