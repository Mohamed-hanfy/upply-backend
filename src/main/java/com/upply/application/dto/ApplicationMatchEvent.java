package com.upply.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationMatchEvent {
    private Long applicationId;
    private Long userId;
    private Long jobId;
}
