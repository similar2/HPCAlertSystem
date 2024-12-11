  <script setup>
  import { ref, onMounted } from 'vue';
  import {
    getAllDeviceAlertsService,
    addDeviceAlertService,
    updateDeviceAlertService,
    deleteDeviceAlertService,
    artGetAllDevicesService,
    getDeviceAlertByIdService,
    artEditService,
    artGetDeviceService,
    searchDeviceAlertsService
  } from '@/api/Warning';
  import { ElMessage, ElMessageBox } from 'element-plus';
  import * as XLSX from 'xlsx';
  import router from "@/router/index.js";

  const alerts = ref([]);  // 所有告警
  const filteredAlerts = ref([]);  // 筛选后的告警
  const devices = ref([]);  // 所有设备
  const loading = ref(false);
  const currentAlert = ref(null);
  const showDialog = ref(false);
  const deviceIds = ref([]);  // 所有设备ID
  const deviceId = ref(null);  // 用户输入的设备ID
  const alertId = ref(null);
  const deviceAlerts = ref([]);  // 用于显示设备特定的告警
  const showDeviceAlertDialog = ref(false);

  // 控制表格是否仅显示过滤后的数据
  const isFiltered = ref(false);  // 过滤状态

  // 获取设备告警
  const fetchDeviceAlerts = async () => {
    if (!deviceId.value) {
      ElMessage.error('请输入设备ID');
      return;
    }

    try {
      const res = await searchDeviceAlertsService(deviceId.value);
      console.log("Device Alerts Response: ", res);

      const deviceAlertsData = res.data.data;

      if (deviceAlertsData && deviceAlertsData.length > 0) {
        deviceAlerts.value = deviceAlertsData;
        isFiltered.value = true;
        filteredAlerts.value = deviceAlertsData;
        console.log("deviceAlerts", deviceAlerts);
      } else {
        ElMessage.info('未找到该设备的告警');
      }
    } catch (error) {
      console.log("error",error)
      ElMessage.error('获取设备告警失败');
    }
  };

  const fetchSingleAlerts = async () => {
    if (!alertId.value) {
      ElMessage.error('请输入告警ID');
      return;
    }
    try {
      const res = await getDeviceAlertByIdService(alertId.value);
      console.log("res", res);  // 查看完整的返回对象
      const alertData = res.data.data;  // 获取实际的告警数据部分

      // 判断是否有数据
      if (alertData) {
        // 如果找到数据，则更新告警列表并打开对应的弹窗
        deviceAlerts.value = [alertData];  // 将单条数据包装成数组形式，方便渲染
        filteredAlerts.value = [alertData];  // 只显示这一条告警
        showDeviceAlertDialog.value = true;  // 打开弹窗显示详细信息
        isFiltered.value = true;  // 标记为过滤状态
      } else {
        ElMessage.info('没有找到该告警');
        // 如果没有找到告警，可以选择恢复全部数据
        resetFilter();  // 这里调用resetFilter恢复显示全部数据
      }
    } catch (error) {
      ElMessage.error('获取告警失败');
    }
  };

  // 获取所有告警
  const fetchAlerts = async () => {
    loading.value = true;
    try {
      const res = await getAllDeviceAlertsService();
      alerts.value = res.data.data.map((alert) => ({
        ...alert,
        deviceName: alert.other?.deviceName || '未知设备',
        resolveDuration:
            alert.resolveTime && alert.alertTime
                ? calculateDuration(alert.alertTime, alert.resolveTime)
                : '挂起',
      }));

      // 默认显示所有告警
      filteredAlerts.value = alerts.value;
      //const deviceIds = [...new Set(devices.value.map((device) => device.id))];
      const allDeviceIds = [...new Set(alerts.value.map((alert) => alert.deviceId))];

      const resolvedDeviceIds = allDeviceIds.filter((deviceId) => {
        const deviceAlerts = alerts.value.filter((alert) => alert.deviceId === deviceId);
        return deviceAlerts.length === 0 || deviceAlerts.every((alert) => alert.alertStatus === '已解决');
      });

      console.log("Resolved Device IDs: ", resolvedDeviceIds);
    } catch (error) {
      ElMessage.error('获取告警数据失败');
    }
    loading.value = false;
  };


  // 返回显示所有告警
  const resetFilter = () => {
    isFiltered.value = false;
    filteredAlerts.value = alerts.value;
    fetchAlerts();
    router.push({ path: '/article/auxiliary-monitoring/alert-management'});
  };

  // 根据设备ID过滤告警
  const filterAlertsByDeviceId = () => {
    if (!deviceId.value) {
      filteredAlerts.value = alerts.value;
    } else {
      filteredAlerts.value = alerts.value.filter(alert => alert.deviceId === deviceId.value);
      console.log("filteredAlerts", filteredAlerts);
    }
  };

  const calculateDuration = (alertTime, resolveTime) => {
    const start = new Date(alertTime);
    const end = new Date(resolveTime);
    const diffMs = end - start;

    if (diffMs <= 0) return '无效时间';

    const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

    if (days > 0) return `${days}天 ${hours}小时 ${minutes}分钟`;
    return `${hours}小时 ${minutes}分钟`;
  };

  const fetchDevices = async () => {
    try {
      const res = await artGetAllDevicesService();
      devices.value = res.data.data || [];
      console.log('devices', devices.value)
      const deviceIds = ref([...new Set(devices.value.map((device) => device.id))]);
      console.log("Device IDs:", deviceIds);
      handleDeviceUpdate();
    } catch (error) {
      //ElMessage.error('获取设备数据失败');
      console.log("error", error);
    }
  };

  // 添加告警
  const addAlert = () => {
    const now = new Date();
    const localTime = new Date(now.getTime() + 8 * 60 * 60 * 1000);

    currentAlert.value = {
      deviceId: null,
      description: '',
      alertLevel: '低',
      alertStatus: '挂起',
      resolveMethod: '',
      resolveTime: null,
      alertTime: localTime.toISOString().replace('Z', ''),
      responsiblePerson: '',
    };
    showDialog.value = true;
  };



  // 排序
  const sortAlertLevel = (a, b) => {
    const levelPriority = { '低': 1, '中': 2, '高': 3 };
    return levelPriority[a.alertLevel] - levelPriority[b.alertLevel];  // 升序排序
  };


  // 编辑告警
  const editAlert = (row) => {
    currentAlert.value = { ...row };
    showDialog.value = true;
  };

  // 保存告警
  const saveAlert = async (alert) => {
    try {
      if (!alert.deviceId) {
        ElMessage.error('设备ID不能为空，请选择设备');
        return;
      }
      if (alert.id) {
        await updateDeviceAlertService(alert.id, alert);
        ElMessage.success('告警更新成功');
      } else {
        await addDeviceAlertService(alert);
        ElMessage.success('告警添加成功');
      }

      // 检查告警状态变化并更新设备信息
      if (alert.alertStatus === '已解决') {
        const deviceAlerts = alerts.value.filter(a => a.deviceId === alert.deviceId);
        const allResolved = deviceAlerts.every(a => a.alertStatus === '已解决');

        if (allResolved) {
          console.log(alert.deviceId)
          console.log('update data now')

            const updateData = {
              other: { alert: false }
            };
            await artEditService(updateData, alert.deviceId);
            //这里要调用方法 查询alert.deviceId的设备信息，然后更新设备信息
            //调用artGetDeviceService
            ElMessage.success('设备告警状态已更新');

        }
      }

      await fetchAlerts();
      showDialog.value = false;
    } catch (error) {
      ElMessage.error('操作失败');
    }
  };

  // 删除告警
  const deleteAlert = async (id) => {
    try {
      await ElMessageBox.confirm('确认删除这条告警吗？', '提示', {
        type: 'warning',
      });
      await deleteDeviceAlertService(id);
      ElMessage.success('告警删除成功');
      fetchAlerts();
    } catch (error) {
      ElMessage.error('删除失败');
    }
  };

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (!file) {
      ElMessage.error('请选择一个文件');
      return;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, { type: 'array' });
      const firstSheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[firstSheetName];
      const jsonData = XLSX.utils.sheet_to_json(worksheet);
      ElMessage.success('文件导入成功');
      alerts.value.push(...jsonData);
      filterAlertsByDeviceId();  // 重新过滤
    };
    reader.readAsArrayBuffer(file);
  };

  onMounted(() => {

    const urlParams = new URLSearchParams(window.location.search);
    deviceId.value = urlParams.get('deviceId');  // 获取 URL 参数 deviceId

    if (deviceId.value) {
      fetchDeviceAlerts();  // 使用设备ID查询告警
    } else {
      fetchAlerts();
      fetchDevices();
    }

  });
  </script>

<template>
  <el-card class="main-card">
    <div class="header">
      <h2 class="header-title">
        <el-icon><i class="el-icon-warning"></i></el-icon> 辅机告警管理
      </h2>
      <el-button type="primary" @click="addAlert" class="custom-button">添加告警</el-button>
      <el-button type="success" @click="$refs.fileInput.click()">导入 Excel</el-button>
      <input type="file" ref="fileInput" @change="handleFileChange" style="display: none" />
    </div>

    <!-- 查询部分 -->
    <div style="display: flex; gap: 20px; margin: 10px 0; padding: 10px; border-radius: 8px;">
      <!-- 设备ID查询 -->
      <div style="display: flex; align-items: center; gap: 10px; flex: 1;">
        <el-input
            v-model="deviceId"
            placeholder="输入设备ID进行查询"
            style="flex: 1; max-width: 180px;"
            size="medium"
            clearable
        />
        <el-button
            type="primary"
            @click="fetchDeviceAlerts"
            style="height: 32px; min-width: 120px; padding: 0 20px;"
            size="medium"
        >
          查询设备告警
        </el-button>
      </div>

      <!-- 告警ID查询 -->
      <div style="display: flex; align-items: center; gap: 10px; flex: 1;">
        <el-input
            v-model="alertId"
            placeholder="输入告警ID进行查询"
            style="flex: 1; max-width: 180px;"
            size="medium"
            clearable
        />
        <el-button
            type="primary"
            @click="fetchSingleAlerts"
            style="height: 32px; min-width: 120px; padding: 0 20px;"
            size="medium"
        >
          查询告警
        </el-button>
      </div>
    </div>

    <!-- 其余内容 -->
    <el-button v-if="isFiltered" @click="resetFilter" type="info" style="margin-bottom: 20px;">
      返回显示所有告警
    </el-button>

    <el-table
        :data="filteredAlerts"
        border
        style="width: 100%"
        v-loading="loading"
        class="alert-table"
        stripe
        size="medium"
    >
      <!-- 告警ID -->
      <el-table-column prop="id" label="告警ID" width="80" align="center" sortable />

      <!-- 设备名称 -->
      <el-table-column prop="deviceName" label="设备名称" align="center" sortable />

      <!-- 告警描述 -->
      <el-table-column prop="description" label="告警描述" align="left" />

      <!-- 告警级别 -->
      <el-table-column prop="alertLevel" label="告警级别" align="center" sortable :sort-method="sortAlertLevel">
        <template #default="scope">
          <el-tag
              :class="{
          'tag-high': scope.row.alertLevel === '高',
          'tag-medium': scope.row.alertLevel === '中',
          'tag-low': scope.row.alertLevel === '低',
        }"
          >
            {{ scope.row.alertLevel }}
          </el-tag>
        </template>
      </el-table-column>


      <!-- 发生时间 -->
      <el-table-column prop="alertTime" label="发生时间" align="center" sortable :sort-method="sortByDate" />

      <!-- 解决方法 -->
      <el-table-column prop="resolveMethod" label="解决方法" align="center" />

      <!-- 负责人 -->
      <el-table-column prop="responsiblePerson" label="负责人" align="center" />

      <!-- 处理时间 -->
      <el-table-column prop="resolveDuration" label="处理时间" align="center" sortable :sort-method="sortByDuration" />

      <!-- 状态 -->
      <el-table-column prop="alertStatus" label="状态" align="center">
        <template #default="scope">
          <el-tag
              :type="scope.row.alertStatus === '挂起' ? 'danger' : scope.row.alertStatus === '已解决' ? 'success' : 'warning'"
              disable-transitions
          >
            {{ scope.row.alertStatus }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 操作 -->
      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button type="text" size="small" @click="editAlert(scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="deleteAlert(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>


    <!-- 编辑告警弹窗 -->
    <el-dialog title="编辑告警" v-model="showDialog" width="40%">
      <el-form :model="currentAlert" label-width="100px">
        <el-form-item label="设备">
          <el-select v-model="currentAlert.deviceId" placeholder="选择设备">
            <el-option
                v-for="device in devices"
                :key="device.id"
                :label="device.name"
                :value="device.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="告警描述">
          <el-input v-model="currentAlert.description" />
        </el-form-item>
        <el-form-item label="告警级别">
          <el-select v-model="currentAlert.alertLevel" placeholder="选择告警级别">
            <el-option label="高" value="高" />
            <el-option label="中" value="中" />
            <el-option label="低" value="低" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警状态">
          <el-select v-model="currentAlert.alertStatus" placeholder="选择告警状态">
            <el-option label="挂起" value="挂起" />
            <el-option label="处理中" value="处理中" />
            <el-option label="已解决" value="已解决" />
          </el-select>
        </el-form-item>
        <el-form-item label="解决方法">
          <el-input v-model="currentAlert.resolveMethod" />
        </el-form-item>
        <el-form-item label="解决时间">
          <el-date-picker v-model="currentAlert.resolveTime" type="datetime" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="currentAlert.responsiblePerson" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAlert(currentAlert)">保存</el-button>
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

.header h2 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.alert-table {
  margin-top: 20px;
}

.tag-high {
  background-color: #f5222d !important;
  color: white !important;
}

.tag-medium {
  background-color: #faad14 !important;
  color: white !important;
}

.tag-low {
  background-color: #52c41a !important;
  color: white !important;
}

.el-dialog {
  border-radius: 10px;
}
</style>
