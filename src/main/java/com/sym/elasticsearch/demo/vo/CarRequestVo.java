package com.sym.elasticsearch.demo.vo;

import com.sym.elasticsearch.demo.entity.CarDocument;
import lombok.*;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: CarRequestVo
 * @Package com.sym.elasticsearch.demo.vo
 * @Description: TODO
 * @date 2020/9/23 15:05
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarRequestVo {

    //删除文档用
    private String id;
    //查询用
    private String keyword;
    private String indexName;
    private String typeName;
    //新增文档用
    private CarDocument body;
}
