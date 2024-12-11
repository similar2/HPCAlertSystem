<template>
  <el-drawer
      v-model="visibleDrawer"
      title="添加报警规则"
      direction="rtl"
      size="50%"
  >
    <el-form
        :model="formModel"
        ref="formRef"
        :rules="rules"
        label-width="100px"
    >
      <el-form-item label="硬件类型" prop="type">
        <el-select v-model="formModel.type" placeholder="请输入硬件类型">
          <el-option label="BMC" value="BMC"></el-option>
          <el-option label="HOST" value="HOST"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="告警名" prop="alertName">
        <el-input
            v-model="formModel.alertName"
            placeholder="请输入告警名"
        ></el-input>
      </el-form-item>
      <el-form-item label="表达式" prop="expr">
        <el-autocomplete
            v-model="formModel.expr"
            placeholder="请输入表达式"
            :fetch-suggestions="fetchSuggestions"
        ></el-autocomplete>
      </el-form-item>

      <el-form-item label="告警严重度" prop="severity">
        <el-select
            v-model="formModel.severity"
            placeholder="请选择告警严重程度"
        >
          <el-option label="NOTE" value="NOTE"></el-option>
          <el-option label="LOW" value="LOW"></el-option>
          <el-option label="HIGH" value="HIGH"></el-option>
          <el-option label="CRITICAL" value="CRITICAL"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="持续时间" prop="timeDuration">
        <el-input
            v-model="formModel.timeDuration"
            placeholder="请输入持续时间"
        ></el-input>
      </el-form-item>

      <el-form-item label="说明" prop="description">
        <el-input
            v-model="formModel.description"
            placeholder="请输入说明"
        ></el-input>
      </el-form-item>

      <el-form-item>
        <el-button @click="onPublish()" typeName="primary">添加</el-button>
      </el-form-item>

    </el-form>
  </el-drawer>
</template>

<style scoped>
/* Add custom styles if necessary */
</style>
<script setup>
import { ref, onMounted } from 'vue'
import { fetchDynamicSuggestions } from '@/utils/fetchMetricsName.js'

// Reactive state variables
const visibleDrawer = ref(false)
const formModel = ref({
  type: '',
  alertName: '',
  metricName: '',
  expr: '',
  severity: '',
  timeDuration: '',
  description: '',
  filters: ''
})

const rules = {
  type: [{ required: true, message: '请输入type', trigger: 'blur' }],
  alertName: [{ required: true }],
  metricName: [{ required: true }],
  expr: [{ required: true, message: '请输入表达式', trigger: 'blur' }],
  severity: [{ required: true }],
  description: [{ required: true }],
  filters: [{ required: true }]
}


// Method to open the drawer and reset form data
const open = () => {
  visibleDrawer.value = true
  formModel.value = {
    type: '',
    alertName: '',
    metricName: '',
    expr: '',
    severity: '',
    timeDuration: '',
    description: '',
    filters: ''
  }
}
onMounted(() => {
      fetchDynamicSuggestions((results) => {
        // Ensure `results` is an array and add it to the existing `suggestions`
        if (Array.isArray(results)) {
          results.forEach((result, index) => {
            console.log(`Result ${index} (type: ${typeof result}):`, result)
          })

          // Extract the `value` property if `results` are objects
          const stringResults = results.map((result) => {
            // Check if the `value` property exists and is a string
            if (result && typeof result.value === 'string') {
              return result.value // Extract the metric name from the `value` field
            } else {
              console.warn('Skipping non-string or missing value field:', result)
              return null // Handle as needed
            }
          })
              .filter(Boolean) // Remove any null/undefined results

          suggestions.value.push(...stringResults) // Add new results to the existing array
        }
      })
    }
)
const fetchSuggestions = (queryString, callback) => {
  try {
    if (queryString) {
      // Split the input by spaces and use the last segment for matching
      const parts = queryString.split(' ')
      const lastPart = parts[parts.length - 1]

      // Filter results that match the last part as a prefix
      const results = suggestions.value.filter((item) => {
        // Ensure item is a string before calling `toLowerCase()`
        if (typeof item === 'string') {
          return item.toLowerCase().includes(lastPart.toLowerCase())
        } else {
          console.warn('Skipping non-string item:', item)
          return false // Skip non-string items
        }
      })

      // Map results to the correct format and pass them to the callback
      callback(results.map((item) => ({ value: item })))
    } else {
      callback([]) // Return an empty array if the query string is empty
    }
  } catch (error) {
    console.error('Error while fetching suggestions:', error)
    callback([]) // Return an empty array in case of an exception
  }
}

// Expose the `open` method to be called from the parent component
defineExpose({
  open
})
// Reactive array for autocomplete suggestions
const suggestions = ref([
  'and', 'or', 'unless',
  'sum', 'min', 'max', 'avg', 'count', 'stddev', 'stdvar', 'bottomk', 'topk', 'quantile',
  'abs()', 'absent()', 'ceil()', 'floor()', 'clamp_max()', 'clamp_min()', 'rate()', 'irate()',
  'delta()', 'increase()', 'deriv()', 'predict_linear()', 'histogram_quantile()',
  'time()', 'day_of_week()', 'hour()', 'minute()',
  'on()', 'ignoring()',
  'label_replace()', 'vector()', 'sort()', 'sort_desc()',
  'count_over_time()', 'avg_over_time()', 'sum_over_time()', 'min_over_time()', 'max_over_time()', 'quantile_over_time()',
  'ALERTS', 'ALERTS_FOR_STATE', 'go_gc_duration_seconds', 'go_gc_duration_seconds_count',
  'go_gc_duration_seconds_sum', 'go_goroutines', 'go_info', 'go_memstats_alloc_bytes',
  'go_memstats_alloc_bytes_total', 'go_memstats_buck_hash_sys_bytes', 'go_memstats_frees_total',
  'go_memstats_gc_sys_bytes', 'go_memstats_heap_alloc_bytes', 'go_memstats_heap_idle_bytes',
  'go_memstats_heap_inuse_bytes', 'go_memstats_heap_objects', 'go_memstats_heap_released_bytes',
  'go_memstats_heap_sys_bytes', 'go_memstats_last_gc_time_seconds', 'go_memstats_lookups_total',
  'go_memstats_mallocs_total', 'go_memstats_mcache_inuse_bytes', 'go_memstats_mcache_sys_bytes',
  'go_memstats_mspan_inuse_bytes', 'go_memstats_mspan_sys_bytes', 'go_memstats_next_gc_bytes',
  'go_memstats_other_sys_bytes', 'go_memstats_stack_inuse_bytes', 'go_memstats_stack_sys_bytes',
  'go_memstats_sys_bytes', 'go_threads', 'ipmi_bmc_info', 'ipmi_chassis_cooling_fault_state',
  'ipmi_chassis_drive_fault_state', 'ipmi_chassis_power_state', 'ipmi_fan_speed_rpm',
  'ipmi_fan_speed_state', 'ipmi_power_state', 'ipmi_power_watts', 'ipmi_scrape_duration_seconds',
  'ipmi_sensor_state', 'ipmi_sensor_value', 'ipmi_temperature_celsius', 'ipmi_temperature_state',
  'ipmi_up', 'ipmi_voltage_state', 'ipmi_voltage_volts', 'logged_in_users', 'node_arp_entries',
  'node_boot_time_seconds', 'node_context_switches_total', 'node_cooling_device_cur_state',
  'node_cooling_device_max_state', 'node_cpu_core_throttles_total', 'node_cpu_frequency_max_hertz',
  'node_cpu_frequency_min_hertz', 'node_cpu_guest_seconds_total', 'node_cpu_package_throttles_total',
  'node_cpu_scaling_frequency_hertz', 'node_cpu_scaling_frequency_max_hertz',
  'node_cpu_scaling_frequency_min_hertz', 'node_cpu_scaling_governor', 'node_cpu_seconds_total',
  'node_disk_info', 'node_disk_io_now', 'node_disk_io_time_seconds_total',
  'node_disk_io_time_weighted_seconds_total', 'node_disk_read_bytes_total',
  'node_disk_read_time_seconds_total', 'node_disk_reads_completed_total',
  'node_disk_reads_merged_total', 'node_disk_write_time_seconds_total',
  'node_disk_writes_completed_total', 'node_disk_writes_merged_total', 'node_disk_written_bytes_total',
  'node_dmi_info', 'node_edac_correctable_errors_total', 'node_edac_csrow_correctable_errors_total',
  'node_edac_csrow_uncorrectable_errors_total', 'node_edac_uncorrectable_errors_total',
  'node_entropy_available_bits', 'node_entropy_pool_size_bits', 'node_exporter_build_info',
  'node_filefd_allocated', 'node_filefd_maximum', 'node_filesystem_avail_bytes',
  'node_filesystem_device_error', 'node_filesystem_files', 'node_filesystem_files_free',
  'node_filesystem_free_bytes', 'node_filesystem_readonly', 'node_filesystem_size_bytes',
  'node_forks_total', 'node_hwmon_chip_names', 'node_hwmon_power_average_interval_max_seconds',
  'node_hwmon_power_average_interval_min_seconds', 'node_hwmon_power_average_interval_seconds',
  'node_hwmon_power_average_watt', 'node_hwmon_power_is_battery_watt', 'node_hwmon_sensor_label',
  'node_hwmon_temp_celsius', 'node_hwmon_temp_crit_alarm_celsius', 'node_hwmon_temp_crit_celsius',
  'node_hwmon_temp_max_celsius', 'node_intr_total', 'node_load1', 'node_load15', 'node_load5',
  'node_memory_Active_anon_bytes', 'node_memory_Active_bytes', 'node_memory_Active_file_bytes',
  'node_memory_AnonHugePages_bytes', 'node_memory_AnonPages_bytes', 'node_memory_Bounce_bytes',
  'node_memory_Buffers_bytes', 'node_memory_Cached_bytes', 'node_memory_CmaFree_bytes',
  'node_memory_CmaTotal_bytes', 'node_memory_CommitLimit_bytes', 'node_memory_Committed_AS_bytes',
  'node_memory_DirectMap1G_bytes', 'node_memory_DirectMap2M_bytes', 'node_memory_DirectMap4k_bytes',
  'node_memory_Dirty_bytes', 'node_memory_HardwareCorrupted_bytes', 'node_memory_HugePages_Free',
  'node_memory_HugePages_Rsvd', 'node_memory_HugePages_Surp', 'node_memory_HugePages_Total',
  'node_memory_Hugepagesize_bytes', 'node_memory_Inactive_anon_bytes', 'node_memory_Inactive_bytes',
  'node_memory_Inactive_file_bytes', 'node_memory_KernelStack_bytes', 'node_memory_Mapped_bytes',
  'node_memory_MemAvailable_bytes', 'node_memory_MemFree_bytes', 'node_memory_MemTotal_bytes',
  'node_memory_Mlocked_bytes', 'node_memory_NFS_Unstable_bytes', 'node_memory_PageTables_bytes',
  'node_memory_Percpu_bytes', 'node_memory_SReclaimable_bytes', 'node_memory_SUnreclaim_bytes',
  'node_memory_Shmem_bytes', 'node_memory_Slab_bytes', 'node_memory_SwapCached_bytes',
  'node_memory_SwapFree_bytes', 'node_memory_SwapTotal_bytes', 'node_memory_Unevictable_bytes',
  'node_memory_VmallocChunk_bytes', 'node_memory_VmallocTotal_bytes', 'node_memory_VmallocUsed_bytes',
  'node_memory_WritebackTmp_bytes', 'node_memory_Writeback_bytes', 'node_netstat_Icmp6_InErrors',
  'node_netstat_Icmp6_InMsgs', 'node_netstat_Icmp6_OutMsgs', 'node_netstat_Icmp_InErrors',
  'node_netstat_Icmp_InMsgs', 'node_netstat_Icmp_OutMsgs', 'node_netstat_Ip6_InOctets',
  'node_netstat_Ip6_OutOctets', 'node_netstat_IpExt_InOctets', 'node_netstat_IpExt_OutOctets',
  'node_netstat_Ip_Forwarding', 'node_netstat_TcpExt_ListenDrops', 'node_netstat_TcpExt_ListenOverflows',
  'node_netstat_TcpExt_SyncookiesFailed', 'node_netstat_TcpExt_SyncookiesRecv',
  'node_netstat_TcpExt_SyncookiesSent', 'node_netstat_TcpExt_TCPSynRetrans',
  'node_netstat_TcpExt_TCPTimeouts', 'node_netstat_Tcp_ActiveOpens', 'node_netstat_Tcp_CurrEstab',
  'node_netstat_Tcp_InErrs', 'node_netstat_Tcp_InSegs', 'node_netstat_Tcp_OutRsts',
  'node_netstat_Tcp_OutSegs', 'node_netstat_Tcp_PassiveOpens', 'node_netstat_Tcp_RetransSegs',
  'node_netstat_Udp6_InDatagrams', 'node_netstat_Udp6_InErrors', 'node_netstat_Udp6_NoPorts',
  'node_netstat_Udp6_OutDatagrams', 'node_netstat_Udp6_RcvbufErrors', 'node_netstat_Udp6_SndbufErrors',
  'node_netstat_UdpLite6_InErrors', 'node_netstat_UdpLite_InErrors', 'node_netstat_Udp_InDatagrams',
  'node_netstat_Udp_InErrors', 'node_netstat_Udp_NoPorts', 'node_netstat_Udp_OutDatagrams',
  'node_netstat_Udp_RcvbufErrors', 'node_netstat_Udp_SndbufErrors', 'node_network_address_assign_type',
  'node_network_carrier', 'node_network_carrier_changes_total', 'node_network_device_id',
  'node_network_dormant', 'node_network_flags', 'node_network_iface_id', 'node_network_iface_link',
  'node_network_iface_link_mode', 'node_network_info', 'node_network_mtu_bytes',
  'node_network_net_dev_group', 'node_network_protocol_type', 'node_network_receive_bytes_total',
  'node_network_receive_compressed_total', 'node_network_receive_drop_total',
  'node_network_receive_errs_total', 'node_network_receive_fifo_total', 'node_network_receive_frame_total',
  'node_network_receive_multicast_total', 'node_network_receive_nohandler_total',
  'node_network_receive_packets_total', 'node_network_speed_bytes', 'node_network_transmit_bytes_total',
  'node_network_transmit_carrier_total', 'node_network_transmit_colls_total',
  'node_network_transmit_compressed_total', 'node_network_transmit_drop_total',
  'node_network_transmit_errs_total', 'node_network_transmit_fifo_total',
  'node_network_transmit_packets_total', 'node_network_transmit_queue_length', 'node_network_up',
  'node_nf_conntrack_entries', 'node_nf_conntrack_entries_limit', 'node_nf_conntrack_stat_drop',
  'node_nf_conntrack_stat_early_drop', 'node_nf_conntrack_stat_found', 'node_nf_conntrack_stat_ignore',
  'node_nf_conntrack_stat_insert', 'node_nf_conntrack_stat_insert_failed', 'node_nf_conntrack_stat_invalid',
  'node_nf_conntrack_stat_search_restart', 'node_procs_blocked', 'node_procs_running',
  'node_rapl_dram_joules_total', 'node_rapl_package_joules_total', 'node_schedstat_running_seconds_total',
  'node_schedstat_timeslices_total', 'node_schedstat_waiting_seconds_total',
  'node_scrape_collector_duration_seconds', 'node_scrape_collector_success', 'node_selinux_enabled',
  'node_sockstat_FRAG6_inuse', 'node_sockstat_FRAG6_memory', 'node_sockstat_FRAG_inuse',
  'node_sockstat_FRAG_memory', 'node_sockstat_RAW6_inuse', 'node_sockstat_RAW_inuse',
  'node_sockstat_TCP6_inuse', 'node_sockstat_TCP_alloc', 'node_sockstat_TCP_inuse',
  'node_sockstat_TCP_mem', 'node_sockstat_TCP_mem_bytes', 'node_sockstat_TCP_orphan',
  'node_sockstat_TCP_tw', 'node_sockstat_UDP6_inuse', 'node_sockstat_UDPLITE6_inuse',
  'node_sockstat_UDPLITE_inuse', 'node_sockstat_UDP_inuse', 'node_sockstat_UDP_mem',
  'node_sockstat_UDP_mem_bytes', 'node_sockstat_sockets_used', 'node_softnet_backlog_len',
  'node_softnet_cpu_collision_total', 'node_softnet_dropped_total', 'node_softnet_flow_limit_count_total',
  'node_softnet_processed_total', 'node_softnet_received_rps_total', 'node_softnet_times_squeezed_total',
  'node_textfile_mtime_seconds', 'node_textfile_scrape_error', 'node_time_clocksource_available_info',
  'node_time_clocksource_current_info', 'node_time_seconds', 'node_time_zone_offset_seconds',
  'node_timex_estimated_error_seconds', 'node_timex_frequency_adjustment_ratio',
  'node_timex_loop_time_constant', 'node_timex_maxerror_seconds', 'node_timex_offset_seconds',
  'node_timex_pps_calibration_total', 'node_timex_pps_error_total', 'node_timex_pps_frequency_hertz',
  'node_timex_pps_jitter_seconds', 'node_timex_pps_jitter_total', 'node_timex_pps_shift_seconds',
  'node_timex_pps_stability_exceeded_total', 'node_timex_pps_stability_hertz', 'node_timex_status',
  'node_timex_sync_status', 'node_timex_tai_offset_seconds', 'node_timex_tick_seconds', 'node_udp_queues',
  'node_uname_info', 'node_vmstat_pgfault', 'node_vmstat_pgmajfault', 'node_vmstat_pgpgin',
  'node_vmstat_pgpgout', 'node_vmstat_pswpin', 'node_vmstat_pswpout',
  'node_xfs_allocation_btree_compares_total', 'node_xfs_allocation_btree_lookups_total',
  'node_xfs_allocation_btree_records_deleted_total', 'node_xfs_allocation_btree_records_inserted_total',
  'node_xfs_block_map_btree_compares_total', 'node_xfs_block_map_btree_lookups_total',
  'node_xfs_block_map_btree_records_deleted_total', 'node_xfs_block_map_btree_records_inserted_total',
  'node_xfs_block_mapping_extent_list_compares_total', 'node_xfs_block_mapping_extent_list_deletions_total',
  'node_xfs_block_mapping_extent_list_insertions_total', 'node_xfs_block_mapping_extent_list_lookups_total',
  'node_xfs_block_mapping_reads_total', 'node_xfs_block_mapping_unmaps_total',
  'node_xfs_block_mapping_writes_total', 'node_xfs_directory_operation_create_total',
  'node_xfs_directory_operation_getdents_total', 'node_xfs_directory_operation_lookup_total',
  'node_xfs_directory_operation_remove_total', 'node_xfs_extent_allocation_blocks_allocated_total',
  'node_xfs_extent_allocation_blocks_freed_total', 'node_xfs_extent_allocation_extents_allocated_total',
  'node_xfs_extent_allocation_extents_freed_total', 'node_xfs_inode_operation_attempts_total',
  'node_xfs_inode_operation_attribute_changes_total', 'node_xfs_inode_operation_duplicates_total',
  'node_xfs_inode_operation_found_total', 'node_xfs_inode_operation_missed_total',
  'node_xfs_inode_operation_reclaims_total', 'node_xfs_inode_operation_recycled_total',
  'node_xfs_read_calls_total', 'node_xfs_vnode_active_total', 'node_xfs_vnode_allocate_total',
  'node_xfs_vnode_get_total', 'node_xfs_vnode_hold_total', 'node_xfs_vnode_reclaim_total',
  'node_xfs_vnode_release_total', 'node_xfs_vnode_remove_total', 'node_xfs_write_calls_total',
  'process_cpu_seconds_total', 'process_max_fds', 'process_open_fds', 'process_resident_memory_bytes',
  'process_start_time_seconds', 'process_virtual_memory_bytes', 'process_virtual_memory_max_bytes',
  'promhttp_metric_handler_errors_total', 'promhttp_metric_handler_requests_in_flight',
  'promhttp_metric_handler_requests_total', 'scrape_duration_seconds',
  'scrape_samples_post_metric_relabeling', 'scrape_samples_scraped', 'scrape_series_added', 'up'
])
</script>