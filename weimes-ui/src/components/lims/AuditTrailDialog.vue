<template>
  <div>
    <!-- 审计追踪查看对话框 -->
    <el-dialog title="变更历史 - 审计追踪" :visible.sync="visible" width="900px" append-to-body>
      <el-table v-loading="loading" :data="auditList" size="small" border>
        <el-table-column label="操作类型" align="center" prop="operationType" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.operationType === 'INSERT'" type="success">新增</el-tag>
            <el-tag v-else-if="scope.row.operationType === 'UPDATE'" type="warning">修改</el-tag>
            <el-tag v-else-if="scope.row.operationType === 'DELETE'" type="danger">删除</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="变更字段" align="center" prop="fieldName" width="120"/>
        <el-table-column label="旧值" align="center" prop="oldValue" show-overflow-tooltip/>
        <el-table-column label="新值" align="center" prop="newValue" show-overflow-tooltip/>
        <el-table-column label="操作人" align="center" prop="operatorName" width="100"/>
        <el-table-column label="操作时间" align="center" prop="operationTime" width="160"/>
        <el-table-column label="变更原因" align="center" prop="reason" show-overflow-tooltip/>
        <el-table-column label="IP地址" align="center" prop="ipAddress" width="120"/>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="visible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAuditHistory } from '@/api/lims/esign'

export default {
  name: 'AuditTrailDialog',
  data() {
    return {
      visible: false,
      loading: false,
      auditList: [],
      tableName: '',
      recordId: null
    }
  },
  methods: {
    show(tableName, recordId) {
      this.tableName = tableName
      this.recordId = recordId
      this.visible = true
      this.loadAuditTrail()
    },
    loadAuditTrail() {
      this.loading = true
      getAuditHistory(this.tableName, this.recordId).then(response => {
        this.auditList = response.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>
