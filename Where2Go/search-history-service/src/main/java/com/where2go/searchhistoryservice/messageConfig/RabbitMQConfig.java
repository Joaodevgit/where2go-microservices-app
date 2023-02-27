package com.where2go.searchhistoryservice.messageConfig;

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

    @Value("${rabbitmq.consumer.queue.point-of-interest-search-service.name}")
    private String pointOfInterestDetailsQueue;
    @Value("${rabbitmq.consumer.exchange.point-of-interest-search-service.name}")
    private String pointOfInterestDetailsExchange;
    @Value("${rabbitmq.consumer.routing.point-of-interest-search-service.name}")
    private String pointOfInterestDetailsRoutingKey;

    @Bean
    public Queue pointOfInterestDetailsQueue() {
        return new Queue(pointOfInterestDetailsQueue);
    }

    @Bean
    public TopicExchange pointOfInterestDetailsExchange() {
        return new TopicExchange(pointOfInterestDetailsExchange);
    }

    @Bean
    public Binding pointOfInterestDetailsServiceBinding(Queue pointOfInterestDetailsQueue, TopicExchange pointOfInterestDetailsExchange) {
        return BindingBuilder.bind(pointOfInterestDetailsQueue).to(pointOfInterestDetailsExchange).with(pointOfInterestDetailsRoutingKey);
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
