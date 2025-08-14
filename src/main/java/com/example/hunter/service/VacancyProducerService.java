package com.example.hunter.service;

import com.example.hunter.client.HhApiClient;
import com.example.hunter.config.KafkaTopicConfig;
import com.example.hunter.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Сервис-продьюсер:
 * 1) Стягивает все вакансии (список ID) через HhApiClient.
 * 2) Фильтрует их по базе (existsByVacancyId).
 * 3) Отправляет только новые vacancyId в Kafka (топик hh-vacancies-ids).
 */
@Service
@RequiredArgsConstructor
public class VacancyProducerService {

    private final HhApiClient hhApiClient;
    private final VacancyRepository vacancyRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Параметры поиска (пример)
    private final String keyword = "java";
    private final String searchField = "name";
    private final int area = 1;      // регион (16 = Санк‐Петербург)
//    private final int area = 16;      // регион (16 = Санк‐Петербург)
    private final int perPage = 100;  // до 100 вакансий на страницу


    /**
     * Проход по всем страницам HH.ru (GET /vacancies?…) и отправка новых vacancyId в Kafka.
     */
    public void collectAndSendNewIdsToKafka() {
        int page = 0;
        int totalPages = Integer.MAX_VALUE;

        while (page < totalPages) {
            ResponseEntity<Map<String, Object>> resp = hhApiClient.listVacancies(
                    keyword, searchField, area, perPage, page
            );
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                System.err.println("Producer: не удалось получить список вакансий (page=" + page + ")");
                break;
            }

            Map<String, Object> body = resp.getBody();
            // Определяем общее число страниц на первой итерации
            if (page == 0 && body.containsKey("pages")) {
                totalPages = (Integer) body.get("pages");
            }

            // Извлекаем список вакансий (items)
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            if (items == null || items.isEmpty()) {
                break;
            }

            for (Map<String, Object> item : items) {
                String vacancyId = (String) item.get("id");
                if (vacancyId == null) {
                    continue;
                }

                // Быстрая проверка в базе: есть ли уже vacancyId
                if (vacancyRepository.existsByVacancyId(vacancyId)) {
                    continue; // уже есть в базе — пропускаем
                }

                // Если нет — отправляем vacancyId в Kafka
                kafkaTemplate.send(
                        KafkaTopicConfig.VACANCY_IDS_TOPIC,
                        vacancyId,   // ключ сообщения
                        vacancyId    // значение (сам ID)
                );
                System.out.println("Producer: отправлен новый vacancyId=" + vacancyId + " в Kafka");
            }

            page++;
        }
        System.out.println("Producer: завершена проверка и отправка всех новых ID");
    }
}