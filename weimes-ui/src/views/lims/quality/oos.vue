<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.调查编号')" prop="investigationNo"><el-input v-model="queryParams.investigationNo" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.类型')" prop="type">
        <el-select v-model="queryParams.type" :placeholder="$t('lims.请选择')" clearable>
          <el-option label="OOS" value="OOS"/><el-option label="OOT" value="OOT"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.草稿')" value="DRAFT"/><el-option :label="$t('lims.调查中')" value="INVESTIGATING"/><el-option :label="$t('lims.审核中')" value="REVIEWING"/><el-option :label="$t('lims.已批准')" value="APPROVED"/><el-option :label="$t('lims.已关闭')" value="CLOSED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:oos:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:oos:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:oos:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.调查编号')" align="center" prop="investigationNo" width="140"/>
      <el-table-column :label="$t('lims.类型')" align="center" prop="type" width="80">
        <template slot-scope="scope"><el-tag :type="scope.row.type === 'OOS' ? 'danger' : 'warning'">{{ scope.row.type }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('lims.样品编号')" align="center" prop="sampleCode" width="130"/>
      <el-table-column :label="$t('lims.检测项')" align="center" prop="testItemName" width="120"/>
      <el-table-column :label="$t('lims.标准值')" align="center" prop="specificationLimit" width="100"/>
      <el-table-column :label="$t('lims.实测值')" align="center" prop="oosValue" width="100"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="{ DRAFT: 'info', INVESTIGATING: 'warning', REVIEWING: '', APPROVED: 'success', CLOSED: 'success' }[scope.row.status]">{{ { DRAFT: $t('lims.草稿'), INVESTIGATING: $t('lims.调查中'), REVIEWING: $t('lims.审核中'), APPROVED: $t('lims.已批准'), CLOSED: $t('lims.已关闭') }[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.创建时间')" align="center" prop="createTime" width="160"/>
      <el-table-column :label="$t('common.operation')" align="center" width="260">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handlePhase(scope.row)">{{ $t('lims.阶段记录') }}</el-button>
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:oos:edit']">{{ $t('lims.提交') }}</el-button>
          <el-button size="mini" type="text" @click="handleReview(scope.row)" v-if="scope.row.status === 'REVIEWING'" v-hasPermi="['lims:oos:review']">{{ $t('lims.审核') }}</el-button>
          <el-button size="mini" type="text" @click="handleApprove(scope.row)" v-if="scope.row.status === 'REVIEWING'" v-hasPermi="['lims:oos:approve']">{{ $t('lims.批准') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:oos:edit']">{{ $t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:oos:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.调查编号')" prop="investigationNo"><el-input v-model="form.investigationNo"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.类型')" prop="type"><el-select v-model="form.type"><el-option label="OOS" value="OOS"/><el-option label="OOT" value="OOT"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="8"><el-form-item :label="$t('lims.标准值')" prop="specificationLimit"><el-input v-model="form.specificationLimit"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item :label="$t('lims.实测值')" prop="oosValue"><el-input v-model="form.oosValue"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item :label="$t('lims.是否实验室原因')" prop="isLabCause"><el-select v-model="form.isLabCause"><el-option :label="$t('lims.是')" value="Y"/><el-option :label="$t('lims.否')" value="N"/></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.实验室调查')" prop="labInvestigation"><el-input v-model="form.labInvestigation" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.根本原因')" prop="rootCause"><el-input v-model="form.rootCause" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.纠正措施')" prop="correctiveAction"><el-input v-model="form.correctiveAction" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>

    <!-- 阶段记录对话框 -->
    <el-dialog :title="$t('lims.调查阶段记录')" :visible.sync="phaseOpen" width="900px" append-to-body>
      <el-row :gutter="10" class="mb8"><el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAddPhase">{{ this.$t('lims.新增阶段') }}</el-button></el-col></el-row>
      <el-table :data="phaseList" border>
        <el-table-column :label="$t('lims.阶段')" prop="phase" width="120"/>
        <el-table-column :label="$t('lims.假设')" prop="hypothesis" :show-overflow-tooltip="true"/>
        <el-table-column :label="$t('lims.实验方法')" prop="experimentMethod" :show-overflow-tooltip="true"/>
        <el-table-column :label="$t('lims.实验结果')" prop="experimentResult" :show-overflow-tooltip="true"/>
        <el-table-column :label="$t('lims.调查结论')" prop="conclusion" :show-overflow-tooltip="true"/>
        <el-table-column :label="$t('lims.操作人')" prop="operatorName" width="100"/>
        <el-table-column :label="$t('common.operation')" width="100">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleEditPhase(scope.row)">{{ $t('common.edit') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog :title="phaseTitle" :visible.sync="phaseEditOpen" width="650px" append-to-body>
      <el-form ref="phaseForm" :model="phaseForm" :rules="phaseRules" label-width="100px">
        <el-form-item :label="$t('lims.阶段')" prop="phase"><el-input v-model="phaseForm.phase" :placeholder="$t('lims.如Phase I/Phase II/Phase III')"/></el-form-item>
        <el-form-item :label="$t('lims.假设')" prop="hypothesis"><el-input v-model="phaseForm.hypothesis" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.实验方法')" prop="experimentMethod"><el-input v-model="phaseForm.experimentMethod" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.实验结果')" prop="experimentResult"><el-input v-model="phaseForm.experimentResult" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.调查结论')" prop="conclusion"><el-input v-model="phaseForm.conclusion" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.操作人')" prop="operatorName"><el-input v-model="phaseForm.operatorName"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitPhaseForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="phaseEditOpen = false">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listInvestigation, getInvestigation, addInvestigation, updateInvestigation, delInvestigation, submitInvestigation, reviewInvestigation, approveInvestigation, listPhaseRecords, addPhaseRecord, updatePhaseRecord } from '@/api/lims/oos'
export default {
  name: 'LimsOos',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, investigationNo: null, type: null, status: null },
      form: {}, rules: { investigationNo: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], type: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'change' }], oosValue: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }] },
      phaseOpen: false, phaseList: [], currentInvestigationId: null, phaseEditOpen: false, phaseTitle: '', phaseForm: {},
      phaseRules: { phase: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listInvestigation(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.investigationId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { investigationId: null, investigationNo: null, type: 'OOS', specificationLimit: null, oosValue: null, isLabCause: null, labInvestigation: null, rootCause: null, correctiveAction: null, status: 'DRAFT', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增OOS/OOT调查') },
    handleUpdate(row) { this.reset(); getInvestigation(row.investigationId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = this.$t('lims.修改OOS/OOT调查') }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.investigationId != null ? updateInvestigation : addInvestigation)(this.form).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.investigationId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delInvestigation(ids)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleSubmit(row) { this.$modal.confirm(this.$t('lims.是否提交该调查？')).then(() => submitInvestigation(row.investigationId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.提交成功')) }).catch(() => {}) },
    handleReview(row) { this.$modal.confirm(this.$t('lims.是否审核通过？')).then(() => reviewInvestigation(row.investigationId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.审核成功')) }).catch(() => {}) },
    handleApprove(row) { this.$modal.confirm(this.$t('lims.是否批准？')).then(() => approveInvestigation(row.investigationId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.批准成功')) }).catch(() => {}) },
    handlePhase(row) { this.currentInvestigationId = row.investigationId; listPhaseRecords(row.investigationId).then(r => { this.phaseList = r.data; this.phaseOpen = true }) },
    handleAddPhase() { this.phaseForm = { investigationId: this.currentInvestigationId, phase: null, hypothesis: null, experimentMethod: null, experimentResult: null, conclusion: null, operatorName: null }; this.phaseTitle = this.$t('lims.新增阶段'); this.phaseEditOpen = true },
    handleEditPhase(row) { this.phaseForm = { ...row }; this.phaseTitle = this.$t('lims.编辑阶段'); this.phaseEditOpen = true },
    submitPhaseForm() { this.$refs['phaseForm'].validate(v => { if (v) { (this.phaseForm.phaseRecordId != null ? updatePhaseRecord : addPhaseRecord)(this.phaseForm).then(() => { this.phaseEditOpen = false; this.handlePhase({ investigationId: this.currentInvestigationId }); this.$modal.msgSuccess(this.$t('common.success')) }) } }) }
  }
}
</script>
