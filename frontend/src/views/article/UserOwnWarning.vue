<script setup>
import { Delete, Edit } from '@element-plus/icons-vue'
import { ref } from 'vue'
// import WarningSelect from './components/WarningSelect.vue'
import WarningEdit from './components/OwnWarningEdit.vue'
import { artGetAllRulesService, artDelRulesService } from '@/api/Warning'
import { ElMessage, ElMessageBox } from 'element-plus'
// import { useRoute } from 'vue-router'

// const cateId = ref(44173)
const loading = ref(false)
const rulesList = ref([])
const machineEditRef = ref()
// const route = useRoute()

const getmachineList = async () => {
  loading.value = true
  const res = await artGetAllRulesService()
  rulesList.value = JSON.parse(res.data.data)
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
  await ElMessageBox.confirm('你确认删除该自定义警告规则吗？', '温馨提示', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  })
  await artDelRulesService({
    type: row.type,
    alert_name: row.alertName
  })
  ElMessage({ type: 'success', message: '删除成功' })
  getmachineList()
}
const onSuccess = (type) => {
  console.log(type)
  getmachineList()
}
</script>
<template>
  <page-container title="自定义告警规则">
    <template #extra>
      <el-button @click="onAddMachine">添加自定义告警规则</el-button>
    </template>
    <!-- <el-form inline>
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
    </el-form> -->
    <el-table :data="rulesList" style="width: 100%" v-loading="loading">
      <el-table-column label="警报名称" prop="alertName"></el-table-column>
      <el-table-column label="警报规则" prop="alertRule"></el-table-column>
      <el-table-column label="严重性" prop="severity"></el-table-column>
      <el-table-column label="概述" prop="summary"></el-table-column>
      <el-table-column label="时间长度" prop="timeLength"></el-table-column>
      <el-table-column label="类型" prop="type"></el-table-column>
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
    <warning-edit ref="machineEditRef" @success="onSuccess"></warning-edit>
  </page-container>
</template>

<style lang="scss" scoped></style>
