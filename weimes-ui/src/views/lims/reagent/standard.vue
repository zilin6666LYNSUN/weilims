<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.标准品编号')" prop="standardCode"><el-input v-model="queryParams.standardCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.标准品名称')" prop="standardName"><el-input v-model="queryParams.standardName" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
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
      <el-table-column :label="$t('lims.标准品编号')" align="center" prop="standardCode" width="130"/>
      <el-table-column :label="$t('lims.标准品名称')" align="center" prop="standardName" width="160"/>
      <el-table-column :label="$t('lims.批号')" align="center" prop="lotNumber" width="120"/>
      <el-table-column :label="$t('lims.来源')" align="center" prop="source" width="120"/>
      <el-table-column :label="$t('lims.效价')" align="center" prop="potency" width="80"/>
      <el-table-column :label="$t('lims.剩余量')" align="center" prop="remainingQuantity" width="90"/>
      <el-table-column :label="$t('lims.有效期至')" align="center" prop="expireDate" width="110"/>
      <el-table-column :label="$t('lims.存储条件')" align="center" prop="storageCondition" width="100"/>
      <el-table-column :label="$t('common.operation')" align="center" width="160">
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
          <el-col :span="12"><el-form-item :label="$t('lims.标准品编号')" prop="standardCode"><el-input v-model="form.standardCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.标准品名称')" prop="standardName"><el-input v-model="form.standardName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.批号')" prop="lotNumber"><el-input v-model="form.lotNumber"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.来源')" prop="source"><el-input v-model="form.source" :placeholder="$t('lims.如USP/NIST')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.效价')" prop="potency"><el-input v-model="form.potency"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.剩余量')" prop="remainingQuantity"><el-input-number v-model="form.remainingQuantity" :min="0" :precision="2"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.有效期至')" prop="expireDate"><el-date-picker v-model="form.expireDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.存储条件')" prop="storageCondition"><el-input v-model="form.storageCondition"/></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listStandard, getStandard, addStandard, updateStandard, delStandard } from '@/api/lims/reagent'
export default {
  name: 'LimsReferenceStandard',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, standardCode: null, standardName: null },
      form: {}, rules: { standardCode: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], standardName: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listStandard(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.standardId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { standardId: null, standardCode: null, standardName: null, lotNumber: null, source: null, potency: null, remainingQuantity: 0, expireDate: null, storageCondition: null, remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增标准品') },
    handleUpdate(row) { this.reset(); getStandard(row.standardId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = this.$t('lims.修改标准品') }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.standardId != null ? updateStandard : addStandard)(this.form).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.standardId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delStandard(ids)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
