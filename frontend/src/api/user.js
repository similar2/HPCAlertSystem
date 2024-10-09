import request from '@/utils/request'

export const userRegisterService = ({
  email,
  username,
  password,
  verifyCode
}) => {
  // console.log(email, username, password, verifyCode)
  return request.post('/api/user/register', {
    email,
    username,
    password,
    verifyCode
  })
}
export const userLoginService = ({ email, password }) => {
  return request.post('/api/user/login', { email, password })
}
export const userSendVerifyCode = (email) => {
  return request.post(`/api/user/send-verify-code?email=${email}`)
}
export const userGetInfoService = () => request.get('/my/userinfo')
export const userUpdateInfoService = ({ id, nickname, email }) =>
  request.put('/my/userinfo', { id, nickname, email })
export const userUploadAvatarService = (avatar) =>
  request.patch('/my/update/avatar', { avatar })
export const userUpdatePassService = ({ old_pwd, new_pwd, re_pwd }) =>
  request.patch('/my/updatepwd', { old_pwd, new_pwd, re_pwd })
export const userGetAll = () => request.get('/api/user/all')
export const userGet = (id) => request.get(`/api/user/all?user_id=${id}`)

export const getUserList = (params) => {
  return request.get('/api/user/page', {
    params:params
  })}
export const enableOrDisableUser = (params) => {
  return request.post( `/api/user/status/${params.status}?id=${params.id}`)}
export const addUser = (params) =>{
  return request.post('/api/user', params)
}
export const queryUserById = (id) => {
  return request.get(`/api/user/${id}`)
}
export const updateUser = (params)=>{
  return request.put('/api/user', params)
}
export const getByEmail = (email) => {
  return request.get(`/api/user/getByEmail/${email}`)
}
export const deleteUser = (id) => {
  return request.delete(`/api/user/${id}`)
}