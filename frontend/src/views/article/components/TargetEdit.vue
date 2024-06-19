<script setup>
import { ref } from 'vue'
import TargetSelect from './TargetSelect.vue'
import { artAddTargetService } from '@/api/Warning'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
// import { baseURL } from '@/utils/request'
// import axios from 'axios'
const visibleDrawer = ref(false)
const emit = defineEmits(['success'])

const defaultForm = {
  title: '',
  ip: '',
  type: '',
  serverId: 1,
  other: {
    username: 'ADMIN',
    password: 'ADMIN',
    port: 623
  }
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
    fd.append(key, formModel.value[key])
  }
  // 添加请求
  await artAddTargetService(fd)
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
    <el-form :model="formModel" ref="formRef" label-width="100px">
      <el-form-item label="target名" prop="title">
        <el-input
          v-model="formModel.title"
          placeholder="请输入target名"
        ></el-input>
      </el-form-item>
      <el-form-item label="ip" prop="ip">
        <el-input v-model="formModel.ip" placeholder="请输入ip"></el-input>
      </el-form-item>
      <el-form-item label="target类型" prop="type">
        <el-input v-model="formModel.type" placeholder="请输入类型"></el-input>
      </el-form-item>
      <el-form-item label="服务器所属" prop="serverId">
        <target-select
          v-model="formModel.serverId"
          width="100%"
        ></target-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="onPublish()" type="primary">添加</el-button>
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
.editor {
  width: 100%;
  :deep(.ql-editor) {
    min-height: 200px;
  }
}
</style>
