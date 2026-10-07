package com.example.batchdemo.repository;

import com.example.batchdemo.domain.ChunkStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChunkStatusMongoRepository extends MongoRepository<ChunkStatus, String> {
    List<ChunkStatus> findByStatus(String status);   // <-- add this
}