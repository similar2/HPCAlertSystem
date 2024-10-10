<script setup>
import { Delete, Edit } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import WarningEdit from './components/WarningEdit.vue'
import {
  artGetAllWarningService,
  artDelWarningService,
  artGetSearchWarningService,
  getWarningList
} from '@/api/Warning'
import { ElMessage, ElMessageBox } from 'element-plus'
import '@/components/pageContainer.vue'

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const params = ref({
  device_name: '',
  solved: '',
  start_time: '',
  end_time: ''
})

const time = ref('')
const loading = ref(false)
const WarningList = ref([])
const WarningEditRef = ref()

const pageQuery = () => {
  loading.value = true
  const data = {
    deviceName: params.value.device_name,
    solved: params.value.solved,
    startTime: params.value.start_time,
    endTime: params.value.end_time,
    page: page.value,
    pageSize: pageSize.value
  }

  getWarningList(data)
      .then((res) => {
        if (res.data.code == 200) {
          total.value = res.data.data.total
          WarningList.value = res.data.data.records
        }
      })
      .catch((err) => {
        ElMessage.error('请求出错了：' + err.message)
      })
      .finally(() => {
        loading.value = false
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

const searchWarningList = async (data) => {
  loading.value = true

  const res = await artGetSearchWarningService(data)
  WarningList.value = JSON.parse(res.data.data).sort((a, b) => {
    if (a.solveTime && !b.solveTime) return 1
    if (!a.solveTime && b.solveTime) return -1
    return 0
  })

  loading.value = false
}

onMounted(() => {
  pageQuery()
})

const onSearch = () => {
  if (params.value.start_time) {
    let date = new Date(+new Date(params.value.start_time) + 8 * 3600 * 1000)
    params.value.start_time = date.toISOString().slice(0, -5)
  }

  if (params.value.end_time) {
    let date = new Date(+new Date(params.value.end_time) + 8 * 3600 * 1000)
    params.value.end_time = date.toISOString().slice(0, -5)
  }

  searchWarningList(params.value)
}

const onReset = () => {
  params.value.device_name = ''
  params.value.solved = ''
  params.value.start_time = ''
  params.value.end_time = ''
  time.value = null
  page.value = 1
  pageSize.value = 10
  pageQuery()
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
        ElMessage({ type: 'info', message: 'Input canceled' })
      })

  pageQuery()
}

const onSuccess = (type) => {
  if (type === 'add') {
    const lastPage = Math.ceil((total.value + 1) / pageSize.value)
    page.value = lastPage
  }
  pageQuery()
}

const hasSolution = (row) => {
  return row.solveMethod !== undefined && row.solveMethod !== null && row.solveMethod !== ''
}

const change = () => {
  if (time.value) {
    params.value.start_time = time.value[0]
    params.value.end_time = time.value[1]
  } else {
    params.value.start_time = ''
    params.value.end_time = ''
  }
  pageQuery()
}
</script>

<template>
  <page-container title="问题具体信息">
    <template #extra>
      <el-button @click="onAddWarning">人工添加问题</el-button>
    </template>

    <el-form inline>
      <el-form-item label="问题状态：" style="width: 20%; margin-right: 20px">
        <el-input
            clearable
            @clear="pageQuery"
            v-model="params.device_name"
            placeholder="请输入设备名称"
            @change="pageQuery"
        ></el-input>
      </el-form-item>

      <el-form-item label="状态:">
        <el-select clearable @clear="pageQuery" v-model="params.solved" style="width: 120px">
          <el-option label="无" value=""></el-option>
          <el-option label="已解决" value="true"></el-option>
          <el-option label="未解决" value="false"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="时间区间:" style="margin-right: 8px">
        <div class="block">
          <el-date-picker
              :editable="false"
              clearable
              @clear="pageQuery"
              v-model="time"
              type="datetimerange"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              date-format="YYYY/MM/DD ddd"
              time-format="A hh:mm:ss"
              @change="change"
          />
        </div>
      </el-form-item>

      <el-form-item>
        <el-button @click="pageQuery" type="primary">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="WarningList" style="width: 100%">
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
              :disabled="hasSolution(row)"
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
      ></el-pagination>
    </div>

    <WarningEdit ref="WarningEditRef" @success="onSuccess"></WarningEdit>
  </page-container>
</template>

<style lang="scss" scoped>
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
