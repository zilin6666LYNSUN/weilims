<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="110px">
      <el-form-item :label="$t('lims.标准品ID')" prop="standardId"><el-input v-model="queryParams.standardId" :placeholder="$t('lims.标准品ID')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.检验单ID')" prop="testOrderId"><el-input v-model="queryParams.testOrderId" :placeholder="$t('lims.检验单ID')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:reagent:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:reagent:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.标准品ID')" align="center" prop="standardId" width="90"/>
      <el-table-column :label="$t('lims.使用日期')" align="center" prop="usedDate" width="110"/>
      <el-table-column :label="$t('lims.使用人ID')" align="center" prop="usedBy" width="90"/>
      <el-table-column :label="$t('lims.检验单ID')" align="center" prop="testOrderId" width="90"/>
      <el-table-column :label="$t('lims.使用量')" align="center" prop="quantityUsed" width="90"/>
      <el-table-column :label="$t('lims.剩余量')" align="center" prop="remainingAfter" width="90"/>
      <el-table-column :label="$t('common.operation')" align="center" width="80">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:reagent:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.标准品ID')" prop="standardId"><el-input-number v-model="form.standardId" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.使用日期')" prop="usedDate"><el-date-picker v-model="form.usedDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.使用人ID')" prop="usedBy"><el-input-number v-model="form.usedBy" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.检验单ID')" prop="testOrderId"><el-input-number v-model="form.testOrderId" :min="1" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.使用量')" prop="quantityUsed"><el-input-number v-model="form.quantityUsed" :min="0" :precision="2" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.剩余量')" prop="remainingAfter"><el-input-number v-model="form.remainingAfter" :min="0" :precision="2" style="width:100%"/></el-form-item></el-col>
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
import { listStandardUsage, addStandardUsage, delStandardUsage } from '@/api/lims/reagent'

export default {
  name: 'StandardUsage',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, standardId: undefined, testOrderId: undefined },
      ids: [], multiple: true, open: false, title: '',
      form: {}, rules: { standardId: [{ required: true, message: this.$t('lims.标准品ID不能为空'), trigger: 'blur' }], quantityUsed: [{ required: true, message: this.$t('lims.使用量不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listStandardUsage(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.usageId); this.multiple = !selection.length },
    resetForm(ref) { if (this.$refs[ref]) this.$refs[ref].resetFields() },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增标准品使用记录') },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { addStandardUsage(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } }) },
    cancel() { this.open = false; this.reset() },
    handleDelete(row) { const ids = row.usageId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除使用记录编号为{v}的数据项？', { v: ids })).then(() => delStandardUsage(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) }
  }
}
</script>
