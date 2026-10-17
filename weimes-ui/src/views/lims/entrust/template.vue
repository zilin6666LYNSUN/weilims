<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="模板编码" prop="templateCode"><el-input v-model="queryParams.templateCode" placeholder="请输入模板编码" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="模板名称" prop="templateName"><el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:entrust:template:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:entrust:template:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:entrust:template:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="模板编码" align="center" prop="templateCode" width="120"/>
      <el-table-column label="模板名称" align="center" prop="templateName" width="120"/>
      <el-table-column label="模板字段" align="center" prop="templateFields" width="120"/>
      <el-table-column label="默认备注" align="center" prop="defaultRemark" width="120"/>
      <el-table-column label="状态" align="center" prop="status" width="120"/>
      <el-table-column label="操作" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:entrust:template:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:entrust:template:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="模板编码" prop="templateCode"><el-input v-model="form.templateCode" placeholder="请输入模板编码"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="模板名称" prop="templateName"><el-input v-model="form.templateName" placeholder="请输入模板名称"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="模板字段" prop="templateFields"><el-input v-model="form.templateFields" placeholder="请输入模板字段"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="默认备注" prop="defaultRemark"><el-input v-model="form.defaultRemark" placeholder="请输入默认备注"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="状态" prop="status"><el-input v-model="form.status" placeholder="请输入状态"/></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listEntrustTemplate, getEntrustTemplate, addEntrustTemplate, updateEntrustTemplate, delEntrustTemplate } from '@/api/lims/entrust'

export default {
  name: 'LimsEntrustTemplate',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, templateCode: undefined, templateName: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listEntrustTemplate(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.templateId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = '新增' },
    handleUpdate(row) { this.reset(); const id = row.templateId || this.ids[0]; getEntrustTemplate(id).then(response => { this.form = response.data; this.open = true; this.title = '修改' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.templateId != null) { updateEntrustTemplate(this.form).then(() => { this.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addEntrustTemplate(this.form).then(() => { this.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    handleDelete(row) { const ids = row.templateId || this.ids; this.$confirm('确认删除选中的数据项?', '警告', { type: 'warning' }).then(() => delEntrustTemplate(ids).then(() => { this.getList(); this.msgSuccess('删除成功') })).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
