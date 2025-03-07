package org.ukdw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String timestamp;
}
