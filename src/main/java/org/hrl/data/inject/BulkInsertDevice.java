package org.hrl.data.inject;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BulkInsertDevice {

    static Logger logger = LoggerFactory.getLogger(BulkInsertDevice.class);

    private RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) throws IOException {
        BulkInsertDevice bulkInsertDevice = new BulkInsertDevice();
        String[] esServerHosts = {"localhost"};
        bulkInsertDevice.initRestClient(esServerHosts, 9200);
        bulkInsertDevice.bulkInsertFromFiles("bulkInsertFiles");
        bulkInsertDevice.restHighLevelClient.close();

    }

    public void initRestClient(String[] esServerHost, int esServerPort) {
        HttpHost[] httpHosts = new HttpHost[esServerHost.length];
        for(int i = 0; i < esServerHost.length; i++) {
            httpHosts[i] = new HttpHost(esServerHost[i], esServerPort, "http");
        }
        this.restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHosts));
    }

    //批量写入， 从文件加载
    public void bulkInsertFromFiles(String fileDirPath) {
        File file = new File(fileDirPath);
        //获取path下子目录
        Iterable<File> childrens = Files.fileTreeTraverser().children(file);
        for (File children : childrens) {
            if(children.isFile()) {
                logger.info("begin.insert file.{}",children.getName());
                bulkInsertFromFile(children);
            }
        }

        logger.info("finished");

    }

    public void bulkInsertFromFile(File file) {
        BulkRequest request = new BulkRequest();
        try {
            List<String> readLines = Files.readLines(file, Charsets.UTF_8);
            for(String devJson : readLines) {
                IndexRequest indexRequest = createIndexFromJsonStr(devJson);
                request.add(indexRequest);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            checkBulkResponse(bulkResponse);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("failed to bulkInsert.",e);
        }

    }

    public void checkBulkResponse(BulkResponse bulkResponse) {
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
            if (bulkItemResponse.isFailed()) {
                BulkItemResponse.Failure failure =
                        bulkItemResponse.getFailure();
                logger.error("insert error.{}",failure);
            }
        }
    }

    public void bulkInsert(String bulkInsertData) {

        BulkRequest request = new BulkRequest();

        request.add(new IndexRequest("posts").id("1")
                .source(XContentType.JSON,"field", "foo"));
        request.add(new IndexRequest("posts").id("2")
                .source(XContentType.JSON,"field", "bar"));
    }

    public IndexRequest createIndexFromJsonStr(String jsonStr) {
        IndexRequest request = new IndexRequest("device");
        request.source(jsonStr, XContentType.JSON);
        return request;
    }
}
