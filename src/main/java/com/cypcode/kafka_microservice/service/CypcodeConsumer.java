package com.cypcode.kafka_microservice.service;

import com.cypcode.kafka_microservice.entity.NotificationDTO;
import com.cypcode.kafka_microservice.entity.NotificationStatusDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CypcodeConsumer {

    @Value("${cypcode.kafka.notification.status.topic}")
    private String notificationStatusTopicName;

    @Autowired
    CypcodeProducer producer;

    @KafkaListener(topics = "${cypcode.kafka.notification.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumer(String data){

        System.out.println("Notification consumed");
        ObjectMapper mapper = new ObjectMapper();
        try {
            NotificationDTO dto = mapper.readValue(data, NotificationDTO.class);
            if(dto != null){
                System.out.println(String.format("Received Notification: %s", dto.getRecipient()));
                NotificationStatusDTO notificationStatus = new NotificationStatusDTO();
                notificationStatus.setStatus(dto.getRecipient());
                notificationStatus.setNotificationId(String.format("%s_%s_%s", dto.getChannel(), dto.getRecipient(), new Date()));

                if(dto.getChannel().equals("SMS")){
                    sendSMS();
                    notificationStatus.setStatus("Success");
                } else if (dto.getChannel().equals("EMAIL")) {
                    sendEMail();
                    notificationStatus.setStatus("Success");
                }else{
                    throw new UnsupportedOperationException("Notification channel not added yet");
                }
                //if you want to publish the notification status into a dedicated topic to be consumed by a microservice
                producer.publish(notificationStatusTopicName, notificationStatus);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sendSMS(){
//        todo send sms
        System.out.println("SEND SMS");
    }

    private void sendEMail(){
//        todo send email
        System.out.println("SEND EMAIL");
    }
}
