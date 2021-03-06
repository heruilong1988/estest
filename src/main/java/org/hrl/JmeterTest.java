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
    private boolean recordRead;
    private int readRatio;
    private int writeRatio;


    /**
     * 参数注册
     *
     * @return
     */
    @Override
    public Arguments getDefaultParameters() {

        Arguments params = new Arguments();
        params.addArgument("esServerHost", "http://localhost:8983/solr");
        params.addArgument("esServerPort", "http://localhost:8983/solr");
        params.addArgument("searchIdLimit", "10000000");
        params.addArgument("readRatio", "7");
        params.addArgument("writeRatio", "1");
        params.addArgument("recordRead", "true");

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
        String esServerHostsStr = context.getParameter("esServerHosts", "localhost");
        int esServerPort = context.getIntParameter("esServerPort", 9200);
        this.searchClient = new SearchClient(esServerHostsStr.split(","), esServerPort);
        this.indexClient = new IndexClient(esServerHostsStr.split(","), esServerPort);
        this.searchIdLimit = context.getIntParameter("searchIdLimit");
        this.readRatio = context.getIntParameter("readRatio");
        this.writeRatio = context.getIntParameter("writeRatio");
        this.recordRead = Boolean.parseBoolean(context.getParameter("recordRead"));

        logger.info("setup. searchClient:{},indexClient:{},searchIdLimit:{}", searchClient, indexClient, searchIdLimit);
        logger.info("setup.recordRead:{},readRatio:{},writeRatio:{}", recordRead,readRatio, writeRatio);
    }


    @Override
    public SampleResult runTest(JavaSamplerContext context) {

        //send request
        SampleResult sr = new SampleResult();
        sr.setSampleLabel("estest");
        sendRequest(sr, searchIdLimit);
        return sr;
    }

    private void sendRequest(SampleResult sr, int searchIdLimit) {


        //1.测试按读写比例为7:1 发送请求
        //[low bound,high bound)
        int ratio = DevUtils.getRandomNumber(1, readRatio + writeRatio + 1); //[1-8]整型随机数
        if (ratio > writeRatio) {
            //读请求
            DeviceInfo deviceInfo = new DeviceInfo();
            //0-searchIdLimit, deviceId格式:0000000...deviceIdNum,一共40位
            String deviceId = DevUtils.buildRandomDeviceId(0, searchIdLimit);
            deviceInfo.setDeviceId(deviceId);

            try {
                if (recordRead) {
                    sr.sampleStart();
                }
                DeviceInfo rspDevInfo = searchClient.getDeviceInfo(deviceInfo);
                if (recordRead) {
                    sr.setSuccessful(true);
                }
            } catch (Throwable e) {
                if (recordRead) {
                    sr.setSuccessful(false);
                }
                logger.error("read request failed", e);
            } finally {
                if (recordRead) {
                    sr.sampleEnd();
                }
            }

        } else {
            //写请求
            DeviceInfo indexDevInfo = DevUtils.buildTemplateDevInfo();
            int deviceIdNum = DevUtils.getRandomNumber(30000000, 50000000);
            String deviceId = DevUtils.buildDeviceId(deviceIdNum);
            String accountId = String.valueOf(deviceIdNum);
            indexDevInfo.setDeviceId(deviceId);
            indexDevInfo.setAccountId(accountId);

            try {
                if (!recordRead) {
                    sr.sampleStart();
                }
                indexClient.index(indexDevInfo);
                if (!recordRead) {
                    sr.setSuccessful(true);
                }
            } catch (Throwable e) {
                if (!recordRead) {
                    sr.setSuccessful(false);
                }
                logger.error("read request failed", e);
            } finally {
                if (!recordRead) {
                    sr.sampleEnd();
                }
            }
        }

    }
}
