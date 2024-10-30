// import { userGetInfoService } from '@/api/user'
import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'

// 用户模块
export const useUserStore = defineStore(
  'big-user',
  () => {
    // 用户基本信息
    const user = reactive({})
    const setUser = (obj) => (user.value = obj)

    // 用户权限信息
    const permissions = ref([])
    const setPermissions = (arr) => (permissions.value = arr)
    return { user, setUser, permissions, setPermissions }
  },
  {
    persist: true // 持久化
  }
)
