/*
 * Copyright (c) 2020, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2020/12/22
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
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
        IndexRequest indexRequest = new IndexRequest("device").source(jsonMap);
        try {
            IndexResponse indexResponse =  restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            String index = indexResponse.getIndex();
            String id = indexResponse.getId();
            if(indexResponse.getResult() == Result.CREATED){
                //Handle (if needed) the case where the document was created for the first time
                System.out.println("created");
            }else if(indexResponse.getResult() == Result.UPDATED) {
//Handle (if needed) the case where the document was rewritten as it was already existing
                System.out.println("updated");
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ElasticsearchException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        IndexClient indexClient = new IndexClient();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.deviceId = "device_id_2";
        indexClient.index(deviceInfo);

        indexClient.restHighLevelClient.close();
    }

}
