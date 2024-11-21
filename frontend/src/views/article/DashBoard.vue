<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { Chart, registerables } from 'chart.js'
import 'chartjs-adapter-date-fns'
import PageContainer from '@/components/pageContainer.vue'
import { getServerURL } from '@/utils/request.js'

Chart.register(...registerables)

// Reactive reference for the cluster name
const selectedCluster = ref('taiyi')

// CPU chart related refs
const cpuChartInstance = ref(null)
const cpuQuery = computed(
  () =>
    `100 - (avg by(instance) (irate(node_cpu_seconds_total{mode="idle", cluster ="${selectedCluster.value}"}[5m])) * 100)`
) //default cpu query
const cpuEndTime = ref(Math.floor(Date.now() / 1000)) // Default to 24 hours ago
const cpuStartTime = ref(cpuEndTime.value - 24 * 3600) // Default to 48 hours ago
const cpuStep = ref(60) // Default to 1-minute interval

// Logged-in user chart related refs
const userChartInstance = ref(null)
const userQuery = computed(
  () => `logged_in_users{cluster="${selectedCluster.value}"}`
)
const userEndTime = ref(Math.floor(Date.now() / 1000)) // Align end time with CPU chart
const userStartTime = ref(userEndTime.value - 24 * 3600) // Align start time with CPU chart
const userStep = ref(60) // Default to 1-minute interval

const userDetailQuery = computed(
  () => `logged_in_user_details{cluster="${selectedCluster.value}"}`
)

const showCpuSettings = ref(false)
const showUserSettings = ref(false) // Control visibility of the settings panel

// Refs for the user data
const loggedInUsernames = ref([])

const fetchPrometheusData = async (
  query,
  endpoint = 'query_range',
  params = {}
) => {
  try {
    const baseURL = getServerURL()
    let url = `${baseURL}:9090/api/v1/${endpoint}?query=${encodeURIComponent(query)}`
    console.log(query)
    // Add additional parameters if provided (e.g., start, end, step)
    for (const [key, value] of Object.entries(params)) {
      url += `&${key}=${value}`
    }

    const response = await fetch(url)
    const result = await response.json()

    console.log('Query URL:', url)
    console.log('Raw Response:', result)

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

const fetchLoggedInUsernames = async () => {
  console.log(userQuery.value)
  const result = await fetchPrometheusData(userDetailQuery.value, 'query')

  if (result.length > 0) {
    loggedInUsernames.value = result.map((entry) => entry.metric.username)
  } else {
    console.warn('Query succeeded but returned no data.')
    loggedInUsernames.value = []
  }
}

const renderChart = (chartRef, chartId, label, data, yLabel) => {
  const ctx = document.getElementById(chartId).getContext('2d')
  if (chartRef.value) {
    chartRef.value.destroy()
  }

  chartRef.value = new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.map((point) => point.timestamp),
      datasets: [
        {
          label,
          data: data.map((point) => point.value),
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 2,
          fill: false
        }
      ]
    },
    options: {
      responsive: true,
      scales: {
        x: {
          type: 'time',
          title: {
            display: true,
            text: 'Time'
          }
        },
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: yLabel
          }
        }
      }
    }
  })
}

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
  const userData = await fetchQueryData(
    userQuery.value,
    userStartTime.value,
    userEndTime.value,
    userStep.value
  )
  renderChart(
    userChartInstance,
    'userChart',
    'Logged-in Users',
    userData,
    'Number of Users'
  )
}
const updateUserNames = async () => {
  await fetchLoggedInUsernames()
}
// Function to handle clicks outside the settings panel
const handleClickOutside = (event) => {
  if (
    !event.target.closest('.settings-panel') &&
    !event.target.closest('.settings-button')
  ) {
    showCpuSettings.value = false
    showUserSettings.value = false
  }
}
// Handle cluster change to update charts
const handleClusterChange = () => {
  updateCpuChart()
  updateUserChart()
  updateUserNames()
}

// Function to update the times dynamically
const updateTimes = () => {
  cpuEndTime.value = Math.floor(Date.now() / 1000) // Recalculate 24 hours ago
  cpuStartTime.value = cpuEndTime.value - 24 * 3600 // Recalculate 48 hours ago
  userEndTime.value = Math.floor(Date.now() / 1000)
  userStartTime.value = userEndTime.value - 24 * 3600
}
document.addEventListener('click', handleClickOutside)

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})

onMounted(() => {
  updateUserNames()
  updateTimes()
  updateCpuChart()
  updateUserChart()
})
</script>

<template>
  <page-container title="概览">
    <!-- Radio group for selecting the cluster -->
    <el-radio-group
      v-model="selectedCluster"
      @change="handleClusterChange"
      class="cluster-selector"
    >
      <el-radio label="taiyi">taiyi</el-radio>
      <el-radio label="qiming">qiming</el-radio>
    </el-radio-group>

    <!-- CPU Chart container -->
    <div class="chart-container">
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
      <canvas id="cpuChart"></canvas>
    </div>

    <!-- User Chart container -->
    <div class="chart-container">
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
      <canvas id="userChart"></canvas>
      <h3>Currently Logged-in Users</h3>
      <ul>
        <li v-for="user in loggedInUsernames" :key="user">{{ user }}</li>
      </ul>
    </div>
  </page-container>
</template>

<style lang="scss" scoped>
.chart-container {
  position: relative;
  padding: 20px;
}

.radio-container {
  margin-bottom: 20px; /* Adds space between the radio group and the charts */
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
  width: 100%;
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
</style>
