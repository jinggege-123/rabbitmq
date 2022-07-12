package com.liangjing.receive;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hewei
 * @date 2022/7/5 15:10
 */
@Component
@RabbitListener(queues = "work")
public class Work1Receive {

    @RabbitHandler

    public void work(Map map) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2000);
        System.out.println("work1111******:"+map);
    }
}
