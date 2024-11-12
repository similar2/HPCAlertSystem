<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { Chart, registerables } from 'chart.js'
import 'chartjs-adapter-date-fns'
import PageContainer from '@/components/pageContainer.vue'
import { getBaseURL } from '@/utils/request.js'
import { formatToDateTimeLocal } from '@/utils/format.js'

Chart.register(...registerables)

// CPU chart related refs
const cpuChartInstance = ref(null)
const cpuQuery = ref(
  '100 - (avg by(instance) (irate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)'
) //default cpu query
const cpuEndTime = ref(Math.floor(Date.now() / 1000) - 24 * 3600) // Default to 24 hours ago
const cpuStartTime = ref(cpuEndTime.value - 24 * 3600) // Default to 48 hours ago
const cpuStep = ref(60) // Default to 1-minute interval

// Logged-in user chart related refs
const userChartInstance = ref(null)
const userQuery = ref('logged_in_users') // Replace with your actual logged-in user query
const userEndTime = ref(cpuEndTime.value) // Align end time with CPU chart
const userStartTime = ref(cpuStartTime.value) // Align start time with CPU chart
const userStep = ref(60) // Default to 1-minute interval

const showCpuSettings = ref(false)
const showUserSettings = ref(false) // Control visibility of the settings panel


// CPU formatted start and end times
const formattedCpuStartTime = ref(formatToDateTimeLocal(cpuStartTime.value))
const formattedCpuEndTime = ref(formatToDateTimeLocal(cpuEndTime.value))

// User formatted start and end times
const formattedUserStartTime = ref(formatToDateTimeLocal(userStartTime.value))
const formattedUserEndTime = ref(formatToDateTimeLocal(userEndTime.value))

// Update Unix timestamp when input changes for CPU chart
const updateCpuStartTime = () => {
  cpuStartTime.value = Math.floor(
    new Date(formattedCpuStartTime.value).getTime() / 1000
  )
}

const updateCpuEndTime = () => {
  cpuEndTime.value = Math.floor(
    new Date(formattedCpuEndTime.value).getTime() / 1000
  )
}

// Update Unix timestamp when input changes for User chart
const updateUserStartTime = () => {
  userStartTime.value = Math.floor(
    new Date(formattedUserStartTime.value).getTime() / 1000
  )
}

const updateUserEndTime = () => {
  userEndTime.value = Math.floor(
    new Date(formattedUserEndTime.value).getTime() / 1000
  )
}

const fetchQueryData = async (query, startTime, endTime, step) => {
  try {
    const baseURL = getBaseURL()
    const url = `${baseURL}:9090/api/v1/query_range?query=${encodeURIComponent(
      query
    )}&start=${startTime}&end=${endTime}&step=${step}`
    const response = await fetch(url)
    const result = await response.json()

    if (result.status === 'success') {
      return result.data.result[0].values.map(([timestamp, value]) => ({
        timestamp: new Date(timestamp * 1000),
        value: parseFloat(value)
      }))
    } else {
      console.error('Prometheus query failed:', result.error)
      return []
    }
  } catch (error) {
    console.error('Error fetching data:', error)
    return []
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

document.addEventListener('click', handleClickOutside)

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})

onMounted(() => {
  updateCpuChart()
  updateUserChart()
})
</script>

<template>
  <page-container title="概览">
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
          <label>Start Time:</label>
          <input
            type="datetime-local"
            v-model="formattedCpuStartTime"
            @change="updateCpuStartTime"
          />
        </div>
        <div class="settings-item">
          <label>End Time:</label>
          <input
            type="datetime-local"
            v-model="formattedCpuEndTime"
            @change="updateCpuEndTime"
          />
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
          <label>Start Time:</label>
          <input
            type="datetime-local"
            v-model="formattedUserStartTime"
            @change="updateUserStartTime"
          />
        </div>
        <div class="settings-item">
          <label>End Time:</label>
          <input
            type="datetime-local"
            v-model="formattedUserEndTime"
            @change="updateUserEndTime"
          />
        </div>
        <div class="settings-item">
          <label>Step (seconds):</label>
          <input type="number" v-model.number="userStep" />
        </div>
        <button @click="updateUserChart">Update User Chart</button>
      </div>
      <canvas id="userChart"></canvas>
    </div>
  </page-container>
</template>

<style lang="scss" scoped>
.chart-container {
  position: relative;
  padding: 20px;
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
