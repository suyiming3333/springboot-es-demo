package com.sym.elasticsearch.demo;

import com.sym.elasticsearch.demo.entity.CarDocument;
import com.sym.elasticsearch.demo.service.ElasticSearchService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.type.TypeExist;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: CarDocumentTest
 * @Package com.sym.elasticsearch.demo
 * @Description: TODO
 * @date 2020/9/23 15:07
 */

@SpringBootTest
public class CarDocumentTest {

    @Autowired
    private JestClient jestClient;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Test
    public void testDeleteIndex() throws IOException {
        DeleteIndex deleteIndex = new DeleteIndex.Builder("test_cars").build();
        JestResult result = jestClient.execute(deleteIndex);
        System.out.println(result.getJsonString());
    }

    @Test
    public void testCreateIndex() throws IOException {
        String indexName = "liverecordidx3";
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put("number_of_shards",5);
        settingsBuilder.put("number_of_replicas",1);

        //判断索引是否存在
        TypeExist indexExist = new TypeExist.Builder(indexName).build();
        JestResult result = jestClient.execute(indexExist);
//        CreateIndex createIndex = new CreateIndex.Builder(indexName).build();
//        JestResult result = jestClient.execute(createIndex);
        System.out.println(result.getJsonString());
    }


    /**
     * 利用辅助工具生成自定义需要的mappings 字符串
     *
     *      *     "mappings" : {
     *      *         "myattachement" : {
     *      *             "properties" : {
     *      *                 "id": {
     *      *                     "type": "keyword"
     *      *                 },
     *      *                 "filename": {
     *      *                     "type": "text",
     *      *                 	   "analyzer": "ik_max_word",
     *      *                 	   "search_analyzer": "ik_max_word"
     *      *
     *      *                 },
     *      *                 "data":{
     *      *                     "type": "text",
     *      *                 	   "analyzer": "ik_max_word",
     *      *                 	   "search_analyzer": "ik_max_word",
     *      *                 	   "store": "true"
     *      *                 	}
     *      *             }
     *      *         }
     *      *     }
     *      * }
     *
     * @throws IOException
     */
    @Test
    public void testCreateIndexByPutMappings() throws IOException {
//        XContentBuilder builder = XContentFactory.jsonBuilder()
//                .startObject()
//                        .field("abc")
//                        .startObject()
//                            .field("properties")
//                            .startObject()
//                                .field("id").startObject().field("type","keyword").endObject()
//                                .field("filename").startObject().field("type","text").field("analyzer","ik_max_word").field("search_analyzer","ik_max_word").endObject()
//                                .field("data").startObject().field("type","text").field("analyzer","ik_max_word").field("search_analyzer","ik_max_word").field("store",true).endObject()
//                            .endObject()
//                        .endObject()
//                .endObject();


        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("liverecord")
                .startObject()
                .field("properties")
                .startObject()
                .field("id")
                .startObject().field("type","keyword").endObject()
                .field("realName")
                .startObject().field("type","text").endObject()
                .field("userName")
                .startObject().field("type","text").endObject()
                .field("orgPath")
                .startObject().field("type","text").endObject()
                .field("sips")
                .startObject().field("type","text").endObject()
                .field("type")
                .startObject().field("type","text").endObject()
                .field("dateTime")
                .startObject().field("type","date").field("format","yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis").endObject()
                .endObject()
                .endObject()
                .endObject();
        String mapping = Strings.toString(builder);

        //判断mapping是否存在
        TypeExist typeExist = new TypeExist.Builder("liverecordidx3").addType("liverecord").build();
        JestResult result = jestClient.execute(typeExist);
        System.out.println(mapping);
        elasticSearchService.createIndexByPutMapping("liverecordidx3","liverecord",mapping);
    }

    /**
     * 插入或更新
     * @throws IOException
     */
    @Test
    public void testAddorUpdateDoc() throws IOException {
        CarDocument carDocument = new CarDocument();
        carDocument.setId("hfE2unQBOGe7WWis5VKS");
        carDocument.setColor("black");
        carDocument.setMake("bmw");
        carDocument.setPrice(50000l);
        carDocument.setSold("2020-09-23");
        carDocument.setLevel("0");

        Index.Builder builder = new Index.Builder(carDocument);
        Index index = builder.index("test_cars1").type("transactions").build();
        JestResult result = jestClient.execute(index);
        System.out.println(result.getJsonString());
    }

    @Test
    public void testSearch() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(new MultiMatchQueryBuilder("bmw", "make")).from(1).size(1);
        System.out.println(searchSourceBuilder.toString());
        SearchResult result = jestClient.execute(new Search.Builder(searchSourceBuilder.toString())
                .addIndex("cars")
                .addType("transactions")
                .build());
        List<CarDocument> resyltList = result.getSourceAsObjectList(CarDocument.class,true);
        resyltList.stream().forEach(System.out::println);
        System.out.println( result.getJsonString());
    }

    @Test
    public void testAggre() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders.terms("colors").field("color");
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        System.out.println(searchSourceBuilder.toString());
        SearchResult result = jestClient.execute(new Search.Builder(searchSourceBuilder.toString())
                .addIndex("cars")
                .addType("transactions")
                .build());
        System.out.println( result.getJsonString());

    }

    @Test
    public void testAggreWithQuery() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders.terms("colors").field("color");
        /**先查询，后聚合**/
        searchSourceBuilder.query(new MultiMatchQueryBuilder("bmw", "make"));
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        System.out.println(searchSourceBuilder.toString());
        SearchResult result = jestClient.execute(new Search.Builder(searchSourceBuilder.toString())
                .addIndex("cars")
                .addType("transactions")
                .build());
        /**获取aggregation对象**/
        TermsAggregation termsAggregation = result.getAggregations().getAggregation("colors", TermsAggregation.class);
        System.out.println(result.getJsonString());

    }
}
