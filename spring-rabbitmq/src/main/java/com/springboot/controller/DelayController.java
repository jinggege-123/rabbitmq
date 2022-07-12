package com.springboot.controller;

import com.springboot.config.DeadConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author hewei
 * @date 2022/7/6 16:19
 */
@RestController
public class DelayController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/delay")
    public String delay(){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID());
        map.put("name","jinggege");
        map.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:ss:dd")));
        rabbitTemplate.convertAndSend("normal_exchange","normal",map);
        return "delay";
    }
}
