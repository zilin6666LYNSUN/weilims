<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="方案编号" prop="protocolCode"><el-input v-model="queryParams.protocolCode" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="产品名称" prop="productName"><el-input v-model="queryParams.productName" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="考察类型" prop="studyType">
        <el-select v-model="queryParams.studyType" placeholder="请选择" clearable>
          <el-option label="长期" value="LONG_TERM"/><el-option label="加速" value="ACCELERATED"/><el-option label="中间" value="INTERMEDIATE"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option label="草稿" value="DRAFT"/><el-option label="进行中" value="IN_PROGRESS"/><el-option label="已完成" value="COMPLETED"/><el-option label="已关闭" value="CLOSED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:stability:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:stability:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:stability:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="方案编号" align="center" prop="protocolCode" width="140"/>
      <el-table-column label="产品名称" align="center" prop="productName" width="160"/>
      <el-table-column label="批号" align="center" prop="batchNo" width="120"/>
      <el-table-column label="考察类型" align="center" prop="studyType" width="100">
        <template slot-scope="scope">{{ { LONG_TERM: '长期', ACCELERATED: '加速', INTERMEDIATE: '中间' }[scope.row.studyType] }}</template>
      </el-table-column>
      <el-table-column label="存储条件" align="center" prop="storageCondition" width="120"/>
      <el-table-column label="开始日期" align="center" prop="startDate" width="110"/>
      <el-table-column label="结束日期" align="center" prop="endDate" width="110"/>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="{ DRAFT: 'info', IN_PROGRESS: '', COMPLETED: 'success', CLOSED: 'success' }[scope.row.status]">{{ { DRAFT: '草稿', IN_PROGRESS: '进行中', COMPLETED: '已完成', CLOSED: '已关闭' }[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleTestPoints(scope.row)">考察点</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:stability:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:stability:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="方案编号" prop="protocolCode"><el-input v-model="form.protocolCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="考察类型" prop="studyType"><el-select v-model="form.studyType"><el-option label="长期" value="LONG_TERM"/><el-option label="加速" value="ACCELERATED"/><el-option label="中间" value="INTERMEDIATE"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="产品名称" prop="productName"><el-input v-model="form.productName"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="批号" prop="batchNo"><el-input v-model="form.batchNo"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="存储条件" prop="storageCondition"><el-input v-model="form.storageCondition" placeholder="如25°C/60%RH"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="考察时间点" prop="testPoints"><el-input v-model="form.testPoints" placeholder="如0,3,6,9,12,18,24,36"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="开始日期" prop="startDate"><el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束日期" prop="endDate"><el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>

    <!-- 考察点对话框 -->
    <el-dialog title="稳定性考察点" :visible.sync="testOpen" width="900px" append-to-body>
      <el-table :data="testList" border>
        <el-table-column label="考察时间点" prop="testPoint" width="120"/>
        <el-table-column label="计划日期" prop="plannedDate" width="120"/>
        <el-table-column label="实际日期" prop="actualDate" width="120"/>
        <el-table-column label="检验结果摘要" prop="testResultSummary" :show-overflow-tooltip="true"/>
        <el-table-column label="OOT" prop="isOot" width="70">
          <template slot-scope="scope"><el-tag :type="scope.row.isOot ? 'danger' : 'success'">{{ scope.row.isOot ? '是' : '否' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="90">
          <template slot-scope="scope">{{ { PENDING: '待检', TESTING: '检验中', COMPLETED: '已完成' }[scope.row.status] }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleEditTest(scope.row)" v-if="scope.row.status !== 'COMPLETED'">录入结果</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog :title="testTitle" :visible.sync="testEditOpen" width="600px" append-to-body>
      <el-form ref="testForm" :model="testForm" :rules="testRules" label-width="110px">
        <el-form-item label="实际日期" prop="actualDate"><el-date-picker v-model="testForm.actualDate" type="date" value-format="yyyy-MM-dd"/></el-form-item>
        <el-form-item label="检验结果摘要" prop="testResultSummary"><el-input v-model="testForm.testResultSummary" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="是否OOT" prop="isOot"><el-switch v-model="testForm.isOot"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitTestForm">确 定</el-button><el-button @click="testEditOpen = false">取 消</el-button></div>
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
      form: {}, rules: { protocolCode: [{ required: true, message: '不能为空', trigger: 'blur' }], productName: [{ required: true, message: '不能为空', trigger: 'blur' }], studyType: [{ required: true, message: '不能为空', trigger: 'change' }] },
      testOpen: false, testList: [], currentProtocolId: null,
      testEditOpen: false, testTitle: '', testForm: {},
      testRules: { actualDate: [{ required: true, message: '不能为空', trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listStability(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.protocolId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { protocolId: null, protocolCode: null, productName: null, batchNo: null, studyType: 'LONG_TERM', storageCondition: null, testPoints: null, startDate: null, endDate: null, status: 'DRAFT', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = '新增稳定性考察' },
    handleUpdate(row) { this.reset(); getStability(row.protocolId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = '修改稳定性考察' }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.protocolId != null ? updateStability : addStability)(this.form).then(() => { this.$modal.msgSuccess('操作成功'); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.protocolId || this.ids; this.$modal.confirm('是否确认删除？').then(() => delStability(ids)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleTestPoints(row) { this.currentProtocolId = row.protocolId; listStabilityTests(row.protocolId).then(r => { this.testList = r.data; this.testOpen = true }) },
    handleEditTest(row) { this.testForm = { ...row }; this.testTitle = '录入考察结果'; this.testEditOpen = true },
    submitTestForm() { this.$refs['testForm'].validate(v => { if (v) { updateStabilityTest(this.testForm).then(() => { this.$modal.msgSuccess('保存成功'); this.testEditOpen = false; this.handleTestPoints({ protocolId: this.currentProtocolId }) }) } }) }
  }
}
</script>
