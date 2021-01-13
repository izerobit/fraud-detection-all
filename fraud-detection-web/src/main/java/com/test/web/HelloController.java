package com.test.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @Autowired
    private KafkaTemplate<String, String> template;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/putTransaction")
    public String putTransaction(@RequestBody Transaction transaction) throws JsonProcessingException {
        template.send(Constants.TRANSACTION, objectMapper.writeValueAsString(transaction));
        return "write transaction to kafka success";
    }

    @PostMapping("/putRule")
    public @ResponseBody Rule putRule(@RequestBody Rule rule) throws JsonProcessingException {
        template.send(Constants.RULE, objectMapper.writeValueAsString(rule));
        return rule;
    }


}
