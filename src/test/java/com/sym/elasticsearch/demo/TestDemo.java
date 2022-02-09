package com.sym.elasticsearch.demo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CreateRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suyiming3333
 * @version 1.0
 * @className: TestDemo
 * @description: TODO
 * @date 2022/2/9 14:11
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDemo {
    public static void main2(String[] args) throws IOException {
        // 部署ES的ip地址和端口
        RestClient restClient = RestClient.builder(
                new HttpHost("192.168.16.139", 9200)).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        ElasticsearchClient client = new ElasticsearchClient(transport);

        // 索引
        String index = "user-index";
        // 索引别名
        String aliases = "user-aliases-01";
        /**
         * 创建索引
         * @param index: 索引名称
         * @param aliases: 别名
         * @author : XI.QING
         * @date : 2021/12/28
         */
        client.indices().create(c -> c
                .index(index)
                .aliases(aliases, a -> a
                        .isWriteIndex(true)));
        /**
         * 创建数据文档
         * @param index: 索引名称
         * @author : XI.QING
         * @date : 2021/12/29
         */
        Map<String, String> map = new HashMap<>();
        map.put("username", "张三");
        map.put("address", "江苏省南京市");
        CreateRequest dataStreamResponse = CreateRequest.of(e -> e
                .index(index)
                .id("1")
                .type("_doc")
                .document(map));
        client.create(dataStreamResponse);

        /**
         * 查询索引
         * @param indexList: 查询索引的名称
         * @param clazz: 返回结果的类型
         * @author : XI.QING
         * @date : 2021/12/29
         */
        // Object是一个po实例,如自定义的User、Book、Student等等
        List<Object> resultList = new ArrayList<>();
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(index)
        );
        SearchResponse<Object> response = client.search(searchRequest, Object.class);
        if (response.hits() != null) {
            List<Hit<Object>> list = response.hits().hits();
            for (Hit<Object> hit :
                    list) {
                Object t = (Object) hit.source();
                resultList.add(t);
            }
        }
        /**
         * 删除索引
         * @param index: 索引名称
         * @author : XI.QING
         * @date : 2021/12/29
         */
        // 删除索引(范围大)
        client.delete(c -> c.index(index));
        // 删除索引和ID(范围小)
        client.delete(c -> c.index(index).id("1"));
    }

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Test
    public void createIndex() throws IOException {
        ElasticsearchIndicesClient indices = elasticsearchClient.indices();
        CreateIndexResponse createIndexResponse = indices.create(c -> c.index("corn"));
        System.out.println(createIndexResponse.toString());
    }

    @Test
    public void deleteIndex() throws IOException {
        ElasticsearchIndicesClient indices = elasticsearchClient.indices();
        indices.delete(c -> c.index("corn"));
    }
}
