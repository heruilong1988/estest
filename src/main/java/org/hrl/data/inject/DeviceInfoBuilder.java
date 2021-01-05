package org.hrl.data.inject;

import com.google.common.io.Files;
import org.hrl.util.DevUtils;
import org.hrl.util.DeviceInfo;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DeviceInfoBuilder {

    private AtomicLong idGenerator = new AtomicLong();
    private String dirPath = "bulkInsertFiles";

    public static void main(String[] args) {
        DeviceInfoBuilder deviceInfoBuilder = new DeviceInfoBuilder();
        deviceInfoBuilder.createDeviceBulkInsertFiles(100,100);
    }

    public JSONObject buildDeviceInfo(int deviceNumber) {
        DeviceInfo indexDevInfo = DevUtils.buildTemplateDevInfo();
        int deviceIdNum = deviceNumber;
        String deviceId = DevUtils.buildDeviceId(deviceIdNum);
        String accountId = String.valueOf(deviceIdNum);
        indexDevInfo.setDeviceId(deviceId);
        indexDevInfo.setAccountId(accountId);
        return DevUtils.toDeviceJSON(indexDevInfo);
    }



    public List<JSONObject> buildDeviceInfos(long numOfDevices) {
        List<JSONObject> devInfoJSONs = new ArrayList<>();

        for(int i = 0; i < numOfDevices ; i++) {
            long devId = idGenerator.getAndIncrement();
            devInfoJSONs.add(buildDeviceInfo((int)devId));
        }
        return devInfoJSONs;
    }

    public void writeDevicesToFile(String filePath,List<JSONObject> deviceInfos) {
        StringBuilder stringBuilder = new StringBuilder();
        for(JSONObject dev : deviceInfos) {
            stringBuilder.append(dev + "\n");
        }

        writeFile(filePath, stringBuilder.toString());
    }

    public void writeFile(String filePath, String content) {
        File file = new File(filePath);
        try {
            Files.write(content, file, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文档存储于bulkInsertFiles目录
     * @param numOfDevice
     * @param numberOfDevicePerFile
     */
    public void createDeviceBulkInsertFiles(long numOfDevice, long numberOfDevicePerFile) {
        long numFile = numOfDevice / numberOfDevicePerFile;
        long lastFileDevNum = numberOfDevicePerFile % numOfDevice;

        for(int i = 0; i < numFile; i++) {
            List<JSONObject> devicesList = buildDeviceInfos(numberOfDevicePerFile);
            writeDevicesToFile(buildFileName(i), devicesList);
        }

        if(lastFileDevNum != 0) {
            List<JSONObject> deviceList = buildDeviceInfos(lastFileDevNum);
            writeDevicesToFile(buildFileName((int)(numFile+1)), deviceList);
        }
    }

    public String buildFileName(int fileNumber) {
        return dirPath + "/builk_insert_device_" + fileNumber + ".txt";
    }

}
