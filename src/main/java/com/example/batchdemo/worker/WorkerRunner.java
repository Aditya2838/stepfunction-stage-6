package com.example.batchdemo.worker;

import com.example.batchdemo.domain.ChunkStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Component
@Profile("worker")
public class WorkerRunner implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(WorkerRunner.class);

    private final MongoTemplate mongoTemplate;
    private final S3Client s3Client;

    public WorkerRunner(MongoTemplate mongoTemplate, S3Client s3Client) {
        this.mongoTemplate = mongoTemplate;
        this.s3Client = s3Client;
    }

    @Override
    public void run(String... args) {
        String chunkId = System.getenv("CHUNK_ID");

        if (chunkId == null) {
            LOG.error("No CHUNK_ID environment variable set. Exiting.");
            return;
        }

        LOG.info("Worker starting for chunkId={}", chunkId);

        ChunkStatus chunk = mongoTemplate.findOne(
                new Query(Criteria.where("chunkId").is(chunkId)),
                ChunkStatus.class
        );

        if (chunk == null) {
            LOG.error("No ChunkStatus document found for chunkId={}", chunkId);
            return;
        }

        String content = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket("step-function-ecs-batch-processing")
                        .key(chunk.getS3Path())
                        .build()
        ).asUtf8String();

        LOG.info("Fetched chunk content ({} chars) for targetSystem={}",
                content.length(), chunk.getTargetSystem());

        // TODO: actual business logic — send `content` to PMA/CHARMS/PRICING

        mongoTemplate.updateFirst(
                new Query(Criteria.where("chunkId").is(chunkId)),
                new Update().set("status", "PROCESSED"),
                ChunkStatus.class
        );

        LOG.info("Marked chunkId={} as PROCESSED", chunkId);
    }
}