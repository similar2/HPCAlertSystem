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
