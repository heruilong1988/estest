package org.hrl;/*
 * Copyright (c) 2020, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2020/12/22
 */

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.hrl.data.inject.DeviceUserInfo;
import org.hrl.util.DevUtils;
import org.hrl.util.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchClient {

    static Logger logger = LoggerFactory.getLogger(SearchClient.class);

    private String deviceIndex = "device";
    private RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) throws IOException {
        SearchClient client = new SearchClient();
        DeviceInfo queryInfo = new DeviceInfo();
        queryInfo.setDeviceId("device_id_1");
        queryInfo.setDeviceType("not_exist");
        DeviceInfo deviceInfo = client.getDeviceInfo(queryInfo);
        System.out.println(deviceInfo);
        client.close();
    }

    public void close() {
        try {
            restHighLevelClient.close();
        } catch (IOException e) {
            logger.error("fail to close rest client.", e);
        }
    }

    public SearchClient() {
        this.restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }

    public SearchClient(String[] esServerHost, int esServerPort) {
        HttpHost[] httpHosts = new HttpHost[esServerHost.length];
        for (int i = 0; i < esServerHost.length; i++) {
            httpHosts[i] = new HttpHost(esServerHost[i], esServerPort, "http");
        }
        this.restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHosts));
    }

    /**
     * //searchRequest.routing();
     * 不为空的字段进行查询
     */
    public DeviceInfo getDeviceInfo(DeviceInfo queryInfo) throws IOException {

        SearchRequest searchRequest = new SearchRequest(deviceIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder boolQueryBuilder = deviceQueryBuilder2(queryInfo);

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        logger.debug("search rsp.total hit:{},rsp:{},queryInfo:{}", searchResponse.getHits().getTotalHits(), searchResponse, queryInfo);

        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if (searchHits.length == 0) {
            return null;
        }
        Map<String, Object> sourceAsMap = searchHits[0].getSourceAsMap();
        //System.out.println(sourceAsMap);
        return DevUtils.toDeviceInfo(sourceAsMap);
    }

    /*
    目前只支持deviceId, accountId, alias, deviceType, mac查询
     */
    public BoolQueryBuilder deviceQueryBuilder(DeviceInfo queryInfo) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (queryInfo.deviceId != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.DEVICE_ID, queryInfo.getDeviceId()));
        }
        if (queryInfo.getAccountId() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.ACCOUNT_ID, queryInfo.getAccountId()));
        }
        if (queryInfo.getAlias() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.ALIAS, queryInfo.getAlias()));
        }
        if (queryInfo.getDeviceType() != null) {
            boolQueryBuilder
                    .must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.DEVICE_TYPE, queryInfo.getDeviceType()));
        }
        if (queryInfo.getMac() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.MAC, queryInfo.getMac()));
        }


        return boolQueryBuilder;
    }

    /*
   目前只支持deviceId, accountId, alias, deviceType, mac查询
    */
    public QueryBuilder deviceQueryBuilder2(DeviceInfo queryInfo) {
        BoolQueryBuilder queryBuilder =  QueryBuilders.boolQuery();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (queryInfo.deviceId != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.DEVICE_ID, queryInfo.getDeviceId()));
        }
        if (queryInfo.getAccountId() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.ACCOUNT_ID, queryInfo.getAccountId()));
        }
        if (queryInfo.getAlias() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.ALIAS, queryInfo.getAlias()));
        }
        if (queryInfo.getDeviceType() != null) {
            boolQueryBuilder
                    .must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.DEVICE_TYPE, queryInfo.getDeviceType()));
        }
        if (queryInfo.getMac() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_FIELDS.MAC, queryInfo.getMac()));
        }

        queryBuilder.filter(boolQueryBuilder);

        return queryBuilder;
    }




    /**
     * //searchRequest.routing();
     * 不为空的字段进行查询
     */
    public DeviceUserInfo getDeviceUserInfo(DeviceUserInfo queryInfo) throws IOException {

        SearchRequest searchRequest = new SearchRequest(deviceIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder boolQueryBuilder = deviceUserQueryBuilder(queryInfo);

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        logger.debug("search rsp.total hit:{},rsp:{},queryInfo:{}", searchResponse.getHits().getTotalHits(), searchResponse, queryInfo);

        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if (searchHits.length == 0) {
            return null;
        }
        Map<String, Object> sourceAsMap = searchHits[0].getSourceAsMap();
        //System.out.println(sourceAsMap);
        return DevUtils.toDeviceUserInfo(sourceAsMap);
    }


    /*
  目前只支持deviceId, accountId, alias, deviceType, mac查询
   */
    public QueryBuilder deviceUserQueryBuilder(DeviceUserInfo queryInfo) {
        BoolQueryBuilder queryBuilder =  QueryBuilders.boolQuery();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (queryInfo.getDeviceId() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_USER_FIELDS.DEVICE_ID, queryInfo.getDeviceId()));
        }
        if (queryInfo.getOwnerId() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_USER_FIELDS.OWNER_ID, queryInfo.getOwnerId()));
        }
        if (queryInfo.getAlias() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_USER_FIELDS.ALIAS, queryInfo.getAlias()));
        }
        if (queryInfo.getDeviceType() != null) {
            boolQueryBuilder
                    .must(QueryBuilders.termQuery(DevUtils.DEVICE_USER_FIELDS.DEVICE_TYPE, queryInfo.getDeviceType()));
        }
        if (queryInfo.getUserId() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DevUtils.DEVICE_USER_FIELDS.USER_ID, queryInfo.getUserId()));
        }

        queryBuilder.filter(boolQueryBuilder);

        return queryBuilder;
    }
}
