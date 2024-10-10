<script setup>
import { Delete, Edit, User } from '@element-plus/icons-vue'
import { ref } from 'vue'
import WarningSelect from './components/WarningSelect.vue'
import ClusterEdit from './components/ClusterEdit.vue'
import ResponseDelete from './components/ResponseDelete.vue'
import { artGetAllDevicesService, artDelService, artGetResponseService, artAddFile } from '@/api/Warning'
import router from '@/router'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const ClusterList = ref([])
const ClusterEditRef = ref()
const ResponseDeleteRef = ref()
const fileInputRef = ref()

const getClusterList = async () => {
  loading.value = true
  const res = await artGetAllDevicesService()
  ClusterList.value = res.data.data.filter(device => device.clusterId === 1)
  loading.value = false
}
getClusterList()

const onAddCluster = () => {
  ClusterEditRef.value.open({})
}
const onEditCluster = (row) => {
  ClusterEditRef.value.open(row)
}
const onDeleteCluster = async (row) => {
  await ElMessageBox.confirm('你确认删除该监控主机吗？', '温馨提示', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  })
  await artDelService(row.id)
  ElMessage({ type: 'success', message: '删除成功' })
  getClusterList()
}
const onEditResponse = async (row) => {
  const res = await artGetResponseService(row.id)
  const user = res.data.data.map(item => item.userId)
  ResponseDeleteRef.value.open(row.id, user)
}
const onSuccess = () => {
  getClusterList()
}
const onUpload = () => {
  fileInputRef.value.click()
}
const handleFile = async (e) => {
  const file = e.target.files[0]
  await artAddFile(file)
  ElMessage({ type: 'success', message: '添加成功' })
  getClusterList()
}
const rowClassName = ({ row }) => {
  return row.other.alert ? 'alert-row' : ''
}
const navigateToCluster = (hostId) => {
  router.push({ path: `/article/Cluster`, query: { clusterId: 1, hostId: hostId } })
}
</script>

<template>
  <page-container title="监控管理">
    <template #extra>
      <el-button @click="onAddCluster">添加监控对象</el-button>
      <el-button type="primary" @click="onUpload">上传Excel文件</el-button>
      <input type="file" ref="fileInputRef" @change="handleFile" style="display: none" accept=".xlsx,.xls"/>
    </template>
    <el-form inline>
      <el-form-item label="监控类型：">
        <WarningSelect></WarningSelect>
      </el-form-item>
      <el-form-item label="活跃情况：">
        <el-select>
          <el-option label="活跃中" value="活跃中"></el-option>
          <el-option label="休眠" value="休眠"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="onSearch" type="primary">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="ClusterList" style="width: 100%" v-loading="loading" :row-class-name="rowClassName">
      <el-table-column label="主机名" prop="name">
        <template #default="{ row }">
          <a class="link hover-blue" @click="navigateToCluster(row.id)">{{ row.name }}</a>
        </template>
      </el-table-column>
      <el-table-column prop="position" label="Position"></el-table-column>
      <el-table-column prop="type" label="Type"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button :icon="User" circle plain type="primary" @click="onEditResponse(row)"></el-button>
          <el-button :icon="Edit" circle plain type="primary" @click="onEditCluster(row)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" @click="onDeleteCluster(row)"></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <ClusterEdit ref="ClusterEditRef" @success="onSuccess"></ClusterEdit>
    <ResponseDelete ref="ResponseDeleteRef" @success="onSuccess"></ResponseDelete>
  </page-container>
</template>

<style lang="scss" scoped>
.el-table::v-deep(.alert-row) {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
.hover-blue:hover {
  color: skyblue;
  cursor: pointer;
}
</style>
