<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="客户编码" prop="customerCode"><el-input v-model="queryParams.customerCode" placeholder="请输入客户编码" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="客户名称" prop="customerName"><el-input v-model="queryParams.customerName" placeholder="请输入客户名称" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="客户类型" prop="customerType"><el-input v-model="queryParams.customerType" placeholder="请输入客户类型" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:customer:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:customer:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:customer:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="客户编码" align="center" prop="customerCode" width="120"/>
      <el-table-column label="客户名称" align="center" prop="customerName" width="120"/>
      <el-table-column label="客户类型" align="center" prop="customerType" width="120"/>
      <el-table-column label="联系人" align="center" prop="contactPerson" width="120"/>
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120"/>
      <el-table-column label="邮箱" align="center" prop="contactEmail" width="120"/>
      <el-table-column label="地址" align="center" prop="address" width="120"/>
      <el-table-column label="信用等级" align="center" prop="creditLevel" width="120"/>
      <el-table-column label="状态" align="center" prop="status" width="120"/>
      <el-table-column label="备注" align="center" prop="remark" width="120"/>
      <el-table-column label="操作" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:customer:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:customer:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="客户编码" prop="customerCode"><el-input v-model="form.customerCode" placeholder="请输入客户编码"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="客户名称" prop="customerName"><el-input v-model="form.customerName" placeholder="请输入客户名称"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="客户类型" prop="customerType"><el-input v-model="form.customerType" placeholder="请输入客户类型"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系人" prop="contactPerson"><el-input v-model="form.contactPerson" placeholder="请输入联系人"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="联系电话" prop="contactPhone"><el-input v-model="form.contactPhone" placeholder="请输入联系电话"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="邮箱" prop="contactEmail"><el-input v-model="form.contactEmail" placeholder="请输入邮箱"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="地址" prop="address"><el-input v-model="form.address" placeholder="请输入地址"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="信用等级" prop="creditLevel"><el-input v-model="form.creditLevel" placeholder="请输入信用等级"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="状态" prop="status"><el-input v-model="form.status" placeholder="请输入状态"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="备注" prop="remark"><el-input v-model="form.remark" placeholder="请输入备注"/></el-form-item></el-col>
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
import { listCustomer, getCustomer, addCustomer, updateCustomer, delCustomer } from '@/api/lims/customer'

export default {
  name: 'LimsCustomer',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, customerCode: undefined, customerName: undefined, customerType: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listCustomer(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.customerId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = '新增' },
    handleUpdate(row) { this.reset(); const id = row.customerId || this.ids[0]; getCustomer(id).then(response => { this.form = response.data; this.open = true; this.title = '修改' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.customerId != null) { updateCustomer(this.form).then(() => { this.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addCustomer(this.form).then(() => { this.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    handleDelete(row) { const ids = row.customerId || this.ids; this.$confirm('确认删除选中的数据项?', '警告', { type: 'warning' }).then(() => delCustomer(ids).then(() => { this.getList(); this.msgSuccess('删除成功') })).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
