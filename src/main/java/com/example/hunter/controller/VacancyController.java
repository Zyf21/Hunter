package com.example.hunter.controller;

import com.example.hunter.service.VacancyProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VacancyController {

    private final VacancyProducerService vacancyProducerService;

    public VacancyController(VacancyProducerService vacancyProducerService) {
        this.vacancyProducerService = vacancyProducerService;
    }

    @GetMapping("/collect")
    public ResponseEntity<String> collectVacancies() {
        vacancyProducerService.collectAndSendNewIdsToKafka();
        return ResponseEntity.ok("Запущен процесс сбора вакансий и отправки новых ID в Kafka");
    }
}
