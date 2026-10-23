<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.批次号')" prop="lotNumber"><el-input v-model="queryParams.lotNumber" :placeholder="$t('lims.请输入批次号')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status"><el-select v-model="queryParams.status" :placeholder="$t('lims.全部')" clearable><el-option :label="$t('lims.有效')" value="VALID"/><el-option :label="$t('lims.已过期')" value="EXPIRED"/><el-option :label="$t('lims.已用尽')" value="DEPLETED"/></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:reagent:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:reagent:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:reagent:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.批次号')" align="center" prop="lotNumber" width="140"/>
      <el-table-column :label="$t('lims.试剂ID')" align="center" prop="reagentId" width="80"/>
      <el-table-column :label="$t('lims.入库日期')" align="center" prop="receivedDate" width="110"/>
      <el-table-column :label="$t('lims.有效期至')" align="center" prop="expireDate" width="110"/>
      <el-table-column :label="$t('lims.入库数量')" align="center" prop="quantity" width="90"/>
      <el-table-column :label="$t('lims.剩余数量')" align="center" prop="remaining" width="90"/>
      <el-table-column :label="$t('lims.COA文件')" align="center" prop="coaFile" width="120"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="80">
        <template slot-scope="scope"><el-tag :type="scope.row.status === 'VALID' ? 'success' : (scope.row.status === 'EXPIRED' ? 'danger' : 'info')">{{ scope.row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:reagent:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:reagent:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.试剂ID')" prop="reagentId"><el-input-number v-model="form.reagentId" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.批次号')" prop="lotNumber"><el-input v-model="form.lotNumber" :placeholder="$t('lims.请输入批次号')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.入库日期')" prop="receivedDate"><el-date-picker v-model="form.receivedDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.有效期至')" prop="expireDate"><el-date-picker v-model="form.expireDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.入库数量')" prop="quantity"><el-input-number v-model="form.quantity" :min="0" :precision="2" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.剩余数量')" prop="remaining"><el-input-number v-model="form.remaining" :min="0" :precision="2" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.COA文件')" prop="coaFile"><el-input v-model="form.coaFile" :placeholder="$t('lims.COA文件路径')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-select v-model="form.status"><el-option :label="$t('lims.有效')" value="VALID"/><el-option :label="$t('lims.已过期')" value="EXPIRED"/><el-option :label="$t('lims.已用尽')" value="DEPLETED"/></el-select></el-form-item></el-col>
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
import { listReagentLot, getReagentLot, addReagentLot, updateReagentLot, delReagentLot } from '@/api/lims/reagent'

export default {
  name: 'ReagentLot',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, lotNumber: undefined, status: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { reagentId: [{ required: true, message: this.$t('lims.试剂ID不能为空'), trigger: 'blur' }], lotNumber: [{ required: true, message: this.$t('lims.批次号不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listReagentLot(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.lotId); this.single = selection.length !== 1; this.multiple = !selection.length },
    resetForm(ref) { if (this.$refs[ref]) this.$refs[ref].resetFields() },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增试剂批次') },
    handleUpdate(row) { this.reset(); const id = row.lotId || this.ids[0]; getReagentLot(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改试剂批次') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.lotId) { updateReagentLot(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addReagentLot(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleDelete(row) { const ids = row.lotId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除试剂批次编号为{v}的数据项？', { v: ids })).then(() => delReagentLot(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) }
  }
}
</script>
