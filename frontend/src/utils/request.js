import axios from 'axios'
import { useUserStore } from '@/stores'
import { ElMessage } from 'element-plus'
import router from '@/router'

const baseURL = 'http://localhost:8080/'

const instance = axios.create({
  baseURL,
  timeout: 10000,
  headers: { 'content-type': 'application/json' }
})

instance.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.user?.value?.token) {
      config.headers.Authorization = userStore.user.value.token
    }
    return config
  },
  (err) => Promise.reject(err)
)

instance.interceptors.response.use(
  (res) => {
    if (res.data.code === 200) {
      return res
    }
    ElMessage.error(res.data.message || '服务异常')
    return Promise.reject(res.data)
  },
  (err) => {
    if (err.response?.status === 401) {
      router.push('/login')
    }
    ElMessage.error(err.response.data.message || '服务异常')
    return Promise.reject(err)
  }
)

export default instance
export { baseURL }
