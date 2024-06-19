package edu.sustech.hpc.model.param;

import com.fasterxml.jackson.databind.JsonNode;
import edu.sustech.hpc.po.HardwareType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class HardwareParam {
    public Integer id;
    @NotEmpty
    public String name;
    @NotEmpty
    public String ip;
    @NotNull
    public HardwareType type;
    public Integer serverId;
    public JsonNode other; //各种硬件专属字段
}
