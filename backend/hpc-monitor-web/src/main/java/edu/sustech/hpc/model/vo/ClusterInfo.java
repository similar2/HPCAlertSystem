package edu.sustech.hpc.model.vo;

import edu.sustech.hpc.po.Cluster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClusterInfo {
    private String name;
    private Integer id;

    public ClusterInfo(Cluster cluster) {
        if (cluster != null) {
            name = cluster.getName();
            id = cluster.getId();
        }
    }
}

