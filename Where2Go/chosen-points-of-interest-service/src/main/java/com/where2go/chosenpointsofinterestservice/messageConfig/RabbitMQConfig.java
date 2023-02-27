package com.where2go.chosenpointsofinterestservice.messageConfig;

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

    @Value("${rabbitmq.consumer.queue.chosen-points-of-interest-search-service.name}")
    private String chosenPointsQueue;
    @Value("${rabbitmq.consumer.exchange.chosen-points-of-interest-search-service.name}")
    private String chosenPointsExchange;
    @Value("${rabbitmq.consumer.routing.chosen-points-of-interest-search-service.name}")
    private String chosenPointsRoutingKey;

    @Bean
    public Queue chosenPointsQueue() {
        return new Queue(chosenPointsQueue);
    }

    @Bean
    public TopicExchange chosenPointsExchange() {
        return new TopicExchange(chosenPointsExchange);
    }

    @Bean
    public Binding chosenPointsBinding(Queue chosenPointsQueue, TopicExchange chosenPointsExchange) {
        return BindingBuilder.bind(chosenPointsQueue).to(chosenPointsExchange).with(chosenPointsRoutingKey);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}
