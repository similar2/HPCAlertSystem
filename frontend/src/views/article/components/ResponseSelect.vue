<script setup>
import { userGetAll } from '@/api/user'
import { ref } from 'vue'

defineProps({
  modelValue: {
    type: [Number, String]
  },
  width: {
    type: String
  }
})

const emit = defineEmits(['update:modelValue'])
const ResponseList = ref([])
const getResponseList = async () => {
  const res = await userGetAll()
  ResponseList.value = res.data.data
  console.log(ResponseList.value)
}
getResponseList()
</script>
<template>
  <el-select
    :style="{ width }"
    :modelValue="modelValue"
    @update:modelValue="emit('update:modelValue', $event)"
  >
    <el-option
      v-for="user in ResponseList"
      :key="user.id"
      :label="user.username"
      :value="user.id"
    ></el-option>
  </el-select>
</template>
