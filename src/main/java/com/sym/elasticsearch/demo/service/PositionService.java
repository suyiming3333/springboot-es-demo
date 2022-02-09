package com.sym.elasticsearch.demo.service;

import com.sym.elasticsearch.demo.entity.Position;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PositionService {
    /* 分页查询 */
    public List<Position> searchPos(String keyword, int pageNo, int pageSize) throws IOException;

    /**
     * 导入数据
     */
    void importAll() throws IOException;
}
