package com.sym.elasticsearch.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Parent;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: Attachement
 * @Package com.sym.elasticsearch.demo.entity
 * @Description: TODO
 * @date 2019/10/24 16:17
 */

@Data
@Document(indexName = "attachment1024",type = "myattachement",shards = 5, replicas = 1)
public class Attachement {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String filename;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String data;

}
