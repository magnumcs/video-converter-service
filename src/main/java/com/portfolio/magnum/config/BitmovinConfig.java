package com.portfolio.magnum.config;

import com.bitmovin.api.BitmovinApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BitmovinConfig {

    @Value("${spring.api.key}")
    private String apiKey;

    @Bean
    public BitmovinApi instanceBitmovin() throws IOException {
        return new BitmovinApi(apiKey);
    }

}
