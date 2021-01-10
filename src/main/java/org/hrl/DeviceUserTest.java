package org.hrl;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.hrl.data.inject.DeviceUserInfo;
import org.hrl.data.inject.DeviceUserInfoBuilder;
import org.hrl.util.DevUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceUserTest extends AbstractJavaSamplerClient {

    Logger logger = LoggerFactory.getLogger(JmeterTest.class);

    private SearchClient searchClient;
    private IndexClient indexClient;
    private int searchIdLimit;
    private boolean recordRead;
    private int readRatio;
    private int writeRatio;

    private String aliasPattern;
    private String deviceTypePattern;
    private String deviceNamePattern;

    private boolean useAliasPattern;
    private boolean useDeviceTypePattern;
    private boolean useDeviceNameAlias;


    public static void main(String[] args) {
        DeviceUserTest deviceUserTest = new DeviceUserTest();
        Arguments arguments = deviceUserTest.getDefaultParameters();
        JavaSamplerContext javaSamplerContext = new JavaSamplerContext(arguments);
        deviceUserTest.setupTest(javaSamplerContext);
        deviceUserTest.runTest(javaSamplerContext);
        deviceUserTest.teardownTest(javaSamplerContext);
    }

    /**
     * 参数注册
     *
     * @return
     */
    @Override
    public Arguments getDefaultParameters() {

        Arguments params = new Arguments();
        params.addArgument("esServerHost", "localhost");
        params.addArgument("esServerPort", "9200");
        params.addArgument("searchIdLimit", "10000000");
        params.addArgument("readRatio", "1");
        params.addArgument("writeRatio", "10");
        params.addArgument("recordRead", "true");
        params.addArgument("aliasPattern","*alias*");
        params.addArgument("deviceTypePattern","*deviceType*");
        params.addArgument("deviceName","*deviceName*");
        params.addArgument("useAliasPattern","true");
        params.addArgument("useDeviceTypePattern","true");
        params.addArgument("useDeviceNamePattern","true");
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

        this.aliasPattern = context.getParameter("aliasPattern");
        this.deviceNamePattern = context.getParameter("deviceNamePattern");
        this.deviceTypePattern = context.getParameter("deviceTypePattern");
        this.useAliasPattern = Boolean.parseBoolean(context.getParameter("useAliasPattern"));
        this.useDeviceTypePattern = Boolean.parseBoolean(context.getParameter("useDeviceTypePattern"));
        this.useDeviceNameAlias = Boolean.parseBoolean(context.getParameter("useDeviceNamePattern"));

        logger.info("setup. searchClient:{},indexClient:{},searchIdLimit:{}", searchClient, indexClient, searchIdLimit);
        logger.info("setup.recordRead:{},readRatio:{},writeRatio:{}", recordRead,readRatio, writeRatio);
        logger.info("setup.aliasPattern:{},deviceNamePattern:{},deviceTypePattern:{}",aliasPattern,deviceNamePattern,deviceTypePattern);
        logger.info("setup.useAliasPattern:{},useDeviceNamePattern:{},useDeviceTypePattern:{}",useAliasPattern,useDeviceNameAlias,useDeviceTypePattern);
    }


    @Override
    public SampleResult runTest(JavaSamplerContext context) {

        //send request
        SampleResult sr = new SampleResult();
        sr.setSampleLabel("deviceUsertest");
        sendRequest(sr, searchIdLimit);
        return sr;
    }

    private void sendRequest(SampleResult sr, int searchIdLimit) {


        //1.测试按读写比例为7:1 发送请求
        //[low bound,high bound)
        int ratio = DevUtils.getRandomNumber(1, readRatio + writeRatio + 1); //[1-8]整型随机数
        if (ratio > writeRatio) {
            //读请求
            DeviceUserInfo deviceUserInfo = new DeviceUserInfo();
            //0-searchIdLimit, deviceId格式:0000000...deviceIdNum,一共40位
            long ownerId = DevUtils.buildRandomAccountId(0, searchIdLimit);
            if(useAliasPattern) {
                deviceUserInfo.setAlias(this.aliasPattern);
            }
            if(useDeviceNameAlias) {
                deviceUserInfo.setDeviceName(this.deviceNamePattern);
            }
            if(useDeviceTypePattern) {
                deviceUserInfo.setDeviceType(this.deviceTypePattern);
            }
            deviceUserInfo.setOwnerId(String.valueOf(ownerId));

            try {
                if (recordRead) {
                    sr.sampleStart();
                }
                DeviceUserInfo rspDevInfo = searchClient.getDeviceUserInfo(deviceUserInfo);
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

            DeviceUserInfo deviceUserInfo = new DeviceUserInfo();
            int deviceIdNum = DevUtils.getRandomNumber(30000000, 50000000);
            String deviceId = DevUtils.buildDeviceId(deviceIdNum);
            deviceUserInfo.setDeviceId(deviceId);
            deviceUserInfo.setOwnerId(String.valueOf(deviceIdNum));
            deviceUserInfo.setUserId(String.valueOf(DeviceUserInfoBuilder.ownerUserGap + deviceIdNum));
            deviceUserInfo.setAlias(DevUtils.buildAlias(deviceIdNum));
            deviceUserInfo.setDeviceType(DevUtils.buildDeviceType(deviceIdNum));
            deviceUserInfo.setDeviceName(DevUtils.buildDeviceName(deviceIdNum));

            try {
                if (!recordRead) {
                    sr.sampleStart();
                }
                indexClient.indexDeviceUser(deviceUserInfo);
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
