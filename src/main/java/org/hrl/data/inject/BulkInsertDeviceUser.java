package org.hrl.data.inject;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BulkInsertDeviceUser {

    static Logger logger = LoggerFactory.getLogger(BulkInsertDeviceUser.class);

    private RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) throws IOException {
        BulkInsertDeviceUser bulkInsertDeviceUser = new BulkInsertDeviceUser();
        String[] esServerHosts = {"localhost"};
        bulkInsertDeviceUser.initRestClient(esServerHosts, 9200);
        bulkInsertDeviceUser.bulkInsertFromFileMultiThread("bulkInsertDeviceUserFiles");
        bulkInsertDeviceUser.restHighLevelClient.close();
        logger.info("bulkInsertDeviceUser.end");
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

    public void bulkInsertFromFileMultiThread(String fileDirPath) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        File file = new File(fileDirPath);
        int fileNum = file.listFiles().length;
        //获取path下子目录
        Iterable<File> childrens = Files.fileTreeTraverser().children(file);
        final CountDownLatch countDownLatch = new CountDownLatch(fileNum);
        for (final File children : childrens) {

            if(children.isFile()) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        logger.info("thread:{},begin.insert file.{}",Thread.currentThread().getName(), children.getName());
                        bulkInsertFromFile(children);
                        countDownLatch.countDown();
                    }
                });

            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("countDownLatch exception", e);
        }
        executorService.shutdown();
        logger.info("insert files finished");
    }

    public void bulkInsertFromFile(File file) {
        BulkRequest request = new BulkRequest();
        try {
            List<String> readLines = Files.readLines(file, Charsets.UTF_8);
            for(String devJsonStr : readLines) {
                JSONObject devJson = new JSONObject(devJsonStr);
                IndexRequest indexRequest = createIndexFromJsonStr(devJsonStr,devJson.getString("device_id"));
                request.add(indexRequest);
            }
        } catch (IOException e) {
            logger.error("exception.",e);
        }

        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            checkBulkResponse(bulkResponse);
        } catch (IOException e) {
            logger.error("failed to bulkInsert.",e);
        }

    }

    public void checkBulkResponse(BulkResponse bulkResponse) {
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
            logger.debug("bulkResponse:{}",bulkItemResponse);
            if (bulkItemResponse.isFailed()) {
                BulkItemResponse.Failure failure =
                        bulkItemResponse.getFailure();
                logger.error("insert error.{}",failure);
                return;
            }

            DocWriteResponse itemResponse = bulkItemResponse.getResponse();

            switch (bulkItemResponse.getOpType()) {
                case INDEX:
                case CREATE:
                    IndexResponse indexResponse = (IndexResponse) itemResponse;
                    logger.debug("create index rsp status.{}",itemResponse.status());
                    break;
                case UPDATE:
                    UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                    logger.debug("update index rsp status.{}",itemResponse.status());
                    break;
                case DELETE:
                    DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
            }

        }
    }

    public IndexRequest createIndexFromJsonStr(String jsonStr, String docId) {
        IndexRequest request = new IndexRequest("device_user");
        request.source(jsonStr, XContentType.JSON);
        request.id(docId);
        request.opType(DocWriteRequest.OpType.CREATE);
        return request;
    }
}
