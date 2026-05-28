<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="维护类型" prop="maintenanceType">
        <el-select v-model="queryParams.maintenanceType" placeholder="请选择" clearable>
          <el-option label="预防性" value="PREVENTIVE"/><el-option label="纠正性" value="CORRECTIVE"/><el-option label="日常" value="ROUTINE"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:instrument:add']">新增</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="维护编号" align="center" prop="maintenanceId" width="140"/>
      <el-table-column label="维护类型" align="center" prop="maintenanceType" width="100">
        <template slot-scope="scope">{{ { PREVENTIVE: '预防性', CORRECTIVE: '纠正性', ROUTINE: '日常' }[scope.row.maintenanceType] }}</template>
      </el-table-column>
      <el-table-column label="维护日期" align="center" prop="maintenanceDate" width="120"/>
      <el-table-column label="维护描述" align="center" prop="description" :show-overflow-tooltip="true"/>
      <el-table-column label="执行人ID" align="center" prop="performedBy" width="100"/>
      <el-table-column label="状态" align="center" prop="status" width="100"/>
      <el-table-column label="操作" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:instrument:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:instrument:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" :visible.sync="open" width="750px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="维护类型" prop="maintenanceType"><el-select v-model="form.maintenanceType"><el-option label="预防性" value="PREVENTIVE"/><el-option label="纠正性" value="CORRECTIVE"/><el-option label="日常" value="ROUTINE"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="维护日期" prop="maintenanceDate"><el-date-picker v-model="form.maintenanceDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="维护描述" prop="description"><el-input v-model="form.description" type="textarea"/></el-form-item>
        <el-form-item label="执行人ID" prop="performedBy"><el-input v-model="form.performedBy"/></el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listMaintenance, addMaintenance, updateMaintenance, delMaintenance } from '@/api/lims/instrumentSub'
export default {
  name: 'LimsInstrumentMaintenance',
  data() {
    return {
      loading: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, maintenanceType: null },
      form: {}, rules: { maintenanceType: [{ required: true, message: '不能为空', trigger: 'change' }], maintenanceDate: [{ required: true, message: '不能为空', trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listMaintenance(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    reset() { this.form = { maintenanceId: null, instrumentId: null, maintenanceType: 'PREVENTIVE', maintenanceDate: null, performedBy: null, description: null, status: 'DRAFT', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = '新增维护记录' },
    handleUpdate(row) { this.reset(); this.form = { ...row }; this.open = true; this.title = '修改维护记录' },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.maintenanceId != null ? updateMaintenance : addMaintenance)(this.form).then(() => { this.$modal.msgSuccess('操作成功'); this.open = false; this.getList() }) } }) },
    handleDelete(row) { this.$modal.confirm('是否确认删除？').then(() => delMaintenance(row.maintenanceId)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
