package com.example.rechargeservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RECHARGE_QUEUE       = "recharge.queue";
    public static final String RECHARGE_EXCHANGE    = "recharge.exchange";
    public static final String RECHARGE_ROUTING_KEY = "recharge.routing.key";

    @Bean
    public Queue rechargeQueue() {
        return new Queue(RECHARGE_QUEUE, true);
    }

    @Bean
    public TopicExchange rechargeExchange() {
        return new TopicExchange(RECHARGE_EXCHANGE, true, false);
    }

    @Bean
    public Binding rechargeBinding() {
        return BindingBuilder
                .bind(rechargeQueue())
                .to(rechargeExchange())
                .with(RECHARGE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        return rabbitTemplate;
    }
}