<template>
  <page-container title="概览">
    <!-- Radio group for selecting the cluster -->
    <el-radio-group
      v-model="selectedCluster"
      @change="handleClusterChange"
      class="radio-container"
    >
      <el-radio label="taiyi">taiyi</el-radio>
      <el-radio label="qiming">qiming</el-radio>
    </el-radio-group>

    <!-- CPU Chart container -->
    <div class="chart-container">
      <div class="chart-title">Cluster CPU Usage</div>
      <div ref="cpuChart" class="chart" id="cpuChart"></div>
      <div class="settings-button" @click="showCpuSettings = !showCpuSettings">
        ⋮
      </div>
      <div v-if="showCpuSettings" class="settings-panel">
        <div class="settings-item">
          <label>CPU Prometheus Query:</label>
          <textarea
            v-model="cpuQuery"
            placeholder="Enter Prometheus query"
            rows="2"
            style="resize: vertical"
          ></textarea>
        </div>
        <div class="settings-item">
          <label>Step (seconds):</label>
          <input type="number" v-model.number="cpuStep" />
        </div>
        <button @click="updateCpuChart">Update CPU Chart</button>
      </div>
    </div>

    <!-- GPU Chart container -->
    <div class="chart-container">
      <div class="chart-title">Cluster GPU Usage</div>
      <div ref="gpuChart" class="chart" id="gpuChart"></div>
      <div class="settings-button" @click="showGpuSettings = !showGpuSettings">
        ⋮
      </div>
      <div v-if="showGpuSettings" class="settings-panel">
        <div class="settings-item">
          <label>GPU Prometheus Query:</label>
          <textarea
            v-model="gpuQuery"
            placeholder="Enter Prometheus query"
            rows="2"
            style="resize: vertical"
          ></textarea>
        </div>
        <div class="settings-item">
          <label>Step (seconds):</label>
          <input type="number" v-model.number="gpuStep" />
        </div>
        <button @click="updateGpuChart">Update GPU Chart</button>
      </div>
    </div>
    <!-- User Chart container -->
    <div class="chart-container">
      <div class="chart-title">Logged-in Users</div>
      <div ref="userChart" class="chart" id="userChart"></div>
      <div
        class="settings-button"
        @click="showUserSettings = !showUserSettings"
      >
        ⋮
      </div>
      <div v-if="showUserSettings" class="settings-panel">
        <div class="settings-item">
          <label>User Prometheus Query:</label>
          <textarea
            v-model="userQuery"
            placeholder="Enter Prometheus query"
            rows="2"
            style="resize: vertical"
          ></textarea>
        </div>
        <div class="settings-item">
          <label>Step (seconds):</label>
          <input type="number" v-model.number="userStep" />
        </div>
        <button @click="updateUserChart">Update User Chart</button>
      </div>
    </div>
  </page-container>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed, shallowRef } from 'vue'
import * as echarts from 'echarts'
import PageContainer from '@/components/pageContainer.vue'
import { getServerURL } from '@/utils/request.js'

// Reactive reference for the cluster name
const selectedCluster = ref('taiyi')

// CPU chart related refs
const cpuChartInstance = shallowRef(null)
const cpuQuery = computed(
  () =>
    `(
    sum(
      (
        1 - (sum(irate(node_cpu_seconds_total{mode="idle", cluster="${selectedCluster.value}"}[5m])) by (instance, cpu) /
             sum(irate(node_cpu_seconds_total{cluster="${selectedCluster.value}"}[5m])) by (instance, cpu))
      )
    ) /
    sum(count(node_cpu_seconds_total{cluster="${selectedCluster.value}", mode="idle"}) by (instance, cpu))
  ) * 100`
) // default CPU query
const cpuEndTime = ref(Math.floor(Date.now() / 1000))
const cpuStartTime = ref(cpuEndTime.value - 24 * 3600)
const cpuStep = ref(60) // Default to 1-minute interval

// User chart related refs
const userChartInstance = shallowRef(null)
const userQuery = computed(
  () => `logged_in_users{cluster="${selectedCluster.value}"}`
)
const userEndTime = ref(Math.floor(Date.now() / 1000))
const userStartTime = ref(userEndTime.value - 24 * 3600)
const userStep = ref(60) // Default to 1-minute interval

// GPU chart related refs
const gpuChartInstance = shallowRef(null)
const gpuQuery = computed(
  () => `gpu_utilization_percentage{cluster="${selectedCluster.value}"}`
)
const gpuEndTime = ref(Math.floor(Date.now() / 1000))
const gpuStartTime = ref(cpuEndTime.value - 24 * 3600)
const gpuStep = ref(60) // Default to 1-minute interval

// User details query
const userDetailQuery = computed(
  () => `logged_in_user_details{cluster="${selectedCluster.value}"}`
)

// Control visibility of settings panels
const showCpuSettings = ref(false)
const showUserSettings = ref(false)
const showGpuSettings = ref(false)

// Fetch Prometheus data
const fetchPrometheusData = async (
  query,
  endpoint = 'query_range',
  params = {}
) => {
  try {
    const baseURL = getServerURL()
    let url = `${baseURL}:9090/api/v1/${endpoint}?query=${encodeURIComponent(query)}`
    for (const [key, value] of Object.entries(params)) {
      url += `&${key}=${value}`
    }

    const response = await fetch(url)
    const result = await response.json()

    if (result.status === 'success') {
      return result.data.result
    } else {
      console.error('Prometheus query failed:', result.error)
      return []
    }
  } catch (error) {
    console.error('Error fetching data:', error)
    return []
  }
}
// Fetch query data for user details with user count
const fetchUserDetailData = async (query, startTime, endTime, step) => {
  const result = await fetchPrometheusData(query, 'query_range', {
    start: startTime,
    end: endTime,
    step: step
  })

  if (result.length > 0) {
    // Object to store users per timestamp
    const usersAtTimestamp = {}

    // Generate all the timestamps within the range (step by step)
    const allTimestamps = []
    for (let t = startTime; t <= endTime; t += step) {
      const timeKey = new Date(t * 1000).toISOString()
      allTimestamps.push(timeKey)

      // Initialize the timestamp entry with empty data (if no data yet)
      if (!usersAtTimestamp[timeKey]) {
        usersAtTimestamp[timeKey] = {
          timestamp: new Date(t * 1000),
          usernames: [],
          count: 0
        }
      }
    }

    // Map over the results and extract the usernames where the value is 1
    result[0].values.forEach(([timestamp, value]) => {
      if (parseFloat(value) === 1) {
        // Check if user is online
        const user = result[0].metric.username
        const timeKey = new Date(timestamp * 1000).toISOString()

        // Add the username and increment the count for this timestamp
        usersAtTimestamp[timeKey].usernames.push(user)
        usersAtTimestamp[timeKey].count += 1
      }
    })

    // Convert the object into an array, sorting by timestamp
    return allTimestamps
      .map((timeKey) => usersAtTimestamp[timeKey])
      .sort((a, b) => a.timestamp - b.timestamp)
  } else {
    console.warn('Query succeeded but returned no data.')
    return []
  }
}

// Fetch query data for charts
const fetchQueryData = async (query, startTime, endTime, step) => {
  const result = await fetchPrometheusData(query, 'query_range', {
    start: startTime,
    end: endTime,
    step: step
  })

  if (result.length > 0) {
    return result[0].values.map(([timestamp, value]) => ({
      timestamp: new Date(timestamp * 1000),
      value: parseFloat(value)
    }))
  } else {
    console.warn('Query succeeded but returned no data.')
    return []
  }
}

// Render chart using ECharts
const renderChart = (chartRef, chartId, label, data) => {
  const chartDom = document.getElementById(chartId)
  if (!chartDom) return

  // Destroy previous chart instance if it exists
  if (chartRef.value) {
    chartRef.value.dispose()
  }

  // Initialize chart
  chartRef.value = echarts.init(chartDom)

  const option = {
    title: {
      text: label,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '10%',
      right: '10%',
      top: '15%',
      bottom: '20%'
    },
    xAxis: {
      type: 'time',
      boundaryGap: false,
      axisLabel: {
        formatter: '{yyyy}-{MM}-{dd} {hh}:{mm}'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: label,
        type: 'line',
        data: data.map((point) => [point.timestamp, point.value]),
        lineStyle: {
          width: 2
        },
        showSymbol: false,
        smooth: true
      }
    ],
    dataZoom: [
      {
        type: 'slider',
        show: true,
        xAxisIndex: 0,
        start: 0,
        end: 100,
        bottom: '10%'
      },
      {
        type: 'inside',
        xAxisIndex: [0],
        start: 0,
        end: 100
      }
    ]
  }

  chartRef.value.setOption(option)
}

// Update charts
const updateCpuChart = async () => {
  const cpuData = await fetchQueryData(
    cpuQuery.value,
    cpuStartTime.value,
    cpuEndTime.value,
    cpuStep.value
  )
  renderChart(
    cpuChartInstance,
    'cpuChart',
    'CPU Usage (%)',
    cpuData,
    'CPU Usage (%)'
  )
}

const updateUserChart = async () => {
  // Fetch logged-in usernames at each time point
  const userDetail = await fetchUserDetailData(
    userDetailQuery.value,
    userStartTime.value,
    userEndTime.value,
    userStep.value
  )

  // Map the userDetail data to include only timestamp and user count for initial rendering
  const userCountData = userDetail.map((data) => {
    return {
      timestamp: data.timestamp, // Timestamp of the data point
      value: data.count // User count
    }
  })

  // Initial chart render with just the user count data
  renderChart(
    userChartInstance,
    'userChart',
    'Logged-in Users',
    userCountData,
    'Number of Users'
  )

  // After initial render, we update the tooltip with the usernames
  const option = userChartInstance.value.getOption()

  // Update the tooltip formatter to display usernames
  option.tooltip = {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    },
    formatter: (params) => {
      const [timestamp, value] = params[0].data // Extract data from the point
      const usernames =
        userDetail.find((item) => item.timestamp === timestamp)?.usernames || [] // Find usernames for this timestamp
      console.log(timestamp)
      return `
        <strong>Time:</strong> ${new Date(timestamp).toLocaleString()}<br/>
        <strong>User Count:</strong> ${value} Users<br/>
        <strong>Usernames:</strong> ${usernames.join(', ')}
      `
    }
  }

  // Set the updated option with the new tooltip
  userChartInstance.value.setOption(option)
}

const updateGpuChart = async () => {
  const gpuData = await fetchQueryData(
    gpuQuery.value,
    gpuStartTime.value,
    gpuEndTime.value,
    gpuStep.value
  )
  renderChart(
    gpuChartInstance,
    'gpuChart',
    'GPU Usage (%)',
    gpuData,
    'GPU Usage (%)'
  )
}

const handleClusterChange = () => {
  updateCpuChart()
  updateUserChart()
  updateGpuChart()
}

const updateTimes = () => {
  const now = Math.floor(Date.now() / 1000)
  cpuEndTime.value = now
  cpuStartTime.value = now - 24 * 3600
  userEndTime.value = now
  userStartTime.value = now - 24 * 3600
  gpuEndTime.value = now
  gpuStartTime.value = now - 24 * 3600
}

onMounted(() => {
  updateTimes()
  handleClusterChange()
})

onBeforeUnmount(() => {
  document.removeEventListener('resize', () => {
    cpuChartInstance.value && cpuChartInstance.value.resize()
    userChartInstance.value && userChartInstance.value.resize()
    gpuChartInstance.value && gpuChartInstance.value.resize()
  })
})
</script>

<style scoped>
body {
  font-family: Arial, sans-serif;
  margin: 20px;
}

.chart-container {
  width: 100%;
  max-width: 1200px;
  height: 500px;
  margin: 0 auto 30px;
  position: relative;
}

.chart-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
  text-align: center;
}

.radio-container {
  margin-bottom: 20px;
}

.settings-button {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 20px;
  cursor: pointer;
  background: none;
  border: none;
  outline: none;
}

.settings-panel {
  position: absolute;
  top: 50px;
  left: 10px;
  background-color: #ffffff;
  border: 1px solid #ddd;
  padding: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
  display: flex;
  flex-direction: column;
  border-radius: 5px;
}

.settings-item {
  margin-bottom: 10px;
}

.settings-panel label {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.settings-panel input,
.settings-panel textarea {
  padding: 5px;
  border-radius: 3px;
  border: 1px solid #ccc;
  box-sizing: border-box;
}

.settings-panel textarea {
  width: 100%;
  resize: vertical;
  min-height: 60px;
}

.settings-panel button {
  width: 100%;
  padding: 8px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 14px;
}

.settings-panel button:hover {
  background-color: #0056b3;
}

.chart {
  width: 100%;
  height: 100%;
}
</style>
