package com.example.hunter;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class HhVacancyService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://api.hh.ru/vacancies";
    private final String keyword = "java";
    // ID региона/города (area) в hh.ru; например, 1 — Москва, 2 — Санкт-Петербург
//    private final int area = 16;
    private final int area = 1;
    // Максимум вакансий на страницу (до 100)
    private final int perPage = 100;

    @PostConstruct
    public void init() {
        System.out.println("=== Поиск вакансий по ключевому слову: \""
                + keyword + "\" (search_field=name) и региону (area=" + area + ") ===");

        int page = 0;
        int totalPages = Integer.MAX_VALUE;
        int globalCount = 1;

        int remoteCount = 0;
        int nonRemoteCount = 0;

        while (page < totalPages) {
            // Строим URL с параметрами:
            // text = keyword
            // search_field = name      (поиск только в заголовке вакансии)
            // area = area
            // per_page = perPage
            // page = page
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("text", keyword)
                    .queryParam("search_field", "name")
                    .queryParam("area", area)
                    .queryParam("per_page", perPage)
                    .queryParam("page", page)
                    .build()
                    .toUriString();

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || !response.containsKey("items")) {
                System.err.println("Ошибка получения данных с HH.ru (страница " + page + ")");
                break;
            }

            // На первой странице (page == 0) забираем общее число страниц
            if (page == 0 && response.containsKey("pages")) {
                totalPages = (Integer) response.get("pages");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            if (items.isEmpty()) {
                // Если вакансий нет — выходим
                break;
            }

            // Обрабатываем каждую вакансию на странице
            for (Map<String, Object> item : items) {
                String vacancyName = (String) item.get("name");

                // Название компании
                String employerName = "";
                @SuppressWarnings("unchecked")
                Map<String, Object> employer = (Map<String, Object>) item.get("employer");
                if (employer != null && employer.containsKey("name")) {
                    employerName = (String) employer.get("name");
                }

                // Тип работы (type.name)
                String workType = "";
                @SuppressWarnings("unchecked")
                Map<String, Object> type = (Map<String, Object>) item.get("type");
                if (type != null && type.containsKey("name")) {
                    workType = (String) type.get("name");
                }

                // График работы (schedule.id и schedule.name), например: remote ("Удалённая работа"), fullDay и т.д.
                String scheduleName = "";
                String scheduleId = "";
                @SuppressWarnings("unchecked")
                Map<String, Object> schedule = (Map<String, Object>) item.get("schedule");
                if (schedule != null) {
                    if (schedule.containsKey("name")) {
                        scheduleName = (String) schedule.get("name");
                    }
                    if (schedule.containsKey("id")) {
                        scheduleId = (String) schedule.get("id");
                    }
                }

                // Увеличиваем соответствующий счётчик: если scheduleId == "remote", считаем как удалённая работа
                if ("remote".equalsIgnoreCase(scheduleId)) {
                    remoteCount++;
                } else {
                    nonRemoteCount++;
                }

                System.out.println(globalCount++
                        + ". " + vacancyName
                        + " — " + employerName
                        + " — " + workType
                        + " — " + scheduleName);
            }

            page++;
        }

        System.out.println("=== Поиск завершён. Всего вакансий найдено: " + (globalCount - 1) + " ===");
        System.out.println("Удалённая работа: " + remoteCount);
        System.out.println("Не удалённая работа: " + nonRemoteCount);
    }
}
