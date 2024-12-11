<template>
  <el-dialog
      :title="device ? '编辑设备' : '新增设备'"
      :visible.sync="visible"
      width="50%"
      @close="$emit('close')"
  >
    <el-form :model="form" ref="formRef" label-width="120px">
      <el-form-item label="设备名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="设备类型" prop="type">
        <el-select v-model="form.type" placeholder="请选择设备类型">
          <el-option
              v-for="type in deviceTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="位置" prop="location">
        <el-input v-model="form.location" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="正常">正常</el-radio>
          <el-radio label="告警">告警</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="$emit('close')">取消</el-button>
      <el-button type="primary" @click="save">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'DeviceEdit',
  props: {
    device: Object,
  },
  data() {
    return {
      visible: true,
      form: {
        id: null,
        name: '',
        type: '',
        location: '',
        status: '正常',
      },
      deviceTypes: [
        { label: '电池', value: 'battery' },
        { label: '冷却系统', value: 'cooling' },
        { label: 'UPS', value: 'ups' },
      ],
    };
  },
  watch: {
    device: {
      immediate: true,
      handler(newDevice) {
        if (newDevice) {
          this.form = { ...newDevice };
        }
      },
    },
  },
  methods: {
    save() {
      this.$emit('save', this.form); // 触发保存事件
    },
  },
};
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>
