package com.sym.elasticsearch.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: TransportClientConfig
 * @Package com.sym.elasticsearch.demo.config
 * @Description: TODO
 * @date 2019/10/30 15:39
 */

@Configuration
public class TransportClientConfig {


    @Bean
    public TransportClientFactory transportClient(){
        TransportClientFactory transportClientFactory=new TransportClientFactory();
        transportClientFactory.setClusterName("my-application");
        transportClientFactory.setHost("localhost");
        transportClientFactory.setPort(9300);
        return transportClientFactory;
    }
}
