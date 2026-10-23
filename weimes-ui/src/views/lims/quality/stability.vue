<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.方案编号')" prop="protocolCode"><el-input v-model="queryParams.protocolCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.产品名称')" prop="productName"><el-input v-model="queryParams.productName" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.考察类型')" prop="studyType">
        <el-select v-model="queryParams.studyType" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.长期')" value="LONG_TERM"/><el-option :label="$t('lims.加速')" value="ACCELERATED"/><el-option :label="$t('lims.中间')" value="INTERMEDIATE"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.草稿')" value="DRAFT"/><el-option :label="$t('lims.进行中')" value="IN_PROGRESS"/><el-option :label="$t('lims.已完成')" value="COMPLETED"/><el-option :label="$t('lims.已关闭')" value="CLOSED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:stability:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:stability:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:stability:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.方案编号')" align="center" prop="protocolCode" width="140"/>
      <el-table-column :label="$t('lims.产品名称')" align="center" prop="productName" width="160"/>
      <el-table-column :label="$t('lims.批号')" align="center" prop="batchNo" width="120"/>
      <el-table-column :label="$t('lims.考察类型')" align="center" prop="studyType" width="100">
        <template slot-scope="scope">{{ { LONG_TERM: this.$t('lims.长期'), ACCELERATED: this.$t('lims.加速'), INTERMEDIATE: this.$t('lims.中间') }[scope.row.studyType] }}</template>
      </el-table-column>
      <el-table-column :label="$t('lims.存储条件')" align="center" prop="storageCondition" width="120"/>
      <el-table-column :label="$t('lims.开始日期')" align="center" prop="startDate" width="110"/>
      <el-table-column :label="$t('lims.结束日期')" align="center" prop="endDate" width="110"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="{ DRAFT: 'info', IN_PROGRESS: '', COMPLETED: 'success', CLOSED: 'success' }[scope.row.status]">{{ { DRAFT: this.$t('lims.草稿'), IN_PROGRESS: this.$t('lims.进行中'), COMPLETED: this.$t('lims.已完成'), CLOSED: this.$t('lims.已关闭') }[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleTestPoints(scope.row)">{{ this.$t('lims.考察点') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:stability:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:stability:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.方案编号')" prop="protocolCode"><el-input v-model="form.protocolCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.考察类型')" prop="studyType"><el-select v-model="form.studyType"><el-option :label="$t('lims.长期')" value="LONG_TERM"/><el-option :label="$t('lims.加速')" value="ACCELERATED"/><el-option :label="$t('lims.中间')" value="INTERMEDIATE"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.产品名称')" prop="productName"><el-input v-model="form.productName"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.批号')" prop="batchNo"><el-input v-model="form.batchNo"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.存储条件')" prop="storageCondition"><el-input v-model="form.storageCondition" :placeholder="$t('lims.如25°C/60%RH')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.考察时间点')" prop="testPoints"><el-input v-model="form.testPoints" :placeholder="$t('lims.如0,3,6,9,12,18,24,36')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.开始日期')" prop="startDate"><el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.结束日期')" prop="endDate"><el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>

    <!-- 考察点对话框 -->
    <el-dialog :title="$t('lims.稳定性考察点')" :visible.sync="testOpen" width="900px" append-to-body>
      <el-table :data="testList" border>
        <el-table-column :label="$t('lims.考察时间点')" prop="testPoint" width="120"/>
        <el-table-column :label="$t('lims.计划日期')" prop="plannedDate" width="120"/>
        <el-table-column :label="$t('lims.实际日期')" prop="actualDate" width="120"/>
        <el-table-column :label="$t('lims.检验结果摘要')" prop="testResultSummary" :show-overflow-tooltip="true"/>
        <el-table-column label="OOT" prop="isOot" width="70">
          <template slot-scope="scope"><el-tag :type="scope.row.isOot ? 'danger' : 'success'">{{ scope.row.isOot ? this.$t('lims.是') : $t('lims.否') }}</el-tag></template>
        </el-table-column>
        <el-table-column :label="$t('lims.状态')" prop="status" width="90">
          <template slot-scope="scope">{{ { PENDING: this.$t('lims.待检'), TESTING: this.$t('lims.检验中'), COMPLETED: this.$t('lims.已完成') }[scope.row.status] }}</template>
        </el-table-column>
        <el-table-column :label="$t('common.operation')" width="100">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleEditTest(scope.row)" v-if="scope.row.status !== 'COMPLETED'">{{ this.$t('lims.录入结果') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog :title="testTitle" :visible.sync="testEditOpen" width="600px" append-to-body>
      <el-form ref="testForm" :model="testForm" :rules="testRules" label-width="110px">
        <el-form-item :label="$t('lims.实际日期')" prop="actualDate"><el-date-picker v-model="testForm.actualDate" type="date" value-format="yyyy-MM-dd"/></el-form-item>
        <el-form-item :label="$t('lims.检验结果摘要')" prop="testResultSummary"><el-input v-model="testForm.testResultSummary" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.是否OOT')" prop="isOot"><el-switch v-model="testForm.isOot"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitTestForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="testEditOpen = false">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listStability, getStability, addStability, updateStability, delStability, listStabilityTests, updateStabilityTest } from '@/api/lims/stability'
export default {
  name: 'LimsStability',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, protocolCode: null, productName: null, studyType: null, status: null },
      form: {}, rules: { protocolCode: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], productName: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], studyType: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'change' }] },
      testOpen: false, testList: [], currentProtocolId: null,
      testEditOpen: false, testTitle: '', testForm: {},
      testRules: { actualDate: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listStability(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.protocolId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { protocolId: null, protocolCode: null, productName: null, batchNo: null, studyType: 'LONG_TERM', storageCondition: null, testPoints: null, startDate: null, endDate: null, status: 'DRAFT', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增稳定性考察') },
    handleUpdate(row) { this.reset(); getStability(row.protocolId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = this.$t('lims.修改稳定性考察') }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.protocolId != null ? updateStability : addStability)(this.form).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.protocolId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delStability(ids)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleTestPoints(row) { this.currentProtocolId = row.protocolId; listStabilityTests(row.protocolId).then(r => { this.testList = r.data; this.testOpen = true }) },
    handleEditTest(row) { this.testForm = { ...row }; this.testTitle = this.$t('lims.录入考察结果'); this.testEditOpen = true },
    submitTestForm() { this.$refs['testForm'].validate(v => { if (v) { updateStabilityTest(this.testForm).then(() => { this.$modal.msgSuccess(this.$t('lims.保存成功')); this.testEditOpen = false; this.handleTestPoints({ protocolId: this.currentProtocolId }) }) } }) }
  }
}
</script>
