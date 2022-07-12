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
 * @date 2022/7/5 15:03
 */
@RestController
public class WorkController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @GetMapping("/work")
    public String work(){
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "work!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("createTime",createTime);
        rabbitTemplate.convertAndSend("work",map);
        return "work";
    }
}
