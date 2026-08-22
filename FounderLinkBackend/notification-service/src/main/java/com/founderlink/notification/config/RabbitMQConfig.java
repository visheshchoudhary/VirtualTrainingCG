package com.founderlink.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange name
    public static final String EXCHANGE = "founderlink.exchange";

    // Queue names
    public static final String STARTUP_QUEUE = "startup.created.queue";
    public static final String INVESTMENT_QUEUE = "investment.created.queue";
    public static final String TEAM_QUEUE = "team.invite.queue";
    public static final String PAYMENT_QUEUE = "payment.success.queue";

    // Routing keys
    public static final String STARTUP_ROUTING_KEY = "startup.created";
    public static final String INVESTMENT_ROUTING_KEY = "investment.created";
    public static final String TEAM_ROUTING_KEY = "team.invite.sent";
    public static final String PAYMENT_ROUTING_KEY = "payment.success";

    // Exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // Queues
    @Bean
    public Queue startupQueue() {
        return new Queue(STARTUP_QUEUE, true);
    }

    @Bean
    public Queue investmentQueue() {
        return new Queue(INVESTMENT_QUEUE, true);
    }

    @Bean
    public Queue teamQueue() {
        return new Queue(TEAM_QUEUE, true);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    // Bindings
    @Bean
    public Binding startupBinding() {
        return BindingBuilder
                .bind(startupQueue())
                .to(exchange())
                .with(STARTUP_ROUTING_KEY);
    }

    @Bean
    public Binding investmentBinding() {
        return BindingBuilder
                .bind(investmentQueue())
                .to(exchange())
                .with(INVESTMENT_ROUTING_KEY);
    }

    @Bean
    public Binding teamBinding() {
        return BindingBuilder
                .bind(teamQueue())
                .to(exchange())
                .with(TEAM_ROUTING_KEY);
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder
                .bind(paymentQueue())
                .to(exchange())
                .with(PAYMENT_ROUTING_KEY);
    }

    // Message converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitMQ Template
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}