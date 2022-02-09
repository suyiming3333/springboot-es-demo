package com.sym.elasticsearch.demo;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.net.InetAddress;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: TestDemo
 * @Package com.sym.elasticsearch.demo
 * @Description: TODO
 * @date 2019/10/24 14:34
 */
public class TestDemo {

    public static void main(String[] args) {

        try {

            File file = new File("D:\\alibaba.pdf");
//            File file = new File("D:\\mydoc.doc");


            InputStream is = new FileInputStream(file);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] tmp = new byte[1024];
            int len = 0;
            while ((len = is.read(tmp)) != -1) {
                out.write(tmp, 0, len);
            }


            String data = Base64Utils.encodeToString(out.toByteArray()).replaceAll("\r|\n", "");

            Settings settings = Settings.builder()
                    .put("cluster.name","my-application")
                    .put("client.transport.ignore_cluster_name", true)
                    .build();

            TransportClient transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.0.75.1"),9300));
            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject()
                    .field("id","10")
                    .field("filename","10filename")
                    .field("data",data)
                    .endObject();

            transportClient.prepareIndex("attachment1024","myattachement")
                    .setPipeline("myattachment")
                    .setSource(xContentBuilder)
                    .get();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("end");

    }
}
