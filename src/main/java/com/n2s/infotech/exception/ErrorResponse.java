package com.n2s.infotech.exception;

import lombok.Builder;
import lombok.Getter;
import java.time.OffsetDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final String message;
    private final String path;
    private final int status;
    @Builder.Default
    private final OffsetDateTime timestamp = OffsetDateTime.now();
}

