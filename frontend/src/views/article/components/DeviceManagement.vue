<script setup>
import { ref, onMounted } from 'vue';
import {
  artGetAllDevicesService,
  artAddDeviceService,
  artEditService,
  artDelService,
  artAddFile, artGetDeviceService
} from '@/api/Warning';
import { ElMessage, ElMessageBox } from 'element-plus';
import {useRouter} from "vue-router";

const devices = ref([]);
console.log('devices', devices.value);
const loading = ref(false);
const editDevice = (device) => {
  currentDevice.value = device;
  showDialog.value = true;
  console.log('editDevice', currentDevice.value);
};

const router = useRouter();
const navigateToAlerts = (deviceId) => {
  if (!deviceId) {
    console.error('Device ID is undefined. Check the binding in your table.');
    return;
  }
  console.log('Navigating to alerts for deviceId:', deviceId);
  router.push({ path: '/article/auxiliary-monitoring/alert-management', query: { deviceId } });
};




const filters = ref({
  deviceName: '',
  deviceType: null
});
const deviceTypes = [
  { label: '电池', value: 'BATTERY' },
  { label: '冷却系统', value: 'AIR_CONDITIONER' },
  { label: 'UPS', value: 'UPS' }
];
const currentDevice = ref({
  name: '',
  type: '',
  position: '',
  status: '正常',
}); // 确保初始化为对象

const showDialog = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const fileInput = ref(null); // 用于上传文件的引用

const fetchDevices = async () => {
  loading.value = true;
  try {
    const res = await artGetAllDevicesService();
    console.log('res', res.data.data);
    let data = res.data.data;
    if (filters.value.deviceName) {
      data = data.filter((device) =>
          device.name.includes(filters.value.deviceName)
      );
    }
    if (filters.value.deviceType) {
      data = data.filter((device) => device.type === filters.value.deviceType);
    }

    const devicesWithOther = await Promise.all(data.map(async (device) => {
      const deviceDetails = await artGetDeviceService(device.id);
      const deviceData = deviceDetails.data.data;
      console.log('deviceData', deviceData);

      // 确保设备的 `other` 字段存在
      deviceData.other = deviceData.other || {};

      // 返回非 SERVER 类型设备数据
      return deviceData.type !== 'SERVER' ? deviceData : null;
    }));

    devices.value = devicesWithOther.filter(device => device !== null);
    total.value = devices.value.length;
    console.log('devices:  ', devices.value);
  } catch (error) {
    ElMessage.error('获取设备数据失败');
    console.log('error', error)
  }
  loading.value = false;
};




const resetFilters = () => {
  filters.value.deviceName = '';
  filters.value.deviceType = null;
  fetchDevices();
};

const showAddDeviceDialog = () => {
  currentDevice.value = {
    name: '',
    type: '',
    location: '',
    status: '正常',
    clusterId: ''
  };
  showDialog.value = true;
  console.log('showDialog', currentDevice.value);
};

const saveDevice = async (device) => {
  try {
    if (!device.other) {
      device.other = {};
    }
    device.other.alert = device.status === '告警';

    if (!device.position || !device.clusterId) {
      ElMessage.error('设备位置和集群 ID 不能为空');
      return;
    }

    if (device.id) {
      await artEditService(device, device.id);
      const index = devices.value.findIndex((d) => d.id === device.id);
      if (index !== -1) {
        devices.value[index] = { ...device }; // 更新对应设备
      }
      devices.value = [...devices.value]; // 强制更新
      ElMessage.success('设备更新成功');
    } else {
      await artAddDeviceService(device);
      ElMessage.success('设备添加成功');
      fetchDevices();
    }

    showDialog.value = false;
  } catch (error) {
    ElMessage.error('操作失败');
  }
};





// 删除设备
const deleteDevice = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该设备吗？', '提示', {
      type: 'warning'
    });
    await artDelService(id);
    ElMessage.success('设备删除成功');
    await fetchDevices();
  } catch (error) {
    ElMessage.error('删除失败');
  }
};


const handleFileUpload = async () => {
  const file = fileInput.value.files[0];
  if (!file) {
    ElMessage.error('请选择文件');
    return;
  }
  try {
    await artAddFile(file);
    ElMessage.success('设备导入成功');
    fetchDevices();
    fileInput.value.value = ''; // 清空文件输入
  } catch (error) {
    ElMessage.error('设备导入失败');
  }
};

onMounted(() => {
  fetchDevices();
});
</script>

<template>
  <el-card class="main-card">
    <!-- 页面头部 -->
    <div class="header">
      <h2 class="header-title">设备管理</h2>
      <div class="header-buttons">
        <el-button type="primary" @click="fileInput.click()">
          上传文件
        </el-button>
        <input
            ref="fileInput"
            type="file"
            accept=".xlsx,.xls"
            style="display: none"
            @change="handleFileUpload"
        />
        <el-button type="primary" @click="showAddDeviceDialog">
          添加设备
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="filters" class="filter-form">
      <el-form-item label="设备名称">
        <el-input v-model="filters.deviceName" placeholder="请输入设备名称" />
      </el-form-item>
      <el-form-item label="设备类型">
        <el-select v-model="filters.deviceType" placeholder="请选择设备类型" style ="width:120px">
          <el-option
              v-for="type in deviceTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchDevices">
          搜索
        </el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 设备表格 -->
    <el-table :data="devices" border style="width: 100%" class="device-table">
      <el-table-column prop="id" label="设备ID" width="100" />
      <el-table-column prop="name" label="设备名称" />
      <el-table-column prop="type" label="设备类型" />
      <el-table-column prop="position" label="位置" />
      <el-table-column prop="clusterId" label="集群 ID" />
      <el-table-column prop="other.alert" label="告警状态">
        <template #default="scope">
          <el-tag :type="scope.row.other && scope.row.other.alert ? 'danger' : 'success'">
            {{ scope.row.other && scope.row.other.alert ? '告警' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button
              type="text"
              size="small"
              @click="editDevice(scope.row)"
          >
            编辑
          </el-button>
          <el-button
              type="text"
              size="small"
              @click="deleteDevice(scope.row.id)"
          >
            删除
          </el-button>

        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button type="text" size="small" @click="navigateToAlerts(scope.row.id)">
            查看告警
          </el-button>
        </template>
      </el-table-column>

    </el-table>

    <!-- 分页 -->
    <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        @current-change="fetchDevices"
    />

    <!-- 添加/编辑设备的弹窗 -->
    <el-dialog v-model="showDialog" title="设备信息" width="40%">
      <el-form :model="currentDevice" label-width="100px">
        <el-form-item label="设备名称">
          <el-input v-model="currentDevice.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="currentDevice.type" placeholder="请选择设备类型">
            <el-option
                v-for="type in deviceTypes"
                :key="type.value"
                :label="type.label"
                :value="type.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="设备位置">
          <el-input v-model="currentDevice.position" placeholder="请输入设备位置" />
        </el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="currentDevice.status" placeholder="请选择设备状态">
            <el-option label="正常" value="正常" />
            <el-option label="告警" value="告警" />
          </el-select>
        </el-form-item>
        <el-form-item label="集群 ID">
          <el-input v-model="currentDevice.clusterId" placeholder="请输入集群 ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveDevice(currentDevice)">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>



<style scoped>
.main-card {
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-buttons {
  display: flex;
  gap: 30px;
}

.header-title {
  display: flex;
  align-items: center;
  font-weight: bold;
  color: #333;
}

.filter-form {
  margin-bottom: 20px;
}

.device-table {
  margin-top: 20px;
}
</style>
