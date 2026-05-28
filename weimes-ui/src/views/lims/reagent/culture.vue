<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="菌种编号" prop="cultureCode"><el-input v-model="queryParams.cultureCode" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="菌种名称" prop="cultureName"><el-input v-model="queryParams.cultureName" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:reagent:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:reagent:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:reagent:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="菌种编号" align="center" prop="cultureCode" width="130"/>
      <el-table-column label="菌种名称" align="center" prop="cultureName" width="160"/>
      <el-table-column label="菌株号" align="center" prop="strainNumber" width="120"/>
      <el-table-column label="来源" align="center" prop="source" width="120"/>
      <el-table-column label="存储方式" align="center" prop="storageMethod" width="100"/>
      <el-table-column label="传代次数" align="center" prop="passageCount" width="90"/>
      <el-table-column label="最大代数" align="center" prop="maxPassage" width="90"/>
      <el-table-column label="有效期至" align="center" prop="expireDate" width="110"/>
      <el-table-column label="操作" align="center" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:reagent:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:reagent:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="菌种编号" prop="cultureCode"><el-input v-model="form.cultureCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="菌种名称" prop="cultureName"><el-input v-model="form.cultureName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="菌株号" prop="strainNumber"><el-input v-model="form.strainNumber" placeholder="如ATCC6538"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="来源" prop="source"><el-input v-model="form.source" placeholder="如ATCC/CMCC"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="存储方式" prop="storageMethod"><el-input v-model="form.storageMethod" placeholder="如冷冻干燥/液氮"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="存储位置" prop="storageLocation"><el-input v-model="form.storageLocation"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="传代次数" prop="passageCount"><el-input-number v-model="form.passageCount" :min="0"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="最大代数" prop="maxPassage"><el-input-number v-model="form.maxPassage" :min="1"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="有效期至" prop="expireDate"><el-date-picker v-model="form.expireDate" type="date" value-format="yyyy-MM-dd"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态" prop="status"><el-select v-model="form.status"><el-option label="启用" value="ENABLED"/><el-option label="停用" value="DISABLED"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listCulture, getCulture, addCulture, updateCulture, delCulture } from '@/api/lims/reagent'
export default {
  name: 'LimsCultureCollection',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, cultureCode: null, cultureName: null },
      form: {}, rules: { cultureCode: [{ required: true, message: '不能为空', trigger: 'blur' }], cultureName: [{ required: true, message: '不能为空', trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listCulture(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.cultureId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { cultureId: null, cultureCode: null, cultureName: null, strainNumber: null, source: null, storageMethod: null, storageLocation: null, passageCount: 0, maxPassage: 5, expireDate: null, status: 'ENABLED', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = '新增菌种' },
    handleUpdate(row) { this.reset(); getCulture(row.cultureId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = '修改菌种' }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.cultureId != null ? updateCulture : addCulture)(this.form).then(() => { this.$modal.msgSuccess('操作成功'); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.cultureId || this.ids; this.$modal.confirm('是否确认删除？').then(() => delCulture(ids)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
