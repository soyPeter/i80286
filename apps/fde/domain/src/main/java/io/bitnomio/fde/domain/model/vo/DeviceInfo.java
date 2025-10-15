/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 11/8/25 Time: 20:50
 *
 */
package io.bitnomio.fde.domain.model.vo;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object representing device information for fraud detection.
 */
public final class DeviceInfo {
    private final String deviceId;
    private final String deviceType;
    private final String operatingSystem;
    private final String browser;
    private final String ipAddress;
    private final LocalDateTime firstSeen;
    private final LocalDateTime lastSeen;
    private final int usageCount;
    private final boolean isTrusted;

    private DeviceInfo(Builder builder) {
        this.deviceId = builder.deviceId;
        this.deviceType = builder.deviceType;
        this.operatingSystem = builder.operatingSystem;
        this.browser = builder.browser;
        this.ipAddress = builder.ipAddress;
        this.firstSeen = builder.firstSeen;
        this.lastSeen = builder.lastSeen;
        this.usageCount = builder.usageCount;
        this.isTrusted = builder.isTrusted;
    }

    // Getters
    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getFirstSeen() {
        return firstSeen;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public boolean isTrusted() {
        return isTrusted;
    }

    // Domain logic
    public boolean isNewDevice() {
        if (firstSeen == null) {
            return true;
        }
        return firstSeen.plusDays(7).isAfter(LocalDateTime.now());
    }

    public boolean isRarelyUsed() {
        return usageCount < 5;
    }

    // Builder
    public static class Builder {
        private String deviceId;
        private String deviceType;
        private String operatingSystem;
        private String browser;
        private String ipAddress;
        private LocalDateTime firstSeen;
        private LocalDateTime lastSeen;
        private int usageCount;
        private boolean isTrusted;

        public Builder(String deviceId) {
            this.deviceId = deviceId;
        }

        public Builder deviceType(String deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        public Builder operatingSystem(String operatingSystem) {
            this.operatingSystem = operatingSystem;
            return this;
        }

        public Builder browser(String browser) {
            this.browser = browser;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder firstSeen(LocalDateTime firstSeen) {
            this.firstSeen = firstSeen;
            return this;
        }

        public Builder lastSeen(LocalDateTime lastSeen) {
            this.lastSeen = lastSeen;
            return this;
        }

        public Builder usageCount(int usageCount) {
            this.usageCount = usageCount;
            return this;
        }

        public Builder trusted(boolean trusted) {
            isTrusted = trusted;
            return this;
        }

        public DeviceInfo build() {
            return new DeviceInfo(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceInfo that = (DeviceInfo) o;
        return Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId);
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", usageCount=" + usageCount +
                ", isTrusted=" + isTrusted +
                '}';
    }
}
