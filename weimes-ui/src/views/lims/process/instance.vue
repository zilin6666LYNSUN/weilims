<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="业务类型" prop="businessType"><el-input v-model="queryParams.businessType" placeholder="请输入业务类型" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="运行中" value="RUNNING"/><el-option label="已通过" value="APPROVED"/><el-option label="已驳回" value="REJECTED"/><el-option label="已终止" value="TERMINATED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="实例ID" align="center" prop="procInstId" width="200"/>
      <el-table-column label="流程KEY" align="center" prop="procKey" width="140"/>
      <el-table-column label="业务类型" align="center" prop="businessType" width="140"/>
      <el-table-column label="业务ID" align="center" prop="businessId" width="90"/>
      <el-table-column label="当前节点" align="center" prop="currentNodeId" width="140"/>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startTime" width="160"/>
      <el-table-column label="结束时间" align="center" prop="endTime" width="160"/>
      <el-table-column label="操作" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleHistory(scope.row)">审批历史</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="'审批历史 - ' + currentProcInstId" :visible.sync="historyOpen" width="760px" append-to-body>
      <el-timeline>
        <el-timeline-item v-for="(item, index) in historyList" :key="index" :timestamp="item.approvalTime" placement="top">
          <el-card>
            <p><b>{{ item.taskName }}</b>：{{ item.action }}（{{ item.assigneeName }}）</p>
            <p style="color:#909399">{{ item.comment || '无审批意见' }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="historyList.length === 0" description="暂无审批记录"/>
    </el-dialog>
  </div>
</template>

<script>
import { listProcessInstance, listApprovalHistory } from '@/api/lims/process'

export default {
  name: 'LimsProcessInstance',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, businessType: undefined, status: undefined },
      historyOpen: false, historyList: [], currentProcInstId: ''
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listProcessInstance(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    statusText(status) { return { RUNNING: '运行中', APPROVED: '已通过', REJECTED: '已驳回', TERMINATED: '已终止' }[status] || status },
    statusType(status) { return { RUNNING: 'warning', APPROVED: 'success', REJECTED: 'danger', TERMINATED: 'info' }[status] || 'info' },
    handleHistory(row) { this.currentProcInstId = row.procInstId; this.historyList = []; this.historyOpen = true; listApprovalHistory(row.procInstId).then(response => { this.historyList = response.data || response || [] }) }
  }
}
</script>
