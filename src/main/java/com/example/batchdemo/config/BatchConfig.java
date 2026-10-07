package com.example.batchdemo.config;

import com.example.batchdemo.domain.SourceRecord;
import com.example.batchdemo.domain.TargetRecord;
import com.example.batchdemo.listener.JobLoggingListener;
import com.example.batchdemo.processor.RecordUpperCaseProcessor;
import com.example.batchdemo.reader.S3CsvItemReader;
import com.example.batchdemo.tasklet.ManifestWriterTasklet;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class BatchConfig {

    private static final int CHUNK_SIZE = 10;

    // === Job ===
    @Bean
    public Job mongoRecordProcessingJob(
            JobRepository jobRepository,
            Step mongoProcessingStep,
            Step writeManifestStep) {

        return new JobBuilder("mongoRecordProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(mongoProcessingStep)
                .next(writeManifestStep)
                .build();
    }

    // === Step ===
    @Bean
    public Step mongoProcessingStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemStreamReader<SourceRecord> sourceRecordItemReader,
            ItemProcessor<SourceRecord, TargetRecord> recordUpperCaseProcessor,
            ItemWriter<TargetRecord> targetRecordItemWriter) {   // fixed type — was RepositoryItemWriter

        return new StepBuilder("mongoProcessingStep", jobRepository)
                .<SourceRecord, TargetRecord>chunk(CHUNK_SIZE, transactionManager)
                .reader(sourceRecordItemReader)
                .processor(recordUpperCaseProcessor)
                .writer(targetRecordItemWriter)
                .build();
    }

    // === Reader ===
    // S3CsvItemReader is a plain class (not @Component), so it MUST be
    // defined here as a @Bean — Spring can't auto-detect it otherwise.
    @Bean
    @StepScope
    public ItemStreamReader<SourceRecord> sourceRecordItemReader(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String bucket,
            @Value("${aws.s3.input-key}") String key) {

        return new S3CsvItemReader(s3Client, bucket, key);
    }

    // === Processor ===
    @Bean
    public ItemProcessor<SourceRecord, TargetRecord> recordUpperCaseProcessor() {
        return new RecordUpperCaseProcessor();
    }

    @Bean
    public Step writeManifestStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ManifestWriterTasklet manifestWriterTasklet) {

        return new StepBuilder("writeManifestStep", jobRepository)
                .tasklet(manifestWriterTasklet, transactionManager)
                .build();
    }
    // Note: no @Bean needed for TargetRecordItemWriter — it's @Component
    // annotated, so Spring auto-detects it and injects it by type above.
}