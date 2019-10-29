package com.sym.elasticsearch.demo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: MyAttachement
 * @Package com.sym.elasticsearch.demo.entity
 * @Description: TODO
 * @date 2019/10/29 17:02
 */
@Data
public class MyAttachement implements Serializable {

    private String id;

    private String filename;

    private Attachement attachement;
}
