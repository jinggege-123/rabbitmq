package com.springboot.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author hewei
 * @date 2022/7/6 16:04
 */
@Configuration
public class DeadConfig {

    private final static String deadExchange= "dead_exchange";
    private final static String deadQueue = "dead_queue";
    private final static String dead_key= "dead";

    @Bean
    public Queue deadQueue(){
        return QueueBuilder.durable(DeadConfig.deadQueue).build();
    }

    @Bean
    public DirectExchange deadExchange(){
        return ExchangeBuilder.directExchange(DeadConfig.deadExchange).durable(true).build();
    }

    @Bean
    public Binding deadBinding(){
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(dead_key);
    }

    private final static String normalExchange= "normal_exchange";
    private final static String normalQueue = "normal_queue";
    private final static String normal_key= "normal";

    @Bean
    public Queue normalQueue(){
        return QueueBuilder.durable(normalQueue)
                .deadLetterExchange(deadExchange)
                .deadLetterRoutingKey(dead_key)
                .ttl(3000)
                .maxLength(10)
                .autoDelete()
//                设置队列优先级
//                .maxPriority(12)
//                设置为惰性队列
//                .lazy()

                .build();
    }

    @Bean
    public DirectExchange normalExchange(){
        return ExchangeBuilder.directExchange(normalExchange).durable(true).build();
    }

    @Bean
    public Binding normalBinding(){
        return BindingBuilder.bind(normalQueue()).to(normalExchange()).with(normal_key);
    }

}
