<script setup>
import { onMounted, ref } from 'vue'
import { Edit, Delete } from '@element-plus/icons-vue'
import {
  getWarningList as getList,
  artGetAllWarningService,
  artDelWarningService
} from '@/api/Warning'
import WarningEdit from './components/WarningEdit.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const WarningList = ref([])
const records = ref([])
const stateList = ref([])
const lowCount = ref(0)
const highCount = ref(0)
const solvedCount = ref(0)
const unsolvedCount = ref(0)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const seriousList = ref([
  { name: 'LOW', value: 1 },
  { name: 'HIGH', value: 2 }
])
const loading = ref(false)
const dialog = ref()
stateList.value = [
  { name: '已解决', value: 1 },
  { name: '未解决', value: 2 }
]

const handleSizeChange = (newPageSize) => {
  pageSize.value = newPageSize
  pageQuery()
}
const handleCurrentChange = (newPage) => {
  page.value = newPage
  pageQuery()
}

const pageQuery = () => {
  const data = {
    page: page.value,
    pageSize: pageSize.value
  }
  getList(data)
    .then((res) => {
      if (res.data.code == 200) {
        total.value = res.data.data.total
        records.value = res.data.data.records
      }
    })
    .catch((err) => {
      ElMessage.error('请求出错了：' + err.message)
    })
}

const getWarningList = async () => {
  loading.value = true
  const res = await artGetAllWarningService()
  WarningList.value = JSON.parse(res.data.data).sort((a, b) => {
    if (a.solveTime && !b.solveTime) return 1
    if (!a.solveTime && b.solveTime) return -1
    return 0
  })

  WarningList.value.forEach(warning => {
    if (warning.severity === 'LOW') lowCount.value++
    else if (warning.severity === 'HIGH') highCount.value++

    if (warning.solveTime) solvedCount.value++
    else unsolvedCount.value++
  })

  seriousList.value = [
    { name: 'LOW', value: lowCount.value },
    { name: 'HIGH', value: highCount.value }
  ]
  stateList.value = [
    { name: '已解决', value: solvedCount.value },
    { name: '未解决', value: unsolvedCount.value }
  ]

  pageQuery()
  loading.value = false
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
})
</script>

<template>
  <page-container title="概览">
    <template #extra>
      <!-- <el-button @click="onAddWarning">人工添加问题</el-button> -->
    </template>

    <el-table v-loading="loading" :data="stateList" style="width: 50%; display: inline-block; margin-right: 20px;">
      <el-table-column label="序号" width="100" type="index"></el-table-column>
      <el-table-column label="状态类名" prop="name"></el-table-column>
      <el-table-column label="状态描述" prop="value"></el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>

    <el-table v-loading="loading" :data="seriousList" style="width: 50%; display: inline-block; margin-right: 20px;">
      <el-table-column label="序号" width="100" type="index"></el-table-column>
      <el-table-column label="问题严重程度" prop="name"></el-table-column>
      <el-table-column label="数量" prop="value"></el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>

    <el-table v-loading="loading" :data="records" style="width: 100%">
      <el-table-column label="告警序号" width="100" type="index"></el-table-column>
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

    <div class="pagination-container">
      <el-pagination
        class="pageList"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page"
        :page-sizes="[10, 20, 30, 40, 50, 100, 200]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
    </div>

    <Warning-edit ref="dialog" @success="getWarningList"></Warning-edit>
  </page-container>
</template>

<style lang="scss" scoped>
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.el-table {
  font-family: 'Arial', sans-serif;
  font-size: 14px;
  line-height: 1.5;
}

.el-table th {
  background-color: #f5f5f5;
  color: #333;
  font-weight: bold;
  padding: 10px;
}

.el-table td {
  padding: 10px;
}

.el-table .el-table__row {
  border-bottom: 1px solid #eee;
}
</style>
