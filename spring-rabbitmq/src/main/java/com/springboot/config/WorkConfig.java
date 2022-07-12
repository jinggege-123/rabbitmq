package com.springboot.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hewei
 * @date 2022/7/5 14:58
 */
@Configuration
public class WorkConfig {
    @Bean
    public Queue workQueue(){
        return new Queue("work",true);
    }
}
