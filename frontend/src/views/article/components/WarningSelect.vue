<script setup>
import { artGetAllWarningService } from '@/api/Warning'
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
const WarningList = ref([])
const getWarningList = async () => {
  const res = await artGetAllWarningService()
  WarningList.value = JSON.parse(res.data.data)
}
getWarningList()
</script>
<template>
  <el-select
    :style="{ width }"
    :modelValue="modelValue"
    @update:modelValue="emit('update:modelValue', $event)"
  >
    <el-option
      v-for="Warning in WarningList"
      :key="Warning.id"
      :label="Warning.cate_name"
      :value="Warning.id"
    ></el-option>
  </el-select>
</template>
