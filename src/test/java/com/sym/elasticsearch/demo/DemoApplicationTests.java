package com.sym.elasticsearch.demo;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.sym.elasticsearch.demo.entity.Attachement;
import com.sym.elasticsearch.demo.entity.Commodity;
import com.sym.elasticsearch.demo.service.CommodityService;
import com.sym.elasticsearch.demo.service.PositionService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private CommodityService commodityService;

    @Autowired
    private TransportClient transportClient;

    @Test
    void contextLoads() {
        System.out.println(commodityService.count());

    }

    @Test
    public void testInsert() {
        Commodity commodity = new Commodity();
        commodity.setSkuId("1501009001");
        commodity.setName("原味切片面包（10片装）");
        commodity.setCategory("101");
        commodity.setPrice(880);
        commodity.setBrand("良品铺子");
        commodityService.save(commodity);

        commodity = new Commodity();
        commodity.setSkuId("1501009002");
        commodity.setName("原味切片面包（6片装）");
        commodity.setCategory("101");
        commodity.setPrice(680);
        commodity.setBrand("良品铺子");
        commodityService.save(commodity);

        commodity = new Commodity();
        commodity.setSkuId("1501009004");
        commodity.setName("元气吐司850g");
        commodity.setCategory("101");
        commodity.setPrice(120);
        commodity.setBrand("百草味");
        commodityService.save(commodity);

    }

    @Test
    public void testGetAll() {
        Iterable<Commodity> iterable = commodityService.getAll();
        iterable.forEach(e->System.out.println(e.toString()));
    }

    @Test
    public void testGetByName() {
        List<Commodity> list = commodityService.getByName("面包");
        System.out.println(list);
    }

    @Test
    public void testPage() {
        Page<Commodity> page = commodityService.pageQuery(0, 10, "切片");
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getContent());
    }


    @Autowired
    public ElasticsearchTemplate elasticsearchTemplate;


    @Test
    public void testInsertByTemplate() {
        Commodity commodity = new Commodity();
        commodity.setSkuId("1501009009");
        commodity.setName("葡萄吐司面包（11片装）");
        commodity.setCategory("101");
        commodity.setPrice(190);
        commodity.setBrand("名创优品");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(commodity).build();
        elasticsearchTemplate.index(indexQuery);
    }

    @Test
    public void testQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", "吐司"))
                .build();
        List<Commodity> list = elasticsearchTemplate.queryForList(searchQuery, Commodity.class);
        System.out.println(list);
    }

    @Test
    public void testQueryByPage() {
        String keyWord = "面包";
        //构造多条件查询
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("name",keyWord).operator(Operator.AND));
//                .operator(Operator.OR))//分隔符连接
//                .must(QueryBuilders.matchQuery("brand","良品"));
        //品牌过滤
        //boolQueryBuilder.filter(QueryBuilders.termQuery("brand.keyword","名创优品"));

        //价格过滤
        //boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(200L).lte(900L));


        HighlightBuilder.Field field = new HighlightBuilder.Field("name");
        field.preTags("<span style='color:red'>");
        field.postTags("</span>");
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightFields(field)
                .withSort(new FieldSortBuilder("price").order(SortOrder.ASC))//设置排序
                .withPageable(PageRequest.of(0,5));//设置分页

        //聚合操作，添加品牌信息返回
        //text字段作为一个整体，默认没有索引,不过text分词之后的keyword是有索引的，因而可以对interests.keyword进行聚合。
        queryBuilder.addAggregation(AggregationBuilders.terms("brandName").field("brand.keyword"));
        AggregatedPage<Commodity> commodities = elasticsearchTemplate.queryForPage(
                queryBuilder.build(),
                Commodity.class,
                new SearchResultMapper() {
                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                        List<T> list = new ArrayList<>();
                        SearchHits hits = searchResponse.getHits();
                        if(null != hits){
                            for(SearchHit hit : hits){
                                Commodity commodity = JSONUtil.toBean(hit.getSourceAsString(), Commodity.class);
                                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                                if (null != highlightFields && highlightFields.size() > 0) {
                                    commodity.setName(highlightFields.get("name").getFragments()[0].toString());
                                    list.add((T)commodity);
                                }
                            }

                        }
                        return new AggregatedPageImpl<T>(list, pageable, hits.getTotalHits(), searchResponse.getAggregations());
                    }

                    @Override
                    public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                        return null;
                    }
                });
        List<Commodity> list = commodities.getContent();
        int totalPages = commodities.getTotalPages();
        long totalElements = commodities.getTotalElements();
        //获取聚合之后的数据
        StringTerms brandName = (StringTerms) commodities.getAggregation("brandName");
        List<String> collect = brandName.getBuckets().stream().map(c -> c.getKeyAsString()).collect(Collectors.toList());
        System.out.println(list);
    }

    @Autowired
    private PositionService positionService;

    @Test
    public void loadMysqlDate() throws IOException {
        positionService.importAll();
        System.out.println("ednd");
    }

    @Test
    public void test(){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("content", "目前"))
                .build();
//        long cnt = elasticsearchTemplate.count(searchQuery);
        List<Attachement> list = elasticsearchTemplate.queryForList(searchQuery, Attachement.class);

        System.out.println(1);


    }



}
