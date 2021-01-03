package org.hrl.util;/*
 * Copyright (c) 2020, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2020/12/23
 */

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DevUtils {

    public static class DEVICE_FIELDS {
        public static String  DEVICE_ID = "device_id";
        public static String  ACCOUNT_ID = "account_id";
        public static String ALIAS = "alias";
        public static String BIND_CODE = "bind_code";
        public static String BIND_REGION = "bind_region";
        public static String DEVICE_TYPE = "device_type";
        public static String DEVICE_MODEL = "device_model";
        public static String HARDWARE_VERSION = "hardware_version";
        public static String LOCALE ="locale";
        public static String MAC = "mac";
        public static String OEM_ID = "oem_id";
        public static String REGION = "region";
        public static String REGION_CODE = "region_code";
        public static String VERSION = "version";
    }

    public static String buildRandomDeviceId(int origin, int bound) {
       return String.format("%40d", ThreadLocalRandom.current().nextInt(origin,bound));
    }

    public static String buildDeviceId(int deviceIdNum) {
        String id = String.format("%40d", deviceIdNum);
        return id.replace(" ", "F");
    }

    public static int getRandomNumber(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin,bound);
    }

    public static String buildAlias() {
        int random = ThreadLocalRandom.current().nextInt(0,1000000);
        return "alias"+ String.valueOf(random);
    }

    public static String buildBindCode() {
        int random = ThreadLocalRandom.current().nextInt(0,1000000);
        return "bindCode"+ String.valueOf(random);
    }

    public static String buildBindRegion() {
        int random = ThreadLocalRandom.current().nextInt(0,3);
        return "bindRegion"+ String.valueOf(random);
    }

    public static String buildDeviceType() {
        int random = ThreadLocalRandom.current().nextInt(0,500);
        return "deviceType"+ String.valueOf(random);
    }

    public static String buildDeviceModel() {
        int random = ThreadLocalRandom.current().nextInt(0,1000);
        return "deviceModel"+ String.valueOf(random);
    }

    public static String buildHardwareId() {
        int random = ThreadLocalRandom.current().nextInt(0,1000);
        return "hardwareId"+ String.valueOf(random);
    }

    public static String buildHardwareVersion() {
        int random = ThreadLocalRandom.current().nextInt(0,1000);
        return "hardwareVersion"+ String.valueOf(random);
    }

    public static String buildLocale() {
        int random = ThreadLocalRandom.current().nextInt(0,1000);
        return "locale"+ String.valueOf(random);
    }

    public static String buildMac() {
        int random = ThreadLocalRandom.current().nextInt(0,1000000000);
        String str = String.format("%10d", random);
        return "mac"+ str;
    }

    public static String buildOemId() {
        int random = ThreadLocalRandom.current().nextInt(0,10000);
        return "oemId"+ String.valueOf(random);
    }

    public static String buildRegion() {
        int random = ThreadLocalRandom.current().nextInt(0,3);
        return "region"+ String.valueOf(random);
    }

    public static String buildRegionCode() {
        int random = ThreadLocalRandom.current().nextInt(0,100);
        return "oemId"+ String.valueOf(random);
    }




    public static DeviceInfo buildTemplateDevInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setAlias(buildAlias());
        deviceInfo.setBindCode(buildBindCode());
        deviceInfo.setBindRegion(buildBindRegion());
        deviceInfo.setDeviceType(buildDeviceType());
        deviceInfo.setDeviceModel(buildDeviceModel());
        deviceInfo.setHardwareVersion(buildHardwareVersion());
        deviceInfo.setLocale(buildLocale());
        deviceInfo.setMac(buildMac());
        deviceInfo.setOemId(buildOemId());
        deviceInfo.setRegion(buildRegion());
        deviceInfo.setRegionCode(buildRegionCode());
        deviceInfo.setVersion("1");
        return deviceInfo;
    }


    public static DeviceInfo toDeviceInfo(Map<String,Object> devMap) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId((String) devMap.get("device_id"));
        deviceInfo.setAccountId((String) devMap.get("account_id"));
        deviceInfo.setAlias((String) devMap.get("alias"));
        deviceInfo.setRegion((String) devMap.get("region"));
        deviceInfo.setBindCode((String) devMap.get("bind_code"));
        deviceInfo.setBindRegion((String) devMap.get("bind_region"));
        deviceInfo.setDeviceType((String) devMap.get("device_type"));
        deviceInfo.setDeviceModel((String) devMap.get("device_model"));
        deviceInfo.setHardwareVersion((String) devMap.get("hardware_version"));
        deviceInfo.setLocale((String) devMap.get("locale"));
        deviceInfo.setMac((String) devMap.get("mac"));
        deviceInfo.setOemId((String) devMap.get("oem_id"));
        deviceInfo.setRegionCode((String) devMap.get("region_code"));
        deviceInfo.setVersion((String) devMap.get("version"));
        return deviceInfo;
    }

    public static JSONObject toDeviceJSON(DeviceInfo deviceInfo) {
        JSONObject devJSON = new JSONObject();
        devJSON.put("device_id", deviceInfo.getDeviceId());
        devJSON.put("account_id", deviceInfo.getAccountId());
        devJSON.put("alias", deviceInfo.getAlias());
        devJSON.put("region", deviceInfo.getRegion());
        devJSON.put("bind_code",deviceInfo.getBindCode());
        devJSON.put("bind_region", deviceInfo.getBindRegion());
        devJSON.put("device_type", deviceInfo.getDeviceType());
        devJSON.put("device_model", deviceInfo.getDeviceModel());
        devJSON.put("hardware_versioin", deviceInfo.getHardwareVersion());
        devJSON.put("locale", deviceInfo.getLocale());
        devJSON.put("mac", deviceInfo.getMac());
        devJSON.put("oem_id", deviceInfo.getOemId());
        devJSON.put("region_code",deviceInfo.getRegionCode());
        devJSON.put("version",deviceInfo.getVersion());
        return devJSON;
    }
}
