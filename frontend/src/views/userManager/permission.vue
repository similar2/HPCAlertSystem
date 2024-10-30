<template>
  <div class="background-container">
    <div class="app-container">
      <!-- 标题 -->
      <h1 class="page-title">权限列表</h1>

      <!-- 操作按钮 -->
      <div class="button-panel">
        <div class="button-container">
          <el-button type="primary" @click="promptQueryById">根据ID查询权限</el-button>
          <el-button type="success" @click="promptUpdatePermission">更新权限</el-button>
          <el-button type="danger" @click="promptDeletePermission">删除权限</el-button>
          <el-button type="primary" @click="openCreateDialog">新建权限</el-button>
          <el-button v-if="isSearching" type="info" @click="resetSearch">返回</el-button>
        </div>
      </div>

      <!-- 权限表格 -->
      <el-table
          v-if="permissions.length || isSearching"
          :data="isSearching ? [searchResult] : permissions"
          border stripe
          class="custom-table"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="menuName" label="菜单名称" align="center" />
        <el-table-column prop="permissionName" label="权限名称" align="center" />
        <el-table-column label="是否必选" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.requiredPermission === 1 ? 'success' : 'info'"
            >
              {{ scope.row.requiredPermission === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>



    <!-- 新建权限的对话框 -->
    <el-dialog title="新建权限" v-model="showCreateDialog" width="400px">
      <el-form :model="newPermission">
        <el-form-item label="菜单名称">
          <el-input v-model="newPermission.menuName" placeholder="输入菜单名称"></el-input>
        </el-form-item>
        <el-form-item label="权限名称">
          <el-input v-model="newPermission.permissionName" placeholder="输入权限名称"></el-input>
        </el-form-item>
        <el-form-item label="是否必选">
          <el-input v-model="newPermission.requiredPermission" placeholder="输入是否必选 (1/2)"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitCreatePermission">确定</el-button>
      </template>
    </el-dialog>

    <!-- 更新权限的对话框 -->
    <el-dialog title="更新权限信息" v-model="showUpdateDialog" width="400px">
      <el-form :model="updateData">
        <el-form-item label="菜单代码">
          <el-input v-model="updateData.menuCode" placeholder="输入菜单代码"></el-input>
        </el-form-item>
        <el-form-item label="菜单名称">
          <el-input v-model="updateData.menuName" placeholder="输入菜单名称"></el-input>
        </el-form-item>
        <el-form-item label="权限名称">
          <el-input v-model="updateData.permissionName" placeholder="输入权限名称"></el-input>
        </el-form-item>
        <el-form-item label="是否必选">
          <el-input v-model="updateData.requiredPermission" placeholder="输入是否必选 (1/0)"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUpdateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUpdatePermission">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { fetchPermissions, fetchPermissionById, updatePermission, deletePermission, createPermission } from '@/api/permission';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  data() {
    return {
      permissions: [],
      searchResult: null,
      isSearching: false,
      errorMessage: '',
      showCreateDialog: false,
      showUpdateDialog: false,
      newPermission: {                 // 新建权限的数据对象
        menuCode: '',
        menuName: '',
        permissionName: '',
        requiredPermission: ''
      },
      updateData: {                    // 更新权限的数据对象
        id: null,
        menuCode: '',
        menuName: '',
        permissionName: '',
        requiredPermission: ''
      }
    };
  },
  methods: {
    loadPermissions() {
      fetchPermissions()
          .then(response => {
            if (response && response.data && Array.isArray(response.data.data)) {
              this.permissions = response.data.data;
            } else {
              this.errorMessage = '权限数据格式不正确';
            }
          })
          .catch(() => {
            this.errorMessage = "获取权限数据失败";
          });
    },

    // 打开新建权限的对话框
    openCreateDialog() {
      this.resetNewPermissionData();   // 重置输入框
      this.showCreateDialog = true;    // 显示对话框
    },

    // 重置新建权限的数据
    resetNewPermissionData() {
      this.newPermission = {
        menuCode: '',
        menuName: '',
        permissionName: '',
        requiredPermission: ''
      };
    },

    // 提交新建权限
    submitCreatePermission() {
      createPermission(this.newPermission)
          .then(() => {
            ElMessage.success("权限创建成功");
            this.showCreateDialog = false;
            this.loadPermissions(); // 刷新表格数据
          })
          .catch(() => {
            ElMessage.error("权限创建失败");
          });
    },

    // 打开更新权限对话框
    promptUpdatePermission() {
      ElMessageBox.prompt('请输入要更新的权限ID', '更新权限', {
        confirmButtonText: '下一步',
        cancelButtonText: '取消',
        inputPattern: /^[0-9]+$/,
        inputErrorMessage: '请输入有效的数字ID'
      }).then(({ value: id }) => {
        this.fetchPermissionDetails(id);
      }).catch(() => {
        ElMessage.info('已取消更新');
      });
    },

    // 获取指定ID的权限数据并打开更新对话框
    fetchPermissionDetails(id) {
      fetchPermissionById(id)
          .then(response => {
            if (response.data && response.data.data) {
              this.updateData = { ...response.data.data };
              this.showUpdateDialog = true; // 打开对话框
            } else {
              ElMessage.error("权限未找到");
            }
          })
          .catch(() => {
            ElMessage.error("根据ID查询权限失败");
          });
    },

    // 提交更新的权限数据
    submitUpdatePermission() {
      updatePermission(this.updateData.id, this.updateData)
          .then(() => {
            ElMessage.success("权限更新成功");
            this.showUpdateDialog = false;
            this.loadPermissions(); // 刷新表格数据
          })
          .catch(() => {
            ElMessage.error("权限更新失败");
          });
    },

    // 删除权限
    promptDeletePermission() {
      ElMessageBox.prompt('请输入要删除的权限ID', '删除权限', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        inputPattern: /^[0-9]+$/,
        inputErrorMessage: '请输入有效的数字ID'
      }).then(({ value: id }) => {
        this.deletePermissionById(id);
      }).catch(() => {
        ElMessage.info('已取消删除');
      });
    },

    deletePermissionById(id) {
      ElMessageBox.confirm(`确认删除权限 ID 为 ${id} 的记录吗？`, '警告', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deletePermission(id)
            .then(() => {
              ElMessage.success('权限删除成功');
              this.loadPermissions(); // 刷新表格数据
            })
            .catch(() => {
              ElMessage.error('权限删除失败');
            });
      }).catch(() => {
        ElMessage.info('已取消删除');
      });
    },

    // 查询权限
    promptQueryById() {
      ElMessageBox.prompt('请输入查询的权限ID', '查询权限', {
        confirmButtonText: '查询',
        cancelButtonText: '取消',
        inputPattern: /^[0-9]+$/,
        inputErrorMessage: '请输入有效的数字ID'
      }).then(({ value }) => {
        this.queryById(value);
      }).catch(() => {
        ElMessage.info('已取消查询');
      });
    },

    queryById(id) {
      fetchPermissionById(id)
          .then(response => {
            if (response.data && response.data.data) {
              this.searchResult = response.data.data;
              this.isSearching = true;
            } else {
              ElMessage.error("权限未找到");
            }
          })
          .catch(() => {
            ElMessage.error("根据ID查询权限失败");
          });
    },

    resetSearch() {
      this.isSearching = false;
      this.searchResult = null;
      this.loadPermissions();
    }
  },

  mounted() {
    this.loadPermissions();
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px 40px;
  background-color: #f4f5f7;
}

.page-title {
  font-size: 16px; /* 增加字体大小 */
  font-weight: bold;
  color: #2b3e50; /* 使用深蓝色 */
  margin-bottom: 25px;
  //border-left: 5px solid #409eff; /* 左侧加上蓝色边框，更显眼 */
  padding-left: 0px; /* 标题与左侧边框之间的距离 */
}

.button-panel {
  display: flex;
  justify-content: center;
  padding: 10px 0;
  margin-bottom: 15px;
}

.button-container {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.el-table__header, .el-table__body {
  background-color: #fff;
}

.el-table__row:hover {
  background-color: #f0f9ff !important;
}

.el-tag.success {
  color: #67c23a;
  background-color: #f0f9eb;
  border-color: #e1f3d8;
}

.el-tag.info {
  color: #909399;
  background-color: #f4f4f5;
  border-color: #dcdfe6;
}

.error-message {
  color: red;
  font-weight: bold;
}

.no-data {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 20px;
}

</style>
