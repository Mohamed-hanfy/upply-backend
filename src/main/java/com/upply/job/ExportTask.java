package com.upply.job;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ExportTask {

    public enum Status {
        PROCESSING, COMPLETED, FAILED
    }

    private final String taskId;
    private final Long jobId;
    private volatile Status status;
    private volatile byte[] data;
    private volatile String errorMessage;
    private final Instant createdAt;
    private Instant expireAt;

    public ExportTask(String taskId, Long jobId) {
        this.taskId = taskId;
        this.jobId = jobId;
        this.status = Status.PROCESSING;
        this.createdAt = Instant.now();
        this.expireAt = Instant.now().plusSeconds(600);
    }
}
