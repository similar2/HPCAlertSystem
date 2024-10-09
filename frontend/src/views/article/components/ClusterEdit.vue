<script setup>
import { ref } from 'vue'
import ChannelSelect from './ClusterSelect.vue'
import ResponseEdit from './ResponseEdit.vue'
import { artAddDeviceService, artEditService, artGetDeviceService } from '@/api/Warning'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { ElMessage } from 'element-plus'
// import { baseURL } from '@/utils/request'
// import axios from 'axios'
const visibleDrawer = ref(false)
const emit = defineEmits(['success'])
const defaultForm = {
  name: '',
  position: '',
  type: '',
  clusterId: 1,
  other: {
          ip: '',
          manageIp: '',
          publicIp: ''
        }
}
const ResponseEditRef = ref()
let flag = false
let id = 0
const formModel = ref({ ...defaultForm })
const open = async (row) => {
  visibleDrawer.value = true
  formModel.value = { ...defaultForm }
  if(row.name !== undefined && row.name !== '') {
    id = row.id
    const res = await artGetDeviceService(row.id)
    formModel.value.name = row.name
    formModel.value.position = row.position
    formModel.value.type = row.type
    formModel.value.clusterId = row.clusterId
    if(res.data.data.other != null) {
      formModel.value.other.ip = res.data.data.other.ip
      formModel.value.other.manageIp = res.data.data.other.manageIp
      formModel.value.other.publicIp = res.data.data.other.publicIp
      console.log(formModel.value)
    } else {
      formModel.value.other = null
    }
    flag = true
  }
}
const onAddResponse = async () => {
  const { ip, manageIp, publicIp } = formModel.value.other;
  if (!ip && !manageIp && !publicIp) {
    formModel.value.other = null;
  }
  // 添加请求
  const res = flag ? await artEditService(formModel.value, id) : await artAddDeviceService(formModel.value)
  console.log(res.data.data.id)
  // eslint-disable-next-line no-undef
  ElMessage.success('添加成功')
  visibleDrawer.value = false
  formModel.value = { ...defaultForm }
  flag = false
  id = 0
  emit('success', 'add')

  ResponseEditRef.value.open(res.data.data.id, [])
}
const onPublish = async () => {
  const { ip, manageIp, publicIp } = formModel.value.other;
  if (!ip && !manageIp && !publicIp) {
    formModel.value.other = null;
  }

  // 添加请求
  flag ? await artEditService(formModel.value, id) : await artAddDeviceService(formModel.value)
  // eslint-disable-next-line no-undef
  ElMessage.success('添加成功')
  visibleDrawer.value = false
  formModel.value = { ...defaultForm }
  flag = false
  id = 0
  emit('success', 'add')
}
const onSuccess = () => {
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
      <el-form-item label="设备名" prop="name">
        <el-input
          v-model="formModel.name"
          placeholder="请输入设备名"
        ></el-input>
      </el-form-item>
      <el-form-item label="设备位置" prop="position">
        <el-input v-model="formModel.position" placeholder="请输入设备位置"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-input v-model="formModel.type" placeholder="请输入类型"></el-input>
      </el-form-item>
      <el-form-item label="服务器所属">
        <channel-select
          v-model="formModel.clusterId"
          width="100%"
        ></channel-select>
      </el-form-item>
      <el-form-item label="校园网IP">
        <el-input v-model="formModel.other.ip"></el-input>
      </el-form-item>
      <el-form-item label="管理网IP">
        <el-input v-model="formModel.other.manageIp"></el-input>
      </el-form-item>
      <el-form-item label="公网IP">
        <el-input v-model="formModel.other.publicIp"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="onPublish()" type="primary">添加</el-button>
      </el-form-item>
      <el-form-item>
        <el-button @click="onAddResponse()" type="primary">下一步</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
  <response-edit ref="ResponseEditRef" @success="onSuccess"></response-edit>
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
