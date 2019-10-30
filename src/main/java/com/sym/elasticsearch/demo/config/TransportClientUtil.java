package com.sym.elasticsearch.demo.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: TransportClientConfig
 * @Package com.sym.elasticsearch.demo.config
 * @Description: TODO
 * @date 2019/10/30 14:50
 */

public class TransportClientUtil {

    private static volatile Settings settings;

    private static volatile TransportClient client;

    /**懒汉式获取setting单例**/
    private static Settings getSettingsInstance(){

        if(settings == null){

            synchronized (Settings.class){

                if(settings == null){
                    settings = Settings.builder()
                            .put("cluster.name","my-application")
                            .put("client.transport.ignore_cluster_name", true)
                            .build();
                }
            }

        }

        return settings;
    }


    public static TransportClient getConnection(){

        if(client == null){

            synchronized (TransportClient.class){
                if(client == null){
                    try {
                        client = new PreBuiltTransportClient(getSettingsInstance())
                                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9300));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return client;

    }
}
