<script setup>
import { ref, watch } from 'vue'
import { artGetMetricNamesService } from '@/api/Warning'

const props = defineProps({
  modelValue: {
    type: [Number, String]
  },
  type: {
    type: String
  },
  width: {
    type: String
  }
})

const emit = defineEmits(['update:modelValue'])
const nameList = ref([])
console.log(props.type)
const getNameList = async () => {
  const res = await artGetMetricNamesService(props.type)
  nameList.value = JSON.parse(res.data.data)
  console.log(nameList.value)
}
getNameList()
watch(
  () => props.type,
  () => {
    getNameList()
  }
)
</script>
<template>
  <el-select
    :style="{ width }"
    :modelValue="modelValue"
    @update:modelValue="emit('update:modelValue', $event)"
  >
    <el-option
      v-for="(name, index) in nameList"
      :key="index"
      :label="name"
      :value="name"
    ></el-option>
  </el-select>
</template>
