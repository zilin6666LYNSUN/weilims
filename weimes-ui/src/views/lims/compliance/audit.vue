<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="88px">
      <el-form-item :label="$t('lims.变更表名')" prop="tableName">
        <el-input v-model="queryParams.tableName" :placeholder="$t('lims.请输入表名')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('lims.操作类型')" prop="operationType">
        <el-select v-model="queryParams.operationType" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('common.add')" value="INSERT" />
          <el-option :label="$t('lims.修改')" value="UPDATE" />
          <el-option :label="$t('common.delete')" value="DELETE" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('lims.操作人')" prop="operatorName">
        <el-input v-model="queryParams.operatorName" :placeholder="$t('lims.请输入操作人')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('lims.操作时间')">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="-" :start-placeholder="$t('lims.开始日期')" :end-placeholder="$t('lims.结束日期')" value-format="yyyy-MM-dd" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="auditList" border>
      <el-table-column :label="$t('lims.审计ID')" align="center" prop="auditId" width="80" />
      <el-table-column :label="$t('lims.变更表名')" align="center" prop="tableName" width="160" />
      <el-table-column :label="$t('lims.记录ID')" align="center" prop="recordId" width="80" />
      <el-table-column :label="$t('lims.变更字段')" align="center" prop="fieldName" width="120" />
      <el-table-column :label="$t('lims.旧值')" align="center" prop="oldValue" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column :label="$t('lims.新值')" align="center" prop="newValue" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column :label="$t('lims.操作类型')" align="center" prop="operationType" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.operationType === 'INSERT' ? 'success' : scope.row.operationType === 'DELETE' ? 'danger' : 'warning'" size="small">{{ scope.row.operationType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.操作人')" align="center" prop="operatorName" width="100" />
      <el-table-column :label="$t('lims.操作时间')" align="center" prop="operationTime" width="160" />
      <el-table-column :label="$t('lims.变更原因')" align="center" prop="reason" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column :label="$t('lims.IP地址')" align="center" prop="ipAddress" width="130" />
      <el-table-column :label="$t('common.operation')" align="center" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleHistory(scope.row)" v-hasPermi="['lims:esign:audit:query']">{{ $t('lims.变更历史') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 变更历史对话框 -->
    <el-dialog :title="$t('lims.变更历史')" :visible.sync="historyOpen" width="70%" append-to-body>
      <el-table v-loading="historyLoading" :data="historyList" border>
        <el-table-column :label="$t('lims.审计ID')" align="center" prop="auditId" width="80" />
        <el-table-column :label="$t('lims.变更字段')" align="center" prop="fieldName" width="120" />
        <el-table-column :label="$t('lims.旧值')" align="center" prop="oldValue" :show-overflow-tooltip="true" min-width="150" />
        <el-table-column :label="$t('lims.新值')" align="center" prop="newValue" :show-overflow-tooltip="true" min-width="150" />
        <el-table-column :label="$t('lims.操作类型')" align="center" prop="operationType" width="80" />
        <el-table-column :label="$t('lims.操作人')" align="center" prop="operatorName" width="100" />
        <el-table-column :label="$t('lims.操作时间')" align="center" prop="operationTime" width="160" />
        <el-table-column :label="$t('lims.变更原因')" align="center" prop="reason" :show-overflow-tooltip="true" min-width="120" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listAuditTrail, getAuditHistory } from '@/api/lims/esign'

export default {
  name: 'LimsAuditTrail',
  data() {
    return {
      loading: false,
      auditList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tableName: undefined,
        operationType: undefined,
        operatorName: undefined
      },
      dateRange: [],
      historyOpen: false,
      historyLoading: false,
      historyList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listAuditTrail(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.auditList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleHistory(row) {
      this.historyOpen = true
      this.historyLoading = true
      getAuditHistory(row.tableName, row.recordId).then(response => {
        this.historyList = response.data
        this.historyLoading = false
      })
    }
  }
}
</script>
