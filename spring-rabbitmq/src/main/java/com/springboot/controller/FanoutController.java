package com.springboot.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author hewei
 * @date 2022/7/5 14:37
 */
@RestController
public class FanoutController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/fanout")
    public String fanout(){
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "fanout!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("createTime",createTime);
        rabbitTemplate.convertAndSend("fanoutExchange",null,map);
        return "fanout";
    }
}
