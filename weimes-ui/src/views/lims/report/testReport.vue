<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.报告编号')" prop="reportCode"><el-input v-model="queryParams.reportCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.报告类型')" prop="reportType">
        <el-select v-model="queryParams.reportType" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.CMA报告')" value="CMA"/><el-option :label="$t('lims.CNAS报告')" value="CNAS"/><el-option :label="$t('lims.内部报告')" value="INTERNAL"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.草稿')" value="DRAFT"/><el-option :label="$t('lims.编制中')" value="PREPARING"/><el-option :label="$t('lims.复核中')" value="REVIEWING"/><el-option :label="$t('lims.批准中')" value="APPROVING"/><el-option :label="$t('lims.已放行')" value="RELEASED"/><el-option :label="$t('lims.已作废')" value="VOIDED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleCreateFromOrder" v-hasPermi="['lims:report:test:add']">{{ this.$t('lims.从检验单创建') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.报告编号')" align="center" prop="reportCode" width="160"/>
      <el-table-column :label="$t('lims.报告类型')" align="center" prop="reportType" width="100">
        <template slot-scope="scope">
          <el-tag :type="{CMA:'danger',CNAS:'warning',INTERNAL:'info'}[scope.row.reportType]">{{ {CMA:'CMA',CNAS:'CNAS',INTERNAL: $t('lims.内部')}[scope.row.reportType] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.报告标题')" align="center" prop="title" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('lims.产品名称')" align="center" prop="productName" width="140"/>
      <el-table-column :label="$t('lims.总结论')" align="center" prop="conclusion" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.conclusion" :type="scope.row.conclusion==='QUALIFIED'?'success':'danger'">{{ scope.row.conclusion==='QUALIFIED'?$t('lims.合格'):$t('lims.不合格') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.编制人')" align="center" prop="generatedBy" width="80"/>
      <el-table-column :label="$t('lims.创建时间')" align="center" prop="createTime" width="160"/>
      <el-table-column :label="$t('common.operation')" align="center" width="320">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleGenerate(scope.row)" v-if="scope.row.status==='DRAFT'" v-hasPermi="['lims:report:test:generate']">{{ $t('lims.生成') }}</el-button>
          <el-button size="mini" type="text" @click="handleReview(scope.row)" v-if="scope.row.status==='PREPARING'" v-hasPermi="['lims:report:test:review']">{{ $t('lims.复核') }}</el-button>
          <el-button size="mini" type="text" @click="handleApprove(scope.row)" v-if="scope.row.status==='REVIEWING'" v-hasPermi="['lims:report:test:approve']">{{ $t('lims.批准') }}</el-button>
          <el-button size="mini" type="text" @click="handleRelease(scope.row)" v-if="scope.row.status==='APPROVING'" v-hasPermi="['lims:report:test:release']">{{ $t('lims.放行') }}</el-button>
          <el-button size="mini" type="text" @click="handlePrint(scope.row)" v-if="scope.row.status==='RELEASED'" v-hasPermi="['lims:report:test:query']">{{ $t('common.print') }}</el-button>
          <el-button size="mini" type="text" @click="handleViewSignatures(scope.row)">{{ $t('lims.签章') }}</el-button>
          <el-button size="mini" type="text" @click="handleVoid(scope.row)" v-if="['DRAFT','PREPARING'].includes(scope.row.status)" v-hasPermi="['lims:report:test:edit']">{{ $t('lims.作废') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-if="scope.row.status==='DRAFT'" v-hasPermi="['lims:report:test:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 从检验单创建报告对话框 -->
    <el-dialog :title="$t('lims.从检验单创建报告')" :visible.sync="createOpen" width="600px" append-to-body>
      <el-form ref="createForm" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item :label="$t('lims.检验单ID')" prop="testOrderId"><el-input v-model="createForm.testOrderId" :placeholder="$t('lims.请输入检验订单ID')"/></el-form-item>
        <el-form-item :label="$t('menu.报告模板')" prop="templateId">
          <el-select v-model="createForm.templateId" :placeholder="$t('lims.请选择报告模板')">
            <el-option :label="$t('lims.CMA检验报告模板')" :value="1"/><el-option :label="$t('lims.CNAS检验报告模板')" :value="2"/><el-option :label="$t('lims.内部检验报告模板')" :value="3"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('lims.报告类型')" prop="reportType">
          <el-select v-model="createForm.reportType" :placeholder="$t('lims.请选择')">
            <el-option :label="$t('lims.CMA报告')" value="CMA"/><el-option :label="$t('lims.CNAS报告')" value="CNAS"/><el-option :label="$t('lims.内部报告')" value="INTERNAL"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitCreate">{{ this.$t('lims.确 定') }}</el-button><el-button @click="createOpen=false">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>

    <!-- 复核对话框 -->
    <el-dialog :title="$t('lims.报告复核')" :visible.sync="reviewOpen" width="500px" append-to-body>
      <el-form ref="reviewForm" :model="reviewForm" label-width="80px">
        <el-form-item :label="$t('lims.复核结果')"><el-radio-group v-model="reviewForm.reviewResult"><el-radio label="PASS">{{ this.$t('lims.通过') }}</el-radio><el-radio label="FAIL">{{ this.$t('lims.退回') }}</el-radio></el-radio-group></el-form-item>
        <el-form-item :label="$t('lims.复核意见')"><el-input v-model="reviewForm.comment" type="textarea" :rows="3"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitReview">{{ this.$t('lims.确 定') }}</el-button><el-button @click="reviewOpen=false">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>

    <!-- 批准对话框 -->
    <el-dialog :title="$t('lims.报告批准')" :visible.sync="approveOpen" width="500px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" label-width="80px">
        <el-form-item :label="$t('lims.批准结果')"><el-radio-group v-model="approveForm.approveResult"><el-radio label="PASS">{{ this.$t('lims.通过') }}</el-radio><el-radio label="FAIL">{{ this.$t('lims.退回') }}</el-radio></el-radio-group></el-form-item>
        <el-form-item :label="$t('lims.批准意见')"><el-input v-model="approveForm.comment" type="textarea" :rows="3"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitApprove">{{ this.$t('lims.确 定') }}</el-button><el-button @click="approveOpen=false">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>

    <!-- 签章记录对话框 -->
    <el-dialog :title="$t('lims.签章记录')" :visible.sync="signatureOpen" width="700px" append-to-body>
      <el-table :data="signatureList" border>
        <el-table-column :label="$t('lims.签章类型')" prop="signatureType" width="100">
          <template slot-scope="scope">{{ {PREPARE: $t('lims.编制'),REVIEW: $t('lims.复核'),APPROVE: $t('lims.批准'),RELEASE: $t('lims.放行')}[scope.row.signatureType] }}</template>
        </el-table-column>
        <el-table-column :label="$t('lims.签章人')" prop="signerName" width="100"/>
        <el-table-column :label="$t('lims.签章含义')" prop="signatureMeaning"/>
        <el-table-column :label="$t('lims.签章时间')" prop="signatureTime" width="160"/>
        <el-table-column :label="$t('lims.状态')" width="80">
          <template slot-scope="scope"><el-tag :type="scope.row.isValid===1?'success':'info'">{{ scope.row.isValid===1?$t('lims.有效'):$t('lims.无效') }}</el-tag></template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listReport, createFromOrder, generateReport, reviewReport, approveReport, releaseReport, voidReport, printReport, getReportSignatures, delReport } from '@/api/lims/report'

export default {
  name: 'TestReport',
  data() {
    return {
      loading: true, showSearch: true, total: 0, dataList: [], ids: [], single: true, multiple: true,
      queryParams: { pageNum: 1, pageSize: 10, reportCode: null, reportType: null, status: null },
      createOpen: false, reviewOpen: false, approveOpen: false, signatureOpen: false,
      createForm: { testOrderId: null, templateId: null, reportType: 'CMA' },
      createRules: { testOrderId: [{ required: true, message: this.$t('lims.请输入检验订单ID'), trigger: 'blur' }], templateId: [{ required: true, message: this.$t('lims.请选择报告模板'), trigger: 'change' }], reportType: [{ required: true, message: this.$t('lims.请选择报告类型'), trigger: 'change' }] },
      reviewForm: { reportId: null, reviewResult: 'PASS', comment: '' },
      approveForm: { reportId: null, approveResult: 'PASS', comment: '' },
      currentReport: null, signatureList: []
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listReport(this.queryParams).then(res => { this.dataList = res.rows; this.total = res.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.reportId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleCreateFromOrder() { this.createForm = { testOrderId: null, templateId: null, reportType: 'CMA' }; this.createOpen = true },
    submitCreate() { this.$refs.createForm.validate(valid => { if (valid) { createFromOrder(this.createForm.testOrderId, this.createForm.templateId, this.createForm.reportType).then(res => { this.$modal.msgSuccess(this.$t('lims.创建成功')); this.createOpen = false; this.getList() }) } }) },
    handleGenerate(row) { this.$modal.confirm(this.$t('lims.确认生成报告数据？')).then(() => generateReport(row.reportId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.生成成功')) }).catch(() => {}) },
    handleReview(row) { this.reviewForm = { reportId: row.reportId, reviewResult: 'PASS', comment: '' }; this.reviewOpen = true },
    submitReview() { reviewReport(this.reviewForm.reportId, this.reviewForm.reviewResult, this.reviewForm.comment).then(() => { this.reviewOpen = false; this.getList(); this.$modal.msgSuccess(this.$t('lims.复核完成')) }) },
    handleApprove(row) { this.approveForm = { reportId: row.reportId, approveResult: 'PASS', comment: '' }; this.approveOpen = true },
    submitApprove() { approveReport(this.approveForm.reportId, this.approveForm.approveResult, this.approveForm.comment).then(() => { this.approveOpen = false; this.getList(); this.$modal.msgSuccess(this.$t('lims.批准完成')) }) },
    handleRelease(row) { this.$modal.confirm(this.$t('lims.确认放行该报告？放行后报告将正式生效。')).then(() => releaseReport(row.reportId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.放行成功')) }).catch(() => {}) },
    handlePrint(row) { this.$modal.confirm(this.$t('lims.确认打印该报告？系统将记录打印操作。')).then(() => printReport(row.reportId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.打印记录已保存')) }).catch(() => {}) },
    handleVoid(row) { this.$prompt(this.$t('lims.请输入作废原因'), this.$t('lims.作废报告'), { confirmButtonText: this.$t('common.confirm'), cancelButtonText: this.$t('common.cancel'), inputPattern: /.+/, inputErrorMessage: this.$t('lims.作废原因不能为空') }).then(({ value }) => voidReport(row.reportId, value)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.已作废')) }).catch(() => {}) },
    handleDelete(row) { this.$modal.confirm(this.$t('lims.确认删除该报告？')).then(() => delReport(row.reportId)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    handleViewSignatures(row) { getReportSignatures(row.reportId).then(res => { this.signatureList = res.data; this.signatureOpen = true }) },
    statusTagType(s) { return { DRAFT:'info', PREPARING:'', REVIEWING:'warning', APPROVING:'warning', RELEASED:'success', VOIDED:'danger' }[s] || 'info' },
    statusLabel(s) { return { DRAFT: this.$t('lims.草稿'), PREPARING: this.$t('lims.待复核'), REVIEWING: this.$t('lims.待批准'), APPROVING: this.$t('lims.待放行'), RELEASED: this.$t('lims.已放行'), VOIDED: this.$t('lims.已作废') }[s] || s }
  }
}
</script>
