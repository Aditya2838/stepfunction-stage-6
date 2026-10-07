package com.example.batchdemo.processor;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.batchdemo.domain.SourceRecord;
import com.example.batchdemo.domain.TargetRecord;


public class RecordUpperCaseProcessor
        implements ItemProcessor<SourceRecord, TargetRecord> {

    private static final Logger LOG =
            LoggerFactory.getLogger(RecordUpperCaseProcessor.class);

    @Override
    public TargetRecord process(SourceRecord item) {

        String upperCaseName =
                item.getName() == null
                        ? null
                        : item.getName().toUpperCase();

        LOG.info(
                "Processing id={}, name={}, uppercaseName={}",
                item.getId(),
                item.getName(),
                upperCaseName
        );

        TargetRecord target = new TargetRecord();

        // Existing mapping
        target.setSourceId(item.getId());
        target.setName(item.getName());
        target.setAge(item.getAge());
        target.setProcessedValue(upperCaseName);
        target.setStatus("PROCESSED");
        target.setTargetSystem(item.getRecordType());

        // Tracking fields
        target.setJobId("JOB-" + UUID.randomUUID());
        target.setChunkId("CHUNK-" + item.getRecordType() + "-" + UUID.randomUUID());
        target.setS3Path("s3://batch-demo-bucket/chunks/chunk-file.csv");

        return target;
    }
}
