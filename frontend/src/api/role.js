import request from '@/utils/request'


export const getRoleList = (roleName) => {
    return request.get('/api/user/rolePageQuery', {
        params:{"roleName":roleName}
    })
}

export const addRole = (params) => {
    return request.post('/api/user/addRole', params)
}

export const updateRole = (params) => {
    return request.post('/api/user/updateRole', params)
}

export const deleteRole = (id) => {
    return request.post(`/api/user/deleteRole/${id}`)
}