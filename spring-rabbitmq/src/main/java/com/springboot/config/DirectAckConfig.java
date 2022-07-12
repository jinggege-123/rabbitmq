package com.springboot.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hewei
 * @date 2022/7/6 9:29
 */
@Configuration
public class DirectAckConfig {

    @Bean
    public Queue ackQueue(){
        return new Queue("acksQueue",false);
//        return QueueBuilder.durable("ackQueue").build();
    }

    @Bean
    public DirectExchange ackDirectExchange(){
        return new DirectExchange("acksExchange",false,false);
//        return ExchangeBuilder.directExchange("ackExchange").build();
    }

    @Bean
    public Binding bindings(){
        return BindingBuilder.bind(ackQueue()).to(ackDirectExchange()).with("acksing");
    }
}
