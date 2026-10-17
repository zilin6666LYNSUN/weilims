<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="变更单号" prop="changeId"><el-input v-model="queryParams.changeId" placeholder="变更ID" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="影响类型" prop="impactType"><el-select v-model="queryParams.impactType" placeholder="全部" clearable><el-option label="产品" value="PRODUCT"/><el-option label="批次" value="BATCH"/><el-option label="客户" value="CUSTOMER"/><el-option label="物料" value="MATERIAL"/></el-select></el-form-item>
      <el-form-item label="评估状态" prop="status"><el-select v-model="queryParams.status" placeholder="全部" clearable><el-option label="待评估" value="PENDING"/><el-option label="评估中" value="ASSESSING"/><el-option label="已完成" value="COMPLETED"/></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:change:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:change:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:change:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="变更ID" align="center" prop="changeId" width="90"/>
      <el-table-column label="影响类型" align="center" prop="impactType" width="100"/>
      <el-table-column label="受影响对象" align="center" prop="impactTarget"/>
      <el-table-column label="影响描述" align="center" prop="impactDesc" show-overflow-tooltip/>
      <el-table-column label="风险等级" align="center" prop="riskLevel" width="90"/>
      <el-table-column label="评估状态" align="center" prop="status" width="100">
        <template slot-scope="scope"><el-tag :type="scope.row.status === 'COMPLETED' ? 'success' : (scope.row.status === 'PENDING' ? 'warning' : 'primary')">{{ scope.row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:change:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:change:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12"><el-form-item label="变更ID" prop="changeId"><el-input-number v-model="form.changeId" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="影响类型" prop="impactType"><el-select v-model="form.impactType"><el-option label="产品" value="PRODUCT"/><el-option label="批次" value="BATCH"/><el-option label="客户" value="CUSTOMER"/><el-option label="物料" value="MATERIAL"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item label="受影响对象" prop="impactTarget"><el-input v-model="form.impactTarget" placeholder="批次号/产品名称/客户名称等"/></el-form-item></el-col></el-row>
        <el-row><el-col :span="24"><el-form-item label="影响描述" prop="impactDesc"><el-input v-model="form.impactDesc" type="textarea" :rows="3"/></el-form-item></el-col></el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="风险等级" prop="riskLevel"><el-select v-model="form.riskLevel"><el-option label="高" value="HIGH"/><el-option label="中" value="MEDIUM"/><el-option label="低" value="LOW"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="评估状态" prop="status"><el-select v-model="form.status"><el-option label="待评估" value="PENDING"/><el-option label="评估中" value="ASSESSING"/><el-option label="已完成" value="COMPLETED"/></el-select></el-form-item></el-col>
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
import { listChangeImpact, getChangeImpact, addChangeImpact, updateChangeImpact, delChangeImpact } from '@/api/lims/change'

export default {
  name: 'ChangeImpact',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, changeId: undefined, impactType: undefined, status: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { changeId: [{ required: true, message: '变更ID不能为空', trigger: 'blur' }], impactType: [{ required: true, message: '影响类型不能为空', trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listChangeImpact(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.impactId); this.single = selection.length !== 1; this.multiple = !selection.length },
    resetForm(ref) { if (this.$refs[ref]) this.$refs[ref].resetFields() },
    handleAdd() { this.reset(); this.open = true; this.title = '新增变更影响评估' },
    handleUpdate(row) { this.reset(); const id = row.impactId || this.ids[0]; getChangeImpact(id).then(response => { this.form = response.data; this.open = true; this.title = '修改变更影响评估' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.impactId) { updateChangeImpact(this.form).then(() => { this.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addChangeImpact(this.form).then(() => { this.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleDelete(row) { const ids = row.impactId || this.ids; this.$modal.confirm('是否确认删除影响评估编号为"' + ids + '"的数据项？').then(() => delChangeImpact(ids).then(() => { this.getList(); this.msgSuccess('删除成功') })).catch(() => {}) }
  }
}
</script>
