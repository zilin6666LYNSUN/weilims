<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.规则编号')" prop="ruleCode"><el-input v-model="queryParams.ruleCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.规则名称')" prop="ruleName"><el-input v-model="queryParams.ruleName" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.规则类型')" prop="ruleType">
        <el-select v-model="queryParams.ruleType" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.范围校验')" value="RANGE"/><el-option :label="$t('lims.异常值')" value="OUTLIER"/><el-option :label="$t('lims.重复性')" value="REPEATABILITY"/><el-option :label="$t('lims.准确度')" value="ACCURACY"/><el-option :label="$t('lims.完整性')" value="COMPLETENESS"/><el-option :label="$t('lims.自定义')" value="CUSTOM"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:report:validation:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:report:validation:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:report:validation:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.规则编号')" align="center" prop="ruleCode" width="120"/>
      <el-table-column :label="$t('lims.规则名称')" align="center" prop="ruleName" width="160"/>
      <el-table-column :label="$t('lims.规则类型')" align="center" prop="ruleType" width="120">
        <template slot-scope="scope">{{ {RANGE: this.$t('lims.范围校验'),OUTLIER: this.$t('lims.异常值'),REPEATABILITY: this.$t('lims.重复性'),ACCURACY: this.$t('lims.准确度'),COMPLETENESS: this.$t('lims.完整性'),CUSTOM: this.$t('lims.自定义')}[scope.row.ruleType] }}</template>
      </el-table-column>
      <el-table-column :label="$t('lims.错误级别')" align="center" prop="errorLevel" width="100">
        <template slot-scope="scope"><el-tag :type="{WARNING:'warning',ERROR:'danger',CRITICAL:'danger'}[scope.row.errorLevel]">{{ scope.row.errorLevel }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('lims.错误提示')" align="center" prop="errorMessage" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="isEnabled" width="80">
        <template slot-scope="scope"><el-tag :type="scope.row.isEnabled===1?'success':'info'">{{ scope.row.isEnabled===1?this.$t('lims.启用'):$t('lims.禁用') }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:report:validation:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:report:validation:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.规则编号')" prop="ruleCode"><el-input v-model="form.ruleCode" :disabled="form.ruleId!=null"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.规则名称')" prop="ruleName"><el-input v-model="form.ruleName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.规则类型')" prop="ruleType">
            <el-select v-model="form.ruleType"><el-option :label="$t('lims.范围校验')" value="RANGE"/><el-option :label="$t('lims.异常值')" value="OUTLIER"/><el-option :label="$t('lims.重复性')" value="REPEATABILITY"/><el-option :label="$t('lims.准确度')" value="ACCURACY"/><el-option :label="$t('lims.完整性')" value="COMPLETENESS"/><el-option :label="$t('lims.自定义')" value="CUSTOM"/></el-select>
          </el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.错误级别')" prop="errorLevel">
            <el-select v-model="form.errorLevel"><el-option :label="$t('lims.警告')" value="WARNING"/><el-option :label="$t('lims.错误')" value="ERROR"/><el-option :label="$t('lims.严重')" value="CRITICAL"/></el-select>
          </el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.校验表达式')" prop="ruleExpression"><el-input v-model="form.ruleExpression" type="textarea" :rows="2"/></el-form-item>
        <el-form-item :label="$t('lims.校验参数(JSON)')" prop="ruleParams"><el-input v-model="form.ruleParams" type="textarea" :rows="2" placeholder='{"key":"value"}'/></el-form-item>
        <el-form-item :label="$t('lims.错误提示')" prop="errorMessage"><el-input v-model="form.errorMessage"/></el-form-item>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.是否启用')"><el-switch v-model="form.isEnabled" :active-value="1" :inactive-value="0"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.检测项名称')"><el-input v-model="form.testItemName" :placeholder="$t('lims.留空则适用于所有检测项')"/></el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listValidationRule, getValidationRule, addValidationRule, updateValidationRule, delValidationRule } from '@/api/lims/validation'

export default {
  name: 'Validation',
  data() {
    return {
      loading: true, showSearch: true, total: 0, dataList: [], ids: [], single: true, multiple: true,
      queryParams: { pageNum: 1, pageSize: 10, ruleCode: null, ruleName: null, ruleType: null },
      open: false, title: '',
      form: {}, rules: { ruleCode: [{ required: true, message: this.$t('lims.请输入规则编号'), trigger: 'blur' }], ruleName: [{ required: true, message: this.$t('lims.请输入规则名称'), trigger: 'blur' }], ruleType: [{ required: true, message: this.$t('lims.请选择规则类型'), trigger: 'change' }], errorLevel: [{ required: true, message: this.$t('lims.请选择错误级别'), trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listValidationRule(this.queryParams).then(res => { this.dataList = res.rows; this.total = res.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.ruleId); this.single = selection.length !== 1; this.multiple = !selection.length },
    reset() { this.form = { ruleId: null, ruleCode: null, ruleName: null, ruleType: null, ruleExpression: null, ruleParams: null, errorLevel: 'ERROR', errorMessage: null, isEnabled: 1, testItemName: null, status: 'ACTIVE', remark: null } },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.添加校验规则') },
    handleUpdate(row) { const ruleId = row.ruleId || this.ids[0]; getValidationRule(ruleId).then(res => { this.form = res.data; this.open = true; this.title = this.$t('lims.修改校验规则') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.ruleId != null) { updateValidationRule(this.form).then(() => { this.$modal.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addValidationRule(this.form).then(() => { this.$modal.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleDelete(row) { const ruleIds = row.ruleId || this.ids; this.$modal.confirm(this.$t('lims.确认删除？')).then(() => delValidationRule(ruleIds)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) }
  }
}
</script>
