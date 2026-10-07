package com.example.batchdemo.writer;

import com.example.batchdemo.domain.ChunkStatus;
import com.example.batchdemo.domain.TargetRecord;
import com.example.batchdemo.repository.ChunkStatusMongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class TargetRecordItemWriter implements ItemWriter<TargetRecord> {

    @Autowired private S3Client s3Client;
    @Autowired private ChunkStatusMongoRepository chunkStatusRepository;
    @Autowired private ObjectMapper objectMapper;   // <-- added
    @Value("${chunk.bucket.name}") private String bucketName;

    @Override
    public void write(Chunk<? extends TargetRecord> items) {
        for (TargetRecord item : items) {
            String s3Key = "chunks/" + item.getTargetSystem() + "/" + item.getChunkId() + ".json";

            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build(),
                RequestBody.fromString(toJson(item))
            );
            item.setS3Path(s3Key);

            ChunkStatus status = new ChunkStatus();
            status.setChunkId(item.getChunkId());
            status.setJobId(item.getJobId());
            status.setTargetSystem(item.getTargetSystem());
            status.setS3Path(s3Key);
            status.setStatus("PENDING");
            chunkStatusRepository.save(status);
        }
    }

    private String toJson(TargetRecord item) {
        try {
            return objectMapper.writeValueAsString(item);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize TargetRecord to JSON", e);
        }
    }
}