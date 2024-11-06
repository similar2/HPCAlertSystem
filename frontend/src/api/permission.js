import request from '@/utils/request'


export const getPermissionList = () => {
    return request.get('/api/user/getAllPermissions')
}

export function fetchPermissions() {
    return request({
        url: '/api/permissions',
        method: 'get'
    });
}

export function fetchPermissionById(id) {
    return request({
        url: `/api/permissions/${id}`,
        method: 'get'
    });
}

export function updatePermission(id, permissionData) {
    return request({
        url: `/api/permissions/${id}`,
        method: 'put',
        data: permissionData
    });
}

export function deletePermission(id) {
    return request({
        url: `/api/permissions/${id}`,
        method: 'delete'
    });
}

export function createPermission(permissionData) {
    return request({
        url: '/api/permissions',
        method: 'post',
        data: permissionData
    });
}
