<script setup>
import { ref } from 'vue'
import { artAddResponseService } from '@/api/Warning'
import ResponseSelect from './ResponseSelect.vue';
import { ElMessage } from 'element-plus'
const dialogVisible = ref(false)
let deviceId = -1
const open = async (Id, user) => {
  dialogVisible.value = true
  deviceId = Id
  if(user.length > 0){
    form.value.dynamicItem = user.map(item => {
      return {
        nameRes: item.nameRes
      }
    })
  } else {
    form.value.dynamicItem = [
      { nameRes: '' }
    ]
  }
}
const emit = defineEmits(['success'])

const form = ref({
  dynamicItem: [
    { nameRes: '' }
  ]
})
const onSubmit = async () => {
  const nameResArray = form.value.dynamicItem.map(item => item.nameRes);
  console.log(nameResArray)
  artAddResponseService(deviceId, nameResArray)
  ElMessage({
    type: 'success',
    message: '添加成功'
  })
  dialogVisible.value = false
  emit('success')
}

const addItem = (length) => {
  if (length >= 10) {
    ElMessage({
      type: 'warning',
      message: '最多可新增10条!'
    })
  } else {
    form.value.dynamicItem.push({
      nameRes: '',
    })
  }
}
//删除方法
const deleteItem = (item, index) => {
  form.value.dynamicItem.splice(index, 1)
}
defineExpose({
  open
})
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="'添加责任人'"
    width="30%"
  >
    <div>
      <el-form :model="form"
        style="padding-right: 30px"
      >
      <div v-for="(item, index) in form.dynamicItem" :key="index">
        <el-row>
          <el-col :span="16">
            <el-form-item label="联系人" :prop="'dynamicItem.' + index + '.nameRes'" style="padding-right: 10px;">
              <!-- <el-input v-model="item.nameRes"/> -->
              <response-select v-model="item.nameRes" width="100%"></response-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item>
              <el-button v-if="index+1 == form.dynamicItem.length" type="primary" size="mini" @click="addItem(form.dynamicItem.length)" style="display: inline-block">+</el-button>
              <el-button v-if="index !== 0" type="danger" size="mini" @click="deleteItem(item, index)" style="display: inline-block">-</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
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
