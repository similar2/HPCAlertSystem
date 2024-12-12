package edu.sustech.hpc.model.param;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAlertParam {
    public Integer id;

    @NotNull(message = "设备 ID 不能为空")
    private Integer deviceId; // 设备 ID

    private LocalDateTime alertTime; // 告警发生时间，可选字段

    @NotBlank(message = "告警级别不能为空")
    private String alertLevel; // 告警级别（低、中、高）

    private String description; // 告警描述（可选）

    private String alertStatus; // 告警状态，可选

    private String resolveMethod; // 解决方法（可选）

    private LocalDateTime resolveTime; // 解决时间（可选）

    private JsonNode other; // 附加字段

    private String responsiblePerson;


}
