package com.gindevp.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Asset Management.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();

    private final Upload upload = new Upload();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public Upload getUpload() {
        return upload;
    }

    public static class Upload {

        /** Thư mục lưu file đính kèm yêu cầu sửa chữa (tương đối hoặc tuyệt đối) */
        private String repairDirectory = "uploads/repair";

        /** Thư mục lưu file đính kèm yêu cầu cấp phát */
        private String allocationDirectory = "uploads/allocation";

        /** Mặc định 50 MB (ảnh/PDF/video ngắn theo Phase 1). */
        private long maxRepairAttachmentBytes = 52_428_800L;

        /** Giới hạn file đính kèm YC cấp phát (mặc định giống sửa chữa). */
        private long maxAllocationAttachmentBytes = 52_428_800L;

        public String getRepairDirectory() {
            return repairDirectory;
        }

        public void setRepairDirectory(String repairDirectory) {
            this.repairDirectory = repairDirectory;
        }

        public String getAllocationDirectory() {
            return allocationDirectory;
        }

        public void setAllocationDirectory(String allocationDirectory) {
            this.allocationDirectory = allocationDirectory;
        }

        public long getMaxRepairAttachmentBytes() {
            return maxRepairAttachmentBytes;
        }

        public void setMaxRepairAttachmentBytes(long maxRepairAttachmentBytes) {
            this.maxRepairAttachmentBytes = maxRepairAttachmentBytes;
        }

        public long getMaxAllocationAttachmentBytes() {
            return maxAllocationAttachmentBytes;
        }

        public void setMaxAllocationAttachmentBytes(long maxAllocationAttachmentBytes) {
            this.maxAllocationAttachmentBytes = maxAllocationAttachmentBytes;
        }
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }
    // jhipster-needle-application-properties-property-class
}
