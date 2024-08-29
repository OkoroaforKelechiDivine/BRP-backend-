package com.project.BRP_backend.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.apache.logging.log4j.util.Strings.EMPTY;

public class ResponseUtil {
    private static final BiConsumer<HttpServletResponse, Response> writeResponse = (httpServletResponse, response) -> {
        try {
            var outputStream = httpServletResponse.getOutputStream();
            new ObjectMapper().writeValue(outputStream, response);
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    };

    public static Response getResponse(HttpServletRequest httpServletRequest,
                                       Map<?,?> data,
                                       String message,
                                       HttpStatus status) {
        return new Response(
                now().format(ISO_LOCAL_DATE_TIME),
                status.value(),
                httpServletRequest.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                message,
                EMPTY,
                (Objects.isNull(data)) ? Collections.EMPTY_MAP : data
        );
    }
}
