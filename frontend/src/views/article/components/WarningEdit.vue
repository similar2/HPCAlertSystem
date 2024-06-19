<script setup>
import { ref } from 'vue'
import { artEditWarningService, artAddWarningService } from '@/api/Warning'
const dialogVisible = ref(false)
const formRef = ref()
const open = async (row) => {
  dialogVisible.value = true
  formModel.value = { ...row }
}
const emit = defineEmits(['success'])
const formModel = ref({
  description: '',
  alertName: '',
  deviceName: '',
  severity: ''
})
const rules = {
  description: [{ required: true, message: '请输入错误信息', trigger: 'blur' }],
  severity: [{ required: true, message: '请输入严重度', trigger: 'blur' }],
  deviceName: [{ required: true, message: '请输入设备名', trigger: 'blur' }]
}
const onSubmit = async () => {
  // await formRef.value.validate()
  // formModel.value.id
  //   ? await artEditWarningService(formModel.value)
  //   : await artAddWarningService(formModel.value)
  artAddWarningService(formModel.value)
  ElMessage({
    type: 'success',
    // message: formModel.value.id ? '编辑成功' : '添加成功'
    message: '添加成功'
  })
  dialogVisible.value = false
  emit('success')
}
defineExpose({
  open
})
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="formModel.id ? '编辑错误' : '添加错误'"
    width="30%"
  >
    <div>
      <el-form
        :model="formModel"
        :rules="rules"
        ref="formRef"
        label-width="100px"
        style="padding-right: 30px"
      >
        <el-form-item label="错误信息" prop="description">
          <el-input v-model="formModel.description"></el-input>
        </el-form-item>
        <el-form-item label="告警名" prop="alertName">
          <el-input v-model="formModel.alertName"></el-input>
        </el-form-item>
        <el-form-item label="严重度" prop="severity">
          <el-input v-model="formModel.severity"></el-input>
        </el-form-item>
        <el-form-item label="设备名" prop="deviceName">
          <el-input v-model="formModel.dutyUserId"></el-input>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button @click="onSubmit" type="primary"> 确认 </el-button>
      </span>
    </template>
  </el-dialog>
</template>
