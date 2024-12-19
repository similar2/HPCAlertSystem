<template>
  <page-container title="监控管理">
    <template #extra>
      <el-button @click="onAddDevice">添加监控对象</el-button>
      <el-button type="primary" @click="onUpload">上传Excel文件</el-button>
      <input
        type="file"
        ref="fileInputRef"
        @change="handleFile"
        style="display: none"
        accept=".xlsx,.xls"
      />
    </template>
    <el-form inline>
      <!-- 集群选择框 -->
      <el-form-item label="选择集群：">
        <el-select
          v-model="selectedCluster"
          placeholder="请选择集群"
          @change="onClusterChange"
          style="width: 200px"
        >
          <el-option v-for="item in ClusterList" :key="item.id" :label="item.name" :value="item.id">
            <template #default>
              <span>{{ item.name }}</span>
              <el-button
                v-if="item.name !== '添加集群'"
                type="text"
                :icon="Delete"
                @click.stop="onDeleteClusterOption(item.id)"
                style="float: right; color: red"
              ></el-button>
            </template>
          </el-option>
          <!-- 添加集群项 -->
          <el-option label="添加集群" value="add">
            <span style="color: #409eff">添加集群</span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="监控类型：">
        <Warning-select></Warning-select>
      </el-form-item>
      <el-form-item label="活跃情况：">
        <el-select style="width: 200px" placeholder="请选择活跃情况">
          <el-option label="活跃中" value="活跃中"></el-option>
          <el-option label="休眠" value="休眠"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="onSearch" type="primary">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="DeviceList"
      style="width: 100%"
      v-loading="loading"
      :row-class-name="rowClassName"
    >
      <el-table-column label="主机名" prop="name">
        <template #default="{ row }">
          <a class="link hover-blue" @click="navigateToCluster(row.id)">{{ row.name }}</a>
        </template>
      </el-table-column>
      <el-table-column prop="position" label="Position"></el-table-column>
      <el-table-column prop="type" label="Type"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button
            :icon="User"
            circle
            plain
            type="primary"
            @click="onEditResponse(row)"
          ></el-button>
          <el-button
            :icon="Edit"
            circle
            plain
            type="primary"
            @click="onEditCluster(row)"
          ></el-button>
          <el-button
            :icon="Delete"
            circle
            plain
            type="danger"
            @click="onDeleteCluster(row)"
          ></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>

    <!-- 添加集群弹窗 -->
    <el-dialog v-model="addClusterDialogVisible" title="添加集群">
      <el-form>
        <el-form-item label="集群名称：" required>
          <el-input v-model="newClusterName" placeholder="请输入集群名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addClusterDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddCluster">确认</el-button>
      </template>
    </el-dialog>

    <Cluster-edit ref="ClusterEditRef" @success="onSuccess"></Cluster-edit>
    <Response-delete ref="ResponseDeleteRef" @success="onSuccess"></Response-delete>
  </page-container>
</template>

<script setup>
import { Delete, Edit, User } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
// import WarningSelect from './components/WarningSelect.vue'
import ClusterEdit from './components/ClusterEdit.vue'
import ResponseDelete from './components/ResponseDelete.vue'
import {
  artGetAllDevicesService,
  artDelService,
  artGetResponseService,
  artAddFile,
} from '@/api/Warning'
import {
  getClusterList,
  getClusterById,
  createCluster,
  updateCluster,
  deleteCluster
} from '@/api/cluster'
import router from '@/router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { c } from 'vite/dist/node/types.d-aGj9QkWt'

const loading = ref(false)
const ClusterList = ref([])
const DeviceList = ref([])
const ClusterEditRef = ref()
const ResponseDeleteRef = ref()
const fileInputRef = ref()
const currentClusterId = ref(0)
const selectedCluster = ref(null)
const addClusterDialogVisible = ref(false)
const newClusterName = ref('')

onMounted(async () => {
  const res = await getClusterList()
  ClusterList.value = res.data.data

  selectedCluster.value = ClusterList.value[0].name
  currentClusterId.value = ClusterList.value[0].id
  getDeviceList()
})

const getDeviceList = async () => {
  loading.value = true
  if (ClusterList.value.length === 0) {
    loading.value = false
    return
  }
  const res = await artGetAllDevicesService(currentClusterId.value)
  DeviceList.value = res.data.data
  loading.value = false
}

// 集群下拉框操作
const onClusterChange = (value) => {
  if (value === 'add') {
    addClusterDialogVisible.value = true
    selectedCluster.value = null
  } else {
    currentClusterId.value = value
    getDeviceList()
  }
}

const confirmAddCluster = async () => {
  if (!newClusterName.value.trim()) {
    ElMessage.warning('集群名称不能为空！')
    return
  }
  await createCluster(newClusterName.value.trim())
  const res = await getClusterList()
  console.log(res.data.data)
  ClusterList.value = res.data.data
  console.log(ClusterList.value)
  ElMessage.success('集群添加成功！')
  newClusterName.value = ''
  addClusterDialogVisible.value = false
}

const onDeleteClusterOption = async (id) => {
  await ElMessageBox.confirm('你确认删除该集群吗？', '温馨提示', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  })
  // const res = await deleteCluster(currentClusterId.value)
  // console.log(res)
  ClusterList.value = ClusterList.value.filter((item) => item.id !== id)
  ElMessage({ type: 'success', message: '集群删除成功' })
}

// 编辑新增逻辑
const onAddDevice = () => {
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
  getDeviceList()
}
const onEditResponse = async (row) => {
  const res = await artGetResponseService(row.id)
  const user = res.data.data.map((item) => item.userId)
  ResponseDeleteRef.value.open(row.id, user)
}
const onSuccess = () => {
  getDeviceList()
}
const onUpload = () => {
  fileInputRef.value.click()
}
const handleFile = async (e) => {
  const file = e.target.files[0]
  await artAddFile(file)
  ElMessage({ type: 'success', message: '添加成功' })
  getDeviceList()
}
const rowClassName = ({ row }) => {
  if (row.other.alert) {
    return 'alert-row'
  }
  return ''
}
const navigateToCluster = (hostId) => {
  router.push({
    path: `/article/Cluster`,
    query: { clusterId: currentClusterId.value, hostId: hostId }
  })
}
</script>

<style lang="scss" scoped>
.el-table::v-deep(.alert-row) {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}

.hover-blue:hover {
  color: skyblue;
  cursor: pointer;
}
</style>
