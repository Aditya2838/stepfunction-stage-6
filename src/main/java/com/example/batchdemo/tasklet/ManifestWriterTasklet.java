package com.example.batchdemo.tasklet;

import com.example.batchdemo.domain.ChunkStatus;
import com.example.batchdemo.repository.ChunkStatusMongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ManifestWriterTasklet implements Tasklet {

    private static final Logger LOG = LoggerFactory.getLogger(ManifestWriterTasklet.class);

    @Autowired private ChunkStatusMongoRepository chunkStatusRepository;
    @Autowired private S3Client s3Client;
    @Autowired private ObjectMapper objectMapper;
    @Value("${chunk.bucket.name}") private String bucketName;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<ChunkStatus> pendingChunks = chunkStatusRepository.findByStatus("PENDING");

        List<ManifestItem> manifestItems = pendingChunks.stream()
                .map(c -> new ManifestItem(c.getChunkId()))
                .collect(Collectors.toList());

        String manifestJson = objectMapper.writeValueAsString(manifestItems);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key("manifests/manifest.json")
                        .build(),
                RequestBody.fromString(manifestJson)
        );

        LOG.info("Wrote manifest with {} chunk(s) to manifests/manifest.json", manifestItems.size());

        return RepeatStatus.FINISHED;
    }

    private static class ManifestItem {
        public String chunkId;
        public ManifestItem(String chunkId) { this.chunkId = chunkId; }
    }
}