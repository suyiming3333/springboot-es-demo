package com.sym.elasticsearch.demo.service;

import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.indices.settings.GetSettings;
import org.elasticsearch.action.ingest.PutPipelineAction;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.ingest.CompoundProcessor;
import org.elasticsearch.ingest.Pipeline;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: ElasticSearchService
 * @Package com.sym.elasticsearch.demo.service
 * @Description: TODO
 * @date 2019/10/30 17:02
 */

@Service
public class ElasticSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    @Autowired
    private JestClient jestClient;

    /**
     * 创建传入的index索引
     * @param index
     */
    public void createIndex(String index){
        try {

            Settings.Builder settingsBuilder = Settings.builder();
            settingsBuilder.put("number_of_shards",5);
            settingsBuilder.put("number_of_replicas",1);
            JestResult jestResult = jestClient.execute(new CreateIndex.Builder(index).settings(settingsBuilder.build().toString()).build());
//            JestResult jestResult = jestClient.execute(new CreateIndex.Builder(index).build());
            logger.info("createIndex:{},result:{}",index,jestResult.isSucceeded());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除index索引
     * @param index
     */
    public void deleteIndex(String index) {
        try {
            JestResult jestResult = jestClient.execute(new DeleteIndex.Builder(index).build());
            logger.info("deleteIndex:{},result:{}",index,jestResult.isSucceeded());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建mapping映射,设置数据类型和分词方式
     * @param index 索引名称
     * @param type 类型
     * @param mappings 构造好的mapping字符串 如：
     *                 {
     *     "mappings" : {
     *         "myattachement" : {
     *             "properties" : {
     *                 "id": {
     *                     "type": "keyword"
     *                 },
     *                 "filename": {
     *                     "type": "text",
     *                 	   "analyzer": "ik_max_word",
     *                 	   "search_analyzer": "ik_max_word"
     *
     *                 },
     *                 "data":{
     *                     "type": "text",
     *                 	   "analyzer": "ik_max_word",
     *                 	   "search_analyzer": "ik_max_word",
     *                 	   "store": "true"
     *                 	}
     *             }
     *         }
     *     }
     * }
     */
    public void createIndexByPutMapping(String index,String type,String mappings){

        PutMapping.Builder builder = new PutMapping.Builder(index, type, mappings);

        try {
            JestResult jestResult = jestClient.execute(builder.build());
            logger.info("createIndexMapping:{},result:{}",mappings,jestResult.isSucceeded());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateIndexMapping(String index,String type,String mappings){

        PutPipelineAction putPipelineAction = new PutPipelineAction();
        CompoundProcessor processor = new CompoundProcessor();

        Pipeline pipeline = new Pipeline("1","my pipeline",1,processor);

        PutMapping.Builder builder = new PutMapping.Builder(index, type, mappings);

        try {
            JestResult jestResult = jestClient.execute(builder.build());
            logger.info("createIndexMapping:{},result:{}",mappings,jestResult.isSucceeded());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取index mapping信息
     * @param indexName
     * @param typeName
     * @return
     */
    public String getMapping(String indexName, String typeName) {
        GetMapping.Builder builder = new GetMapping.Builder();
        builder.addIndex(indexName).addType(typeName);
        try {
            JestResult result = jestClient.execute(builder.build());
            if (result != null && result.isSucceeded()) {
                return result.getSourceAsObject(JsonObject.class).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取index settings 配置
     * @param index
     * @return
     */
    public String getIndexSettings(String index) {
        try {
            JestResult jestResult = jestClient.execute(new GetSettings.Builder().addIndex(index).build());
            System.out.println(jestResult.getJsonString());
            if (jestResult != null) {
                return jestResult.getJsonString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新增或者更新文档
     * @param o
     * @param index
     * @param type
     * @param uniqueId
     * @param <T>
     */
    public <T> void insertOrUpdateDocumentById(T o, String index, String type, String uniqueId) {
        Index.Builder builder = new Index.Builder(o);
        builder.id(uniqueId);
        builder.refresh(true);//允许更新
        Index indexDoc = builder.index(index).type(type).build();
        try {
            jestClient.execute(indexDoc);
        } catch (IOException e) {
            logger.warn("insertOrUpdateDocumentById again!! error={} id={}", e.getMessage(), uniqueId);
//            insertOrUpdateDocumentById(o, index, type, uniqueId);
        }
    }


    /**
     * 根据id获取文档
     * @param object
     * @param index
     * @param id
     * @param <T>
     * @return
     */
    public <T> T getDocumentById(T object, String index, String id) {
        Get get = new Get.Builder(index, id).build();
        T o = null;
        try {
            JestResult result = jestClient.execute(get);
            o = (T) result.getSourceAsObject(object.getClass());
        } catch (IOException e) {
            logger.warn("getDocumentById again!! error={} id=");
            getDocumentById(object, index, id);
        }
        return o;
    }

    /**
     * 根据id删除文档
     * @param index
     * @param type
     * @param id
     */
    public void deleteDocumentById(String index, String type, String id) {
        Delete delete = new Delete.Builder(id).index(index).type(type).build();
        try {
            jestClient.execute(delete);
        } catch (IOException e) {
            logger.warn("deleteDocumentById again!! error={} id={}", e.getMessage(), id);
            deleteDocumentById(index, type, id);
        }
    }


    /**
     * 批量插入数据
     * @param list
     * @param indexName
     * @param type
     * @param <T>
     */
    public <T> void bulkIndex(List<T> list, String indexName,String type) {
        Bulk.Builder bulk = new Bulk.Builder();
        for (T o : list) {
            Index index = new Index.Builder(o).index(indexName).type(type).build();
//            Index index = new Index.Builder(o).id(o.getPK()).index(indexName).type(o.getType()).build();

            bulk.addAction(index);
        }
        try {
            jestClient.execute(bulk.build());
        } catch (IOException e) {
            logger.warn("bulkIndex again!! error={} index={}", e.getMessage(), indexName);
            bulkIndex(list, indexName,type);
        }
    }

    /**
     * 通过searchsourcebuilder 查询
     * @param searchSourceBuilder
     * @param indexName
     * @param typeName
     * @return
     */
    public SearchResult esSearch(SearchSourceBuilder searchSourceBuilder, String indexName, String typeName) {
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(indexName)
                .addType(typeName)
                .build();
        try {
            return jestClient.execute(search);
        } catch (Exception e) {
            logger.warn("index:{}, type:{}, search again!! error = {}", indexName, typeName, e.getMessage());
            return esSearch(searchSourceBuilder, indexName, typeName);
        }
    }


}
