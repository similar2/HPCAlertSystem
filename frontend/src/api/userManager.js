import request from '@/utils/request'

export const getUserList = (params) => {
  return request.get('/api/user/page', {
    params:params
  })
}