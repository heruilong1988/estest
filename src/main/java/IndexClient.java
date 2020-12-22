/*
 * Copyright (c) 2020, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2020/12/22
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class IndexClient {

    RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
        RestClient.builder(new HttpHost("localhost", 9200, "http")));

    public void index(DeviceInfo deviceInfo) {
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("device_id", deviceInfo.deviceId);
        jsonMap.put("postDate", "2013-01-30");
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest req2 = new IndexRequest("post").source(jsonMap);
    }

    public static void main(String[] args) throws IOException {
        IndexClient indexClient = new IndexClient();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.deviceId = "device_id_2";
        indexClient.index(deviceInfo);

        indexClient.restHighLevelClient.close();
    }

}
