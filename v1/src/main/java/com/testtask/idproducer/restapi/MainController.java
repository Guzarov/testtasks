package com.testtask.idproducer.restapi;

import com.testtask.idproducer.producer.IdProducer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    private final IdProducer producer;

    public MainController(IdProducer producer) {
        this.producer = producer;
    }

    @GetMapping(value = "/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public IdDto getNextId() {
        return new IdDto(producer.generateId());
    }

}
