<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="流程KEY" prop="procKey"><el-input v-model="queryParams.procKey" placeholder="请输入流程KEY" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="流程名称" prop="procName"><el-input v-model="queryParams.procName" placeholder="请输入流程名称" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" value="ACTIVE"/><el-option label="停用" value="INACTIVE"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:process:definition:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:process:definition:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:process:definition:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="流程名称" align="center" prop="procName" width="160"/>
      <el-table-column label="流程KEY" align="center" prop="procKey" width="160"/>
      <el-table-column label="业务类型" align="center" prop="procType" width="140"/>
      <el-table-column label="版本" align="center" prop="version" width="80"/>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'">{{ scope.row.status === 'ACTIVE' ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="生效日期" align="center" prop="effectiveDate" width="140"/>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:process:definition:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-circle-check" @click="handleActivate(scope.row)" v-if="scope.row.status !== 'ACTIVE'" v-hasPermi="['lims:process:definition:edit']">启用</el-button>
          <el-button size="mini" type="text" icon="el-icon-circle-close" @click="handleDeactivate(scope.row)" v-if="scope.row.status === 'ACTIVE'" v-hasPermi="['lims:process:definition:edit']">停用</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:process:definition:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="流程名称" prop="procName"><el-input v-model="form.procName" placeholder="请输入流程名称"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="流程KEY" prop="procKey"><el-input v-model="form.procKey" placeholder="如 report_release" :disabled="form.procDefId != null"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="业务类型" prop="procType"><el-input v-model="form.procType" placeholder="如 REPORT_RELEASE"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="生效日期" prop="effectiveDate"><el-date-picker v-model="form.effectiveDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择生效日期" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="流程定义XML" prop="bpmnXml">
          <el-input v-model="form.bpmnXml" type="textarea" :rows="10" placeholder="请输入流程定义XML（节点与审批规则）"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProcessDefinition, getProcessDefinition, addProcessDefinition, updateProcessDefinition, delProcessDefinition, activateProcessDefinition, deactivateProcessDefinition } from '@/api/lims/process'

export default {
  name: 'LimsProcessDefinition',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, procKey: undefined, procName: undefined, status: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { procName: [{ required: true, message: '流程名称不能为空', trigger: 'blur' }], procKey: [{ required: true, message: '流程KEY不能为空', trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listProcessDefinition(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.procDefId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = '新增流程定义' },
    handleUpdate(row) { this.reset(); const id = row.procDefId || this.ids[0]; getProcessDefinition(id).then(response => { this.form = response.data; this.open = true; this.title = '修改流程定义' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.procDefId != null) { updateProcessDefinition(this.form).then(() => { this.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addProcessDefinition(this.form).then(() => { this.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    handleActivate(row) { this.$confirm('确认启用流程定义「' + row.procName + '」?同一KEY下其他启用版本将自动停用。', '警告', { type: 'warning' }).then(() => activateProcessDefinition(row.procDefId).then(() => { this.msgSuccess('启用成功'); this.getList() })).catch(() => {}) },
    handleDeactivate(row) { this.$confirm('确认停用流程定义「' + row.procName + '」?', '警告', { type: 'warning' }).then(() => deactivateProcessDefinition(row.procDefId).then(() => { this.msgSuccess('停用成功'); this.getList() })).catch(() => {}) },
    handleDelete(row) { const ids = row.procDefId || this.ids; this.$confirm('确认删除选中的数据项?', '警告', { type: 'warning' }).then(() => delProcessDefinition(ids).then(() => { this.getList(); this.msgSuccess('删除成功') })).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
