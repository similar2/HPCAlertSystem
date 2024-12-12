<template>
  <el-dialog
      :title="alert ? '编辑告警' : '新增告警'"
      :visible.sync="visible"
      width="50%"
      @close="$emit('close')"
  >
    <el-form :model="form" ref="formRef" label-width="120px">
      <el-form-item label="告警描述" prop="description">
        <el-input v-model="form.description" placeholder="请输入告警描述" />
      </el-form-item>
      <el-form-item label="告警级别" prop="level">
        <el-select v-model="form.level" placeholder="请选择告警级别">
          <el-option label="严重" value="严重" />
          <el-option label="中等" value="中等" />
          <el-option label="轻微" value="轻微" />
        </el-select>
      </el-form-item>
      <el-form-item label="告警状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="未处理">未处理</el-radio>
          <el-radio label="已处理">已处理</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="告警时间" prop="time">
        <el-date-picker
            v-model="form.time"
            type="datetime"
            placeholder="请选择时间"
        />
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
  name: 'AlertEdit',
  props: {
    alert: Object, // 传入的告警数据
  },
  data() {
    return {
      visible: true,
      form: {
        id: null,
        description: '',
        level: '',
        status: '未处理',
        time: null,
      },
    };
  },
  watch: {
    alert: {
      immediate: true,
      handler(newAlert) {
        if (newAlert) {
          this.form = { ...newAlert }; // 如果有传入告警数据，初始化表单
        } else {
          this.resetForm(); // 如果没有传入数据，重置表单
        }
      },
    },
  },
  methods: {
    save() {
      const isValid = this.validateForm();
      if (isValid) {
        this.$emit('save', this.form); // 触发保存事件，将表单数据传递给父组件
      }
    },
    resetForm() {
      this.form = {
        id: null,
        description: '',
        level: '',
        status: '未处理',
        time: null,
      };
    },
    validateForm() {
      if (!this.form.description) {
        this.$message.error('请填写告警描述！');
        return false;
      }
      if (!this.form.level) {
        this.$message.error('请选择告警级别！');
        return false;
      }
      if (!this.form.time) {
        this.$message.error('请选择告警时间！');
        return false;
      }
      return true;
    },
  },
};
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>
