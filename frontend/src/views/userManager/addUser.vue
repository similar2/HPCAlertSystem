<template>
  <div class="addBrand-container">
    <div class="container">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="180px">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="ruleForm.email"></el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="name">
          <el-input v-model="ruleForm.name"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="ruleForm.password"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="ruleForm.phone"></el-input>
        </el-form-item>
        <div class="subBox">
          <el-button type="primary" @click="submitForm('ruleForm', false)">保存</el-button>
          <el-button
            v-if="this.optType === 'add'"
            type="primary"
            @click="submitForm('ruleForm', true)"
            >保存并继续添加用户
          </el-button>
          <el-button @click="back">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>
  
  <script lang="ts">
import { addUser, queryUserById, updateUser, getByEmail } from '@/api/user'
export default {
  data() {
    return {
      path: '',
      optType: '', //当前操作的类型，新增或者修改
      ruleForm: {
        role: '',
        name: '',
        password: '',
        phone: '',
        email: ''
      },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        password: [
          {
            required: true,
            trigger: 'blur',
            validator: (rule, value, callback) => {
              const passwordPattern = /^(?=.{8,20}$)(?!.*\s).*$/ // 正则表达式：8-20位，不能包含空格
              if (value === '') {
                callback(new Error('请输入初始密码'))
              } else if (!passwordPattern.test(value)) {
                callback(new Error('密码长度必须在 8 到 20 位之间，且不能包含空格'))
              } else {
                callback()
              }
            }
          }
        ],
        phone: [
          {
            required: false,
            trigger: 'blur',
            validator: (rule, value, callback) => {
              if (value && !/^1(3|4|5|6|7|8)\d{9}$/.test(value)) {
                callback(new Error('请输入正确的手机号！'))
              } else {
                callback()
              }
            }
          }
        ],
        email: [
          {
            required: true,
            trigger: 'blur',
            validator: (rule, value, callback) => {
              const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
              if (value === '' || !emailPattern.test(value)) {
                callback(new Error('请输入正确的邮箱地址！'))
              } else {
                getByEmail(value).then((res) => {
                  if (res.data.code === 200) {
                    console.log(res.data.data)
                    if(res.data.data.email == null){
                        callback()
                    }else if(res.data.data.email == value){
                        callback(new Error('邮箱已被注册，请使用其他邮箱！'))
                    }
                    callback()
                  }
                })
              }
            }
          }
        ]
      }
    }
  },
  created() {
    //获取路由参数（id），如果有则为修改操作，否则为新增操作
    this.optType = this.$route.query.id ? 'update' : 'add'
    this.ruleForm.role = this.$route.query.role
    if (this.optType === 'update') {
      //修改操作，需要根据id查询用户信息用于页面回显
      queryUserById(this.$route.query.id).then((res) => {
        if (res.data.code === 200) {
          this.ruleForm = res.data.data
          this.ruleForm.password = '***************'
        }
      })
    }
  },
  methods: {
    submitForm(formName, isContinue) {
      //进行表单校验
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.optType === 'add') {
            //新增操作
            addUser(this.ruleForm).then((res) => {
              if (res.data.code === 200) {
                ElMessage.success('用户添加成功！')
                if (isContinue) {
                  this.ruleForm = {
                    name: '',
                    password: '',
                    sex: '1',
                    phone: '',
                    email: ''
                  }
                } else {
                  this.$router.push(
                    this.ruleForm.role == 2 ? '/userManager/systemManager' : '/userManager/engineer'
                  )
                }
              } else {
                ElMessage.error(res.data.message)
              }
            })
          } else {
            if (this.ruleForm.password != '' && this.ruleForm.password != '***************') {
              ElMessageBox.confirm('确认要修改当前用户账号的密码吗?', '温馨提示', {
                type: 'warning',
                confirmButtonText: '确认',
                cancelButtonText: '取消'
              }).then(() => {
                const p = {
                  id: this.$route.query.id,
                  name: this.ruleForm.name,
                  phone: this.ruleForm.phone,
                  email: this.ruleForm.email,
                  role: this.ruleForm.role,
                  password: this.ruleForm.password
                }
                //修改密码
                updateUser(p).then((res) => {
                  if (res.data.code === 200) {
                    ElMessage.success('用户信息修改成功！')
                    this.$router.push(
                      this.ruleForm.role == 2
                        ? '/userManager/systemManager'
                        : '/userManager/engineer'
                    )
                  }
                })
              })
            } else {
              ElMessageBox.confirm('确认要修改当前用户的信息吗?', '温馨提示', {
                type: 'warning',
                confirmButtonText: '确认',
                cancelButtonText: '取消'
              }).then(() => {
                const p = {
                  id: this.$route.query.id,
                  name: this.ruleForm.name,
                  phone: this.ruleForm.phone,
                  email: this.ruleForm.email,
                  role: this.ruleForm.role
                }
                //修改操作
                updateUser(p).then((res) => {
                  if (res.data.code === 200) {
                    ElMessage.success('用户信息修改成功！')
                    this.$router.push(
                      this.ruleForm.role == 2
                        ? '/userManager/systemManager'
                        : '/userManager/engineer'
                    )
                  }
                })
              })
            }
          }
        }
      })
    },
    back() {
      this.$router.push(
        this.ruleForm.role == 2 ? '/userManager/systemManager' : '/userManager/engineer'
      )
    }
  }
}
</script>
  
  <style lang="scss" scoped>
.addBrand {
  &-container {
    margin: 30px;
    margin-top: 30px;
    .HeadLable {
      background-color: transparent;
      margin-bottom: 0px;
      padding-left: 0px;
    }
    .container {
      position: relative;
      z-index: 1;
      background: #fff;
      padding: 30px;
      border-radius: 4px;
      // min-height: 500px;
      .subBox {
        padding-top: 30px;
        text-align: center;
        border-top: solid 1px;
      }
    }
    .email {
      margin-bottom: 39px;
    }

    .el-form-item {
      margin-bottom: 29px;
    }
    .el-input {
      width: 293px;
    }
  }
}
</style>
  