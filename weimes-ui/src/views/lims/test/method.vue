<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="方法编号" prop="methodCode">
        <el-input v-model="queryParams.methodCode" placeholder="请输入方法编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="方法名称" prop="methodName">
        <el-input v-model="queryParams.methodName" placeholder="请输入方法名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:test:method:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:test:method:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:test:method:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="方法编号" align="center" prop="methodCode" width="140"/>
      <el-table-column label="方法名称" align="center" prop="methodName" width="180"/>
      <el-table-column label="方法类型" align="center" prop="methodType" width="120"/>
      <el-table-column label="依据标准" align="center" prop="referenceStandard" width="160"/>
      <el-table-column label="版本" align="center" prop="versionNo" width="80"/>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 'ENABLED' ? 'success' : 'info'">{{ scope.row.status === 'ENABLED' ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:test:method:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:test:method:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="方法编号" prop="methodCode"><el-input v-model="form.methodCode" placeholder="请输入方法编号"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="方法名称" prop="methodName"><el-input v-model="form.methodName" placeholder="请输入方法名称"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="方法类型" prop="methodType"><el-input v-model="form.methodType" placeholder="如理化/微生物"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="依据标准" prop="referenceStandard"><el-input v-model="form.referenceStandard" placeholder="如ChP/USP"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="版本" prop="versionNo"><el-input v-model="form.versionNo" placeholder="请输入版本号"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择"><el-option label="启用" value="ENABLED"/><el-option label="停用" value="DISABLED"/></el-select>
          </el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" placeholder="请输入备注"/></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listMethod, getMethod, addMethod, updateMethod, delMethod } from '@/api/lims/testConfig'
export default {
  name: 'LimsTestMethod',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, methodCode: null, methodName: null },
      form: {}, rules: { methodCode: [{ required: true, message: '方法编号不能为空', trigger: 'blur' }], methodName: [{ required: true, message: '方法名称不能为空', trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listMethod(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.methodId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { methodId: null, methodCode: null, methodName: null, methodType: null, referenceStandard: null, versionNo: null, status: 'ENABLED', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = '新增检验方法' },
    handleUpdate(row) { this.reset(); getMethod(row.methodId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = '修改检验方法' }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.methodId != null ? updateMethod : addMethod)(this.form).then(() => { this.$modal.msgSuccess(this.form.methodId ? '修改成功' : '新增成功'); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.methodId || this.ids; this.$modal.confirm('是否确认删除？').then(() => delMethod(ids)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
