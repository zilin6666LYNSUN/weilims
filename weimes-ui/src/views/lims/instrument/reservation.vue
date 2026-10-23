<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.待确认')" value="PENDING"/><el-option :label="$t('lims.已确认')" value="CONFIRMED"/><el-option :label="$t('lims.已取消')" value="CANCELLED"/><el-option :label="$t('lims.已完成')" value="COMPLETED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:instrument:add']">{{ this.$t('lims.新增预约') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList">
      <el-table-column :label="$t('lims.预约人ID')" align="center" prop="testerId" width="100"/>
      <el-table-column :label="$t('lims.开始时间')" align="center" prop="startTime" width="160"/>
      <el-table-column :label="$t('lims.结束时间')" align="center" prop="endTime" width="160"/>
      <el-table-column :label="$t('lims.用途')" align="center" prop="purpose" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="{ PENDING: 'warning', CONFIRMED: '', CANCELLED: 'info', COMPLETED: 'success' }[scope.row.status]">{{ { PENDING: this.$t('lims.待确认'), CONFIRMED: this.$t('lims.已确认'), CANCELLED: this.$t('lims.已取消'), COMPLETED: this.$t('lims.已完成') }[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:instrument:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" @click="handleConfirm(scope.row)" v-if="scope.row.status === 'PENDING'" v-hasPermi="['lims:instrument:edit']">{{ this.$t('lims.确认') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:instrument:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('lims.开始时间')" prop="startTime"><el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"/></el-form-item>
        <el-form-item :label="$t('lims.结束时间')" prop="endTime"><el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"/></el-form-item>
        <el-form-item :label="$t('lims.用途')" prop="purpose"><el-input v-model="form.purpose" type="textarea"/></el-form-item>
        <el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listReservation, addReservation, updateReservation, delReservation } from '@/api/lims/instrumentSub'
export default {
  name: 'LimsInstrumentReservation',
  data() {
    return {
      loading: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, status: null },
      form: {}, rules: { startTime: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'change' }], endTime: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listReservation(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    reset() { this.form = { reservationId: null, instrumentId: null, startTime: null, endTime: null, purpose: null, status: 'PENDING', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增预约') },
    handleUpdate(row) { this.reset(); this.form = { ...row }; this.open = true; this.title = this.$t('lims.修改预约') },
    handleConfirm(row) { this.$modal.confirm(this.$t('lims.是否确认该预约？')).then(() => updateReservation({ reservationId: row.reservationId, status: 'CONFIRMED' })).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.确认成功')) }).catch(() => {}) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.reservationId != null ? updateReservation : addReservation)(this.form).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.open = false; this.getList() }) } }) },
    handleDelete(row) { this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delReservation(row.reservationId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
