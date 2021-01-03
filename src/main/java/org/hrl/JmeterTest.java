package org.hrl;/*
 * Copyright (c) 2020, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2020/12/23
 */

import java.io.IOException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.hrl.util.DevUtils;
import org.hrl.util.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmeterTest extends AbstractJavaSamplerClient {

    Logger logger = LoggerFactory.getLogger(JmeterTest.class);

    private SearchClient searchClient;
    private IndexClient indexClient;
    private int searchIdLimit;

    /**
     * 参数注册
     * @return
     */
    @Override
    public Arguments getDefaultParameters() {

        Arguments params = new Arguments();
        params.addArgument("esServerHost", "http://localhost:8983/solr");
        params.addArgument("esServerPort", "http://localhost:8983/solr");

        return params;
    }


    @Override
    public void teardownTest(JavaSamplerContext context) {
        super.teardownTest(context);
        searchClient.close();
        indexClient.close();
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        String esServerHostsStr = context.getParameter("esServerHosts","localhost");
        int esServerPort = context.getIntParameter("esServerPort", 9200);
        this.searchClient = new SearchClient(esServerHostsStr.split(","), esServerPort);
        this.indexClient = new IndexClient(esServerHostsStr.split(","), esServerPort);
        this.searchIdLimit = context.getIntParameter("searchIdLimit");
    }


    @Override
    public SampleResult runTest(JavaSamplerContext context) {

        //send request
        SampleResult sr = new SampleResult();
        sr.setSampleLabel( "estest");
        try {
            sr.sampleStart();
            sendRequest(sr,searchIdLimit);
            sr.setSuccessful(true);
        } catch (Throwable e) {
            sr.setSuccessful(false);
            logger.error("request failed", e);
        } finally {
            sr.sampleEnd();
        }
        return sr;
    }

    private void sendRequest(SampleResult sr, int searchIdLimit) throws IOException {
        //1.测试按读写比例为7:1 发送请求
        int ratio = DevUtils.getRandomNumber(0,8); //[1-8]整型随机数
        if(ratio > 1) {
            //读请求
            DeviceInfo deviceInfo = new DeviceInfo();
            //0-searchIdLimit, deviceId格式:0000000...deviceIdNum,一共40位
            String deviceId = DevUtils.buildRandomDeviceId(0, searchIdLimit);
            deviceInfo.setDeviceId(deviceId);
            DeviceInfo rspDevInfo = searchClient.getDeviceInfo(deviceInfo);

        }else {
            //写请求
            DeviceInfo indexDevInfo = DevUtils.buildTemplateDevInfo();
            int deviceIdNum = DevUtils.getRandomNumber(30000000, 50000000);
            String deviceId = DevUtils.buildDeviceId(deviceIdNum);
            String accountId = String.valueOf(deviceIdNum);
            indexDevInfo.setDeviceId(deviceId);
            indexDevInfo.setAccountId(accountId);
            indexClient.index(indexDevInfo);
        }
    }
}
