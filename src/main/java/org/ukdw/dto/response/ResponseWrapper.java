package org.ukdw.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Project: SRM-BE
 * Package: com.srmbe.util
 * <p>
 * Creator: dendy
 * Date: 6/30/2020
 * Time: 7:59 AM
 * <p>
 * Description : a response wrapper
 */

@JsonInclude(NON_NULL)
@Data
public class ResponseWrapper<T> {
    private T data;
    private int status;
    private LocalDateTime timestamp;
    private String message;

    public ResponseWrapper() {}

    /**
     * @param status
     * @param message
     * @param data
     */
    public ResponseWrapper(int status, String message, T data) {
        super();
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.data = data;
    }

    /**
     * @param status
     * @param data
     */
    public ResponseWrapper(int status, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.data = data;
        if(status == HttpStatus.OK.value()) {
            this.message = "Data fetched successfully";
        } else if (status == HttpStatus.BAD_REQUEST.value()) {
            this.message = "Request parameter error";
        } else {
            this.message = "";
        }
    }
}
