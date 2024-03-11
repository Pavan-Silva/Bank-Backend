package com.example.transactionservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.queues.email}")
    private String emailQueue;

    @Value("${rabbitmq.queues.accounts}")
    private String accountsQueue;

    @Value("${rabbitmq.routing.email-key}")
    private String emailsRoutingKey;

    @Value("${rabbitmq.routing.accounts-key}")
    private String accountsRoutingKey;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueue);
    }

    @Bean
    public Binding emailQueueBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(exchange())
                .with(emailsRoutingKey);
    }

    @Bean
    public Queue accountsQueue() {
        return new Queue(accountsQueue);
    }

    @Bean
    public Binding accountsQueueBinding() {
        return BindingBuilder
                .bind(accountsQueue())
                .to(exchange())
                .with(accountsRoutingKey);
    }
}
