package com.sym.elasticsearch.demo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.sym.elasticsearch.demo.entity.MyAttachement;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private JestClient jestClient;


    @Test
    public void createIndex(){
        JestResult jr = null;
        try {
            jr = jestClient.execute(new CreateIndex.Builder("jest").build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jr.isSucceeded());
    }


//    @Test
//    public void createIndexByFormattedJsonString(){
//        String settings = "settings\" : {\n" +
//                "        \"number_of_shards\" : 5,\n" +
//                "        \"number_of_replicas\" : 1\n" +
//                "    }\n";
//
//        JestResult jr = null;
//        try {
//            jr = jestClient.execute(new CreateIndex.Builder("articles").settings(Settings.builder().loadFromSource(settings, XContentType.JSON).build()).build());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(jr.isSucceeded());
//
//    }

//    @Test
//    public void createIndexByBuilder(){
//        JestResult jr = null;
//        Settings.Builder settingsBuilder = Settings.builder();
//        settingsBuilder.put("number_of_shards",3);
//        settingsBuilder.put("number_of_replicas",1);
//
//        try {
//            jr = jestClient.execute(new CreateIndex.Builder("articles").settings(settingsBuilder.build()).build());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(jr.isSucceeded());
//
//    }

    @Test
    public void createIndexMapping(){
        JestResult jr = null;

        PutMapping putMapping = new PutMapping.Builder(
                "my_index",
                "my_type",
                "{ \"my_type\" : { \"properties\" : { \"message\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }"
        ).build();

        try {
            jr = jestClient.execute(putMapping);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jr.isSucceeded());

    }

    public void createIndexMappingBy(){
//        RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder("my_mapping_name").add(
//                new AllFieldMapper.Builder().Builder("message").store(true)
//        );
//        DocumentMapper documentMapper = new DocumentMapper.Builder("my_index", null, rootObjectMapperBuilder).build(null);
//        String expectedMappingSource = documentMapper.mappingSource().toString();
//        PutMapping putMapping = new PutMapping.Builder(
//                "my_index",
//                "my_type",
//                expectedMappingSource
//        ).build();
//        client.execute(putMapping);
    }


    /**
     * jest 根据id查询
     */
    @Test
    public void getTest(){
        JestResult jr = null;

        try {
            jr = jestClient.execute(new Get.Builder("attachment1024", "2").build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jr.isSucceeded());

    }


    @Test
    public void searchTest(){
        JestResult jr = null;

        String search = "{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"attachment.content\": \"目前\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"highlight\": {\n" +
                "    \"fields\": {\n" +
                "      \"attachment.content\": {}\n" +
                "    }\n" +
                "  }\n" +
                "}";

        try {
            jr = jestClient.execute(new Search.Builder(search).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jr.isSucceeded());

    }


    @Test
    public void searchTest2(){
        JestResult jr = null;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode1 = mapper.createObjectNode()
                .set("query",
                        mapper.createObjectNode().set("match",
                                mapper.createObjectNode().put("attachment.content","目前")));

        JsonNode jsonNode2 = mapper.createObjectNode()
                .set("highlight",
                        mapper.createObjectNode().set("fields",
                                mapper.createObjectNode().put("attachment.content","")));


        try {
            jr = jestClient.execute(new Search.Builder(jsonNode1.toString()).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jsonNode1.toString());
        System.out.println(jr.isSucceeded());

    }

    @Test
    public void searchTest3(){
        SearchResult jr = null;

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("attachment.content","目前"));

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("attachment.content");//高亮title
        highlightBuilder.preTags("<em>").postTags("</em>");//高亮标签
        highlightBuilder.fragmentSize(500);//高亮内容长度
        searchSourceBuilder.highlighter(highlightBuilder);

        System.out.println(searchSourceBuilder.toString());

        try {
            jr = jestClient.execute(
                    new Search.Builder(searchSourceBuilder.toString())
                            .addIndex("attachment1024")
                            .addType("myattachement")
//                            .addSort()
                            .build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("本次查询共查到："+ jr.getTotal()+"篇文章！");

        JsonObject object = jr.getJsonObject();
        List<MyAttachement> hits = jr.getSourceAsObjectList(MyAttachement.class,true);

        System.out.println(hits.size());

//        for(MyAttachement hit : hits){
//            String source = hit.source.toString();
//            JSON json = JSON.parseObject(source);
//
//            Map<String, List<String>> highlight = hit.highlight;
//
//            List<String> views = highlight.get("content");//高亮后的title
//
//
//        }

        System.out.println("end");

    }



}
