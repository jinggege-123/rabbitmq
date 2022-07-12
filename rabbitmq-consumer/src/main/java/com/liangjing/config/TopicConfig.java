package com.liangjing.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hewei
 * @date 2022/7/4 16:50
 */
//@Configuration
public class TopicConfig {

    private static final String man = "topic.man";
    private static final String women = "topic.women";

//    @Bean
    public Queue manQueue(){
        return new Queue(TopicConfig.man);
    }
//    @Bean
    public Queue womenQueue(){
        return new Queue(TopicConfig.women);
    }

//    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("topicExchange");
    }

//    @Bean
    public Binding bindingExchange1(){
        return  BindingBuilder.bind(manQueue()).to(topicExchange()).with(man);
    }

//    @Bean
    public Binding bindingExchange2(){
        return  BindingBuilder.bind(womenQueue()).to(topicExchange()).with("topic.#");
    }
}
