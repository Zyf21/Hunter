package com.example.hunter.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;

/**
 * Декларативный HTTP-клиент для HH.ru API.
 * Использует Spring 6 @HttpExchange / @GetExchange.
 */
@HttpExchange(
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface HhApiClient {

    /**
     * GET /vacancies
     * @param text        параметр text (например, "java")
     * @param searchField параметр search_field (например, "name")
     * @param area        параметр area (например, 16)
     * @param perPage     параметр per_page (максимум 100)
     * @param page        параметр page (от 0 до pages-1)
     * @return ResponseEntity<Map<String,Object>>
     *         ожидание: ключи "items": List<Map<String,Object>>, "pages": Integer, "found": Integer
     */
//    @GetExchange("/vacancies?text={text}&search_field={searchField}&area={area}&per_page={perPage}&page={page}")
//    ResponseEntity<Map<String, Object>> listVacancies(
//            @RequestParam("text") String text,
//            @RequestParam("search_field") String searchField,
//            @RequestParam("area") int area,
//            @RequestParam("per_page") int perPage,
//            @RequestParam("page") int page
//    );


    @GetExchange("/vacancies")
    ResponseEntity<Map<String, Object>> listVacancies(
            @RequestParam("text") String text,
            @RequestParam("search_field") String searchField,
            @RequestParam("area") int area,
            @RequestParam("per_page") int perPage,
            @RequestParam("page") int page
    );
    /**
     * GET /vacancies/{id}
     * @param id внешний ID вакансии (строка)
     * @return ResponseEntity<Map<String,Object>> с деталями вакансии
     */
    @GetExchange("/vacancies/{id}")
    ResponseEntity<Map<String, Object>> getVacancyById(@PathVariable("id") String id);
}