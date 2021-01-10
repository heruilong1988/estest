package org.hrl;/*
 * Copyright (c) 2020, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2020/12/22
 */

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.hrl.data.inject.DeviceUserInfo;
import org.hrl.util.DevUtils;
import org.hrl.util.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IndexClient {

    static Logger logger = LoggerFactory.getLogger(IndexClient.class);

    RestHighLevelClient restHighLevelClient;

    public IndexClient() {
        this.restHighLevelClient = new RestHighLevelClient(
            RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }

    public IndexClient(String[] esServerHost, int esServerPort) {
        HttpHost[] httpHosts = new HttpHost[esServerHost.length];
        for(int i = 0; i < esServerHost.length; i++) {
            httpHosts[i] = new HttpHost(esServerHost[i], esServerPort, "http");
        }
        this.restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHosts));
    }

    public void close() {
        try {
            restHighLevelClient.close();
        } catch (IOException e) {
            logger.error("fail to close  index rest client.", e);
        }
    }

    public IndexClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public void index(DeviceInfo deviceInfo) {
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap = DevUtils.toDeviceMap(deviceInfo);
        IndexRequest indexRequest = new IndexRequest("device").source(jsonMap);
        indexRequest.id(deviceInfo.getDeviceId());
        try {
            IndexResponse indexResponse =  restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            String index = indexResponse.getIndex();
            String id = indexResponse.getId();
            if(indexResponse.getResult() == Result.CREATED){
                //Handle (if needed) the case where the document was created for the first time
                //System.out.println("created");
                logger.debug("created deviceInfo:{}",deviceInfo);
            }else if(indexResponse.getResult() == Result.UPDATED) {
//Handle (if needed) the case where the document was rewritten as it was already existing
                //System.out.println("updated");
                logger.debug("updated deviceInfo:{}",deviceInfo);
            }
        } catch (IOException e) {
            logger.error("ioe.", e);
        } catch (ElasticsearchException e) {
            logger.error("exception.",e);
        } catch (Exception e) {
            logger.error("exception.",e);
        }
    }

    public void indexDeviceUser(DeviceUserInfo deviceUserInfo) {
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap =DevUtils.toDeviceUserMap(deviceUserInfo);
        IndexRequest indexRequest = new IndexRequest("device_user").source(jsonMap);
        try {
            IndexResponse indexResponse =  restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            String index = indexResponse.getIndex();
            String id = indexResponse.getId();
            if(indexResponse.getResult() == Result.CREATED){
                //Handle (if needed) the case where the document was created for the first time
                logger.debug("created deviceUserInfo:{}",deviceUserInfo);
            }else if(indexResponse.getResult() == Result.UPDATED) {
                //Handle (if needed) the case where the document was rewritten as it was already existing
                logger.debug("updated deviceUserInfo:{}",deviceUserInfo);
            }
        } catch (IOException e) {
            logger.error("ioe.", e);
        } catch (ElasticsearchException e) {
            logger.error("exception.",e);
        } catch (Exception e) {
            logger.error("exception.",e);
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
