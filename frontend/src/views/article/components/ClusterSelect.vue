<script setup>
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
WarningList.value = [
  { id: 1, cate_name: 'Taiyi' },
  { id: 2, cate_name: 'Qiming' }
]
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
