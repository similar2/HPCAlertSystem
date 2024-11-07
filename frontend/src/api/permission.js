import request from '@/utils/request'


export const getPermissionList = () => {
    return request.get('/api/user/getAllPermissions')
}

export const fetchPermissions = () => {
    return request.get('/api/permissions');
}

export const fetchPermissionById = (id) => {
    return request.get(`/api/permissions/${id}`);
}

export const updatePermission = (id, permissionData) => {
    return request.put(`/api/permissions/${id}`, permissionData);
}

export const deletePermission = (id) => {
    return request.delete(`/api/permissions/${id}`);
}

export const createPermission = (permissionData) => {
    return request.post('/api/permissions', permissionData);
}
