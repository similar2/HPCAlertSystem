package edu.sustech.hpc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.sustech.hpc.dao.ClusterDao;
import edu.sustech.hpc.dao.DeviceDao;
import edu.sustech.hpc.po.Cluster;
import edu.sustech.hpc.po.Device;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-19 2:36
 */
@Service
public class ClusterService {

    @Resource
    ClusterDao clusterDao;

    @Resource
    DeviceDao deviceDao;

    public List<Cluster> all() {
        return clusterDao.selectList(null);
    }

    public Cluster get(Integer id) {
        return clusterDao.selectById(id);
    }

    public void add(String name) {
        clusterDao.insert(new Cluster().setName(name));
    }

    public void update(Cluster cluster) {
        clusterDao.updateById(cluster);
    }

    public void delete(Integer id) {
        List<Device> devices = deviceDao.selectList(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getClusterId, id)
        );
        if (!devices.isEmpty()) {
            throw new RuntimeException("Cluster has devices, cannot delete");
        }
        clusterDao.deleteById(id);
    }

}
