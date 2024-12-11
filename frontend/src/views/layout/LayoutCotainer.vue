    <script setup>
    import {
      Management,
      Promotion,
      UserFilled,
      User,
      Crop,
      EditPen,
      SwitchButton,
      CaretBottom
    } from '@element-plus/icons-vue'
    import { ElMessageBox, ElAvatar } from 'element-plus'
    import avatar from '@/assets/default.png'
    import { useUserStore } from '@/stores'
    import { onMounted } from 'vue'
    import router from '@/router'
    const userStore = useUserStore()
    import { ref } from 'vue'
    const clusters = ref([])
    clusters.value = [
      { id: 1, name: '太乙集群' },
      { id: 2, name: '启明集群' }
    ]
    onMounted(() => {
      // userStore.getUser()
      // fetchClusters()
    })
    // const fetchClusters = async () => {
    //   const res = await yourApi.getClusters()
    //   clusters.value = res.data
    // }

    const handleCommand = async (command) => {
      if (command !== 'logout') {
        router.push(`/user/${command}`)
      } else {
        await ElMessageBox.confirm('你确认退出大事件吗？', '温馨提示', {
          type: 'warning',
          confirmButtonText: '确认',
          cancelButtonText: '取消'
        })
        userStore.setUser({})
        userStore.setPermissions([])
        router.push('/login')
      }
    }

    console.log('userStore', userStore.user.value.id)
    </script>

    <template>
      <el-container class="layout-container">
        <el-aside width="200px">
          <div class="el-aside__logo"></div>
          <el-menu
              active-text-color="#ffd04b"
              background-color="#232323"
              :default-active="$route.path"
              text-color="#fff"
              router
          >
              <el-sub-menu index="/article/manage">
              <template #title>
                <el-icon><UserFilled /></el-icon>
                <span>监控数据</span>
              </template>
              <el-menu-item index="/article/manage">
                <el-icon><Management /></el-icon>
                <span>概要栏</span>
              </el-menu-item>
              <el-menu-item index="/article/detail" v-permission="'article:list'">
                <el-icon><Promotion /></el-icon>
                <span>问题列表</span>
              </el-menu-item>
              <el-sub-menu index="/article/observers">
                <template #title>
                  <el-icon><Promotion /></el-icon>
                  <span>监控集群</span>
                </template>
                <el-menu-item
                    v-for="cluster in clusters"
                    :key="cluster.id"
                    :index="`/article/observers${cluster.id}`"
                    @click="() => router.push(`/article/observers${cluster.id}`)"
                    v-permission="`cluster${cluster.id}:list`"
                >
                  <span>{{ cluster.name }}</span>
                </el-menu-item>
              </el-sub-menu>
                <el-sub-menu index="/article/auxiliary-monitoring">
                  <template #title>
                    <el-icon><Promotion /></el-icon>
                    <span>辅机监控</span>
                  </template>
                  <el-menu-item index="/article/auxiliary-monitoring/device" v-permission ="'aux_monitor:device'" >
                    <el-icon><Promotion /></el-icon>
                    <span>设备管理</span>
                  </el-menu-item>
                  <el-menu-item index="/article/auxiliary-monitoring/alert-management" v-permission ="'aux_monitor:alert'">
                    <el-icon><Promotion /></el-icon>
                    <span>告警管理</span>
                  </el-menu-item>
                  <el-menu-item index="/article/auxiliary-monitoring/statistics" v-permission ="'aux_monitor:alert'">
                    <el-icon><Promotion /></el-icon>
                    <span>数据分析</span>
                  </el-menu-item>
                </el-sub-menu>

                <el-menu-item index="/article/warning" v-permission="'rule:list'">
                <el-icon><Promotion /></el-icon>
                <span>自定义告警规则</span>
              </el-menu-item>
            </el-sub-menu>




            <el-sub-menu index="/userManager">
              <template #title>
                <el-icon><UserFilled /></el-icon>
                <span>用户权限</span>
              </template>
              <el-menu-item index="/userManager/user" v-permission="'user:list'">
                <el-icon><User /></el-icon>
                <span>用户列表</span>
              </el-menu-item>

              <el-menu-item index="/userManager/role" v-permission="'role:list'">
                <el-icon><User /></el-icon>
                <span>角色管理</span>
              </el-menu-item>
              <el-menu-item index="/userManager/permission" v-if="userStore.user.value.id === 1" >
                <el-icon><User /></el-icon>
                <span>权限管理</span>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="/user">
              <template #title>
                <el-icon><UserFilled /></el-icon>
                <span>个人中心</span>
              </template>
              <el-menu-item index="/user/profile">
                <el-icon><User /></el-icon>
                <span>基本资料</span>
              </el-menu-item>
              <!-- <el-menu-item index="/user/avatar">
                <el-icon><Crop /></el-icon>
                <span>更换头像</span>
              </el-menu-item> -->
              <el-menu-item index="/user/password">
                <el-icon><EditPen /></el-icon>
                <span>重置密码</span>
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>
        <el-container>
          <el-header>
            <div>
              程序员：<strong>{{
                userStore.user.nickname || userStore.user.username
              }}</strong>
            </div>
            <el-dropdown placement="bottom-end" @command="handleCommand">
              <span class="el-dropdown__box">
                <el-avatar :src="userStore.user.user_pic || avatar" />
                <el-icon><CaretBottom /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile" :icon="User"
                  >基本资料</el-dropdown-item
                  >
                  <el-dropdown-item command="avatar" :icon="Crop"
                  >更换头像</el-dropdown-item
                  >
                  <el-dropdown-item command="password" :icon="EditPen"
                  >重置密码</el-dropdown-item
                  >
                  <el-dropdown-item command="logout" :icon="SwitchButton"
                  >退出登录</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-header>
          <el-main>
            <router-view></router-view>
          </el-main>
          <el-footer>监控系统 ©2023 Created by black</el-footer>
        </el-container>
      </el-container>
    </template>

    <style lang="scss" scoped>
    .layout-container {
      height: 100vh;
      .el-aside {
        background-color: #232323;
        &__logo {
          height: 120px;
          background: url('@/assets/logo.png') no-repeat center / 120px auto;
        }
        .el-menu {
          border-right: none;
        }
      }
      .el-header {
        background-color: #fff;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .el-dropdown__box {
          display: flex;
          align-items: center;
          .el-icon {
            color: #999;
            margin-left: 10px;
          }

          &:active,
          &:focus {
            outline: none;
          }
        }
      }
      .el-footer {
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        color: #666;
      }
    }
    </style>