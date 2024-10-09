<script setup>
import { ref } from 'vue'
import { artDeleteResponseService } from '@/api/Warning'
import { userGet } from '@/api/user'
import { ElMessage } from 'element-plus'
const dialogVisible = ref(false)
const form = ref([])
const deleteList = ref([])
let deviceId = -1
const open = async (Id, user) => {
  dialogVisible.value = true
  deviceId = Id
  for (const id of user) {
    const res = await userGet(id);
    form.value.push({
      nameRes: id,
      name: res.data.data[0].username,
    });
  }
}
const emit = defineEmits(['success'])
const onSubmit = async () => {
  artDeleteResponseService(deviceId, deleteList.value)
  ElMessage({
    type: 'success',
    message: '删除成功'
  })
  dialogVisible.value = false
  form.value = []
  emit('success')
}
//删除方法
const deleteItem = (index) => {
  if (!deleteList.value.includes(index)) {
    deleteList.value.push(index);
  }
}
defineExpose({
  open
})
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="'删除责任人'"
    width="30%"
  >
  <div>
    <el-form :model="form"
      style="padding-right: 30px"
    >
      <el-table :data="form">
        <el-table-column
          prop="name"
          label="负责人"
        >
        </el-table-column>
        <el-table-column
          label="操作"
        >
          <template #default="scope">
            <el-button type="danger" size="mini" @click="deleteItem(scope.row.nameRes)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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
