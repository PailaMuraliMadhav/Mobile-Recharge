package com.example.rechargeservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RabbitMQConfig
 * DESCRIPTION:
 *   Spring configuration class that declares the RabbitMQ queue, exchange, binding,
 *   and message converter beans used by the Recharge Service to publish notification events.
 */
@Configuration
public class RabbitMQConfig {

    public static final String RECHARGE_QUEUE       = "recharge.queue";
    public static final String RECHARGE_EXCHANGE    = "recharge.exchange";
    public static final String RECHARGE_ROUTING_KEY = "recharge.routing.key";

    /* ================================================================
     * METHOD: rechargeQueue
     * DESCRIPTION:
     *   Declares a durable RabbitMQ queue for recharge notification events.
     * ================================================================ */
    @Bean
    public Queue rechargeQueue() {
        return new Queue(RECHARGE_QUEUE, true);
    }

    /* ================================================================
     * METHOD: rechargeExchange
     * DESCRIPTION:
     *   Declares a durable topic exchange for routing recharge events.
     * ================================================================ */
    @Bean
    public TopicExchange rechargeExchange() {
        return new TopicExchange(RECHARGE_EXCHANGE, true, false);
    }

    /* ================================================================
     * METHOD: rechargeBinding
     * DESCRIPTION:
     *   Binds the recharge queue to the exchange using the defined routing key.
     * ================================================================ */
    @Bean
    public Binding rechargeBinding() {
        return BindingBuilder
                .bind(rechargeQueue())
                .to(rechargeExchange())
                .with(RECHARGE_ROUTING_KEY);
    }

    /* ================================================================
     * METHOD: jsonMessageConverter
     * DESCRIPTION:
     *   Configures Jackson-based JSON serialization for RabbitMQ messages.
     * ================================================================ */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /* ================================================================
     * METHOD: rabbitTemplate
     * DESCRIPTION:
     *   Creates a RabbitTemplate configured with the JSON message converter
     *   for publishing messages to the exchange.
     * ================================================================ */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
