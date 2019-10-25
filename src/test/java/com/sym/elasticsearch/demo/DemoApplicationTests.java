package com.sym.elasticsearch.demo;

import com.google.gson.Gson;
import com.sym.elasticsearch.demo.entity.Attachement;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

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



}
