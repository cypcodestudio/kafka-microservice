package com.cypcode.kafka_microservice.service;

import com.cypcode.kafka_microservice.entity.NotificationDTO;
import com.cypcode.kafka_microservice.entity.SubscriptionDTO;
import com.cypcode.kafka_microservice.entity.UserRegistrationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CypcodeProducer {

    @Value("${cypcode.kafka.topic}")
    private String registrationTopicName;
    @Value("${cypcode.kafka.notification.topic}")
    private String notificationTopicName;
    @Value("${cypcode.kafka.subscription.topic}")
    private String subscriptionTopicName;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void register(UserRegistrationDTO payload){
        publish(registrationTopicName, payload);
        System.out.println(String.format("Registration data published: %s", payload.getEmail()));

        NotificationDTO notification = new NotificationDTO();
        notification.setChannel("SMS");
        notification.setContent(String.format("Hi, %s, new registration to cypcode", payload.getFirstname()));
        notification.setRecipient(payload.getMobile());
        notification.setTitle("Registration");
        publish(notificationTopicName, notification);

        if(payload.isSubscribe()){
            SubscriptionDTO subscription = new SubscriptionDTO();
            subscription.setSubscriber(payload.getEmail());
            subscription.setType("Free");
            publish(subscriptionTopicName, subscription);
        }

    }

    public void publish(String topicName, Object payload){
        ObjectMapper mapper = new ObjectMapper();
       try {
            String data = mapper.writeValueAsString(payload);
            kafkaTemplate.send(topicName, data);
       }catch (JsonProcessingException e){
           e.printStackTrace();
       }

    }
}
