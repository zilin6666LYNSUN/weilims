<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.计划编码')" prop="planCode"><el-input v-model="queryParams.planCode" :placeholder="$t('lims.请输入计划编码')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.计划名称')" prop="planName"><el-input v-model="queryParams.planName" :placeholder="$t('lims.请输入计划名称')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:training:plan:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:training:plan:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:training:plan:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.计划编码')" align="center" prop="planCode" width="120"/>
      <el-table-column :label="$t('lims.计划名称')" align="center" prop="planName" width="120"/>
      <el-table-column :label="$t('lims.年度')" align="center" prop="planYear" width="120"/>
      <el-table-column :label="$t('lims.培训类型')" align="center" prop="trainingType" width="120"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="120"/>
      <el-table-column :label="$t('common.operation')" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:training:plan:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:training:plan:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.计划编码')" prop="planCode"><el-input v-model="form.planCode" :placeholder="$t('lims.请输入计划编码')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.计划名称')" prop="planName"><el-input v-model="form.planName" :placeholder="$t('lims.请输入计划名称')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.年度')" prop="planYear"><el-input-number v-model="form.planYear" :min="0" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.培训类型')" prop="trainingType"><el-input v-model="form.trainingType" :placeholder="$t('lims.请输入培训类型')"/></el-form-item></el-col>
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
import { listTrainingPlan, getTrainingPlan, addTrainingPlan, updateTrainingPlan, delTrainingPlan } from '@/api/lims/training'

export default {
  name: 'LimsTrainingPlan',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, planCode: undefined, planName: undefined, planYear: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listTrainingPlan(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.planId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('common.add') },
    handleUpdate(row) { this.reset(); const id = row.planId || this.ids[0]; getTrainingPlan(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.planId != null) { updateTrainingPlan(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addTrainingPlan(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    handleDelete(row) { const ids = row.planId || this.ids; this.$confirm(this.$t('lims.确认删除选中的数据项?'), this.$t('lims.警告'), { type: 'warning' }).then(() => delTrainingPlan(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
