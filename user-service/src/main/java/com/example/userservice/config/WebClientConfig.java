package com.example.userservice.config;

import com.example.userservice.client.AccHolderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final LoadBalancedExchangeFilterFunction filterFunction;

    @Bean
    public WebClient accountHolderWebClient() {
        return WebClient.builder()
                .baseUrl("http://account-service").
                filter(filterFunction)
                .build();
    }

    @Bean
    public AccHolderClient accountHolderClient() {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(accountHolderWebClient()))
                .build();

        return  httpServiceProxyFactory.createClient(AccHolderClient.class);
    }
}
