<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.人员ID')" prop="staffId"><el-input v-model="queryParams.staffId" :placeholder="$t('lims.请输入人员ID')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.资质类型')" prop="qualificationType"><el-input v-model="queryParams.qualificationType" :placeholder="$t('lims.请输入资质类型')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('lims.请选择状态')" clearable>
          <el-option :label="$t('lims.有效')" value="VALID"/><el-option :label="$t('lims.已过期')" value="EXPIRED"/><el-option :label="$t('lims.作废')" value="VOID"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button><el-button type="warning" icon="el-icon-alarm-clock" size="mini" @click="handleExpiring">{{ this.$t('lims.到期提醒(30天内)') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:qualification:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:qualification:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:qualification:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.人员ID')" align="center" prop="staffId" width="90"/>
      <el-table-column :label="$t('lims.资质类型')" align="center" prop="qualificationType" width="120"/>
      <el-table-column :label="$t('lims.资质名称')" align="center" prop="qualificationName" width="160"/>
      <el-table-column :label="$t('lims.资质范围')" align="center" prop="qualificationScope" width="160"/>
      <el-table-column :label="$t('lims.发证日期')" align="center" prop="grantedDate" width="110"/>
      <el-table-column :label="$t('lims.有效期至')" align="center" prop="expireDate" width="110"/>
      <el-table-column :label="$t('lims.再认证日期')" align="center" prop="requalificationDate" width="110"/>
      <el-table-column :label="$t('lims.状态')" align="center" width="90">
        <template slot-scope="scope"><el-tag :type="scope.row.status === 'VALID' ? 'success' : scope.row.status === 'EXPIRED' ? 'danger' : 'info'">{{ statusText(scope.row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:qualification:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:qualification:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.人员ID')" prop="staffId"><el-input-number v-model="form.staffId" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.资质类型')" prop="qualificationType"><el-input v-model="form.qualificationType" :placeholder="$t('lims.如 上岗证/检验员证')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.资质名称')" prop="qualificationName"><el-input v-model="form.qualificationName" :placeholder="$t('lims.请输入资质名称')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.资质范围')" prop="qualificationScope"><el-input v-model="form.qualificationScope" :placeholder="$t('lims.请输入资质范围')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.发证日期')" prop="grantedDate"><el-date-picker v-model="form.grantedDate" type="date" value-format="yyyy-MM-dd" :placeholder="$t('lims.请选择发证日期')" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.有效期至')" prop="expireDate"><el-date-picker v-model="form.expireDate" type="date" value-format="yyyy-MM-dd" :placeholder="$t('lims.请选择有效期至')" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.再认证日期')" prop="requalificationDate"><el-date-picker v-model="form.requalificationDate" type="date" value-format="yyyy-MM-dd" :placeholder="$t('lims.请选择再认证日期')" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-select v-model="form.status" :placeholder="$t('lims.请选择状态')"><el-option :label="$t('lims.有效')" value="VALID"/><el-option :label="$t('lims.已过期')" value="EXPIRED"/><el-option :label="$t('lims.作废')" value="VOID"/></el-select></el-form-item></el-col>
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
import { listQualification, getQualification, addQualification, updateQualification, delQualification, listExpiringQualification } from '@/api/lims/training'

export default {
  name: 'LimsQualification',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, staffId: undefined, qualificationType: undefined, status: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { staffId: [{ required: true, message: this.$t('lims.人员ID不能为空'), trigger: 'blur' }], qualificationName: [{ required: true, message: this.$t('lims.资质名称不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listQualification(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleExpiring() { this.loading = true; listExpiringQualification().then(response => { this.dataList = response.data || response || []; this.total = this.dataList.length; this.loading = false; this.msgSuccess(this.$t('lims.已展示30天内到期的资质')) }) },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.qualificationId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增上岗资质') },
    handleUpdate(row) { this.reset(); const id = row.qualificationId || this.ids[0]; getQualification(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改上岗资质') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.qualificationId != null) { updateQualification(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addQualification(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    handleDelete(row) { const ids = row.qualificationId || this.ids; this.$confirm(this.$t('lims.确认删除选中的数据项?'), this.$t('lims.警告'), { type: 'warning' }).then(() => delQualification(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    statusText(status) { return { VALID: this.$t('lims.有效'), EXPIRED: this.$t('lims.已过期'), VOID: this.$t('lims.作废') }[status] || status }
  }
}
</script>
