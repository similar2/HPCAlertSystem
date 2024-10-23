import request from '@/utils/request'

export const getUserList = (params) => {
  return request.get('/api/user/page', {
    params:params
  })
}

export const getRoleList = () => {
  return request.get('/api/user/roleList')
}