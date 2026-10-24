<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.培训主题')" prop="trainingTopic"><el-input v-model="queryParams.trainingTopic" :placeholder="$t('lims.请输入培训主题')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.培训类型')" prop="trainingType"><el-input v-model="queryParams.trainingType" :placeholder="$t('lims.请输入培训类型')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:training:record:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:training:record:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:training:record:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.培训主题')" align="center" prop="trainingTopic" width="180"/>
      <el-table-column :label="$t('lims.培训类型')" align="center" prop="trainingType" width="110"/>
      <el-table-column :label="$t('lims.培训方式')" align="center" prop="trainingMethod" width="110"/>
      <el-table-column :label="$t('lims.讲师')" align="center" prop="trainerName" width="100"/>
      <el-table-column :label="$t('lims.培训日期')" align="center" prop="trainingDate" width="110"/>
      <el-table-column :label="$t('lims.学时')" align="center" prop="trainingHours" width="80"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="90"/>
      <el-table-column :label="$t('common.operation')" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:training:record:edit']">{{ $t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-user" @click="handleParticipants(scope.row)" v-hasPermi="['lims:training:record:list']">{{ $t('menu.参训人员') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:training:record:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.培训主题')" prop="trainingTopic"><el-input v-model="form.trainingTopic" :placeholder="$t('lims.请输入培训主题')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.培训类型')" prop="trainingType"><el-input v-model="form.trainingType" :placeholder="$t('lims.请输入培训类型')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.培训方式')" prop="trainingMethod"><el-input v-model="form.trainingMethod" :placeholder="$t('lims.如 课堂/实操/线上')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.计划ID')" prop="planId"><el-input-number v-model="form.planId" :min="0" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.讲师')" prop="trainerName"><el-input v-model="form.trainerName" :placeholder="$t('lims.请输入讲师')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.培训日期')" prop="trainingDate"><el-date-picker v-model="form.trainingDate" type="date" value-format="yyyy-MM-dd" :placeholder="$t('lims.请选择培训日期')" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.学时')" prop="trainingHours"><el-input-number v-model="form.trainingHours" :min="0" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-input v-model="form.status" :placeholder="$t('lims.请选择状态')"/></el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.培训内容')" prop="trainingContent"><el-input v-model="form.trainingContent" type="textarea" :rows="3" :placeholder="$t('lims.请输入培训内容')"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="$t('lims.参训人员 - {v}', { v: participantTrainingTopic })" :visible.sync="participantOpen" width="800px" append-to-body>
      <el-table :data="participantList" size="mini">
        <el-table-column :label="$t('lims.人员ID')" align="center" prop="staffId" width="90"/>
        <el-table-column :label="$t('lims.姓名')" align="center" prop="staffName" width="100"/>
        <el-table-column :label="$t('lims.出勤')" align="center" prop="attendanceStatus" width="90"/>
        <el-table-column :label="$t('lims.考试成绩')" align="center" prop="examScore" width="90"/>
        <el-table-column :label="$t('lims.理论合格')" align="center" prop="examPassed" width="90"/>
        <el-table-column :label="$t('lims.实操合格')" align="center" prop="practicalPassed" width="90"/>
        <el-table-column :label="$t('lims.证书号')" align="center" prop="certificateNo" width="130"/>
        <el-table-column :label="$t('lims.证书到期')" align="center" prop="certificateExpire" width="110"/>
        <el-table-column :label="$t('common.operation')" align="center" width="80">
          <template slot-scope="scope"><el-button size="mini" type="text" icon="el-icon-delete" @click="removeParticipant(scope.row)">{{ $t('common.delete') }}</el-button></template>
        </el-table-column>
      </el-table>
      <el-form size="mini" label-width="80px" style="margin-top:10px">
        <el-form-item :label="$t('lims.新增参训')">
          <el-input v-model="participantTemp.staffName" :placeholder="$t('lims.姓名')" style="width:150px"/>
          <el-input v-model="participantTemp.staffId" :placeholder="$t('lims.人员ID')" style="width:120px"/>
          <el-button type="primary" @click="addParticipant">{{ this.$t('lims.添加') }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { listTrainingRecord, getTrainingRecord, addTrainingRecord, updateTrainingRecord, delTrainingRecord, listParticipantByTraining, addTrainingParticipant, delTrainingParticipant } from '@/api/lims/training'

export default {
  name: 'LimsTrainingRecord',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, trainingTopic: undefined, trainingType: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { trainingTopic: [{ required: true, message: this.$t('lims.培训主题不能为空'), trigger: 'blur' }] },
      participantOpen: false, participantList: [], participantTrainingTopic: '', currentTrainingId: null, participantTemp: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listTrainingRecord(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.trainingId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增培训记录') },
    handleUpdate(row) { this.reset(); const id = row.trainingId || this.ids[0]; getTrainingRecord(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改培训记录') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.trainingId != null) { updateTrainingRecord(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addTrainingRecord(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    handleDelete(row) { const ids = row.trainingId || this.ids; this.$confirm(this.$t('lims.确认删除选中的数据项?'), this.$t('lims.警告'), { type: 'warning' }).then(() => delTrainingRecord(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) },
    handleParticipants(row) { this.currentTrainingId = row.trainingId; this.participantTrainingTopic = row.trainingTopic; this.participantList = []; this.participantOpen = true; listParticipantByTraining(row.trainingId).then(response => { this.participantList = response.data || response || [] }) },
    addParticipant() { if (this.currentTrainingId == null) return; addTrainingParticipant({ trainingId: this.currentTrainingId, staffId: this.participantTemp.staffId, staffName: this.participantTemp.staffName }).then(() => { this.participantTemp = {}; this.handleParticipants({ trainingId: this.currentTrainingId, trainingTopic: this.participantTrainingTopic }) }) },
    removeParticipant(row) { this.$confirm(this.$t('lims.确认移除该参训人员?'), this.$t('lims.警告'), { type: 'warning' }).then(() => delTrainingParticipant(row.participantId).then(() => { this.handleParticipants({ trainingId: this.currentTrainingId, trainingTopic: this.participantTrainingTopic }) })).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
