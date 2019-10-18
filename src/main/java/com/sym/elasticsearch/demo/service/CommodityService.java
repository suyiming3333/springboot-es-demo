package com.sym.elasticsearch.demo.service;

import com.sym.elasticsearch.demo.entity.Commodity;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author suyiming3333@gmail.com
 * @version V1.0
 * @Title: CommodityService
 * @Package com.sym.elasticsearch.demo.dao
 * @Description: TODO
 * @date 2019/10/18 11:04
 */
public interface CommodityService {

    long count();

    Commodity save(Commodity commodity);

    void delete(Commodity commodity);

    Iterable<Commodity> getAll();

    List<Commodity> getByName(String name);

    Page<Commodity> pageQuery(Integer pageNo, Integer pageSize, String kw);
}
