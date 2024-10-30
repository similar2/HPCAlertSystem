import request from '@/utils/request'


export const getPermissionList = () => {
    return request.get('/api/user/getAllPermissions')
}