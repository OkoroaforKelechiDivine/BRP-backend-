package com.project.BRP_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor
public class ResponseDetails {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm::ss")
    private LocalDateTime timestamp;

    private String message;

    private String status;
    private Map<?,?> data;

    public ResponseDetails(LocalDateTime timestamp, String message, String status) {
        this.timestamp = timestamp;
        this.message = message;
        this.status = status;
        data = Map.of();
    }
}