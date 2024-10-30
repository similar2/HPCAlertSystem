import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'

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
          component: () => import('@/views/article/WarningManage.vue')
        },
        {
          path: '/article/cluster',
          component: () => import('@/views/article/ClusterManage.vue')
        },
        // {
        //   path: '/article/machine',
        //   component: () => import('@/views/article/MachineManage.vue')
        // },
        {
          path: '/article/detail',
          component: () => import('@/views/article/WarningDetail.vue')
        },
        {
          path: '/article/observers1',
          component: () => import('@/views/article/ClusterObservers1.vue')
        },
        {
          path: '/article/observers2',
          component: () => import('@/views/article/ClusterObservers2.vue')
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
          component: () => import('@/views/userManager/user.vue')
        },
        {
          path: '/userManager/role',
          component: () => import('@/views/userManager/permission.vue')
        },
        // {
        //   path: '/userManager/add',
        //   component: () => import('@/views/userManager/addUser.vue'),
        //   meta: {
        //     title: '添加/修改用户'
        //     // hidden: true
        //   }
        // }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (!userStore.token && to.path !== '/login') return '/login'
})

export default router
