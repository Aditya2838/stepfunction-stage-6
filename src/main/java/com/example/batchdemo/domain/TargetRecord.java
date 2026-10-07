package com.example.batchdemo.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "target_record")
public class TargetRecord {

    @Id
    private String id;

    private Integer sourceId;
    private String name;
    private Integer age;
    private String processedValue;
    private String status;
    private LocalDateTime processedAt;

    // Tracking fields
    private String chunkId;
    private String jobId;
    private String targetSystem; // PMA / CHARMS / PRICING
    private String s3Path;

    public TargetRecord() {
    }

    public TargetRecord(
            Integer sourceId,
            String name,
            Integer age,
            String processedValue,
            String status,
            String chunkId,
            String jobId,
            String targetSystem,
            String s3Path) {

        this.sourceId = sourceId;
        this.name = name;
        this.age = age;
        this.processedValue = processedValue;
        this.status = status;
        this.chunkId = chunkId;
        this.jobId = jobId;
        this.targetSystem = targetSystem;
        this.s3Path = s3Path;
        this.processedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getProcessedValue() {
        return processedValue;
    }

    public void setProcessedValue(String processedValue) {
        this.processedValue = processedValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getChunkId() {
        return chunkId;
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }

    public String getS3Path() {
        return s3Path;
    }

    public void setS3Path(String s3Path) {
        this.s3Path = s3Path;
    }

    @Override
    public String toString() {
        return "TargetRecord{" +
                "id='" + id + '\'' +
                ", sourceId=" + sourceId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", processedValue='" + processedValue + '\'' +
                ", status='" + status + '\'' +
                ", processedAt=" + processedAt +
                ", chunkId='" + chunkId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", targetSystem='" + targetSystem + '\'' +
                ", s3Path='" + s3Path + '\'' +
                '}';
    }
}