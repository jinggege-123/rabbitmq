package com.liangjing.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author hewei
 * @date 2022/7/4 14:27
 */
@Component
public class DirectConfig {

    @Bean
    public Queue directQueue(){
        return new Queue("directQueue",true);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("directExchange");
    }

    @Bean
    public Binding bindDirect(){
        return BindingBuilder.bind(directQueue()).to(directExchange()).with("routing");
    }

}
