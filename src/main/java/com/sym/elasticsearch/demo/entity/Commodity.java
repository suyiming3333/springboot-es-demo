package com.sym.elasticsearch.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: Commodity
 * @Package com.sym.elasticsearch.demo.entity
 * @Description: TODO
 * @date 2019/10/18 11:03
 */
@Data
@Document(indexName = "commodity")
public class Commodity implements Serializable {

    @Id
    private String skuId;

    private String name;

    private String category;

    private Integer price;

    private String brand;

    private Integer stock;

}