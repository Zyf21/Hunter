package com.example.hunter.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String VACANCY_IDS_TOPIC = "hh-vacancies-ids";

    @Bean
    public NewTopic createVacancyIdsTopic() {
        // 1 партиция, репликация = 1 (для локального dev — достаточно).
        return new NewTopic(VACANCY_IDS_TOPIC, 1, (short) 1);
    }
}
