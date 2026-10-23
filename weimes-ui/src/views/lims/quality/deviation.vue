<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.偏差编号')" prop="deviationCode"><el-input v-model="queryParams.deviationCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.偏差类型')" prop="deviationType">
        <el-select v-model="queryParams.deviationType" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.计划内')" value="PLANNED"/><el-option :label="$t('lims.计划外')" value="UNPLANNED"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.草稿')" value="DRAFT"/><el-option :label="$t('lims.待审批')" value="PENDING_APPROVAL"/><el-option :label="$t('lims.已批准')" value="APPROVED"/><el-option :label="$t('lims.执行中')" value="IN_PROGRESS"/><el-option :label="$t('lims.已关闭')" value="CLOSED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:deviation:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:deviation:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:deviation:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.偏差编号')" align="center" prop="deviationCode" width="140"/>
      <el-table-column :label="$t('lims.偏差类型')" align="center" prop="deviationType" width="100">
        <template slot-scope="scope">{{ scope.row.deviationType === 'PLANNED' ? this.$t('lims.计划内') : $t('lims.计划外') }}</template>
      </el-table-column>
      <el-table-column :label="$t('lims.偏差描述')" align="center" prop="description" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('lims.发现日期')" align="center" prop="discoveryDate" width="110"/>
      <el-table-column :label="$t('lims.严重程度')" align="center" prop="severityLevel" width="100">
        <template slot-scope="scope"><el-tag :type="{ CRITICAL: 'danger', MAJOR: 'warning', MINOR: 'info' }[scope.row.severityLevel]">{{ { CRITICAL: this.$t('lims.严重'), MAJOR: this.$t('lims.主要'), MINOR: this.$t('lims.次要') }[scope.row.severityLevel] }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="{ DRAFT: 'info', PENDING_APPROVAL: 'warning', APPROVED: '', IN_PROGRESS: '', CLOSED: 'success' }[scope.row.status]">{{ { DRAFT: this.$t('lims.草稿'), PENDING_APPROVAL: this.$t('lims.待审批'), APPROVED: this.$t('lims.已批准'), IN_PROGRESS: this.$t('lims.执行中'), CLOSED: this.$t('lims.已关闭') }[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:deviation:edit']">{{ this.$t('lims.提交') }}</el-button>
          <el-button size="mini" type="text" @click="handleApprove(scope.row)" v-if="scope.row.status === 'PENDING_APPROVAL'" v-hasPermi="['lims:deviation:approve']">{{ this.$t('lims.审批') }}</el-button>
          <el-button size="mini" type="text" @click="handleClose(scope.row)" v-if="scope.row.status === 'IN_PROGRESS'" v-hasPermi="['lims:deviation:close']">{{ this.$t('lims.关闭') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:deviation:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:deviation:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.偏差编号')" prop="deviationCode"><el-input v-model="form.deviationCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.偏差类型')" prop="deviationType"><el-select v-model="form.deviationType"><el-option :label="$t('lims.计划内')" value="PLANNED"/><el-option :label="$t('lims.计划外')" value="UNPLANNED"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.发现日期')" prop="discoveryDate"><el-date-picker v-model="form.discoveryDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.严重程度')" prop="severityLevel"><el-select v-model="form.severityLevel"><el-option :label="$t('lims.严重')" value="CRITICAL"/><el-option :label="$t('lims.主要')" value="MAJOR"/><el-option :label="$t('lims.次要')" value="MINOR"/></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.偏差描述')" prop="description"><el-input v-model="form.description" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.根本原因')" prop="rootCause"><el-input v-model="form.rootCause" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.即时措施')" prop="immediateAction"><el-input v-model="form.immediateAction" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listDeviation, getDeviation, addDeviation, updateDeviation, delDeviation, submitDeviation, approveDeviation, closeDeviation } from '@/api/lims/deviation'
export default {
  name: 'LimsDeviation',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, deviationCode: null, deviationType: null, status: null },
      form: {}, rules: { deviationCode: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], description: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], severityLevel: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listDeviation(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.deviationId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { deviationId: null, deviationCode: null, deviationType: 'UNPLANNED', description: null, discoveryDate: null, severityLevel: 'MINOR', rootCause: null, immediateAction: null, status: 'DRAFT', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增偏差') },
    handleUpdate(row) { this.reset(); getDeviation(row.deviationId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = this.$t('lims.修改偏差') }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.deviationId != null ? updateDeviation : addDeviation)(this.form).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.deviationId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delDeviation(ids)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleSubmit(row) { this.$modal.confirm(this.$t('lims.是否提交该偏差？')).then(() => submitDeviation(row.deviationId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.提交成功')) }).catch(() => {}) },
    handleApprove(row) { this.$modal.confirm(this.$t('lims.是否审批通过？')).then(() => approveDeviation(row.deviationId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.审批成功')) }).catch(() => {}) },
    handleClose(row) { this.$modal.confirm(this.$t('lims.是否关闭该偏差？')).then(() => closeDeviation(row.deviationId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.关闭成功')) }).catch(() => {}) }
  }
}
</script>
