<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="变更编号" prop="changeCode"><el-input v-model="queryParams.changeCode" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="变更类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="请选择" clearable>
          <el-option label="临时变更" value="TEMPORARY"/><el-option label="永久变更" value="PERMANENT"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option label="草稿" value="DRAFT"/><el-option label="待审批" value="PENDING_APPROVAL"/><el-option label="已批准" value="APPROVED"/><el-option label="执行中" value="IMPLEMENTING"/><el-option label="已完成" value="COMPLETED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:change:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:change:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:change:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="变更编号" align="center" prop="changeCode" width="140"/>
      <el-table-column label="变更类型" align="center" prop="changeType" width="100">
        <template slot-scope="scope">{{ scope.row.changeType === 'TEMPORARY' ? '临时' : '永久' }}</template>
      </el-table-column>
      <el-table-column label="变更描述" align="center" prop="changeDescription" :show-overflow-tooltip="true"/>
      <el-table-column label="影响范围" align="center" prop="impactAnalysis" width="120"/>
      <el-table-column label="申请人" align="center" prop="requesterId" width="100"/>
      <el-table-column label="计划日期" align="center" prop="effectiveDate" width="110"/>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="{ DRAFT: 'info', PENDING_APPROVAL: 'warning', APPROVED: '', IMPLEMENTING: '', COMPLETED: 'success' }[scope.row.status]">{{ { DRAFT: '草稿', PENDING_APPROVAL: '待审批', APPROVED: '已批准', IMPLEMENTING: '执行中', COMPLETED: '已完成' }[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="260">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:change:edit']">提交</el-button>
          <el-button size="mini" type="text" @click="handleApprove(scope.row)" v-if="scope.row.status === 'PENDING_APPROVAL'" v-hasPermi="['lims:change:approve']">审批</el-button>
          <el-button size="mini" type="text" @click="handleImplement(scope.row)" v-if="scope.row.status === 'APPROVED'" v-hasPermi="['lims:change:implement']">执行</el-button>
          <el-button size="mini" type="text" @click="handleComplete(scope.row)" v-if="scope.row.status === 'IMPLEMENTING'" v-hasPermi="['lims:change:complete']">完成</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:change:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:change:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="变更编号" prop="changeCode"><el-input v-model="form.changeCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="变更类型" prop="changeType"><el-select v-model="form.changeType"><el-option label="临时变更" value="TEMPORARY"/><el-option label="永久变更" value="PERMANENT"/></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="变更描述" prop="changeDescription"><el-input v-model="form.changeDescription" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="变更原因" prop="changeReason"><el-input v-model="form.changeReason" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="影响范围" prop="impactAnalysis"><el-input v-model="form.impactAnalysis" placeholder="如产品/工艺/设备"/></el-form-item>
        <el-form-item label="风险评估" prop="riskAssessment"><el-input v-model="form.riskAssessment" type="textarea" :rows="3"/></el-form-item>
        <el-row>
          <el-col :span="12"><el-form-item label="申请人" prop="requesterId"><el-input v-model="form.requesterId"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="计划日期" prop="effectiveDate"><el-date-picker v-model="form.effectiveDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listChange, getChange, addChange, updateChange, delChange, submitChange, approveChange, implementChange, completeChange } from '@/api/lims/change'
export default {
  name: 'LimsChangeControl',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, changeCode: null, changeType: null, status: null },
      form: {}, rules: { changeCode: [{ required: true, message: '不能为空', trigger: 'blur' }], changeDescription: [{ required: true, message: '不能为空', trigger: 'blur' }], changeType: [{ required: true, message: '不能为空', trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listChange(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.changeId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { changeId: null, changeCode: null, changeType: 'PERMANENT', changeDescription: null, changeReason: null, impactAnalysis: null, riskAssessment: null, requesterId: null, effectiveDate: null, status: 'DRAFT', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = '新增变更控制' },
    handleUpdate(row) { this.reset(); getChange(row.changeId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = '修改变更控制' }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.changeId != null ? updateChange : addChange)(this.form).then(() => { this.$modal.msgSuccess('操作成功'); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.changeId || this.ids; this.$modal.confirm('是否确认删除？').then(() => delChange(ids)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleSubmit(row) { this.$modal.confirm('是否提交？').then(() => submitChange(row.changeId)).then(() => { this.getList(); this.$modal.msgSuccess('提交成功') }).catch(() => {}) },
    handleApprove(row) { this.$modal.confirm('是否审批通过？').then(() => approveChange(row.changeId)).then(() => { this.getList(); this.$modal.msgSuccess('审批成功') }).catch(() => {}) },
    handleImplement(row) { this.$modal.confirm('是否开始执行？').then(() => implementChange(row.changeId)).then(() => { this.getList(); this.$modal.msgSuccess('执行开始') }).catch(() => {}) },
    handleComplete(row) { this.$modal.confirm('是否确认完成？').then(() => completeChange(row.changeId)).then(() => { this.getList(); this.$modal.msgSuccess('已完成') }).catch(() => {}) }
  }
}
</script>
