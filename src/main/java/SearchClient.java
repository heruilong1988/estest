/*
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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

        BoolQueryBuilder boolQueryBuilder = deviceQueryBuilder(queryInfo);

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if(searchHits.length == 0) {
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


}
