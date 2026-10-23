<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.检验单号')" prop="testOrderCode"><el-input v-model="queryParams.testOrderCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.样品编号')" prop="sampleCode"><el-input v-model="queryParams.sampleCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.草稿')" value="DRAFT"/><el-option :label="$t('lims.待检验')" value="PENDING_TEST"/><el-option :label="$t('lims.检验中')" value="TESTING"/><el-option :label="$t('lims.待复核')" value="PENDING_REVIEW"/><el-option :label="$t('lims.待批准')" value="PENDING_APPROVAL"/><el-option :label="$t('lims.已完成')" value="COMPLETED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:test:order:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:test:order:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:test:order:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.检验单号')" align="center" prop="testOrderCode" width="140"/>
      <el-table-column :label="$t('lims.样品编号')" align="center" prop="sampleCode" width="130"/>
      <el-table-column :label="$t('lims.样品名称')" align="center" prop="sampleName" width="140"/>
      <el-table-column :label="$t('lims.检验类型')" align="center" prop="testType" width="100"/>
      <el-table-column :label="$t('lims.检验人')" align="center" prop="testerId" width="100"/>
      <el-table-column :label="$t('lims.计划完成')" align="center" prop="plannedEndDate" width="110"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="{ DRAFT: 'info', PENDING_TEST: 'warning', TESTING: '', PENDING_REVIEW: '', PENDING_APPROVAL: 'warning', COMPLETED: 'success' }[scope.row.status]">{{ { DRAFT: this.$t('lims.草稿'), PENDING_TEST: this.$t('lims.待检验'), TESTING: this.$t('lims.检验中'), PENDING_REVIEW: this.$t('lims.待复核'), PENDING_APPROVAL: this.$t('lims.待批准'), COMPLETED: this.$t('lims.已完成') }[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.创建时间')" align="center" prop="createTime" width="160"/>
      <el-table-column :label="$t('common.operation')" align="center" width="280">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleResult(scope.row)" v-if="['PENDING_TEST','TESTING'].includes(scope.row.status)" v-hasPermi="['lims:test:order:edit']">{{ this.$t('lims.录入结果') }}</el-button>
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:test:order:submit']">{{ this.$t('lims.提交') }}</el-button>
          <el-button size="mini" type="text" @click="handleReview(scope.row)" v-if="scope.row.status === 'PENDING_REVIEW'" v-hasPermi="['lims:test:order:review']">{{ this.$t('lims.复核') }}</el-button>
          <el-button size="mini" type="text" @click="handleApprove(scope.row)" v-if="scope.row.status === 'PENDING_APPROVAL'" v-hasPermi="['lims:test:order:approve']">{{ this.$t('lims.批准') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:test:order:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:test:order:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.检验单号')" prop="testOrderCode"><el-input v-model="form.testOrderCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.样品编号')" prop="sampleCode"><el-input v-model="form.sampleCode"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.样品名称')" prop="sampleName"><el-input v-model="form.sampleName"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.检验类型')" prop="testType"><el-select v-model="form.testType"><el-option :label="$t('lims.常规检验')" value="ROUTINE"/><el-option :label="$t('lims.复检')" value="RETEST"/><el-option :label="$t('lims.稳定性')" value="STABILITY"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.检验人')" prop="testerId"><el-input v-model="form.testerId"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.计划完成')" prop="plannedEndDate"><el-date-picker v-model="form.plannedEndDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>

    <!-- 检验结果录入对话框 -->
    <el-dialog :title="$t('lims.检验结果录入')" :visible.sync="resultOpen" width="900px" append-to-body>
      <el-table :data="resultList" border>
        <el-table-column :label="$t('lims.检测项')" prop="testItemName" width="140"/>
        <el-table-column :label="$t('lims.检测项类型')" prop="testItemType" width="100"/>
        <el-table-column :label="$t('lims.标准值')" prop="specificationVal" width="100"/>
        <el-table-column :label="$t('lims.原始值')" width="150">
          <template slot-scope="scope"><el-input v-model="scope.row.originalValue" :placeholder="$t('lims.请输入')"/></template>
        </el-table-column>
        <el-table-column :label="$t('lims.报告值')" width="150">
          <template slot-scope="scope"><el-input v-model="scope.row.reportedValue" :placeholder="$t('lims.请输入')"/></template>
        </el-table-column>
        <el-table-column :label="$t('lims.单项结论')" width="120">
          <template slot-scope="scope">
            <el-select v-model="scope.row.resultConclusion" :placeholder="$t('lims.判定')">
              <el-option :label="$t('lims.合格')" value="QUALIFIED"/><el-option :label="$t('lims.不合格')" value="UNQUALIFIED"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column :label="$t('lims.备注')" prop="remark">
          <template slot-scope="scope"><el-input v-model="scope.row.remark"/></template>
        </el-table-column>
      </el-table>
      <div slot="footer"><el-button type="primary" @click="submitResults">{{ this.$t('lims.保存结果') }}</el-button><el-button @click="resultOpen = false">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>

    <!-- 复核/批准对话框 -->
    <el-dialog :title="actionTitle" :visible.sync="actionOpen" width="500px" append-to-body>
      <el-form ref="actionForm" :model="actionForm" label-width="80px">
        <el-form-item :label="$t('lims.结果')" prop="result">
          <el-select v-model="actionForm.result"><el-option :label="$t('lims.通过')" value="APPROVED"/><el-option :label="$t('lims.驳回')" value="REJECTED"/></el-select>
        </el-form-item>
        <el-form-item :label="$t('lims.意见')" prop="comment"><el-input v-model="actionForm.comment" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitAction">{{ this.$t('lims.确 定') }}</el-button><el-button @click="actionOpen = false">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listTestOrder, getTestOrder, addTestOrder, updateTestOrder, delTestOrder, submitTestOrder, reviewTestOrder, approveTestOrder, getTestResults, batchSaveTestResult } from '@/api/lims/test'
export default {
  name: 'LimsTestOrder',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, testOrderCode: null, sampleCode: null, status: null },
      form: {}, rules: { testOrderCode: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], sampleCode: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }] },
      resultOpen: false, resultList: [], currentOrderId: null,
      actionOpen: false, actionTitle: '', actionType: '', currentActionId: null, actionForm: { result: 'APPROVED', comment: null }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listTestOrder(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.testOrderId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { testOrderId: null, testOrderCode: null, sampleCode: null, sampleName: null, testType: 'ROUTINE', testerId: null, plannedEndDate: null, status: 'DRAFT', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增检验订单') },
    handleUpdate(row) { this.reset(); getTestOrder(row.testOrderId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = this.$t('lims.修改检验订单') }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.testOrderId != null ? updateTestOrder : addTestOrder)(this.form).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.testOrderId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delTestOrder(ids)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleSubmit(row) { this.$modal.confirm(this.$t('lims.是否提交？')).then(() => submitTestOrder(row.testOrderId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.提交成功')) }).catch(() => {}) },
    handleResult(row) { this.currentOrderId = row.testOrderId; getTestResults(row.testOrderId).then(r => { this.resultList = r.data || []; this.resultOpen = true }) },
    submitResults() { batchSaveTestResult({ testOrderId: this.currentOrderId, results: this.resultList }).then(() => { this.$modal.msgSuccess(this.$t('lims.保存成功')); this.resultOpen = false; this.getList() }) },
    handleReview(row) { this.actionType = 'review'; this.currentActionId = row.testOrderId; this.actionTitle = this.$t('lims.检验复核'); this.actionForm = { result: 'APPROVED', comment: null }; this.actionOpen = true },
    handleApprove(row) { this.actionType = 'approve'; this.currentActionId = row.testOrderId; this.actionTitle = this.$t('lims.检验批准'); this.actionForm = { result: 'APPROVED', comment: null }; this.actionOpen = true },
    submitAction() {
      const fn = this.actionType === 'review' ? reviewTestOrder : approveTestOrder
      fn(this.currentActionId, this.actionForm.result, this.actionForm.comment).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.actionOpen = false; this.getList() })
    }
  }
}
</script>
