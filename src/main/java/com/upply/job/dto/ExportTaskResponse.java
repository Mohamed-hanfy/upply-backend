package com.upply.job.dto;

import lombok.Builder;

@Builder
public record ExportTaskResponse(
        String taskId,
        String status,
        String downloadUrl
) {
}
