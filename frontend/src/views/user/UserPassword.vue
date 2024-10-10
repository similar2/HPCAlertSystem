<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores'
import { userUpdatePassService } from '@/api/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const formRef = ref()
const router = useRouter()
const userStore = useUserStore()

const pwdForm = ref({
  old_pwd: '',
  new_pwd: '',
  re_pwd: ''
})

const checkOldSame = (rule, value, cb) => {
  if (value === pwdForm.value.new_pwd) {
    cb(new Error('原密码和新密码不能相同!'))
  } else {
    cb()
  }
}

const checkNewSame = (rule, value, cb) => {
  if (value !== pwdForm.value.new_pwd) {
    cb(new Error('新密码与确认新密码不一致!'))
  } else {
    cb()
  }
}

const rules = {
  old_pwd: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    {
      pattern: /^\S{6,15}$/,
      message: '密码长度必须是6-15位的非空字符串',
      trigger: 'blur'
    }
  ],
  new_pwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    {
      pattern: /^\S{6,15}$/,
      message: '密码长度必须是6-15位的非空字符串',
      trigger: 'blur'
    },
    { validator: checkOldSame, trigger: 'blur' }
  ],
  re_pwd: [
    { required: true, message: '请再次确认新密码', trigger: 'blur' },
    {
      pattern: /^\S{6,15}$/,
      message: '密码长度必须是6-15位的非空字符串',
      trigger: 'blur'
    },
    { validator: checkNewSame, trigger: 'blur' }
  ]
}

const onSubmit = async () => {
  const valid = await formRef.value.validate()
  if (valid) {
    await userUpdatePassService(pwdForm.value)
    ElMessage({ type: 'success', message: '更换密码成功' })
    userStore.setToken('')
    userStore.setUser({})
    router.push('/login')
  }
}

const onReset = () => {
  formRef.value.resetFields()
}
</script>

<template>
  <page-container title="重置密码">
    <el-row justify="center" align="middle" class="reset-password-row">
      <el-col :span="8" class="reset-password-col">
        <el-form
            :model="pwdForm"
            :rules="rules"
            ref="formRef"
            label-width="100px"
            size="large"
            class="reset-password-form"
        >
          <el-form-item label="原密码" prop="old_pwd">
            <el-input v-model="pwdForm.old_pwd" type="password" prefix-icon="el-icon-lock" class="input-field"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="new_pwd">
            <el-input v-model="pwdForm.new_pwd" type="password" prefix-icon="el-icon-lock" class="input-field"></el-input>
          </el-form-item>
          <el-form-item label="确认新密码" prop="re_pwd">
            <el-input v-model="pwdForm.re_pwd" type="password" prefix-icon="el-icon-lock" class="input-field"></el-input>
          </el-form-item>
          <el-form-item>
            <div class="button-group">
              <el-button @click="onSubmit" type="primary" class="submit-button">修改密码</el-button>
              <el-button @click="onReset" class="reset-button">重置</el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </page-container>
</template>

<style scoped>
.reset-password-row {
  margin-top: 50px;
}

.reset-password-col {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.reset-password-form {
  max-width: 400px;
  margin: auto;
}

.input-field {
  border-radius: 4px;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.1);
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.submit-button {
  width: 48%;
  background-color: #409eff;
  color: white;
  border: none;
}

.reset-button {
  width: 48%;
  background-color: #f56c6c;
  color: white;
  border: none;
}

.reset-button:hover {
  background-color: #f78989;
}

.submit-button:hover {
  background-color: #66b1ff;
}
</style>
