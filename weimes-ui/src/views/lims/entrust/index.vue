<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="委托编号" prop="entrustCode"><el-input v-model="queryParams.entrustCode" placeholder="请输入委托编号" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="样品名称" prop="sampleName"><el-input v-model="queryParams.sampleName" placeholder="请输入样品名称" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="受理状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="草稿" value="DRAFT"/><el-option label="已提交" value="SUBMITTED"/><el-option label="已受理" value="ACCEPTED"/><el-option label="已登记" value="REGISTERED"/><el-option label="已拒绝" value="REJECTED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:entrust:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:entrust:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:entrust:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="委托编号" align="center" prop="entrustCode" width="160"/>
      <el-table-column label="客户ID" align="center" prop="customerId" width="80"/>
      <el-table-column label="委托日期" align="center" prop="entrustDate" width="110"/>
      <el-table-column label="样品名称" align="center" prop="sampleName" width="140"/>
      <el-table-column label="规格" align="center" prop="specification" width="100"/>
      <el-table-column label="批号" align="center" prop="batchNo" width="110"/>
      <el-table-column label="数量" align="center" prop="quantity" width="80"/>
      <el-table-column label="优先级" align="center" prop="priority" width="80"/>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope"><el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="280">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-upload" @click="handleSubmit(scope.row)" v-if="scope.row.status === 'DRAFT'" v-hasPermi="['lims:entrust:submit']">提交受理</el-button>
          <el-button size="mini" type="text" icon="el-icon-check" @click="handleReview(scope.row)" v-if="scope.row.status === 'SUBMITTED'" v-hasPermi="['lims:entrust:review']">受理评审</el-button>
          <el-button size="mini" type="text" icon="el-icon-box" @click="handleRegister(scope.row)" v-if="scope.row.status === 'ACCEPTED'" v-hasPermi="['lims:entrust:register']">登记样品</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:entrust:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:entrust:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="委托编号" prop="entrustCode"><el-input v-model="form.entrustCode" placeholder="请输入委托编号" :disabled="form.entrustId != null"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="客户ID" prop="customerId"><el-input-number v-model="form.customerId" :min="1" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="委托日期" prop="entrustDate"><el-date-picker v-model="form.entrustDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择委托日期" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="模板ID" prop="templateId"><el-input-number v-model="form.templateId" :min="0" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="样品名称" prop="sampleName"><el-input v-model="form.sampleName" placeholder="请输入样品名称"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="规格" prop="specification"><el-input v-model="form.specification" placeholder="请输入规格"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="批号" prop="batchNo"><el-input v-model="form.batchNo" placeholder="请输入批号"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="数量" prop="quantity"><el-input-number v-model="form.quantity" :min="0" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="计量单位" prop="unitOfMeasure"><el-input v-model="form.unitOfMeasure" placeholder="请输入计量单位"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="交付日期" prop="deliveryDate"><el-date-picker v-model="form.deliveryDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择交付日期" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="优先级" prop="priority"><el-select v-model="form.priority" placeholder="请选择优先级"><el-option label="常规" value="NORMAL"/><el-option label="加急" value="URGENT"/><el-option label="特急" value="EMERGENCY"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="报告格式" prop="reportFormat"><el-input v-model="form.reportFormat" placeholder="请输入报告格式"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="需要报告" prop="reportRequired"><el-switch v-model="form.reportRequired" :active-value="1" :inactive-value="0"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态" prop="status"><el-input v-model="form.status" placeholder="请选择状态"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="受理评审" :visible.sync="reviewOpen" width="560px" append-to-body>
      <el-form ref="reviewForm" :model="reviewForm" label-width="100px">
        <el-form-item label="评审结果"><el-radio-group v-model="reviewForm.status"><el-radio label="ACCEPTED">受理通过</el-radio><el-radio label="REJECTED">拒绝受理</el-radio></el-radio-group></el-form-item>
        <el-form-item label="评审意见" prop="reviewComment"><el-input v-model="reviewForm.reviewComment" type="textarea" :rows="3" placeholder="请输入评审意见"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReview">确 定</el-button>
        <el-button @click="reviewOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listEntrust, getEntrust, addEntrust, updateEntrust, delEntrust, submitEntrust, reviewEntrust, registerEntrust } from '@/api/lims/entrust'

export default {
  name: 'LimsEntrust',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, entrustCode: undefined, sampleName: undefined, status: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { entrustCode: [{ required: true, message: '委托编号不能为空', trigger: 'blur' }], sampleName: [{ required: true, message: '样品名称不能为空', trigger: 'blur' }] },
      reviewOpen: false, reviewForm: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listEntrust(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.entrustId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = '新增委托单' },
    handleUpdate(row) { this.reset(); const id = row.entrustId || this.ids[0]; getEntrust(id).then(response => { this.form = response.data; this.open = true; this.title = '修改委托单' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.entrustId != null) { updateEntrust(this.form).then(() => { this.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addEntrust(this.form).then(() => { this.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    handleSubmit(row) { this.$confirm('确认提交受理?提交后不可修改。', '警告', { type: 'warning' }).then(() => submitEntrust(row.entrustId).then(() => { this.msgSuccess('提交成功'); this.getList() })).catch(() => {}) },
    handleReview(row) { this.reviewForm = { entrustId: row.entrustId, status: 'ACCEPTED', reviewComment: '' }; this.reviewOpen = true },
    submitReview() { reviewEntrust(this.reviewForm).then(() => { this.msgSuccess('评审完成'); this.reviewOpen = false; this.getList() }) },
    handleRegister(row) { this.$confirm('确认为该委托单登记样品?', '警告', { type: 'warning' }).then(() => registerEntrust(row.entrustId).then(() => { this.msgSuccess('登记成功'); this.getList() })).catch(() => {}) },
    handleDelete(row) { const ids = row.entrustId || this.ids; this.$confirm('确认删除选中的数据项?', '警告', { type: 'warning' }).then(() => delEntrust(ids).then(() => { this.getList(); this.msgSuccess('删除成功') })).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    statusText(status) { return { DRAFT: '草稿', SUBMITTED: '已提交', ACCEPTED: '已受理', REGISTERED: '已登记', REJECTED: '已拒绝' }[status] || status },
    statusType(status) { return { DRAFT: 'info', SUBMITTED: 'warning', ACCEPTED: 'success', REGISTERED: 'primary', REJECTED: 'danger' }[status] || 'info' }
  }
}
</script>
