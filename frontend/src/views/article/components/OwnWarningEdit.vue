<script setup>
import { ref } from 'vue'
import { artAddRulesService } from '@/api/Warning'
import MetricNameSelect from './MetricNamesSelect.vue'
import FilterSelect from './FilterSelect.vue'
import { ElMessage } from 'element-plus'
// import { baseURL } from '@/utils/request'
// import axios from 'axios'
const visibleDrawer = ref(false)
const emit = defineEmits(['success'])

const defaultForm = {
  type: 'BMC',
  alertName: '',
  metricName: '',
  expr: '',
  severity: '',
  timeDuration: '',
  description: '',
  filters: ''
}
const validateExpr = (rule, value, callback) => {
  const pattern = /^(>|<|=)\s\d+$/
  if (!pattern.test(value)) {
    callback(new Error('Invalid format. It should be like "> 40"'))
  } else {
    callback()
  }
}
const rules = {
  type: [{ required: true, message: '请输入type', trigger: 'blur' }],
  alertName: [{ required: true }],
  metricName: [{ required: true }],
  expr: [{ required: true }, { validator: validateExpr, trigger: 'blur' }],
  severity: [{ required: true }],
  description: [{ required: true }],
  filters: [{ required: true }]
}
const formModel = ref({ ...defaultForm })
const open = async () => {
  visibleDrawer.value = true
  formModel.value = { ...defaultForm }
}
const onPublish = async () => {
  // 转换 formData 数据
  const fd = new FormData()
  for (let key in formModel.value) {
    if (key === 'filters') {
      fd.append(key, JSON.stringify({ name: formModel.value[key] }))
    } else {
      fd.append(key, formModel.value[key])
    }
  }
  // 添加请求
  await artAddRulesService(fd)
  // eslint-disable-next-line no-undef
  ElMessage.success('添加成功')
  visibleDrawer.value = false
  emit('success', 'add')
}
defineExpose({
  open
})
</script>

<template>
  <el-drawer
    v-model="visibleDrawer"
    :title="'添加错误'"
    direction="rtl"
    size="50%"
  >
    <!-- 发表文章表单 -->
    <el-form
      :model="formModel"
      ref="formRef"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="硬件类型" prop="type">
        <el-input
          v-model="formModel.type"
          placeholder="请输入硬件类型"
        ></el-input>
      </el-form-item>

      <el-form-item label="告警名" prop="alertName">
        <el-input
          v-model="formModel.alertName"
          placeholder="请输入告警名"
        ></el-input>
      </el-form-item>
      <el-form-item label="metric" prop="metricName">
        <MetricNameSelect
          v-model="formModel.metricName"
          :type="formModel.type"
          width="100%"
        ></MetricNameSelect>
      </el-form-item>
      <el-form-item label="表达式" prop="expr">
        <el-input
          v-model="formModel.expr"
          placeholder="请输入表达式"
        ></el-input>
      </el-form-item>

      <el-form-item label="告警严重度" prop="severity">
        <el-select
          v-model="formModel.severity"
          placeholder="请选择告警严重程度"
        >
          <el-option label="NOTE" value="NOTE"></el-option>
          <el-option label="LOW" value="LOW"></el-option>
          <el-option label="HIGH" value="HIGH"></el-option>
          <el-option label="CRITICAL" value="CRITICAL"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="持续时间" prop="timeDuration">
        <el-input
          v-model="formModel.timeDuration"
          placeholder="请输入持续时间"
        ></el-input>
      </el-form-item>

      <el-form-item label="说明" prop="description">
        <el-input
          v-model="formModel.description"
          placeholder="请输入说明"
        ></el-input>
      </el-form-item>

      <el-form-item label="过滤器" prop="filters">
        <FilterSelect
          v-model="formModel.filters"
          :type="formModel.type"
          :metric="formModel.metricName"
          width="100%"
        ></FilterSelect>
      </el-form-item>
      <el-form-item>
        <el-button @click="onPublish()" typeName="primary">添加</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>
<style lang="scss" scoped>
.avatar-uploader {
  :deep() {
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
    }
    .el-upload {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
    }
    .el-upload:hover {
      border-color: var(--el-color-primary);
    }
    .el-icon.avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      text-align: center;
    }
  }
}
</style>
