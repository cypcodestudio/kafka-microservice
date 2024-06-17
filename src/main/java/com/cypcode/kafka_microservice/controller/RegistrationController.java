package com.cypcode.kafka_microservice.controller;

import com.cypcode.kafka_microservice.entity.UserRegistrationDTO;
import com.cypcode.kafka_microservice.service.CypcodeProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/register")
public class RegistrationController {

    @Autowired
    CypcodeProducer producer;

    @PostMapping
    public ResponseEntity<?> publish(@RequestBody UserRegistrationDTO payload){
        producer.register(payload);
        return ResponseEntity.ok("User registration completed");
    }
}
