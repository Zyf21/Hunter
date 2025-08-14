package com.example.hunter.config;

import com.example.hunter.client.HhApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class HhClientConfig extends AbstractClient {

    /**
     * Базовый URL подтягивается из application.yml (hh.api.base-url).
     */
    @Value("${hh.api.base-url}")
    private String baseUrl;

    @Bean
    public HhApiClient hhApiClient() {
        // Создаём RestClient с базовым URL из настроек
        RestClient restClient = getRestClient(baseUrl);

        // Оборачиваем RestClient в адаптер для прокси-фабрики
        var adapter = RestClientAdapter.create(restClient);

        // Создаём фабрику прокси
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        // Возвращаем прокси-клиент HhApiClient
        return factory.createClient(HhApiClient.class);
    }
}