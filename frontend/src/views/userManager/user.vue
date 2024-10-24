<template>
  <div class="app-container">
    <div class="filter-container">
      <el-form>
        <el-form-item>
          <el-button type="primary" v-permission="'user:add'" @click="showCreate">添加 </el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="list" v-loading="listLoading" border fit highlight-current-row>
      <el-table-column align="center" label="序号" width="80">
        <template v-slot="{ $index }">
          <span v-text="getIndex($index)"> </span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="姓名"
        prop="name"
        style="width: 60px"
      ></el-table-column>
      <el-table-column
        align="center"
        label="邮箱"
        prop="email"
        style="width: 60px"
      ></el-table-column>
      <el-table-column
        align="center"
        label="电话"
        prop="phone"
        style="width: 60px"
      ></el-table-column>
      <el-table-column align="center" label="角色" width="200">
        <template v-slot="{ row }">
          <div
            style="margin-right: 4px; display: inline-block"
            v-for="(i, index) in row.roles"
            :key="index"
          >
            <el-tag type="success" v-if="i.id === 1">{{ i.roleName }}</el-tag>
            <el-tag type="primary" v-else>{{ i.roleName }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="创建时间"
        prop="createTime"
        width="170"
      ></el-table-column>
      <el-table-column
        align="center"
        label="最近修改时间"
        prop="updateTime"
        width="170"
      ></el-table-column>
      <el-table-column align="center" label="管理" width="220">
        <template v-slot="{ row, $index }">
          <el-button type="primary" @click="showUpdate($index)" v-permission="'user:update'"
            >修改</el-button
          >
          <el-button
            type="danger"
            v-if="row.id !== id"
            @click="removeUser($index)"
            v-permission="'user:update'"
            >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination
        class="pageList"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="listQuery.pageNum"
        :page-sizes="[10, 20, 30, 40, 50]"
        :page-size="listQuery.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount"
      >
      </el-pagination>
    </div>
    <el-dialog :title="textMap[dialogStatus]" v-model="dialogFormVisible">
      <el-form
        class="small-space"
        :model="tempUser"
        :rules="rules"
        label-position="left"
        label-width="80px"
        style="width: 300px; margin-left: 50px"
      >
        <el-form-item label="姓名" required prop="name">
          <el-input type="text" v-model="tempUser.name"> </el-input>
        </el-form-item>
        <el-form-item label="邮箱" required prop="email">
          <el-input type="text" v-model="tempUser.email"> </el-input>
        </el-form-item>
        <el-form-item label="电话" required prop="phone">
          <el-input type="text" v-model="tempUser.phone"> </el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="dialogStatus === 'create'" required prop="password">
          <el-input type="password" v-model="tempUser.password"> </el-input>
        </el-form-item>
        <el-form-item label="新密码" v-else>
          <el-input type="password" v-model="tempUser.password" placeholder="不填则表示不修改">
          </el-input>
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select
            v-model="tempUser.roleIds"
            multiple
            placeholder="支持多角色"
            style="width: 300px"
          >
            <el-option v-for="item in roles" :key="item.id" :label="item.roleName" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button v-if="dialogStatus === 'create'" type="success" @click="createUser"
            >创 建</el-button
          >
          <el-button type="primary" v-else @click="updateUser">修 改</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, getRoleList, addUser, updateUser, deleteUser, getByEmail } from '@/api/user'
export default {
  data() {
    return {
      initEmail: '',
      rules: {
        name: [{ required: true, message: '请填写姓名', trigger: 'blur' }],
        password: [
          {
            required: true,
            trigger: 'blur',
            validator: (rule, value, callback) => {
              const passwordPattern = /^(?=.{8,20}$)(?!.*\s).*$/ // 正则表达式：8-20位，不能包含空格
              if (value === '') {
                callback(new Error('请输入初始密码'))
              } else if (!passwordPattern.test(value)) {
                callback(new Error('密码长度须在 8 到 20 位之间，无空格'))
              } else {
                callback()
              }
            }
          }
        ],
        phone: [
          {
            required: true,
            trigger: 'blur',
            validator: (rule, value, callback) => {
              if (value === '') {
                callback(new Error('请填写电话！'))
              } else if (value && !/^1(3|4|5|6|7|8)\d{9}$/.test(value)) {
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
              if (value === '') {
                callback(new Error('请填写邮箱！'))
              } else if (!emailPattern.test(value)) {
                callback(new Error('请输入正确的邮箱地址！'))
              } else {
                getByEmail(value).then((res) => {
                  if (res.data.code === 200) {
                    if (res.data.data.email == null) {
                      callback()
                    } else if (res.data.data.email == value) {
                      if (this.dialogStatus === 'update' && this.initEmail === value) {
                        callback()
                      }
                      callback(new Error('邮箱已被注册，请使用其他邮箱！'))
                    }
                    callback()
                  }
                })
              }
            }
          }
        ]
      },
      totalCount: 0, //分页组件--数据总条数
      list: [],
      listLoading: false,
      listQuery: {
        pageNum: 1, //页码
        pageSize: 10 //每页条数
      },
      roles: [], //角色列表
      dialogStatus: 'create',
      dialogFormVisible: false,
      textMap: {
        update: '编辑',
        create: '新建用户'
      },
      tempUser: {
        name: '',
        phone: '',
        email: '',
        password: '',
        name: '',
        roleIds: [],
        id: ''
      }
    }
  },
  created() {
    this.getList()
    // if (this.hasPerm('user:add') || this.hasPerm('user:update')) {
    this.getAllRoles()
    // }
  },
  methods: {
    getAllRoles() {
      getRoleList().then((res) => {
        this.roles = res.data.data
      })
      // this.api({
      //   url: "/user/getAllRoles",
      //   method: "get"
      // }).then(data => {
      //   this.roles = data.list;
      // })
    },
    getList() {
      //查询列表
      this.listLoading = true
      getUserList(this.listQuery).then((res) => {
        this.listLoading = false
        this.list = res.data.data.records
        this.totalCount = res.data.data.total
      })
      // this.api({
      //   url: "/user/list",
      //   method: "get",
      //   params: this.listQuery
      // }).then(data => {
      //   this.listLoading = false;
      //   this.list = data.list;
      //   this.totalCount = data.totalCount;
      // })
    },
    handleSizeChange(val) {
      //改变每页数量
      this.listQuery.pageSize = val
      this.handleFilter()
    },
    handleCurrentChange(val) {
      //改变页码
      this.listQuery.pageNum = val
      this.getList()
    },
    handleFilter() {
      //查询事件
      this.listQuery.pageNum = 1
      this.getList()
    },
    getIndex($index) {
      //表格序号
      return (this.listQuery.pageNum - 1) * this.listQuery.pageSize + $index + 1
    },
    showCreate() {
      //显示新增对话框
      this.tempUser.email = ''
      this.tempUser.password = ''
      this.tempUser.name = ''
      this.tempUser.phone = ''
      this.tempUser.roleIds = []
      this.tempUser.id = ''
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
    },
    showUpdate($index) {
      let user = this.list[$index]
      this.tempUser.email = user.email
      this.initEmail = user.email
      this.tempUser.name = user.name
      this.tempUser.phone = user.phone
      this.tempUser.roleIds = user.roles.map((x) => x.id)
      this.tempUser.id = user.id
      this.tempUser.password = ''
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
    },
    validate(isCreate) {
      let u = this.tempUser
      if (isCreate) {
        if (isCreate && u.name.trim().length === 0) {
          this.$message.warning('请填写姓名')
          return false
        }
        if (isCreate && u.email.trim().length === 0) {
          this.$message.warning('请填写邮箱')
          return false
        }
        if (isCreate && u.phone.trim().length === 0) {
          this.$message.warning('请填写电话')
          return false
        }
        if (isCreate && u.password.length === 0) {
          this.$message.warning('请填写密码')
          return false
        }
      }
      if (isCreate && u.name.trim().length === 0) {
        this.$message.warning('请填写姓名')
        return false
      }
      if (isCreate && u.email.trim().length === 0) {
        this.$message.warning('请填写邮箱')
        return false
      }
      if (isCreate && u.phone.trim().length === 0) {
        this.$message.warning('请填写电话')
        return false
      }
      if (isCreate && u.password.length === 0) {
        this.$message.warning('请填写密码')
        return false
      }
      if (u.name.trim().length === 0) {
        this.$message.warning('请填写姓名')
        return false
      }
      if (u.roleIds.length === 0) {
        this.$message.warning('请选择角色')
        return false
      }
      return true
    },
    createUser() {
      if (!this.validate(true)) return
      this.tempUser.deleted = 0
      addUser(this.tempUser).then((res) => {
        this.$message.success('添加成功')
        this.getList()
        this.dialogFormVisible = false
      })
    },
    updateUser() {
      if (!this.validate(false)) return
      updateUser(this.tempUser).then((res) => {
        this.$message.success('修改成功')
        this.getList()
        this.dialogFormVisible = false
      })
    },
    removeUser($index) {
      // let _vue = this;
      this.$confirm('确定删除此用户?', '提示', {
        confirmButtonText: '确定',
        showCancelButton: false,
        type: 'warning'
      }).then(() => {
        deleteUser(this.list[$index].id)
          .then((res) => {
            this.$message.success('删除成功')
            this.getList()
          })
          .catch(() => {
            this.$message.error('删除失败')
          })
      })
    }
  }
}
</script>

<style>
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>