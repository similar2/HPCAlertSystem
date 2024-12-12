<script setup>
import { ref, onMounted } from 'vue';
import { getAllDeviceAlertsService } from '@/api/Warning';
import * as echarts from 'echarts';

const statistics = ref({
  weekly: [],
  monthly: [],
  byPerson: [],
  byType: [],
  byDevice: [],
});

const loading = ref(false);
const selectedDimension = ref('weekly'); // 默认维度为“按周统计”

const chartInstance = ref(null);
const selectedMonth = ref(''); // 当前选中的月份
const availableMonths = ref([]); // 动态生成的月份选项


// 获取完整的月份或周数数组，用于补全数据

const getDynamicMonths = (alertsData) => {
  const months = new Set(
      alertsData.map((alert) => {
        const date = new Date(alert.alertTime);
        return `${date.getFullYear()}-${date.getMonth() + 1}`;
      })
  );

  const sortedMonths = Array.from(months).sort();
  if (sortedMonths.length < 6) {
    const year = new Date().getFullYear();
    const allMonths = Array.from({ length: 12 }, (_, i) => `${year}-${i + 1}`);
    const first = Math.max(0, allMonths.indexOf(sortedMonths[0]) - 2);
    const last = Math.min(allMonths.length - 1, allMonths.indexOf(sortedMonths[sortedMonths.length - 1]) + 2);
    return allMonths.slice(first, last + 1);
  }

  return sortedMonths;
};



const getDynamicWeeks = (alertsData) => {
  const months = new Set(
      alertsData.map((alert) => {
        const date = new Date(alert.alertTime);
        return `${date.getFullYear()}-${date.getMonth() + 1}`; // 按年-月分组
      })
  );

  return Array.from(months).sort(); // 返回完整的 "2024-11" 等标签
};



const initChart = () => {
  const chartDom = document.getElementById('statistics-chart');
  chartInstance.value = echarts.init(chartDom);
};

const updateChart = (data, title) => {
  if (!chartInstance.value) return;

  const option = {
    title: {
      text: title,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333',
      },
    },
    tooltip: {
      trigger: 'item',
    },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.label),
      axisLabel: {
        rotate: 45,
        textStyle: {
          fontSize: 12,
        },
      },
    },
    yAxis: {
      type: 'value',
      name: '数量',
      nameTextStyle: {
        fontSize: 12,
      },
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '20%',
    },
    series: [
      {
        data: data.map((item) => item.value),
        type: 'bar',
        barWidth: data.length > 10 ? '20%' : '50%', // 数据多时自动缩小柱宽
        itemStyle: {
          color: '#409EFF',
          borderRadius: [5, 5, 0, 0],
        },
      },
    ],
  };

  chartInstance.value.setOption(option);
};

// 获取统计数据
const fetchStatistics = async () => {
  loading.value = true;
  try {
    const res = await getAllDeviceAlertsService();
    const alertsData = res.data.data;

    const dynamicMonths = getDynamicMonths(alertsData);
    availableMonths.value = dynamicMonths; // 动态生成月份列表
    selectedMonth.value = dynamicMonths[0] || ''; // 默认选中第一个月份

    statistics.value.weekly = calculateWeeklyStats(alertsData, getDynamicWeeks(alertsData));
    statistics.value.monthly = calculateMonthlyStats(alertsData, dynamicMonths);

    statistics.value.byPerson = calculateStatsByPerson(alertsData);
    statistics.value.byType = calculateStatsByType(alertsData);
    statistics.value.byDevice = calculateStatsByDevice(alertsData);

    handleDimensionChange(); // 初始化默认维度
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
  loading.value = false;
};


// 统计计算逻辑
const calculateMonthlyStats = (alertsData, fullMonths) => {
  const monthGroups = groupBy(alertsData, (alert) => {
    const date = new Date(alert.alertTime);
    return `${date.getFullYear()}-${date.getMonth() + 1}`;
  });

  // 补全数据
  return fullMonths.map((month) => ({
    label: month,
    value: monthGroups[month]?.length || 0, // 如果没有数据，填充为 0
  }));
};

const calculateWeeklyStats = (alertsData, fullWeeks) => {
  const weekGroups = groupBy(alertsData, (alert) => {
    const date = new Date(alert.alertTime);
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 月份从 0 开始
    const monthStart = new Date(year, month - 1, 1); // 当月第一天
    const week = Math.ceil(((date - monthStart) / 86400000 + 1) / 7); // 计算当月第几周
    console.log('Alert:', alert, 'Grouped as:', `${year}-${month}-${week}`); // Debug 输出
    return `${year}-${month}-${week}`; // 分组键为 "2024-11-3"
  });

  // 确保每个月的 1-4 周完整
  const weeks = ['第1周', '第2周', '第3周', '第4周']; // 月内固定 4 周
  return weeks.map((week, index) => ({
    label: `${fullWeeks}-${index + 1}`, // 对应分组键
    value: weekGroups[`${fullWeeks}-${index + 1}`]?.length || 0, // 如果没有数据，补 0
  }));
};




const calculateStatsByPerson = (alertsData) => {
  const personGroups = groupBy(alertsData, (alert) => alert.responsiblePerson || '未分配');

  return Object.keys(personGroups).map((person) => ({
    label: person,
    value: personGroups[person].length,
  }));
};

const calculateStatsByType = (alertsData) => {
  const typeGroups = groupBy(alertsData, (alert) => alert.description);

  return Object.keys(typeGroups).map((type) => ({
    label: type,
    value: typeGroups[type].length,
  }));
};

const calculateStatsByDevice = (alertsData) => {
  const deviceGroups = groupBy(alertsData, (alert) => alert.deviceId);

  return Object.keys(deviceGroups).map((deviceId) => ({
    label: `设备 ${deviceId}`,
    value: deviceGroups[deviceId].length,
  }));
};

// 分组工具函数
const groupBy = (array, keyFn) => {
  return array.reduce((acc, item) => {
    const key = keyFn(item);
    if (!acc[key]) acc[key] = [];
    acc[key].push(item);
    return acc;
  }, {});
};

// 切换统计维度
const handleDimensionChange = () => {
  let data = [];
  let title = '';

  switch (selectedDimension.value) {
    case 'weekly':
      const weeklyData = statistics.value.weekly.filter(
          (item) => item.label.startsWith(selectedMonth.value) // 筛选出当前月份的周数据
      );
      const weeks = ['第1周', '第2周', '第3周', '第4周']; // 固定周次
      data = weeks.map((week, index) => ({
        label: week,
        value: weeklyData[index]?.value || 0, // 补全没有数据的周次为0
      }));
      title = `按周统计 (${selectedMonth.value})`;
      console.log('Weekly data:', data); // Debug output
      break;
    case 'monthly':
      data = statistics.value.monthly;
      title = '按月统计';
      break;
    case 'byPerson':
      data = statistics.value.byPerson;
      title = '按负责人统计';
      break;
    case 'byType':
      data = statistics.value.byType;
      title = '按类型统计';
      break;
    case 'byDevice':
      data = statistics.value.byDevice;
      title = '按设备统计';
      break;
  }

  updateChart(data, title);
};


onMounted(() => {
  initChart();
  fetchStatistics();
});
</script>

<template>
  <div style="display: flex; flex-direction: column; gap: 20px; padding: 20px; background-color: #f9f9f9;">
    <header style="display: flex; justify-content: space-between; align-items: center;">
      <h2 style="margin: 0; font-size: 24px; color: #333;">统计分析</h2>
      <button @click="fetchStatistics" style="padding: 10px 20px; background-color: #409EFF; color: #fff; border: none; border-radius: 4px; cursor: pointer;">
        刷新数据
      </button>
    </header>

    <section style="display: flex; gap: 20px;">
      <aside style="width: 300px; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);">
        <h3 style="margin-top: 0; font-size: 18px;">选择统计维度</h3>
        <el-select v-model="selectedDimension" placeholder="请选择统计维度" style="width: 100%; margin-bottom: 20px;" @change="handleDimensionChange">
          <el-option label="按周统计" value="weekly" />
          <el-option label="按月统计" value="monthly" />
          <el-option label="按负责人统计" value="byPerson" />
          <el-option label="按类型统计" value="byType" />
          <el-option label="按设备统计" value="byDevice" />
        </el-select>
        <el-select
            v-if="selectedDimension === 'weekly'"
            v-model="selectedMonth"
            placeholder="请选择月份"
            style="width: 100%; margin-bottom: 20px;"
            @change="handleDimensionChange"
        >
          <el-option v-for="month in availableMonths" :key="month" :label="month" :value="month" />
        </el-select>
        <p style="color: #666;">选择维度以查看对应数据</p>
      </aside>

      <div style="flex: 1; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);">
        <div id="statistics-chart" style="width: 100%; height: 400px;"></div>
      </div>
    </section>
  </div>
</template>
