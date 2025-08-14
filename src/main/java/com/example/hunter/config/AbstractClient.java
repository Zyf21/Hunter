package com.example.hunter.config;

import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import org.springframework.http.HttpHeaders;
import java.util.function.Consumer;

@Component
@Setter(onMethod_ = @Autowired)
public abstract class AbstractClient {

    /** Этот бин автоконфигурируется Spring Boot 3.4+ */
    @Autowired
    @Setter(AccessLevel.NONE)
    protected RestClient.Builder restClientBuilder;

    /** Если вам нужен load-balanced RestClient, можно задать @LoadBalanced */
    @Autowired
    @Setter(AccessLevel.NONE)
    protected RestClient.Builder loadBalancedBuilder;

    /**
     * Простой RestClient без дополнительных заголовков.
     */
    protected RestClient getRestClient(String url) {
        return restClientBuilder
                .baseUrl(url)
                .build();
    }

    /**
     * RestClient с дефолтными заголовками (например, Basic Auth).
     */
    protected RestClient getRestClient(String url, Consumer<HttpHeaders> headers) {
        return restClientBuilder
                .baseUrl(url)
                .defaultHeaders(headers)
                .build();
    }

    /**
     * Если нужен load-balanced (например, при использовании Spring Cloud LoadBalancer).
     */
//    protected RestClient getLoadBalancedRestClient(String url) {
//        return loadBalancedBuilder
//                .baseUrl(url)
//                .build();
//    }
}