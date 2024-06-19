<script setup>
import { ref, watch } from 'vue'
import { artGetMetricFiltersService } from '@/api/Warning'

const props = defineProps({
  modelValue: {
    type: [Number, String]
  },
  type: {
    type: String
  },
  metric: {
    type: String
  },
  width: {
    type: String
  }
})

const emit = defineEmits(['update:modelValue'])
const filterList = ref([])
console.log(props.type)
const getNameList = async () => {
  const res = await artGetMetricFiltersService(props.type, props.metric)
  filterList.value = JSON.parse(res.data.data).name
  console.log(filterList.value)
}
watch([() => props.type, () => props.metric], () => {
  getNameList()
})
getNameList()
</script>
<template>
  <el-select
    :style="{ width }"
    :modelValue="modelValue"
    @update:modelValue="emit('update:modelValue', $event)"
  >
    <el-option
      v-for="(filter, index) in filterList"
      :key="index"
      :label="filter"
      :value="filter"
    ></el-option>
  </el-select>
</template>
