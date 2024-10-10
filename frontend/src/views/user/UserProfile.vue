<script setup>
import { useUserStore } from '@/stores'
import { ref } from 'vue'
import { userUpdateInfoService } from '@/api/user'
import { ElMessage, ElCard, ElAvatar, ElDivider, ElTooltip, ElUpload } from 'element-plus'

const {
  user: { username, nickname, email, id, avatar } // 获取头像 URL
} = useUserStore()
const userStore = useUserStore()
const userInfo = ref({ username, nickname, email, id })
const avatarUrl = ref(avatar || 'https://i.pravatar.cc/300') // 默认头像 URL
const formRef = ref()

const rules = {
  nickname: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
    {
      pattern: /^\S{2,10}$/,
      message: '昵称必须是2-10位的非空字符串',
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入用户邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

// 提交用户信息
const onSubmit = async () => {
  const valid = await formRef.value.validate()
  if (valid) {
    await userUpdateInfoService(userInfo.value)
    await userStore.getUser()
    ElMessage.success('修改成功')
  }
}

// 处理头像上传
const handleAvatarUpload = async (file) => {
  const reader = new FileReader()
  reader.onload = async (e) => {
    avatarUrl.value = e.target.result  // 更新头像 URL
    // 这里可以调用 API 上传头像文件
    const formData = new FormData()
    formData.append('avatar', file.raw)

    try {
      await uploadAvatarService(formData)  // 这里调用你的头像上传服务
      ElMessage.success('头像更换成功')
      await userStore.getUser()  // 更新用户信息
    } catch (error) {
      ElMessage.error('头像更换失败，请重试')
    }
  }
  reader.readAsDataURL(file.raw)  // 读取文件
  return false  // 阻止上传
}

// 模拟头像上传的服务函数
const uploadAvatarService = async (formData) => {
  // 这里需要实现你的 API 调用逻辑
  // 例如：
  // return await axios.post('/api/user/upload-avatar', formData)
}
</script>

<template>
  <page-container title="基本资料">
    <el-row justify="center" align="middle">
      <el-col :span="12" :md="8">
        <el-card shadow="hover" class="user-card">
          <div class="avatar-container">
            <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :before-upload="handleAvatarUpload"
                accept="image/*"
            >
              <el-avatar size="100" :src="avatarUrl" class="user-avatar" />
              <el-tooltip class="item" effect="dark" content="点击更换头像" placement="bottom">
                <span class="avatar-overlay"></span>
              </el-tooltip>
            </el-upload>
          </div>
          <el-divider></el-divider>
          <el-form
              :model="userInfo"
              :rules="rules"
              ref="formRef"
              label-width="100px"
              size="large"
              class="user-form"
          >
            <el-form-item label="登录名称">
              <el-input v-model="userInfo.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="用户昵称" prop="nickname">
              <el-input v-model="userInfo.nickname" placeholder="请输入您的昵称"></el-input>
            </el-form-item>
            <el-form-item label="用户邮箱" prop="email">
              <el-input v-model="userInfo.email" placeholder="请输入您的邮箱"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="onSubmit" style="width: 100%">提交修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </page-container>
</template>

<style>
.user-card {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.user-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

.avatar-container {
  position: relative;
  text-align: center;
  margin-bottom: 15px;
}

.user-avatar {
  display: block;
  margin: 0 auto;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  transition: background-color 0.3s;
}

.avatar-overlay:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

.user-form {
  margin-top: 20px;
}

.el-divider {
  margin: 10px 0;
}

.avatar-uploader {
  cursor: pointer;
}
</style>
