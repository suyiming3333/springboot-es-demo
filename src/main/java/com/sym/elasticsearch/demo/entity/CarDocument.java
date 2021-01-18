package com.sym.elasticsearch.demo.entity;

import io.searchbox.annotations.JestId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: CarDocument
 * @Package com.sym.elasticsearch.demo.entity
 * @Description: TODO
 * @date 2020/9/23 14:58
 */


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarDocument {

    @JestId
    private String id;
    private Long price;
    private String color;
    private String make;
    private String sold;
    private String level;
}
