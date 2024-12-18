package edu.sustech.hpc.controller;

import edu.sustech.hpc.annotation.PathController;
import edu.sustech.hpc.po.Cluster;
import edu.sustech.hpc.result.ApiResponse;
import edu.sustech.hpc.service.ClusterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-19 2:46
 */
@PathController("/cluster")
@CrossOrigin
@Slf4j
public class ClusterController {

    @Resource
    private ClusterService clusterService;

    /**
     * Get all clusters
     * @return list of clusters
     */
    @GetMapping("/all")
    public ApiResponse<List<Cluster>> all(){
        log.info("Get all clusters");
        return ApiResponse.success(clusterService.all());
    }

    /**
     * Get cluster by id
     * @param id cluster id
     * @return cluster
     */
    @GetMapping
    public ApiResponse<Cluster> getById(Integer id){
        log.info("Get cluster by id: {}", id);
        return ApiResponse.success(clusterService.get(id));
    }

    /**
     * Add cluster
     * @param name cluster name
     * @return success
     */
    @PostMapping
    public ApiResponse add(String name){
        log.info("Add cluster: {}", name);
        clusterService.add(name);
        return ApiResponse.success();
    }

    /**
     * Update cluster
     * @param cluster cluster
     * @return success
     */
    @PostMapping("/update")
    public ApiResponse update(Cluster cluster){
        log.info("Update cluster: {}", cluster);
        clusterService.update(cluster);
        return ApiResponse.success();
    }

    /**
     * Delete cluster
     * @param id cluster id
     * @return success
     */
    @PostMapping("/delete")
    public ApiResponse delete(Integer id){
        log.info("Delete cluster: {}", id);
        clusterService.delete(id);
        return ApiResponse.success();
    }
}
