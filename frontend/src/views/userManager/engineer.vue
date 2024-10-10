<template>
  <div class="page-container">
    <el-card class="container" shadow="hover">
      <div class="tableBar">
        <el-row>
          <el-col :span="6">
            <label>工程师姓名:</label>
            <el-input
                clearable
                v-model="name"
                placeholder="请输入查询的姓名"
                @clear="pageQuery"
            />
          </el-col>
          <el-col :span="6">
            <label>邮箱:</label>
            <el-input
                clearable
                v-model="email"
                placeholder="请输入查询的邮箱号"
                @clear="pageQuery"
            />
          </el-col>
          <el-col :span="6">
            <label>电话:</label>
            <el-input
                clearable
                v-model="phone"
                placeholder="请输入查询的电话号"
                @clear="pageQuery"
            />
          </el-col>
          <el-col :span="6" class="button-group">
            <el-button type="primary" @click="pageQuery">查询</el-button>
            <el-button type="success" @click="handleAddUser">+ 添加运维工程师</el-button>
          </el-col>
        </el-row>
      </div>
      <el-table :data="records" stripe style="width: 100%">
        <el-table-column prop="name" label="工程师姓名" width="280"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="300"></el-table-column>
        <el-table-column prop="phone" label="电话" width="300"></el-table-column>
        <el-table-column label="账号状态" width="180">
          <template #default="scope">
            <el-badge
                :value="scope.row.status === 1 ? '启用' : '禁用'"
                :is-dot="scope.row.status === 0"
                class="status-badge"
                :type="scope.row.status === 1 ? 'success' : 'danger'"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleUpdateUser(scope.row)">修改</el-button>
            <el-button size="small" type="warning" @click="handleStartOrStop(scope.row)">
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteUser(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
            class="pageList"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="page"
            :page-sizes="[10, 20, 30, 40, 50]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getUserList, enableOrDisableUser, deleteUser } from '@/api/user'
import router from '@/router'
import { ElMessage, ElMessageBox } from 'element-plus'

onMounted(() => {
  pageQuery()
})

const name = ref('')
const email = ref('')
const phone = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const records = reactive([])

const pageQuery = async () => {
  const params = {
    name: name.value,
    email: email.value,
    phone: phone.value,
    page: page.value,
    pageSize: pageSize.value,
    role: 1
  }
  getUserList(params)
      .then((res) => {
        if (res.data.code == 200) {
          total.value = res.data.data.total
          records.length = 0
          Object.assign(records, res.data.data.records)
        }
      })
      .catch((err) => {
        ElMessage.error('请求出错了：' + err.message)
      })
}
const handleSizeChange = (newPageSize) => {
  pageSize.value = newPageSize
  pageQuery()
}
const handleCurrentChange = (newPage) => {
  page.value = newPage
  pageQuery()
}

const handleStartOrStop = async (row) => {
  await ElMessageBox.confirm('确认要修改当前工程师账号的状态吗?', '温馨提示', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  }).then(() => {
    const p = {
      id: row.id,
      status: !row.status ? 1 : 0
    }
    enableOrDisableUser(p).then((res) => {
      if (res.data.code == 200) {
        ElMessage.success('账号状态修改成功！')
        pageQuery()
      } else {
        ElMessage.error('操作失败：' + res.data.message)
      }
    })
  })
}

const handleAddUser = () => {
  router.push({
    path: '/userManager/add',
    query: { role: 1 }
  })
}
const handleUpdateUser = (row) => {
  router.push({
    path: '/userManager/add',
    query: { id: row.id, role: 1 }
  })
}
const handleDeleteUser = (row) => {
  ElMessageBox.confirm('确认要删除当前运维工程师账号吗?', '温馨提示', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  }).then(() => {
    deleteUser(row.id).then((res) => {
      if (res.data.code == 200) {
        ElMessage.success('账号删除成功！')
        pageQuery()
      } else {
        ElMessage.error('操作失败：' + res.data.message)
      }
    })
  })
}
</script>

<style>
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.container {
  background: #fff;
  position: relative;
  z-index: 1;
  padding: 30px 28px;
  border-radius: 4px;
}
.tableBar {
  margin-bottom: 20px;
}
.button-group {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}
.status-badge {
  margin-right: 5px;
}
</style>
