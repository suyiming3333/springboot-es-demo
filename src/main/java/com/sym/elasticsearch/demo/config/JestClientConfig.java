package com.sym.elasticsearch.demo.config;

import com.google.gson.GsonBuilder;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: JestClientConfig
 * @Package com.sym.elasticsearch.demo.config
 * @Description: TODO
 * @date 2019/10/25 15:00
 */

@Configuration
public class JestClientConfig {

    private String url = "http://10.0.75.1:9200";

    @Bean
    public JestClient jestClient(){

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(url)
                .gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create())
                .multiThreaded(true)
                .readTimeout(10000)
                .build());

        JestClient client = factory.getObject();
        System.out.println("init jest");
        return client;
    }

}
