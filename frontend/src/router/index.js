import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'
import { getPermissionsByUserId } from '@/api/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      component: () => import('@/views/login/LoginPage.vue')
    },
    {
      path: '/',
      component: () => import('@/views/layout/LayoutCotainer.vue'),
      redirect: '/article/manage',
      children: [
        {
          path: '/article/manage',
          component: () => import('@/views/article/DashBoard.vue')
        },
        {
          path: '/article/detail',
          component: () => import('@/views/article/WarningDetail.vue')
        },
        {
          path: '/article/observers',
          component: () => import('@/views/article/ClusterObservers.vue')
        },
        {
          path: '/article/warning',
          component: () => import('@/views/article/UserOwnWarning.vue')
        },
        {
          path: '/article/update',
          component: () => import('@/views/user/UserUpdate.vue')
        },
        {
          path: '/user/profile',
          component: () => import('@/views/user/UserProfile.vue')
        },
        {
          path: '/user/avatar',
          component: () => import('@/views/user/UserAvatar.vue')
        },
        {
          path: '/user/password',
          component: () => import('@/views/user/UserPassword.vue')
        },
        {
          path: '/userManager/user',
          component: () => import('@/views/userManager/user.vue'),
          meta: {
            requiresAuth : true ,
            requiredPermission: 'user:list'
          }
        },
        {
          path: '/userManager/role',
          component: () => import('@/views/userManager/role.vue'),
          meta: {
            requiresAuth : true ,
            requiredPermission: 'role:list'
          }
        },
        {
          path: '/userManager/permission',
          //name: 'Permission',
          component: () => import('@/views/userManager/permission.vue'),
          meta: { permission: 'permission:list' }
        },
        {
          path: '/article/auxiliary-monitoring/device',
          component: () => import('@/views/article/components/DeviceManagement.vue'),
          meta: {
            name: 'DeviceManagement',
            requiresAuth: true,
            //requiredPermission: 'device:list',
          },
        },
        {
          path: '/article/auxiliary-monitoring/alert-management',
          component: () => import('@/views/article/components/AlertManagement.vue'),
          meta: {
            name: 'AlertManagement',
            requiresAuth: true,
            //requiredPermission: 'alert:manage',
          },
        },
        {
          path: '/article/auxiliary-monitoring/statistics',
          component: () => import('@/views/article/components/Statistics.vue'),
          meta: {
            requiresAuth: true,
            //requiredPermission: 'alert:manage',
          },
        }

      ]
    },
    {
      path: '/404',
      component: () => import('@/views/404.vue'), // 404 页面组件
    },
    {path: '/:catchAll(.*)', redirect: '/404', hidden: true},
  ]
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (!userStore?.user?.value?.token && to.path !== '/login') return '/login'

  if (userStore?.user?.value?.id){
    getPermissionsByUserId(userStore.user.value.id).then(res => {
      userStore.setPermissions(res.data.data)
  })}
  //if (to.meta.requiresAuth && !userStore.permissions.includes(to.meta.requiredPermission)) return '/404'
})

export default router
