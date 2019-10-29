package com.sym.elasticsearch.demo.entity;

import lombok.Data;

import java.io.Serializable;


/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: Attachement
 * @Package com.sym.elasticsearch.demo.entity
 * @Description: TODO
 * @date 2019/10/24 16:17
 */

@Data
public class Attachement implements Serializable {

    private String content_type;

    private String language;

    private String content;

    private String content_length;

}
