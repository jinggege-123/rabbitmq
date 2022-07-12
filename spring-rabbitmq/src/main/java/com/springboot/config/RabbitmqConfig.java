package com.springboot.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hewei
 * @date 2022/7/4 15:56
 */
@Configuration
public class RabbitmqConfig {
    @Bean
    public RabbitTemplate returnRabbitTemplate(ConnectionFactory connectionFactory){

        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        //true:交换机无法将消息进行路由时，会将该消息返回给生产者
        //false:如果发现消息无法进行路由，则直接丢弃;默认false
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
                System.out.println("ConfirmCallback:     "+"确认情况："+b);
                System.out.println("ConfirmCallback:     "+"原因："+s);
            }
        });

        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage re) {

                System.out.println("ReturnCallback:     "+"消息："+re.getMessage());
                System.out.println("ReturnCallback:     "+"回应码："+re.getReplyCode());
                System.out.println("ReturnCallback:     "+"回应信息："+re.getReplyText());
                System.out.println("ReturnCallback:     "+"交换机："+re.getExchange());
                System.out.println("ReturnCallback:     "+"路由键："+re.getRoutingKey());
            }
        });
        return rabbitTemplate;
    }

}
