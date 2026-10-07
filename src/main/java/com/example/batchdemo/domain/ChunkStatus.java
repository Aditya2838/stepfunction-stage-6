package com.example.batchdemo.domain;

public class ChunkStatus {
    private String chunkId;
    private String jobId;
    private String targetSystem;   // <-- this field must exist
    private String s3Path;
    private String status;
    private int attempts;

    public String getChunkId() { return chunkId; }
    public void setChunkId(String chunkId) { this.chunkId = chunkId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getTargetSystem() { return targetSystem; }
    public void setTargetSystem(String targetSystem) { this.targetSystem = targetSystem; }  // <-- this setter

    public String getS3Path() { return s3Path; }
    public void setS3Path(String s3Path) { this.s3Path = s3Path; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }


    @Override
    public String toString() {
        return "ChunkStatus{" +
                "chunkId='" + chunkId + '\'' +
                ", jobId='" + jobId + '\'' +
                ",targetSystem'"+ targetSystem+'\''+
                ", s3Path='" + s3Path + '\'' +
                ", status='" + status + '\'' +
                ", attempts=" + attempts +
                '}';
    }
}