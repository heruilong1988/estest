/*
 * Copyright (c) 2020, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2020/12/22
 */

import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class SearchClient {


    RestHighLevelClient restHighLevelClient;

    public DeviceInfo getDeviceInfo(DeviceInfo queryInfo) {

        SearchRequest searchRequest = new SearchRequest("device");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilders.boolQuery();

        searchSourceBuilder.query();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(1);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);


        //searchRequest.routing();


        return null;
    }

}
