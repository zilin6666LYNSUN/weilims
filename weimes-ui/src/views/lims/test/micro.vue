<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="检验单ID" prop="testOrderId"><el-input v-model="queryParams.testOrderId" placeholder="检验单ID" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="检测类型" prop="testType"><el-select v-model="queryParams.testType" placeholder="全部" clearable><el-option label="限度检查" value="LIMIT"/><el-option label="无菌检查" value="STERILITY"/><el-option label="菌种鉴定" value="IDENTIFICATION"/></el-select></el-form-item>
      <el-form-item label="结果" prop="result"><el-select v-model="queryParams.result" placeholder="全部" clearable><el-option label="合格" value="PASS"/><el-option label="不合格" value="FAIL"/></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:test:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:test:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="warning" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleRecord" v-hasPermi="['lims:test:edit']">录入结果</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:test:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="检验单ID" align="center" prop="testOrderId" width="90"/>
      <el-table-column label="样品ID" align="center" prop="sampleId" width="90"/>
      <el-table-column label="检测类型" align="center" prop="testType" width="100"/>
      <el-table-column label="检验项" align="center" prop="testItem" width="130"/>
      <el-table-column label="方法依据" align="center" prop="methodRef" width="130"/>
      <el-table-column label="检测人ID" align="center" prop="testedBy" width="90"/>
      <el-table-column label="检测日期" align="center" prop="testDate" width="110"/>
      <el-table-column label="结果" align="center" prop="result" width="90">
        <template slot-scope="scope"><el-tag :type="scope.row.result === 'PASS' ? 'success' : 'danger'">{{ scope.row.result }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:test:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit-outline" @click="handleRecord(scope.row)" v-hasPermi="['lims:test:edit']">结果</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:test:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12"><el-form-item label="检验单ID" prop="testOrderId"><el-input-number v-model="form.testOrderId" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="样品ID" prop="sampleId"><el-input-number v-model="form.sampleId" :min="1" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="检测类型" prop="testType"><el-select v-model="form.testType"><el-option label="限度检查" value="LIMIT"/><el-option label="无菌检查" value="STERILITY"/><el-option label="菌种鉴定" value="IDENTIFICATION"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="检验项" prop="testItem"><el-input v-model="form.testItem" placeholder="如微生物限度/无菌"/></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item label="方法依据" prop="methodRef"><el-input v-model="form.methodRef" placeholder="如中国药典通则1105"/></el-form-item></el-col></el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="检测人ID" prop="testedBy"><el-input-number v-model="form.testedBy" :min="1" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="检测日期" prop="testDate"><el-date-picker v-model="form.testDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item label="结果" prop="result"><el-radio-group v-model="form.result"><el-radio label="PASS">合格</el-radio><el-radio label="FAIL">不合格</el-radio></el-radio-group></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="录入检测结果" :visible.sync="openResult" width="500px" append-to-body>
      <el-form ref="resultForm" :model="resultForm" label-width="110px">
        <el-row><el-col :span="24"><el-form-item label="检测类型">{{ resultForm.testType }}</el-form-item></el-col></el-row>
        <el-row><el-col :span="24"><el-form-item label="检验项">{{ resultForm.testItem }}</el-form-item></el-col></el-row>
        <el-row><el-col :span="24"><el-form-item label="结果" prop="result"><el-radio-group v-model="resultForm.result"><el-radio label="PASS">合格</el-radio><el-radio label="FAIL">不合格</el-radio></el-radio-group></el-form-item></el-col></el-row>
        <el-row><el-col :span="24"><el-form-item label="菌落数" prop="colonyCount"><el-input-number v-model="resultForm.colonyCount" :min="0" style="width:100%"/></el-form-item></el-col></el-row>
        <el-row><el-col :span="24"><el-form-item label="结论说明" prop="conclusion"><el-input v-model="resultForm.conclusion" type="textarea" :rows="2"/></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitResult">确 定</el-button>
        <el-button @click="openResult = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMicroTestRecord, getMicroTestRecord, addMicroTestRecord, updateMicroTestRecord, recordMicroTestResult, delMicroTestRecord } from '@/api/lims/test'

export default {
  name: 'MicroTestRecord',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, testOrderId: undefined, testType: undefined, result: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { testOrderId: [{ required: true, message: '检验单ID不能为空', trigger: 'blur' }], testType: [{ required: true, message: '检测类型不能为空', trigger: 'change' }], testItem: [{ required: true, message: '检验项不能为空', trigger: 'blur' }] },
      openResult: false, resultForm: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listMicroTestRecord(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.recordId); this.single = selection.length !== 1; this.multiple = !selection.length },
    resetForm(ref) { if (this.$refs[ref]) this.$refs[ref].resetFields() },
    handleAdd() { this.reset(); this.open = true; this.title = '新增微生物检测记录' },
    handleUpdate(row) { this.reset(); const id = row.recordId || this.ids[0]; getMicroTestRecord(id).then(response => { this.form = response.data; this.open = true; this.title = '修改微生物检测记录' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.recordId) { updateMicroTestRecord(this.form).then(() => { this.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addMicroTestRecord(this.form).then(() => { this.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleRecord(row) { this.resultForm = Object.assign({}, row); this.openResult = true },
    submitResult() { recordMicroTestResult(this.resultForm).then(() => { this.msgSuccess('结果录入成功'); this.openResult = false; this.getList() }) },
    handleDelete(row) { const ids = row.recordId || this.ids; this.$modal.confirm('是否确认删除微生物检测记录编号为"' + ids + '"的数据项？').then(() => delMicroTestRecord(ids).then(() => { this.getList(); this.msgSuccess('删除成功') })).catch(() => {}) }
  }
}
</script>
