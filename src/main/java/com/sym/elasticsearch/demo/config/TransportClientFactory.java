package com.sym.elasticsearch.demo.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import java.net.InetAddress;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: TransportClientFactory
 * @Package com.sym.elasticsearch.demo
 * @Description: transportclient 工厂类
 * @date 2019/10/30 15:31
 */
public class TransportClientFactory implements FactoryBean<TransportClient>, InitializingBean, DisposableBean {

    private String clusterName;

    private String host;

    private int port;

    private TransportClient client;


    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public TransportClient getClient() {
        return client;
    }

    public void setClient(TransportClient client) {
        this.client = client;
    }



    @Override
    public void destroy() throws Exception {
        if(client!=null){
            client.close();
        }

    }

    @Override
    public TransportClient getObject() throws Exception {
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return TransportClient.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Settings settings=Settings.builder().put("cluster.name",this.clusterName).build();
        client=new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(this.host),this.port));

    }
}
