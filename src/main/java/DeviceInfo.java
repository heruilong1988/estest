/*
 * Copyright (c) 2020, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2020/12/22
 */

import com.google.common.base.MoreObjects;
import java.util.Map;

public class DeviceInfo {

    public String deviceId;
    public String accountId;
    public String alias;
    public String bindCode;
    public String bindRegion;
    public String deviceType;
    public String deviceModel;
    public String hardwareVersion;
    public String locale;
    public String mac;
    public String oemId;
    public String region;
    public String regionCode;
    public String version;


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getBindCode() {
        return bindCode;
    }

    public void setBindCode(String bindCode) {
        this.bindCode = bindCode;
    }

    public String getBindRegion() {
        return bindRegion;
    }

    public void setBindRegion(String bindRegion) {
        this.bindRegion = bindRegion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getOemId() {
        return oemId;
    }

    public void setOemId(String oemId) {
        this.oemId = oemId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("deviceId", deviceId)
            .add("accountId", accountId)
            .add("alias", alias)
            .add("bindCode", bindCode)
            .add("bindRegion", bindRegion)
            .add("deviceType", deviceType)
            .add("deviceModel", deviceModel)
            .add("hardwareVersion", hardwareVersion)
            .add("locale", locale)
            .add("mac", mac)
            .add("oemId", oemId)
            .add("region", region)
            .add("regionCode", regionCode)
            .add("version", version)
            .toString();
    }


}
