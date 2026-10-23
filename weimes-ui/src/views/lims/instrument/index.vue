<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.仪器编号')" prop="instrumentCode"><el-input v-model="queryParams.instrumentCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.仪器名称')" prop="instrumentName"><el-input v-model="queryParams.instrumentName" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.正常')" value="NORMAL"/><el-option :label="$t('lims.维修中')" value="MAINTENANCE"/><el-option :label="$t('lims.停用')" value="DISABLED"/><el-option :label="$t('lims.报废')" value="SCRAPPED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:instrument:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:instrument:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:instrument:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.仪器编号')" align="center" prop="instrumentCode" width="130"/>
      <el-table-column :label="$t('lims.仪器名称')" align="center" prop="instrumentName" width="160"/>
      <el-table-column :label="$t('lims.仪器类型')" align="center" prop="instrumentTypeName" width="120"/>
      <el-table-column :label="$t('lims.型号')" align="center" prop="model" width="120"/>
      <el-table-column :label="$t('lims.序列号')" align="center" prop="serialNumber" width="130"/>
      <el-table-column :label="$t('lims.位置')" align="center" prop="location" width="100"/>
      <el-table-column :label="$t('lims.下次校准')" align="center" prop="nextCalibration" width="110"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag :type="{ NORMAL: 'success', MAINTENANCE: 'warning', DISABLED: 'info', SCRAPPED: 'danger' }[scope.row.status]">{{ { NORMAL: this.$t('lims.正常'), MAINTENANCE: this.$t('lims.维修中'), DISABLED: this.$t('lims.停用'), SCRAPPED: this.$t('lims.报废') }[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:instrument:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:instrument:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.仪器编号')" prop="instrumentCode"><el-input v-model="form.instrumentCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.仪器名称')" prop="instrumentName"><el-input v-model="form.instrumentName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.仪器类型')" prop="instrumentTypeId"><el-input v-model="form.instrumentTypeId" :placeholder="$t('lims.类型ID')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.型号')" prop="model"><el-input v-model="form.model"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.序列号')" prop="serialNumber"><el-input v-model="form.serialNumber"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.制造商')" prop="manufacturer"><el-input v-model="form.manufacturer"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.位置')" prop="location"><el-input v-model="form.location"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-select v-model="form.status"><el-option :label="$t('lims.正常')" value="NORMAL"/><el-option :label="$t('lims.维修中')" value="MAINTENANCE"/><el-option :label="$t('lims.停用')" value="DISABLED"/><el-option :label="$t('lims.报废')" value="SCRAPPED"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.购入日期')" prop="purchaseDate"><el-date-picker v-model="form.purchaseDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.下次校准')" prop="nextCalibration"><el-date-picker v-model="form.nextCalibration" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listInstrument, getInstrument, addInstrument, updateInstrument, delInstrument } from '@/api/lims/instrument'
export default {
  name: 'LimsInstrument',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, instrumentCode: null, instrumentName: null, status: null },
      form: {}, rules: { instrumentCode: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], instrumentName: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listInstrument(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.instrumentId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { instrumentId: null, instrumentCode: null, instrumentName: null, instrumentTypeId: null, model: null, serialNumber: null, manufacturer: null, location: null, status: 'NORMAL', purchaseDate: null, nextCalibration: null, remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增仪器') },
    handleUpdate(row) { this.reset(); getInstrument(row.instrumentId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = this.$t('lims.修改仪器') }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.instrumentId != null ? updateInstrument : addInstrument)(this.form).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.instrumentId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delInstrument(ids)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
