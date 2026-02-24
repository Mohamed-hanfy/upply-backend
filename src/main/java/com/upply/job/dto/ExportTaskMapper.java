package com.upply.job.dto;

import com.upply.job.ExportTask;
import org.springframework.stereotype.Component;

@Component
public class ExportTaskMapper {

    public ExportTaskResponse toExportTaskResponse(ExportTask task) {
        return ExportTaskResponse.builder()
                .taskId(task.getTaskId())
                .status(task.getStatus().name())
                .build();
    }

    public ExportTaskResponse toExportTaskResponse(ExportTask task, String downloadUrl) {
        return ExportTaskResponse.builder()
                .taskId(task.getTaskId())
                .status(task.getStatus().name())
                .downloadUrl(downloadUrl)
                .build();
    }
}
