package com.where2go.pointsofinterestmanagementservice.messageConfig;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    @Value("${rabbitmq.producer.queue.point-of-interest-search-service.name}")
    private String pointOfInterestSearchQueue;
    @Value("${rabbitmq.producer.exchange.point-of-interest-search-service.name}")
    private String pointOfInterestSearchExchange;
    @Value("${rabbitmq.producer.routing.point-of-interest-search-service.name}")
    private String pointOfInterestSearchRoutingKey;

    @Bean
    public Queue queue(){
        return new Queue(pointOfInterestSearchQueue);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(pointOfInterestSearchExchange);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(pointOfInterestSearchRoutingKey);
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}
