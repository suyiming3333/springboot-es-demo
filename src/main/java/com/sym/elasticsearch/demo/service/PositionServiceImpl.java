package com.sym.elasticsearch.demo.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.sym.elasticsearch.demo.entity.Commodity;
import com.sym.elasticsearch.demo.entity.Position;
import com.sym.elasticsearch.demo.util.DBHelper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author suyiming3333
 * @version 1.0
 * @className: PositionServiceImpl
 * @description: TODO
 * @date 2022/2/9 15:31
 */
@Slf4j
@Service
public class PositionServiceImpl implements PositionService {

    private  static  final  String  POSITION_INDEX = "position";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public List<Position> searchPos(String keyword, int pageNo, int pageSize) throws IOException {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("positionName",keyword));
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(PageRequest.of(pageNo-1,pageSize));//设置分页
        AggregatedPage<Position> positions = elasticsearchTemplate.queryForPage(
                queryBuilder.build(),
                Position.class);
        List<Position> content = positions.getContent();
//        List<Map<String, Object>> result = new ArrayList<>();
//        content.stream().forEach(v->{
//            Map<String, Object> map = new HashMap<String, Object>();
//            BeanUtil.copyProperties(v, map);
//            result.add(map);
//        });
        return content;
    }

    @Override
    public void importAll() throws IOException {
        writeMySQLDataToES("position");
    }

    private void writeMySQLDataToES(String tableName) throws IOException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List queries = new ArrayList();
            connection = DBHelper.getConn();
            log.info("start handle data :" + tableName);
            String sql = "select * from " + tableName;
            ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            // 根据自己需要设置 fetchSize
            ps.setFetchSize(20);
            rs = ps.executeQuery();
            ResultSetMetaData colData = rs.getMetaData();
            HashMap<String, String> map = null;
            int count = 0;
            // c 就是列的名字   v 就是列对应的值
            String c = null;
            String v = null;
            while (rs.next()) {
                count++;
                map = new HashMap<String, String>(128);
                for (int i = 1; i < colData.getColumnCount(); i++) {
                    c = colData.getColumnName(i);
                    v = rs.getString(c);
                    map.put(c, v);
                }

                IndexQuery indexQuery = new IndexQuery();
                indexQuery.setId(map.get("id"));
                indexQuery.setSource(JSONUtil.toJsonPrettyStr(map));
                indexQuery.setIndexName(POSITION_INDEX);
                indexQuery.setType(POSITION_INDEX);
                queries.add(indexQuery);
                // 每1万条 写一次   不足的批次的数据 最后一次提交处理
                if (count % 10000 == 0) {
                    log.info("mysql handle data  number:" + count);
                    elasticsearchTemplate.bulkIndex(queries);
                    //每提交一次 清空 map 和  dataList
                    map.clear();
                    queries.clear();
                }
            }

            //不足批的索引最后不要忘记提交
            if (queries.size() > 0) {
                elasticsearchTemplate.bulkIndex(queries);
            }
            elasticsearchTemplate.refresh(POSITION_INDEX);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                ps.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
