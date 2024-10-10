<script setup>
import { Delete, Edit } from '@element-plus/icons-vue'
import { ref, onMounted, watch } from 'vue'
import WarningSelect from './components/WarningSelect.vue'
import MachineEdit from './components/TargetEdit.vue'
import { artGetAllTargetService, artDelTargetService } from '@/api/Warning'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const machineList = ref([])
const filteredMachineList = ref([])
const machineEditRef = ref()
const route = useRoute()
const clusterId = route.query.clusterId
const hostId = route.query.hostId

const activeStatus = ref('活跃中')

const getMachineList = async () => {
  loading.value = true
  try {
    const res = await artGetAllTargetService()
    machineList.value = JSON.parse(res.data.data).filter(
        (item) => item.clusterInfo.id == clusterId && item.deviceInfo.id == hostId
    )
    filterMachines()
  } catch (error) {
    ElMessage({ type: 'error', message: '获取监控对象失败' })
  } finally {
    loading.value = false
  }
}

const filterMachines = () => {
  filteredMachineList.value = machineList.value.filter(item => {
    return item.status === activeStatus.value
  })
}

watch(activeStatus, () => {
  filterMachines()
})

const onAddMachine = () => {
  machineEditRef.value.open({})
}

const onEditMachine = (row) => {
  machineEditRef.value.open(row)
}

const onDeleteMachine = async (row) => {
  try {
    await ElMessageBox.confirm('你确认删除该监控主机吗？', '温馨提示', {
      type: 'warning',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await artDelTargetService(row.hardwareInfo.hardwareId)
    ElMessage({ type: 'success', message: '删除成功' })
    await getMachineList()
  } catch (error) {
    ElMessage({ type: 'error', message: '删除失败，请重试' })
  }
}

const onSuccess = () => {
  getMachineList()
}

onMounted(() => {
  getMachineList()
})
</script>

<template>
  <page-container title="监控管理">
    <template #extra>
      <el-button type="primary" @click="onAddMachine">添加监控对象</el-button>
    </template>
    <el-form inline>
      <el-form-item label="监控类型：">
        <WarningSelect></WarningSelect>
      </el-form-item>
      <el-form-item label="活跃情况：">
        <el-select v-model="activeStatus">
          <el-option label="活跃中" value="活跃中" />
          <el-option label="休眠" value="休眠" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="filteredMachineList" style="width: 100%" v-loading="loading">
      <el-table-column label="硬件ID" prop="hardwareInfo.hardwareId" />
      <el-table-column label="IP" prop="hardwareInfo.ip" />
      <el-table-column label="名称" prop="hardwareInfo.name" />
      <el-table-column label="类型" prop="hardwareInfo.type" />
      <el-table-column label="服务器ID" prop="hardwareInfo.serverId" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
              :icon="Edit"
              circle
              plain
              type="primary"
              @click="onEditMachine(row)"
          />
          <el-button
              :icon="Delete"
              circle
              plain
              type="danger"
              @click="onDeleteMachine(row)"
          />
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <machine-edit ref="machineEditRef" @success="onSuccess" />
  </page-container>
</template>

<style lang="scss" scoped>

.el-form {
  margin-bottom: 20px;
}

.el-table {
  margin-top: 20px;
}

.el-button {
  margin-right: 10px;
}
</style>
