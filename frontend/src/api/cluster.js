import request from '@/utils/request'

export const getClusterList = () => {
    return request({
        url: '/api/cluster/all',
        method: 'get'
    })
}

export const getClusterById = (id) => {
    return request({
        url: `/api/cluster/${id}`,
        method: 'get'
    })
}

export const createCluster = (name) => {
    return request({
        url: '/api/cluster',
        method: 'post',
        params: {name}
    })
}

export const updateCluster = (cluster) => {
    return request({
        url: `/api/cluster/update`,
        method: 'post',
        data: cluster
    })
}

export const deleteCluster = (id) => {
    return request({
        url: `/api/cluster/delete`,
        method: 'post',
        params: {id}
    })
}