package com.example.transactionservice.config;

import com.example.transactionservice.client.AccountClient;
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
    public WebClient accountWebClient() {
        return WebClient.builder().baseUrl("http://account-service").filter(filterFunction).build();
    }

    @Bean
    public AccountClient accountClient() {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(accountWebClient()))
                .build();

        return  httpServiceProxyFactory.createClient(AccountClient.class);
    }
}
