package edu.sustech.hpc.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author Yuxian Wu
 * @version 1.0
 * @Description:
 * @Create: 2024-10-09 19:40
 */
@Data
public class AlertPageQueryDTO {
    private String deviceName;
    private Boolean solved;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private int page;
    private int pageSize;
}
