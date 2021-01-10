package org.hrl.data.inject;

import com.google.common.base.MoreObjects;

public class DeviceUserInfo {

    String deviceId;
    String ownerId;
    String userId;
    String deviceType;
    String alias;
    String deviceName;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("deviceId", deviceId)
                .add("ownerId", ownerId)
                .add("userId", userId)
                .add("deviceType", deviceType)
                .add("alias", alias)
                .add("deviceName", deviceName)
                .toString();
    }
}
