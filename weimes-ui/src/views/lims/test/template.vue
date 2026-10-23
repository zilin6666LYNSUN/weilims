<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.模板编号')" prop="templateCode"><el-input v-model="queryParams.templateCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.模板名称')" prop="templateName"><el-input v-model="queryParams.templateName" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:test:template:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:test:template:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:test:template:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.模板编号')" align="center" prop="templateCode" width="140"/>
      <el-table-column :label="$t('lims.模板名称')" align="center" prop="templateName" width="180"/>
      <el-table-column :label="$t('lims.样品类型')" align="center" prop="sampleType" width="120"/>
      <el-table-column :label="$t('lims.检测项数')" align="center" prop="itemCount" width="90"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100">
        <template slot-scope="scope"><el-tag :type="scope.row.status === 'ENABLED' ? 'success' : 'info'">{{ scope.row.status === 'ENABLED' ? this.$t('lims.启用') : $t('lims.停用') }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('lims.创建时间')" align="center" prop="createTime" width="160"/>
      <el-table-column :label="$t('common.operation')" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleViewItems(scope.row)">{{ this.$t('lims.检测项') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:test:template:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:test:template:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.模板编号')" prop="templateCode"><el-input v-model="form.templateCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.模板名称')" prop="templateName"><el-input v-model="form.templateName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.样品类型')" prop="sampleType"><el-input v-model="form.sampleType" :placeholder="$t('lims.如原料/成品')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-select v-model="form.status"><el-option :label="$t('lims.启用')" value="ENABLED"/><el-option :label="$t('lims.停用')" value="DISABLED"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>

    <!-- 模板检测项 -->
    <el-dialog :title="$t('lims.模板检测项')" :visible.sync="itemOpen" width="900px" append-to-body>
      <el-row :gutter="10" class="mb8"><el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAddItem">{{ this.$t('common.add') }}</el-button></el-col></el-row>
      <el-table :data="itemList" border>
        <el-table-column :label="$t('lims.检测项名称')" prop="testItemName" width="160"/>
        <el-table-column :label="$t('lims.检测方法')" prop="testMethod" width="120"/>
        <el-table-column :label="$t('lims.标准值')" prop="specValue" width="120"/>
        <el-table-column :label="$t('lims.单位')" prop="unit" width="80"/>
        <el-table-column :label="$t('lims.序号')" prop="sortOrder" width="80"/>
        <el-table-column :label="$t('common.operation')" width="120">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleEditItem(scope.row)">{{ this.$t('common.edit') }}</el-button>
            <el-button size="mini" type="text" @click="handleDelItem(scope.row)">{{ this.$t('common.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog :title="itemTitle" :visible.sync="itemEditOpen" width="600px" append-to-body>
      <el-form ref="itemForm" :model="itemForm" :rules="itemRules" label-width="100px">
        <el-form-item :label="$t('lims.检测项名称')" prop="testItemName"><el-input v-model="itemForm.testItemName"/></el-form-item>
        <el-form-item :label="$t('lims.检测方法')" prop="testMethod"><el-input v-model="itemForm.testMethod"/></el-form-item>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.标准值')" prop="specValue"><el-input v-model="itemForm.specValue"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.单位')" prop="unit"><el-input v-model="itemForm.unit"/></el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.序号')" prop="sortOrder"><el-input-number v-model="itemForm.sortOrder" :min="1"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitItemForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="itemEditOpen = false">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listTemplate, getTemplate, addTemplate, updateTemplate, delTemplate, listTemplateItems, addTemplateItem, updateTemplateItem, delTemplateItem } from '@/api/lims/testConfig'
export default {
  name: 'LimsTestTemplate',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, templateCode: null, templateName: null },
      form: {}, rules: { templateCode: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }], templateName: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }] },
      itemOpen: false, itemList: [], currentTemplateId: null, itemEditOpen: false, itemTitle: '', itemForm: {},
      itemRules: { testItemName: [{ required: true, message: this.$t('lims.不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listTemplate(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.templateId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { templateId: null, templateCode: null, templateName: null, sampleType: null, status: 'ENABLED', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增检验模板') },
    handleUpdate(row) { this.reset(); getTemplate(row.templateId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = this.$t('lims.修改检验模板') }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.templateId != null ? updateTemplate : addTemplate)(this.form).then(() => { this.$modal.msgSuccess(this.$t('common.success')); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.templateId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delTemplate(ids)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleViewItems(row) { this.currentTemplateId = row.templateId; listTemplateItems(row.templateId).then(r => { this.itemList = r.data; this.itemOpen = true }) },
    handleAddItem() { this.itemForm = { templateId: this.currentTemplateId, testItemName: null, testMethod: null, specValue: null, unit: null, sortOrder: 1 }; this.itemTitle = this.$t('lims.新增检测项'); this.itemEditOpen = true },
    handleEditItem(row) { this.itemForm = { ...row }; this.itemTitle = this.$t('lims.编辑检测项'); this.itemEditOpen = true },
    handleDelItem(row) { this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => delTemplateItem(row.templateItemId)).then(() => { this.handleViewItems({ templateId: this.currentTemplateId }); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    submitItemForm() { this.$refs['itemForm'].validate(v => { if (v) { (this.itemForm.templateItemId != null ? updateTemplateItem : addTemplateItem)(this.itemForm).then(() => { this.itemEditOpen = false; this.handleViewItems({ templateId: this.currentTemplateId }); this.$modal.msgSuccess(this.$t('common.success')) }) } }) }
  }
}
</script>
