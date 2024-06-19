<script setup>
import { Delete, Edit } from '@element-plus/icons-vue'
import { ref } from 'vue'
import WarningSelect from './components/WarningSelect.vue'
import MachineEdit from './components/TargetEdit.vue'
import { artGetAllTargetService, artDelTargetService } from '@/api/Warning'
import { useRoute } from 'vue-router'

// const cateId = ref(44173)
const loading = ref(false)
const machineList = ref([])
const machineEditRef = ref()
const route = useRoute()
const clusterId = route.query.clusterId
const hostId = route.query.hostId

const getmachineList = async () => {
  loading.value = true
  const res = await artGetAllTargetService()
  machineList.value = JSON.parse(res.data.data).filter(
    (item) => item.clusterInfo.id == clusterId && item.deviceInfo.id == hostId
  )
  console.log(JSON.parse(res.data.data))
  loading.value = false
}
getmachineList()
// 编辑新增逻辑
const onAddMachine = () => {
  machineEditRef.value.open({})
}
const onEditMachine = (row) => {
  machineEditRef.value.open(row)
}
const onDeleteMachine = async (row) => {
  await ElMessageBox.confirm('你确认删除该监控主机吗？', '温馨提示', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  })
  await artDelTargetService(row.hardwareInfo.hardwareId)
  ElMessage({ type: 'success', message: '删除成功' })
  getmachineList()
}
const onSuccess = (type) => {
  console.log(type)
  getmachineList()
}
</script>
<template>
  <page-container title="监控管理">
    <template #extra>
      <el-button @click="onAddMachine">添加监控对象</el-button>
    </template>
    <el-form inline>
      <el-form-item label="监控类型：">
        <Warning-select></Warning-select>
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
    <el-table :data="machineList" style="width: 100%" v-loading="loading">
      <el-table-column
        label="硬件ID"
        prop="hardwareInfo.hardwareId"
      ></el-table-column>
      <el-table-column label="IP" prop="hardwareInfo.ip"></el-table-column>
      <el-table-column label="名称" prop="hardwareInfo.name"></el-table-column>
      <el-table-column label="类型" prop="hardwareInfo.type"></el-table-column>
      <el-table-column
        label="服务器ID"
        prop="hardwareInfo.serverId"
      ></el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            :icon="Edit"
            circle
            plain
            type="primary"
            @click="onEditMachine(row)"
          ></el-button>
          <el-button
            :icon="Delete"
            circle
            plain
            type="danger"
            @click="onDeleteMachine(row)"
          ></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <machine-edit ref="machineEditRef" @success="onSuccess"></machine-edit>
  </page-container>
</template>

<style lang="scss" scoped></style>
