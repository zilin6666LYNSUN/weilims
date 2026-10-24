<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.CAPA编号')" prop="capaId"><el-input v-model="queryParams.capaId" placeholder="CAPA ID" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.任务状态')" prop="status"><el-select v-model="queryParams.status" :placeholder="$t('lims.全部')" clearable><el-option :label="$t('lims.待处理')" value="PENDING"/><el-option :label="$t('lims.进行中')" value="IN_PROGRESS"/><el-option :label="$t('lims.已完成')" value="COMPLETED"/></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:capa:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:capa:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:capa:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="CAPA ID" align="center" prop="capaId" width="90"/>
      <el-table-column :label="$t('lims.任务名称')" align="center" prop="taskName" width="160"/>
      <el-table-column :label="$t('lims.责任人ID')" align="center" prop="assignee" width="90"/>
      <el-table-column :label="$t('lims.计划完成')" align="center" prop="dueDate" width="110"/>
      <el-table-column :label="$t('lims.任务说明')" align="center" prop="description" show-overflow-tooltip/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100">
        <template slot-scope="scope"><el-tag :type="scope.row.status === 'COMPLETED' ? 'success' : (scope.row.status === 'PENDING' ? 'warning' : 'primary')">{{ scope.row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:capa:edit']">{{ $t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-check" :disabled="scope.row.status === 'COMPLETED'" @click="handleComplete(scope.row)" v-hasPermi="['lims:capa:edit']">{{ $t('lims.完成') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:capa:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12"><el-form-item label="CAPA ID" prop="capaId"><el-input-number v-model="form.capaId" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.任务名称')" prop="taskName"><el-input v-model="form.taskName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.责任人ID')" prop="assignee"><el-input-number v-model="form.assignee" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.计划完成')" prop="dueDate"><el-date-picker v-model="form.dueDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item :label="$t('lims.任务说明')" prop="description"><el-input v-model="form.description" type="textarea" :rows="3"/></el-form-item></el-col></el-row>
        <el-row><el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-select v-model="form.status"><el-option :label="$t('lims.待处理')" value="PENDING"/><el-option :label="$t('lims.进行中')" value="IN_PROGRESS"/><el-option :label="$t('lims.已完成')" value="COMPLETED"/></el-select></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCapaTask, getCapaTask, addCapaTask, updateCapaTask, delCapaTask, completeCapaTask } from '@/api/lims/capa'

export default {
  name: 'CapaTask',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, capaId: undefined, status: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { capaId: [{ required: true, message: this.$t('lims.CAPA ID不能为空'), trigger: 'blur' }], taskName: [{ required: true, message: this.$t('lims.任务名称不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listCapaTask(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.taskId); this.single = selection.length !== 1; this.multiple = !selection.length },
    resetForm(ref) { if (this.$refs[ref]) this.$refs[ref].resetFields() },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增CAPA任务') },
    handleUpdate(row) { this.reset(); const id = row.taskId || this.ids[0]; getCapaTask(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改CAPA任务') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.taskId) { updateCapaTask(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addCapaTask(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleComplete(row) { this.$modal.confirm(this.$t('lims.确认完成CAPA任务{v}？', { v: row.taskName })).then(() => completeCapaTask(row.taskId).then(() => { this.getList(); this.msgSuccess(this.$t('lims.任务已完成')) })).catch(() => {}) },
    handleDelete(row) { const ids = row.taskId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除CAPA任务编号为{v}的数据项？', { v: ids })).then(() => delCapaTask(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) }
  }
}
</script>
