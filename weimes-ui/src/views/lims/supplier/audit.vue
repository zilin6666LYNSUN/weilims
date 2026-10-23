<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.审计编号')" prop="auditCode"><el-input v-model="queryParams.auditCode" :placeholder="$t('lims.请输入审计编号')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.审计类型')" prop="auditType"><el-input v-model="queryParams.auditType" :placeholder="$t('lims.请输入审计类型')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:supplier:audit:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:supplier:audit:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:supplier:audit:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.供应商ID')" align="center" prop="vendorId" width="120"/>
      <el-table-column :label="$t('lims.审计编号')" align="center" prop="auditCode" width="120"/>
      <el-table-column :label="$t('lims.审计类型')" align="center" prop="auditType" width="120"/>
      <el-table-column :label="$t('lims.审计日期')" align="center" prop="auditDate" width="110"/>
      <el-table-column :label="$t('lims.审计人员')" align="center" prop="auditorIds" width="120"/>
      <el-table-column :label="$t('lims.审计范围')" align="center" prop="auditScope" width="120"/>
      <el-table-column :label="$t('lims.审计发现')" align="center" prop="auditFindings" width="120"/>
      <el-table-column :label="$t('lims.分类')" align="center" prop="auditClassification" width="120"/>
      <el-table-column :label="$t('lims.需CAPA')" align="center" prop="capaRequired" width="120"/>
      <el-table-column :label="$t('lims.CAPA编号')" align="center" prop="capaIds" width="120"/>
      <el-table-column :label="$t('lims.评分')" align="center" prop="auditScore" width="110"/>
      <el-table-column :label="$t('lims.下次审计')" align="center" prop="nextAuditDate" width="110"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="120"/>
      <el-table-column :label="$t('common.operation')" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:supplier:audit:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:supplier:audit:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.供应商ID')" prop="vendorId"><el-input-number v-model="form.vendorId" :min="0" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.审计编号')" prop="auditCode"><el-input v-model="form.auditCode" :placeholder="$t('lims.请输入审计编号')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.审计类型')" prop="auditType"><el-input v-model="form.auditType" :placeholder="$t('lims.请输入审计类型')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.审计日期')" prop="auditDate"><el-date-picker v-model="form.auditDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.审计人员')" prop="auditorIds"><el-input v-model="form.auditorIds" :placeholder="$t('lims.请输入审计人员')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.审计范围')" prop="auditScope"><el-input v-model="form.auditScope" :placeholder="$t('lims.请输入审计范围')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.审计发现')" prop="auditFindings"><el-input v-model="form.auditFindings" :placeholder="$t('lims.请输入审计发现')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.分类')" prop="auditClassification"><el-input v-model="form.auditClassification" :placeholder="$t('lims.请输入分类')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.需CAPA')" prop="capaRequired"><el-input-number v-model="form.capaRequired" :min="0" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.CAPA编号')" prop="capaIds"><el-input v-model="form.capaIds" :placeholder="$t('lims.请输入CAPA编号')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.评分')" prop="auditScore"><el-input-number v-model="form.auditScore" :min="0" :precision="2" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.下次审计')" prop="nextAuditDate"><el-date-picker v-model="form.nextAuditDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-input v-model="form.status" :placeholder="$t('lims.请输入状态')"/></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSupplierAudit, getSupplierAudit, addSupplierAudit, updateSupplierAudit, delSupplierAudit } from '@/api/lims/supplier'

export default {
  name: 'LimsSupplierAudit',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, vendorId: undefined, auditCode: undefined, auditType: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listSupplierAudit(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.auditId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('common.add') },
    handleUpdate(row) { this.reset(); const id = row.auditId || this.ids[0]; getSupplierAudit(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.auditId != null) { updateSupplierAudit(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addSupplierAudit(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    handleDelete(row) { const ids = row.auditId || this.ids; this.$confirm(this.$t('lims.确认删除选中的数据项?'), this.$t('lims.警告'), { type: 'warning' }).then(() => delSupplierAudit(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
