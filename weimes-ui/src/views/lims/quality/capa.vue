<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="CAPA编号" prop="capaCode"><el-input v-model="queryParams.capaCode" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="类型" prop="capaType">
        <el-select v-model="queryParams.capaType" placeholder="请选择" clearable>
          <el-option label="纠正措施" value="CA"/><el-option label="预防措施" value="PA"/><el-option label="纠正+预防" value="CAPA"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option label="草稿" value="DRAFT"/><el-option label="待审批" value="PENDING_APPROVAL"/><el-option label="已批准" value="APPROVED"/><el-option label="执行中" value="IN_PROGRESS"/><el-option label="已关闭" value="CLOSED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:capa:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:capa:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:capa:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="CAPA编号" align="center" prop="capaCode" width="140"/>
      <el-table-column label="类型" align="center" prop="capaType" width="100">
        <template slot-scope="scope">{{ { CA: '纠正', PA: '预防', CAPA: '纠正+预防' }[scope.row.capaType] }}</template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="sourceType" width="120">
        <template slot-scope="scope">{{ { OOS: 'OOS调查', DEVIATION: '偏差', COMPLAINT: '投诉', AUDIT: '审计' }[scope.row.sourceType] }}</template>
      </el-table-column>
      <el-table-column label="来源编号" align="center" prop="sourceCode" width="140"/>
      <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true"/>
      <el-table-column label="负责人" align="center" prop="responsibleId" width="100"/>
      <el-table-column label="截止日期" align="center" prop="dueDate" width="110"/>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="{ DRAFT: 'info', PENDING_APPROVAL: 'warning', APPROVED: '', IN_PROGRESS: '', CLOSED: 'success' }[scope.row.status]">{{ { DRAFT: '草稿', PENDING_APPROVAL: '待审批', APPROVED: '已批准', IN_PROGRESS: '执行中', CLOSED: '已关闭' }[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:capa:edit']">提交</el-button>
          <el-button size="mini" type="text" @click="handleApprove(scope.row)" v-if="scope.row.status === 'PENDING_APPROVAL'" v-hasPermi="['lims:capa:approve']">审批</el-button>
          <el-button size="mini" type="text" @click="handleClose(scope.row)" v-if="scope.row.status === 'IN_PROGRESS'" v-hasPermi="['lims:capa:close']">关闭</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:capa:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:capa:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="CAPA编号" prop="capaCode"><el-input v-model="form.capaCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="类型" prop="capaType"><el-select v-model="form.capaType"><el-option label="纠正措施" value="CA"/><el-option label="预防措施" value="PA"/><el-option label="纠正+预防" value="CAPA"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="来源类型" prop="sourceType"><el-select v-model="form.sourceType"><el-option label="OOS调查" value="OOS"/><el-option label="偏差" value="DEVIATION"/><el-option label="投诉" value="COMPLAINT"/><el-option label="审计" value="AUDIT"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="来源编号" prop="sourceCode"><el-input v-model="form.sourceCode"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="描述" prop="description"><el-input v-model="form.description" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="根本原因" prop="rootCause"><el-input v-model="form.rootCause" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="纠正措施" prop="correctiveAction"><el-input v-model="form.correctiveAction" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="预防措施" prop="preventiveAction"><el-input v-model="form.preventiveAction" type="textarea" :rows="3"/></el-form-item>
        <el-row>
          <el-col :span="12"><el-form-item label="负责人" prop="responsibleId"><el-input v-model="form.responsibleId" placeholder="用户ID"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="截止日期" prop="dueDate"><el-date-picker v-model="form.dueDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listCapa, getCapa, addCapa, updateCapa, delCapa, submitCapa, approveCapa, closeCapa } from '@/api/lims/capa'
export default {
  name: 'LimsCapa',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, capaCode: null, capaType: null, status: null },
      form: {}, rules: { capaCode: [{ required: true, message: '不能为空', trigger: 'blur' }], description: [{ required: true, message: '不能为空', trigger: 'blur' }], capaType: [{ required: true, message: '不能为空', trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listCapa(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.capaId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { capaId: null, capaCode: null, capaType: 'CAPA', sourceType: null, sourceCode: null, description: null, rootCause: null, correctiveAction: null, preventiveAction: null, responsibleId: null, dueDate: null, status: 'DRAFT', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = '新增CAPA' },
    handleUpdate(row) { this.reset(); getCapa(row.capaId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = '修改CAPA' }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.capaId != null ? updateCapa : addCapa)(this.form).then(() => { this.$modal.msgSuccess('操作成功'); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.capaId || this.ids; this.$modal.confirm('是否确认删除？').then(() => delCapa(ids)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleSubmit(row) { this.$modal.confirm('是否提交？').then(() => submitCapa(row.capaId)).then(() => { this.getList(); this.$modal.msgSuccess('提交成功') }).catch(() => {}) },
    handleApprove(row) { this.$modal.confirm('是否审批通过？').then(() => approveCapa(row.capaId)).then(() => { this.getList(); this.$modal.msgSuccess('审批成功') }).catch(() => {}) },
    handleClose(row) { this.$modal.confirm('是否关闭？').then(() => closeCapa(row.capaId)).then(() => { this.getList(); this.$modal.msgSuccess('关闭成功') }).catch(() => {}) }
  }
}
</script>
