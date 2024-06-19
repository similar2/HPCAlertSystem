<script setup>
import { Delete, Edit } from '@element-plus/icons-vue'
import { ref } from 'vue'
import WarningEdit from './components/WarningEdit.vue'
import { artGetAllWarningService, artDelWarningService, artGetSearchWarningService } from '@/api/Warning'
// import { formatTime } from '@/utils/format.js'
// const cateId = ref(44173)
const params = ref({
  // pagenum: 1,
  // pagesize: 5,
  device_name: '',
  solved: '',
  start_time: '',
  end_time: ''
})
const loading = ref(false)
const WarningList = ref([])
const total = ref(0)
const WarningEditRef = ref()

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
  loading.value = false
}
const searchWarningList = async (data) => {
  loading.value = true
  const res = await artGetSearchWarningService(data)
  WarningList.value = JSON.parse(res.data.data).sort((a, b) => {
  if (a.solveTime && !b.solveTime) {
    return 1;
  }
  if (!a.solveTime && b.solveTime) {
    return -1;
  }
  return 0;
  })
  console.log(WarningList.value)
  loading.value = false
}
getWarningList()
const onSizeChange = (size) => {
  // params.value.pagenum = 1
  // params.value.pagesize = size
  console.log(size)
  getWarningList()
}
const onCurrentChange = (page) => {
  // params.value.pagenum = page
  getWarningList()
}
const onSearch = () => {
  // params.value.pagenum = 1
  if (params.value.start_time !== '') {
    let date = new Date(+new Date(params.value.start_time) + 8*3600*1000);
    params.value.start_time = date.toISOString().slice(0, -5);
  }

  if(params.value.end_time !== '') {
    let date = new Date(+new Date(params.value.end_time) + 8*3600*1000);
    params.value.end_time = date.toISOString().slice(0, -5);
  }
  console.log(params.value)
  searchWarningList(params.value)
}
const onReset = () => {
  params.value.pagenum = 1
  params.value.cate_id = ''
  params.value.state = ''
  getWarningList()
}
// 编辑新增逻辑
const onAddWarning = () => {
  WarningEditRef.value.open({})
}
const onEditWarning = (row) => {
  WarningEditRef.value.open(row)
}
const onDelWarning = async (row) => {
  await ElMessageBox.prompt('请输入解决手段', 'Tip', {
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel'
  })
    .then(async ({ value }) => {
      await ElMessageBox.confirm('你确认解决该报错吗？', '温馨提示', {
        type: 'warning',
        confirmButtonText: '确认',
        cancelButtonText: '取消'
      })
      await artDelWarningService(row.id, value)
      ElMessage({ type: 'success', message: '删除成功' })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: 'Input canceled'
      })
    })
  getWarningList()
}
const onSuccess = (type) => {
  console.log(type)
  if (type === 'add') {
    // 如果是添加，需要跳转渲染最后一页，编辑直接渲染当前页
    const lastPage = Math.ceil((total.value + 1) / params.value.pagesize)
    params.value.pagenum = lastPage
  }
  getWarningList()
}
const hasSolution = (row) => {
  return (
    row.solveMethod !== undefined &&
    row.solveMethod !== null &&
    row.solveMethod !== ''
  )
}
</script>
<template>
  <page-container title="问题具体信息">
    <template #extra>
      <el-button @click="onAddWarning">人工添加问题</el-button>
    </template>
    <el-form inline>
      <el-form-item label="问题状态：">
        <el-input v-model="params.device_name" placeholder="请输入设备名称"></el-input>
      </el-form-item>
      <el-form-item label="发布状态：">
        <el-select v-model="params.solved" style="width: 240px">
          <el-option label="无" value=""></el-option>
          <el-option label="已解决" value="true"></el-option>
          <el-option label="未解决" value="false"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="时间区间：">
        <el-date-picker v-model="params.start_time" type="date" placeholder="选择日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="">
        <el-date-picker v-model="params.end_time" type="date" placeholder="选择日期"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button @click="onSearch" type="primary">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
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
            :disabled="hasSolution(row)"
            @click="onDelWarning(row)"
          ></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <el-pagination
      v-model:current-page="params.pagenum"
      v-model:page-size="params.pagesize"
      :page-sizes="[2, 3, 4, 5, 10]"
      layout="jumper, total, sizes, prev, pager, next"
      background
      :total="total"
      @size-change="onSizeChange"
      @current-change="onCurrentChange"
      style="margin-top: 20px; justify-content: flex-end"
    />
    <Warning-edit ref="WarningEditRef" @success="onSuccess"></Warning-edit>
  </page-container>
</template>

<style lang="scss" scoped></style>
