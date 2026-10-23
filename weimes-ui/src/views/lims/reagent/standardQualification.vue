<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item :label="$t('lims.标准品ID')" prop="standardId"><el-input v-model="queryParams.standardId" :placeholder="$t('lims.标准品ID')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.资质类型')" prop="qualType"><el-select v-model="queryParams.qualType" :placeholder="$t('lims.全部')" clearable><el-option :label="$t('lims.检定证书')" value="CERT"/><el-option :label="$t('lims.标定')" value="CALIBRATION"/><el-option :label="$t('lims.期间核查')" value="PERIODIC"/></el-select></el-form-item>
      <el-form-item :label="$t('lims.结论')" prop="qualifiedConclusion"><el-select v-model="queryParams.qualifiedConclusion" :placeholder="$t('lims.全部')" clearable><el-option :label="$t('lims.合格')" value="PASS"/><el-option :label="$t('lims.不合格')" value="FAIL"/></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:reagent:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:reagent:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:reagent:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.标准品ID')" align="center" prop="standardId" width="90"/>
      <el-table-column :label="$t('lims.资质类型')" align="center" prop="qualType" width="110"/>
      <el-table-column :label="$t('lims.资质日期')" align="center" prop="qualDate" width="110"/>
      <el-table-column :label="$t('lims.资质方法')" align="center" prop="qualMethod" width="120"/>
      <el-table-column :label="$t('lims.测试结果')" align="center" prop="testResult"/>
      <el-table-column :label="$t('lims.结论')" align="center" prop="qualifiedConclusion" width="90">
        <template slot-scope="scope"><el-tag :type="scope.row.qualifiedConclusion === 'PASS' ? 'success' : 'danger'">{{ scope.row.qualifiedConclusion }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('lims.资质人')" align="center" prop="qualifiedBy" width="90"/>
      <el-table-column :label="$t('lims.批准人')" align="center" prop="approvedBy" width="90"/>
      <el-table-column :label="$t('common.operation')" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:reagent:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:reagent:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.标准品ID')" prop="standardId"><el-input-number v-model="form.standardId" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.资质类型')" prop="qualType"><el-select v-model="form.qualType"><el-option :label="$t('lims.检定证书')" value="CERT"/><el-option :label="$t('lims.标定')" value="CALIBRATION"/><el-option :label="$t('lims.期间核查')" value="PERIODIC"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.资质日期')" prop="qualDate"><el-date-picker v-model="form.qualDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.资质方法')" prop="qualMethod"><el-input v-model="form.qualMethod" :placeholder="$t('lims.如按药典/企业内控')"/></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item :label="$t('lims.测试结果')" prop="testResult"><el-input v-model="form.testResult" type="textarea"/></el-form-item></el-col></el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.结论')" prop="qualifiedConclusion"><el-select v-model="form.qualifiedConclusion"><el-option :label="$t('lims.合格')" value="PASS"/><el-option :label="$t('lims.不合格')" value="FAIL"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-select v-model="form.status"><el-option :label="$t('lims.启用')" value="ENABLED"/><el-option :label="$t('lims.停用')" value="DISABLED"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.资质人ID')" prop="qualifiedBy"><el-input-number v-model="form.qualifiedBy" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.批准人ID')" prop="approvedBy"><el-input-number v-model="form.approvedBy" :min="1" style="width:100%"/></el-form-item></el-col>
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
import { listStandardQualification, getStandardQualification, addStandardQualification, updateStandardQualification, delStandardQualification } from '@/api/lims/reagent'

export default {
  name: 'StandardQualification',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, standardId: undefined, qualType: undefined, qualifiedConclusion: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { standardId: [{ required: true, message: this.$t('lims.标准品ID不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listStandardQualification(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.qualId); this.single = selection.length !== 1; this.multiple = !selection.length },
    resetForm(ref) { if (this.$refs[ref]) this.$refs[ref].resetFields() },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增标准品资质') },
    handleUpdate(row) { this.reset(); const id = row.qualId || this.ids[0]; getStandardQualification(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改标准品资质') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.qualId) { updateStandardQualification(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addStandardQualification(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleDelete(row) { const ids = row.qualId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除标准品资质编号为{v}的数据项？', { v: ids })).then(() => delStandardQualification(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) }
  }
}
</script>
