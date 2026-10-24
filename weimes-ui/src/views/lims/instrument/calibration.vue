<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.校准类型')" prop="calibrationType">
        <el-select v-model="queryParams.calibrationType" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.内部校准')" value="INTERNAL"/><el-option :label="$t('lims.外部校准')" value="EXTERNAL"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('lims.校准结果')" prop="calibrationResult">
        <el-select v-model="queryParams.calibrationResult" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.合格')" value="QUALIFIED"/><el-option :label="$t('lims.不合格')" value="UNQUALIFIED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:instrument:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList">
      <el-table-column :label="$t('lims.校准编号')" align="center" prop="calibrationCode" width="140"/>
      <el-table-column :label="$t('lims.仪器')" align="center" prop="instrumentName" width="140"/>
      <el-table-column :label="$t('lims.校准类型')" align="center" prop="calibrationType" width="100">
        <template slot-scope="scope">{{ scope.row.calibrationType === 'INTERNAL' ? $t('lims.内部') : $t('lims.外部') }}</template>
      </el-table-column>
      <el-table-column :label="$t('lims.校准日期')" align="center" prop="calibrationDate" width="120"/>
      <el-table-column :label="$t('lims.下次校准')" align="center" prop="nextCalibration" width="120"/>
      <el-table-column :label="$t('lims.校准机构')" align="center" prop="calibrationOrg" width="140"/>
      <el-table-column :label="$t('lims.证书号')" align="center" prop="certificateNo" width="140"/>
      <el-table-column :label="$t('lims.校准结果')" align="center" prop="calibrationResult" width="100">
        <template slot-scope="scope"><el-tag :type="scope.row.calibrationResult === 'QUALIFIED' ? 'success' : 'danger'">{{ scope.row.calibrationResult === 'QUALIFIED' ? $t('lims.合格') : $t('lims.不合格') }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('lims.校准人')" align="center" prop="calibratedBy" width="100"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100"/>
      <el-table-column :label="$t('common.operation')" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:instrument:edit']">{{ $t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:instrument:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.校准编号')" prop="calibrationCode"><el-input v-model="form.calibrationCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.校准类型')" prop="calibrationType"><el-select v-model="form.calibrationType"><el-option :label="$t('lims.内部校准')" value="INTERNAL"/><el-option :label="$t('lims.外部校准')" value="EXTERNAL"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.校准日期')" prop="calibrationDate"><el-date-picker v-model="form.calibrationDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.下次校准')" prop="nextCalibration"><el-date-picker v-model="form.nextCalibration" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.校准机构')" prop="calibrationOrg"><el-input v-model="form.calibrationOrg"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.证书号')" prop="certificateNo"><el-input v-model="form.certificateNo"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.校准结果')" prop="calibrationResult"><el-select v-model="form.calibrationResult"><el-option :label="$t('lims.合格')" value="QUALIFIED"/><el-option :label="$t('lims.不合格')" value="UNQUALIFIED"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.校准人')" prop="calibratedBy"><el-input v-model="form.calibratedBy"/></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listCalibration, addCalibration, updateCalibration, delCalibration } from '@/api/lims/instrumentSub'
export default {
  name: 'LimsInstrumentCalibration',
  data() {
    return {
      loading: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, calibrationType: null, calibrationResult: null },
      form: {}, rules: { calibrationCode: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], calibrationDate: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listCalibration(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    reset() { this.form = { calibrationId: null, instrumentId: null, calibrationCode: null, calibrationType: 'EXTERNAL', calibrationDate: null, nextCalibration: null, calibrationOrg: null, certificateNo: null, calibrationResult: null, calibratedBy: null, status: 'DRAFT', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增校准记录') },
    handleUpdate(row) { this.reset(); this.form = { ...row }; this.open = true; this.title = this.$t('lims.修改校准记录') },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.calibrationId != null ? updateCalibration : addCalibration)(this.form).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.open = false; this.getList() }) } }) },
    handleDelete(row) { this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delCalibration(row.calibrationId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
