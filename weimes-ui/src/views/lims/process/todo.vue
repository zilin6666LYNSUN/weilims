<template>
  <div class="app-container">
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="实例ID" align="center" prop="procInstId" width="200"/>
      <el-table-column label="流程KEY" align="center" prop="procKey" width="140"/>
      <el-table-column label="业务类型" align="center" prop="businessType" width="140"/>
      <el-table-column label="业务ID" align="center" prop="businessId" width="90"/>
      <el-table-column label="当前节点" align="center" prop="currentNodeId" width="140"/>
      <el-table-column label="开始时间" align="center" prop="startTime" width="160"/>
      <el-table-column label="操作" align="center" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-check" @click="handleApprove(scope.row)">同意</el-button>
          <el-button size="mini" type="text" icon="el-icon-close" @click="handleReject(scope.row)">驳回</el-button>
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleHistory(scope.row)">历史</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && dataList.length === 0" description="暂无待办任务"/>

    <el-dialog :title="todoTitle" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-form-item label="审批动作"><el-tag :type="form.action === 'APPROVE' ? 'success' : 'danger'">{{ form.action === 'APPROVE' ? '同意' : '驳回' }}</el-tag></el-form-item>
        <el-form-item label="审批意见" prop="comment"><el-input v-model="form.comment" type="textarea" :rows="3" placeholder="请输入审批意见"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="'审批历史 - ' + historyProcInstId" :visible.sync="historyOpen" width="760px" append-to-body>
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
import { listTodo, completeProcessTask, listApprovalHistory } from '@/api/lims/process'

export default {
  name: 'LimsProcessTodo',
  data() {
    return {
      loading: true, dataList: [], open: false, todoTitle: '', historyOpen: false, historyList: [], historyProcInstId: '',
      form: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listTodo().then(response => { this.dataList = response.data || response || []; this.loading = false }) },
    handleApprove(row) { this.form = { procInstId: row.procInstId, nodeId: row.currentNodeId, action: 'APPROVE', comment: '' }; this.todoTitle = '办理 - 同意'; this.open = true },
    handleReject(row) { this.form = { procInstId: row.procInstId, nodeId: row.currentNodeId, action: 'REJECT', comment: '' }; this.todoTitle = '办理 - 驳回'; this.open = true },
    submitForm() { completeProcessTask(this.form).then(response => { this.msgSuccess('办理成功'); this.open = false; this.getList() }) },
    cancel() { this.open = false; this.form = {} },
    handleHistory(row) { this.historyProcInstId = row.procInstId; this.historyList = []; this.historyOpen = true; listApprovalHistory(row.procInstId).then(response => { this.historyList = response.data || response || [] }) }
  }
}
</script>
