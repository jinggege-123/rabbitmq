package com.springboot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hewei
 * @date 2022/7/5 14:33
 */
@Configuration
public class FanoutConfig {

    @Bean
    public Queue queueA(){
        return new Queue("queue.A");
    }
    @Bean
    public Queue queueB(){
        return new Queue("queue.B");
    }
    @Bean
    public Queue queueC(){
        return new Queue("queue.C");
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("fanoutExchange");
    }

    @Bean
    public Binding bindingA(){
        return BindingBuilder.bind(queueA()).to(fanoutExchange());
    }
    @Bean
    public Binding bindingB(){
        return BindingBuilder.bind(queueB()).to(fanoutExchange());
    }

    @Bean
    public Binding bindingC(){
        return BindingBuilder.bind(queueC()).to(fanoutExchange());
    }


}
