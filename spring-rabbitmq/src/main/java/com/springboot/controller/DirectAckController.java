package com.springboot.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hewei
 * @date 2022/7/6 9:48
 */
@RestController
public class DirectAckController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/ack/{id}")
    public String acks(@PathVariable Integer id){
        Map<String,String> map = new HashMap<>();
        map.put("id",id.toString());
        map.put("name","jinggege");
        map.put("age","16");
        rabbitTemplate.convertAndSend("acksExchange","acksing",map);
        return "ack";
    }
}
