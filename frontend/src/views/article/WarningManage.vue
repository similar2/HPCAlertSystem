<script setup>
import { onMounted, ref } from 'vue'
import { Edit, Delete } from '@element-plus/icons-vue'
import {
  artGetAllWarningService,
  artDelWarningService,
  artGetAllTarget
} from '@/api/Warning'
import WarningEdit from './components/WarningEdit.vue'
const WarningList = ref([])
const stateList = ref([])
const lowCount = ref(0);
const highCount = ref(0);
const solvedCount = ref(0);
const unsolvedCount = ref(0);
const seriousList = ref([
  {
    name: 'LOW',
    value: 1
  },
  {
    name: 'HIGH',
    value: 2
  }
])
const loading = ref(false)
const dialog = ref()
stateList.value = [
  {
    name: '已解决',
    value: 1
  },
  {
    name: '未解决',
    value: 2
  }
]
const getWarningList = async () => {
  loading.value = true
  const res = await artGetAllWarningService()
  WarningList.value = JSON.parse(res.data.data).sort((a, b) => {
  if (a.solveTime && !b.solveTime) {
    return 1;
  }
  if (!a.solveTime && b.solveTime) {
    return -1;
  }
  return 0;
  })
  WarningList.value.forEach(warning => {
      if (warning.severity === 'LOW') {
        lowCount.value++;
      } else if (warning.severity === 'HIGH') {
        highCount.value++;
      }
      if (warning.solveTime) {
      solvedCount.value++;
    } else {
      unsolvedCount.value++;
    }
    });
  seriousList.value = [
    {
      name: 'LOW',
      value: lowCount.value
    },
    {
      name: 'HIGH',
      value: highCount.value
    }
  ];
  stateList.value = [
    {
      name: '已解决',
      value: solvedCount.value
    },
    {
      name: '未解决',
      value: unsolvedCount.value
    }
  ];
  console.log(WarningList.value)
  loading.value = false
}
const getAllTarget = async () => {
  const res = await artGetAllTarget()
  console.log(res.data)
}
// const onAddWarning = () => {
//   dialog.value.open({})
// }
const onEditWarning = (row) => {
  dialog.value.open(row)
}
const onDelWarning = async (row) => {
  await ElMessageBox.confirm('你确认删除该错误信息吗？', '温馨提示', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  })
  await artDelWarningService(row.id)
  ElMessage({ type: 'success', message: '删除成功' })
  getWarningList()
}
onMounted(() => {
  getWarningList()
  getAllTarget()
})
</script>

<template>
  <page-container title="概览">
    <template #extra>
      <!-- <el-button @click="onAddWarning">人工添加问题</el-button> -->
    </template>
    <el-table
      v-loading="loading"
      :data="stateList"
      style="width: 50%; display: inline-block"
    >
      <el-table-column label="序号" width="100" type="index"></el-table-column>
      <el-table-column label="状态类名" prop="name"></el-table-column>
      <el-table-column label="状态描述" prop="value"></el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <el-table
      v-loading="loading"
      :data="seriousList"
      style="width: 50%; display: inline-block"
    >
      <el-table-column label="序号" width="100" type="index"></el-table-column>
      <el-table-column label="问题严重程度" prop="name"></el-table-column>
      <el-table-column label="数量" prop="value"></el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <el-table v-loading="loading" :data="WarningList" style="width: 100%">
      <el-table-column
        label="告警序号"
        width="100"
        type="index"
      ></el-table-column>
      <el-table-column label="严重性" prop="severity"></el-table-column>
      <el-table-column label="创建时间" prop="createTime"></el-table-column>
      <el-table-column label="警报名称" prop="alertName"></el-table-column>
      <el-table-column label="解决时间" prop="solveTime"></el-table-column>
      <el-table-column label="描述" prop="description"></el-table-column>
      <el-table-column label="类型" prop="type"></el-table-column>
      <el-table-column label="设备名称" prop="deviceName"></el-table-column>
      <el-table-column label="解决方法" prop="solveMethod"></el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            :icon="Edit"
            circle
            plain
            type="primary"
            @click="onEditWarning(row)"
          ></el-button>
          <el-button
            :icon="Delete"
            circle
            plain
            type="danger"
            @click="onDelWarning(row)"
          ></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <Warning-edit ref="dialog" @success="getWarningList"></Warning-edit>
  </page-container>
</template>

<style lang="scss" scoped></style>
