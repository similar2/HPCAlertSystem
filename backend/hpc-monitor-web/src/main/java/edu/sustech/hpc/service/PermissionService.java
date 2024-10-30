package edu.sustech.hpc.service;

import edu.sustech.hpc.dao.PermissionDao;
import edu.sustech.hpc.po.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionDao permissionDAO;

    public List<Permission> getAllPermissions() {
        return permissionDAO.findAllPermissions();
    }

    public Permission getPermissionById(Long id) {
        return permissionDAO.findPermissionById(id);
    }

    public boolean updatePermission(Long id, Permission permission) {
        permission.setId(Math.toIntExact(id));
        return permissionDAO.updatePermission(permission) > 0;
    }

    public boolean deletePermission(Long id) {
        return permissionDAO.deletePermission(id) > 0;
    }

    public boolean createPermission(Permission permission) {
        return permissionDAO.insertPermission(permission) > 0;
    }
}

