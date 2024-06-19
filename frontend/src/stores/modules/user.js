// import { userGetInfoService } from '@/api/user'
import { defineStore } from 'pinia'
import { ref } from 'vue'

// 用户模块
export const useUserStore = defineStore(
  'big-user',
  () => {
    const token = ref('') // 定义 token
    const setToken = (t) => (token.value = t)
    const user = ref({})
    const getUser = async () => {
      // const res = await userGetInfoService()
      // user.value = res.data.data
    }
    const setUser = (obj) => (user.value = obj)
    return { token, setToken, user, getUser, setUser }
  },
  {
    persist: true // 持久化
  }
)
