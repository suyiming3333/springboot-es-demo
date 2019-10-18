package com.sym.elasticsearch.demo.dao;

import com.sym.elasticsearch.demo.entity.Commodity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: sa
 * @Package com.sym.elasticsearch.demo.dao
 * @Description: TODO
 * @date 2019/10/18 11:07
 */
@Repository
public interface CommodityRepository extends ElasticsearchRepository<Commodity, String> {

}
