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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class DeviceUserInfoBuilder {

    private AtomicLong idGenerator = new AtomicLong();
    private String dirPath = "bulkInsertDeviceUserFiles";
    public static long ownerUserGap = 1000000000;

    public static void main(String[] args) {
        DeviceUserInfoBuilder deviceUserInfoBuilder = new DeviceUserInfoBuilder();
        deviceUserInfoBuilder.createDeviceUserBulkInsertFiles(101,100);
    }

    public JSONObject buildDeviceUserInfo(int deviceNumber) {

        DeviceUserInfo deviceUserInfo = new DeviceUserInfo();

        int deviceIdNum = deviceNumber;
        String deviceId = DevUtils.buildDeviceId(deviceIdNum);
        String accountId = String.valueOf(deviceIdNum);
        deviceUserInfo.setDeviceId(deviceId);
        deviceUserInfo.setOwnerId(accountId);
        deviceUserInfo.setUserId(String.valueOf(Long.parseLong(accountId)+ownerUserGap));
        deviceUserInfo.setAlias(DevUtils.buildAlias(deviceIdNum));
        deviceUserInfo.setDeviceName(DevUtils.buildDeviceName(deviceIdNum));
        deviceUserInfo.setDeviceType(DevUtils.buildDeviceType(deviceIdNum));

        return toDeviceUserJSON(deviceUserInfo);
    }

    public static JSONObject toDeviceUserJSON(DeviceUserInfo deviceUserInfo) {
        JSONObject devJSON = new JSONObject();
        devJSON.put("device_id", deviceUserInfo.getDeviceId());
        devJSON.put("owner_id", deviceUserInfo.getOwnerId());
        devJSON.put("user_id", deviceUserInfo.getUserId());
        devJSON.put("device_name", deviceUserInfo.getDeviceName());
        devJSON.put("alias", deviceUserInfo.getAlias());
        devJSON.put("device_name", deviceUserInfo.getDeviceName());
        return devJSON;
    }
    public List<JSONObject> buildDeviceUserInfos(long numOfDevices) {
        List<JSONObject> devInfoJSONs = new ArrayList<>();

        for(int i = 0; i < numOfDevices ; i++) {
            long devId = idGenerator.getAndIncrement();
            devInfoJSONs.add(buildDeviceUserInfo((int)devId));
        }
        return devInfoJSONs;
    }

    public void writeDeviceUsersToFile(String filePath,List<JSONObject> deviceInfos) {
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
    public void createDeviceUserBulkInsertFiles(long numOfDevice, long numberOfDevicePerFile) {
        long numFile = numOfDevice / numberOfDevicePerFile;
        long lastFileDevNum = numOfDevice % numberOfDevicePerFile;

        for(int i = 1; i < numFile+1; i++) {
            List<JSONObject> devicesList = buildDeviceUserInfos(numberOfDevicePerFile);
            writeDeviceUsersToFile(buildFileName(i), devicesList);
        }

        if(lastFileDevNum != 0) {
            List<JSONObject> deviceList = buildDeviceUserInfos(lastFileDevNum);
            writeDeviceUsersToFile(buildFileName((int)(numFile+1)), deviceList);
        }
    }

    public String buildFileName(int fileNumber) {
        return dirPath + "/builk_insert_device_user_" + fileNumber + ".txt";
    }

}
