package edu.sustech.hpc.model.param;

import com.fasterxml.jackson.databind.JsonNode;
import edu.sustech.hpc.po.DeviceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceParam {
    public Integer id;
    @NotEmpty
    public String name;
    @NotEmpty
    public String position;
    @NotNull
    public DeviceType type;
    @NotNull
    public Integer clusterId; //1=太乙 2=启明
    public JsonNode other; //各种设备专有字段
}
