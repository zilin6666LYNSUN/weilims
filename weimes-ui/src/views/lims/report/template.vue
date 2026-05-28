<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="模板编号" prop="templateCode"><el-input v-model="queryParams.templateCode" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="模板名称" prop="templateName"><el-input v-model="queryParams.templateName" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="模板类型" prop="templateType">
        <el-select v-model="queryParams.templateType" placeholder="请选择" clearable>
          <el-option label="CMA报告" value="CMA"/><el-option label="CNAS报告" value="CNAS"/><el-option label="内部报告" value="INTERNAL"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option label="草稿" value="DRAFT"/><el-option label="已发布" value="PUBLISHED"/><el-option label="已停用" value="DISABLED"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:report:template:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:report:template:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:report:template:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="模板编号" align="center" prop="templateCode" width="140"/>
      <el-table-column label="模板名称" align="center" prop="templateName" width="200"/>
      <el-table-column label="模板类型" align="center" prop="templateType" width="100">
        <template slot-scope="scope"><el-tag :type="{CMA:'danger',CNAS:'warning',INTERNAL:'info'}[scope.row.templateType]">{{ {CMA:'CMA',CNAS:'CNAS',INTERNAL:'内部'}[scope.row.templateType] }}</el-tag></template>
      </el-table-column>
      <el-table-column label="模板分类" align="center" prop="templateCategory" width="120">
        <template slot-scope="scope">{{ {FOOD:'食品',DRUG:'药品',ENVIRONMENT:'环境',WATER:'水质',OTHER:'其他'}[scope.row.templateCategory] || scope.row.templateCategory }}</template>
      </el-table-column>
      <el-table-column label="版本号" align="center" prop="versionNumber" width="80"/>
      <el-table-column label="纸张大小" align="center" prop="paperSize" width="80"/>
      <el-table-column label="方向" align="center" prop="orientation" width="80">
        <template slot-scope="scope">{{ scope.row.orientation==='LANDSCAPE'?'横向':'纵向' }}</template>
      </el-table-column>
      <el-table-column label="默认" align="center" prop="isDefault" width="60">
        <template slot-scope="scope"><el-tag :type="scope.row.isDefault===1?'success':'info'">{{ scope.row.isDefault===1?'是':'否' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope"><el-tag :type="{DRAFT:'info',PUBLISHED:'success',DISABLED:'danger'}[scope.row.status]">{{ {DRAFT:'草稿',PUBLISHED:'已发布',DISABLED:'已停用'}[scope.row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160"/>
      <el-table-column label="操作" align="center" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:report:template:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:report:template:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12"><el-form-item label="模板编号" prop="templateCode"><el-input v-model="form.templateCode" :disabled="form.templateId!=null" placeholder="如TPL-CMA-001"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="模板名称" prop="templateName"><el-input v-model="form.templateName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="模板类型" prop="templateType">
            <el-select v-model="form.templateType"><el-option label="CMA报告" value="CMA"/><el-option label="CNAS报告" value="CNAS"/><el-option label="内部报告" value="INTERNAL"/></el-select>
          </el-form-item></el-col>
          <el-col :span="12"><el-form-item label="模板分类" prop="templateCategory">
            <el-select v-model="form.templateCategory"><el-option label="食品" value="FOOD"/><el-option label="药品" value="DRUG"/><el-option label="环境" value="ENVIRONMENT"/><el-option label="水质" value="WATER"/><el-option label="其他" value="OTHER"/></el-select>
          </el-form-item></el-col>
        </el-row>
        <el-form-item label="描述" prop="description"><el-input v-model="form.description" type="textarea" :rows="2"/></el-form-item>
        <el-row>
          <el-col :span="8"><el-form-item label="版本号" prop="versionNumber"><el-input v-model="form.versionNumber" placeholder="V1.0"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="纸张大小"><el-select v-model="form.paperSize"><el-option label="A4" value="A4"/><el-option label="A3" value="A3"/><el-option label="B5" value="B5"/></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="方向"><el-select v-model="form.orientation"><el-option label="纵向" value="PORTRAIT"/><el-option label="横向" value="LANDSCAPE"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="6"><el-form-item label="上边距(cm)"><el-input-number v-model="form.marginTop" :precision="2" :step="0.5" :min="0"/></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="下边距(cm)"><el-input-number v-model="form.marginBottom" :precision="2" :step="0.5" :min="0"/></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="左边距(cm)"><el-input-number v-model="form.marginLeft" :precision="2" :step="0.5" :min="0"/></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="右边距(cm)"><el-input-number v-model="form.marginRight" :precision="2" :step="0.5" :min="0"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="页眉内容"><el-input v-model="form.headerContent" type="textarea" :rows="2" placeholder="支持HTML，如：&lt;div style='text-align:center'&gt;检验报告&lt;/div&gt;"/></el-form-item>
        <el-form-item label="页脚内容"><el-input v-model="form.footerContent" type="textarea" :rows="2" placeholder="支持HTML，如：&lt;div&gt;第 {page} 页 / 共 {total} 页&lt;/div&gt;"/></el-form-item>
        <el-row>
          <el-col :span="8"><el-form-item label="水印"><el-input v-model="form.watermark" placeholder="如：受控文件"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="默认模板"><el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="状态"><el-select v-model="form.status"><el-option label="草稿" value="DRAFT"/><el-option label="已发布" value="PUBLISHED"/><el-option label="已停用" value="DISABLED"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="生效日期"><el-date-picker v-model="form.effectiveDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="失效日期"><el-date-picker v-model="form.expiryDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listTemplate, getTemplate, addTemplate, updateTemplate, delTemplate } from '@/api/lims/template'

export default {
  name: 'ReportTemplate',
  data() {
    return {
      loading: true, showSearch: true, total: 0, dataList: [], ids: [], single: true, multiple: true,
      queryParams: { pageNum: 1, pageSize: 10, templateCode: null, templateName: null, templateType: null, status: null },
      open: false, title: '',
      form: {},
      rules: {
        templateCode: [{ required: true, message: '请输入模板编号', trigger: 'blur' }],
        templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
        templateType: [{ required: true, message: '请选择模板类型', trigger: 'change' }]
      }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listTemplate(this.queryParams).then(res => { this.dataList = res.rows; this.total = res.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.templateId); this.single = selection.length !== 1; this.multiple = !selection.length },
    reset() { this.form = { templateId: null, templateCode: null, templateName: null, templateType: 'CMA', templateCategory: null, description: null, versionNumber: 'V1.0', paperSize: 'A4', orientation: 'PORTRAIT', marginTop: 2.0, marginBottom: 2.0, marginLeft: 2.5, marginRight: 2.5, headerContent: null, footerContent: null, watermark: null, isDefault: 0, effectiveDate: null, expiryDate: null, status: 'DRAFT', remark: null } },
    handleAdd() { this.reset(); this.open = true; this.title = '添加报告模板' },
    handleUpdate(row) { const id = row.templateId || this.ids[0]; getTemplate(id).then(res => { this.form = res.data; this.open = true; this.title = '修改报告模板' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.templateId != null) { updateTemplate(this.form).then(() => { this.$modal.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addTemplate(this.form).then(() => { this.$modal.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleDelete(row) { const ids = row.templateId || this.ids; this.$modal.confirm('确认删除？').then(() => delTemplate(ids)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) }
  }
}
</script>
