<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.偏差单号')" prop="deviationId"><el-input v-model="queryParams.deviationId" :placeholder="$t('lims.偏差ID')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.影响类型')" prop="impactType"><el-select v-model="queryParams.impactType" :placeholder="$t('lims.全部')" clearable><el-option :label="$t('lims.产品')" value="PRODUCT"/><el-option :label="$t('lims.批次')" value="BATCH"/><el-option :label="$t('lims.客户')" value="CUSTOMER"/><el-option :label="$t('lims.物料')" value="MATERIAL"/></el-select></el-form-item>
      <el-form-item :label="$t('lims.评估状态')" prop="status"><el-select v-model="queryParams.status" :placeholder="$t('lims.全部')" clearable><el-option :label="$t('lims.待评估')" value="PENDING"/><el-option :label="$t('lims.评估中')" value="ASSESSING"/><el-option :label="$t('lims.已完成')" value="COMPLETED"/></el-select></el-form-item>
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
      <el-table-column :label="$t('lims.偏差ID')" align="center" prop="deviationId" width="90"/>
      <el-table-column :label="$t('lims.影响类型')" align="center" prop="impactType" width="100"/>
      <el-table-column :label="$t('lims.受影响对象')" align="center" prop="impactTarget"/>
      <el-table-column :label="$t('lims.影响描述')" align="center" prop="impactDesc" show-overflow-tooltip/>
      <el-table-column :label="$t('lims.风险等级')" align="center" prop="riskLevel" width="90"/>
      <el-table-column :label="$t('lims.评估状态')" align="center" prop="status" width="100">
        <template slot-scope="scope"><el-tag :type="scope.row.status === 'COMPLETED' ? 'success' : (scope.row.status === 'PENDING' ? 'warning' : 'primary')">{{ scope.row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:deviation:edit']">{{ $t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:deviation:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.偏差ID')" prop="deviationId"><el-input-number v-model="form.deviationId" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.影响类型')" prop="impactType"><el-select v-model="form.impactType"><el-option :label="$t('lims.产品')" value="PRODUCT"/><el-option :label="$t('lims.批次')" value="BATCH"/><el-option :label="$t('lims.客户')" value="CUSTOMER"/><el-option :label="$t('lims.物料')" value="MATERIAL"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item :label="$t('lims.受影响对象')" prop="impactTarget"><el-input v-model="form.impactTarget" :placeholder="$t('lims.批次号/产品名称/客户名称等')"/></el-form-item></el-col></el-row>
        <el-row><el-col :span="24"><el-form-item :label="$t('lims.影响描述')" prop="impactDesc"><el-input v-model="form.impactDesc" type="textarea" :rows="3"/></el-form-item></el-col></el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.风险等级')" prop="riskLevel"><el-select v-model="form.riskLevel"><el-option :label="$t('lims.高')" value="HIGH"/><el-option :label="$t('lims.中')" value="MEDIUM"/><el-option :label="$t('lims.低')" value="LOW"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.评估状态')" prop="status"><el-select v-model="form.status"><el-option :label="$t('lims.待评估')" value="PENDING"/><el-option :label="$t('lims.评估中')" value="ASSESSING"/><el-option :label="$t('lims.已完成')" value="COMPLETED"/></el-select></el-form-item></el-col>
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
import { listDeviationImpact, getDeviationImpact, addDeviationImpact, updateDeviationImpact, delDeviationImpact } from '@/api/lims/deviation'

export default {
  name: 'DeviationImpact',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, deviationId: undefined, impactType: undefined, status: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { deviationId: [{ required: true, message: this.$t('lims.偏差ID不能为空'), trigger: 'blur' }], impactType: [{ required: true, message: this.$t('lims.影响类型不能为空'), trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listDeviationImpact(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.impactId); this.single = selection.length !== 1; this.multiple = !selection.length },
    resetForm(ref) { if (this.$refs[ref]) this.$refs[ref].resetFields() },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增偏差影响评估') },
    handleUpdate(row) { this.reset(); const id = row.impactId || this.ids[0]; getDeviationImpact(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改偏差影响评估') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.impactId) { updateDeviationImpact(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addDeviationImpact(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleDelete(row) { const ids = row.impactId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除影响评估编号为{v}的数据项？', { v: ids })).then(() => delDeviationImpact(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) }
  }
}
</script>
