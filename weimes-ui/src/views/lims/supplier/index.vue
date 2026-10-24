<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.供应商编码')" prop="vendorCode"><el-input v-model="queryParams.vendorCode" :placeholder="$t('lims.请输入供应商编码')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.供应商名称')" prop="vendorName"><el-input v-model="queryParams.vendorName" :placeholder="$t('lims.请输入供应商名称')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.供应商类型')" prop="vendorType"><el-input v-model="queryParams.vendorType" :placeholder="$t('lims.请输入供应商类型')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:supplier:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:supplier:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:supplier:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.供应商编码')" align="center" prop="vendorCode" width="120"/>
      <el-table-column :label="$t('lims.供应商名称')" align="center" prop="vendorName" width="120"/>
      <el-table-column :label="$t('lims.供应商类型')" align="center" prop="vendorType" width="120"/>
      <el-table-column :label="$t('lims.联系人')" align="center" prop="contactPerson" width="120"/>
      <el-table-column :label="$t('lims.联系电话')" align="center" prop="contactPhone" width="120"/>
      <el-table-column :label="$t('lims.邮箱')" align="center" prop="email" width="120"/>
      <el-table-column :label="$t('lims.地址')" align="center" prop="address" width="120"/>
      <el-table-column :label="$t('lims.等级')" align="center" prop="grade" width="120"/>
      <el-table-column :label="$t('lims.合格')" align="center" prop="approved" width="120"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="120"/>
      <el-table-column :label="$t('lims.备注')" align="center" prop="remark" width="120"/>
      <el-table-column :label="$t('common.operation')" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:supplier:edit']">{{ $t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:supplier:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.供应商编码')" prop="vendorCode"><el-input v-model="form.vendorCode" :placeholder="$t('lims.请输入供应商编码')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.供应商名称')" prop="vendorName"><el-input v-model="form.vendorName" :placeholder="$t('lims.请输入供应商名称')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.供应商类型')" prop="vendorType"><el-input v-model="form.vendorType" :placeholder="$t('lims.请输入供应商类型')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.联系人')" prop="contactPerson"><el-input v-model="form.contactPerson" :placeholder="$t('lims.请输入联系人')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.联系电话')" prop="contactPhone"><el-input v-model="form.contactPhone" :placeholder="$t('lims.请输入联系电话')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.邮箱')" prop="email"><el-input v-model="form.email" :placeholder="$t('lims.请输入邮箱')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.地址')" prop="address"><el-input v-model="form.address" :placeholder="$t('lims.请输入地址')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.等级')" prop="grade"><el-input v-model="form.grade" :placeholder="$t('lims.请输入等级')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.合格')" prop="approved"><el-input-number v-model="form.approved" :min="0" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-input v-model="form.status" :placeholder="$t('lims.请输入状态')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" :placeholder="$t('lims.请输入备注')"/></el-form-item></el-col>
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
import { listSupplier, getSupplier, addSupplier, updateSupplier, delSupplier } from '@/api/lims/supplier'

export default {
  name: 'LimsSupplier',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, vendorCode: undefined, vendorName: undefined, vendorType: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listSupplier(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.vendorId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('common.add') },
    handleUpdate(row) { this.reset(); const id = row.vendorId || this.ids[0]; getSupplier(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.vendorId != null) { updateSupplier(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addSupplier(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    handleDelete(row) { const ids = row.vendorId || this.ids; this.$confirm(this.$t('lims.确认删除选中的数据项?'), this.$t('lims.警告'), { type: 'warning' }).then(() => delSupplier(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
