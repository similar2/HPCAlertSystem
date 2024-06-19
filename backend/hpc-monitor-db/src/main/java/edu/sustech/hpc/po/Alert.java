package edu.sustech.hpc.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import edu.sustech.hpc.annotation.DateTimeField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class Alert {
    @TableId(type= IdType.AUTO)
    private Integer id;

    @TableField(fill = INSERT)
    @DateTimeField
    private LocalDateTime createTime;

    private String description;

    @DateTimeField
    private LocalDateTime solveTime;

    private String alertName;

    private String solveMethod; //解决方式

    private String deviceName; //设备名称
    private AlertSeverity severity;
    private AlertType type = AlertType.SELF_DEFINED;

    public String getSubject() {
        return "SUSTech_HPC_Monitor(Alert): " + alertName + "_" + deviceName;
    }

    public String getInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        buildUtil(stringBuilder, "Alert Name", alertName);
        buildUtil(stringBuilder, "Description", description);
        buildUtil(stringBuilder, "Device Name", deviceName);
        buildUtil(stringBuilder, "Create Time", String.valueOf(createTime));
        buildUtil(stringBuilder, "Severity", String.valueOf(severity));
        return stringBuilder.toString();
    }

    public void buildUtil(StringBuilder stringBuilder, String name, String content) {
        stringBuilder.append(name).append(": ")
                .append(content).append("\n");

    }
}
