package com.example.hunter.service;

import com.example.hunter.client.HhApiClient;
import com.example.hunter.model.Vacancy;
import com.example.hunter.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VacancyConsumerService {

    private final HhApiClient hhApiClient;
    private final VacancyRepository vacancyRepository;


    @KafkaListener(topics = "hh-vacancies-ids", groupId = "hh-consumer-group")
    public void consumeVacancyId(String vacancyId) {
        System.out.println("Consumer: получил vacancyId=" + vacancyId + " из Kafka");

        // Запрос деталей вакансии
        ResponseEntity<Map<String, Object>> resp = hhApiClient.getVacancyById(vacancyId);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            System.err.println("Consumer: не удалось получить детали вакансии ID=" + vacancyId);
            return;
        }

        Map<String, Object> detail = resp.getBody();
        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyId(vacancyId);

        // name
        String name = (String) detail.get("name");
        vacancy.setName(name != null ? name : "N/A");

        // employer.name
        @SuppressWarnings("unchecked")
        Map<String, Object> employer = (Map<String, Object>) detail.get("employer");
        if (employer != null && employer.containsKey("name")) {
            vacancy.setEmployer((String) employer.get("name"));
        }

        // type.name
        @SuppressWarnings("unchecked")
        Map<String, Object> type = (Map<String, Object>) detail.get("type");
        if (type != null && type.containsKey("name")) {
            vacancy.setWorkType((String) type.get("name"));
        }

        // schedule.name
        @SuppressWarnings("unchecked")
        Map<String, Object> schedule = (Map<String, Object>) detail.get("schedule");
        if (schedule != null && schedule.containsKey("name")) {
            vacancy.setSchedule((String) schedule.get("name"));
        }

        // description (HTML-текст)
        String description = (String) detail.get("description");
        vacancy.setDescription(description != null ? description : "");

        // Сохраняем. Если UNIQUE (vacancy_id) нарушен, ловим исключение и игнорируем.
        try {
            vacancyRepository.save(vacancy);
            System.out.println("Consumer: успешно сохранил вакансию ID=" + vacancyId);
        } catch (DataIntegrityViolationException ex) {
            System.err.println("Consumer: вакансия ID=" + vacancyId + " уже существует, пропускаем");
        }
    }
}
