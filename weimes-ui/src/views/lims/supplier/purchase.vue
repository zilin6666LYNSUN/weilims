<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="采购单号" prop="purchaseNo"><el-input v-model="queryParams.purchaseNo" placeholder="请输入采购单号" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="物料名称" prop="materialName"><el-input v-model="queryParams.materialName" placeholder="请输入物料名称" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:supplier:purchase:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:supplier:purchase:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:supplier:purchase:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="供应商ID" align="center" prop="vendorId" width="120"/>
      <el-table-column label="采购单号" align="center" prop="purchaseNo" width="120"/>
      <el-table-column label="采购日期" align="center" prop="purchaseDate" width="110"/>
      <el-table-column label="物料类型" align="center" prop="materialType" width="120"/>
      <el-table-column label="物料名称" align="center" prop="materialName" width="120"/>
      <el-table-column label="试剂ID" align="center" prop="reagentId" width="120"/>
      <el-table-column label="数量" align="center" prop="quantity" width="110"/>
      <el-table-column label="单位" align="center" prop="unitOfMeasure" width="120"/>
      <el-table-column label="单价" align="center" prop="unitPrice" width="110"/>
      <el-table-column label="总金额" align="center" prop="totalAmount" width="110"/>
      <el-table-column label="状态" align="center" prop="status" width="120"/>
      <el-table-column label="备注" align="center" prop="remark" width="120"/>
      <el-table-column label="操作" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:supplier:purchase:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:supplier:purchase:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="供应商ID" prop="vendorId"><el-input-number v-model="form.vendorId" :min="0" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="采购单号" prop="purchaseNo"><el-input v-model="form.purchaseNo" placeholder="请输入采购单号"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="采购日期" prop="purchaseDate"><el-date-picker v-model="form.purchaseDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="物料类型" prop="materialType"><el-input v-model="form.materialType" placeholder="请输入物料类型"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="物料名称" prop="materialName"><el-input v-model="form.materialName" placeholder="请输入物料名称"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="试剂ID" prop="reagentId"><el-input-number v-model="form.reagentId" :min="0" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="数量" prop="quantity"><el-input-number v-model="form.quantity" :min="0" :precision="2" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="单位" prop="unitOfMeasure"><el-input v-model="form.unitOfMeasure" placeholder="请输入单位"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="单价" prop="unitPrice"><el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="总金额" prop="totalAmount"><el-input-number v-model="form.totalAmount" :min="0" :precision="2" style="width:100%"/></el-form-item></el-col>
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
import { listSupplierPurchase, getSupplierPurchase, addSupplierPurchase, updateSupplierPurchase, delSupplierPurchase } from '@/api/lims/supplier'

export default {
  name: 'LimsSupplierPurchase',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, vendorId: undefined, purchaseNo: undefined, materialName: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listSupplierPurchase(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.purchaseId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = '新增' },
    handleUpdate(row) { this.reset(); const id = row.purchaseId || this.ids[0]; getSupplierPurchase(id).then(response => { this.form = response.data; this.open = true; this.title = '修改' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.purchaseId != null) { updateSupplierPurchase(this.form).then(() => { this.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addSupplierPurchase(this.form).then(() => { this.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    handleDelete(row) { const ids = row.purchaseId || this.ids; this.$confirm('确认删除选中的数据项?', '警告', { type: 'warning' }).then(() => delSupplierPurchase(ids).then(() => { this.getList(); this.msgSuccess('删除成功') })).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
