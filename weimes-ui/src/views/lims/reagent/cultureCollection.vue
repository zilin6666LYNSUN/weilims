<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.菌种名称')" prop="cultureName"><el-input v-model="queryParams.cultureName" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status"><el-select v-model="queryParams.status" :placeholder="$t('lims.全部')" clearable><el-option :label="$t('lims.待入库')" value="PENDING"/><el-option :label="$t('lims.在库')" value="ACTIVE"/><el-option :label="$t('lims.已销毁')" value="DISCARDED"/></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:reagent:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:reagent:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:reagent:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.菌种编号')" align="center" prop="cultureCode" width="120"/>
      <el-table-column :label="$t('lims.菌种名称')" align="center" prop="cultureName" width="140"/>
      <el-table-column :label="$t('lims.菌株号')" align="center" prop="strainNumber" width="110"/>
      <el-table-column :label="$t('lims.来源')" align="center" prop="source" width="110"/>
      <el-table-column :label="$t('lims.储存方式')" align="center" prop="storageMethod" width="90"/>
      <el-table-column :label="$t('lims.传代次数')" align="center" prop="passageCount" width="90"/>
      <el-table-column :label="$t('lims.最大传代')" align="center" prop="maxPassage" width="90"/>
      <el-table-column :label="$t('lims.入库日期')" align="center" prop="receivedDate" width="110"/>
      <el-table-column :label="$t('lims.有效期至')" align="center" prop="expireDate" width="110"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="90">
        <template slot-scope="scope"><el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : (scope.row.status === 'DISCARDED' ? 'danger' : 'info')">{{ scope.row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="230">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-download" @click="handleReceive(scope.row)" v-hasPermi="['lims:reagent:edit']">{{ this.$t('lims.入库') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-refresh" @click="handlePassage(scope.row)" v-hasPermi="['lims:reagent:edit']">{{ this.$t('lims.传代') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDiscard(scope.row)" v-hasPermi="['lims:reagent:edit']">{{ this.$t('lims.销毁') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.菌种编号')" prop="cultureCode"><el-input v-model="form.cultureCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.菌种名称')" prop="cultureName"><el-input v-model="form.cultureName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.菌株号')" prop="strainNumber"><el-input v-model="form.strainNumber" :placeholder="$t('lims.如ATCC 25923')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.来源')" prop="source"><el-input v-model="form.source" :placeholder="$t('lims.如CMCC/ATCC')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.储存方式')" prop="storageMethod"><el-input v-model="form.storageMethod" :placeholder="$t('lims.如冻干/深低温')"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.储存位置')" prop="storageLocation"><el-input v-model="form.storageLocation" :placeholder="$t('lims.如冰箱A-1层')"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.传代次数')" prop="passageCount"><el-input-number v-model="form.passageCount" :min="0" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.最大传代')" prop="maxPassage"><el-input-number v-model="form.maxPassage" :min="1" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.有效期至')" prop="expireDate"><el-date-picker v-model="form.expireDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.状态')" prop="status"><el-select v-model="form.status"><el-option :label="$t('lims.待入库')" value="PENDING"/><el-option :label="$t('lims.在库')" value="ACTIVE"/><el-option :label="$t('lims.已销毁')" value="DISCARDED"/></el-select></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCulture, getCulture, addCulture, updateCulture, delCulture, receiveCulture, passageCulture, discardCulture } from '@/api/lims/reagent'

export default {
  name: 'CultureCollection',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, cultureName: undefined, status: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { cultureCode: [{ required: true, message: this.$t('lims.菌种编号不能为空'), trigger: 'blur' }], cultureName: [{ required: true, message: this.$t('lims.菌种名称不能为空'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listCulture(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.cultureId); this.single = selection.length !== 1; this.multiple = !selection.length },
    resetForm(ref) { if (this.$refs[ref]) this.$refs[ref].resetFields() },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.新增菌种') },
    handleUpdate(row) { this.reset(); const id = row.cultureId || this.ids[0]; getCulture(id).then(response => { this.form = response.data; this.open = true; this.title = this.$t('lims.修改菌种') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.cultureId) { updateCulture(this.form).then(() => { this.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addCulture(this.form).then(() => { this.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleReceive(row) { this.$modal.confirm(this.$t('lims.确认接收菌种{v}入库？', { v: row.cultureName })).then(() => receiveCulture(row.cultureId).then(() => { this.getList(); this.msgSuccess(this.$t('lims.入库成功')) })).catch(() => {}) },
    handlePassage(row) { this.$modal.confirm(this.$t('lims.确认对菌种{v}进行传代？', { v: row.cultureName })).then(() => passageCulture(row.cultureId).then(() => { this.getList(); this.msgSuccess(this.$t('lims.传代成功')) })).catch(() => {}) },
    handleDiscard(row) { this.$modal.confirm(this.$t('lims.确认销毁菌种{v}？销毁后不可恢复。', { v: row.cultureName })).then(() => discardCulture(row.cultureId).then(() => { this.getList(); this.msgSuccess(this.$t('lims.销毁成功')) })).catch(() => {}) },
    handleDelete(row) { const ids = row.cultureId || this.ids; this.$modal.confirm(this.$t('lims.是否确认删除菌种编号为{v}的数据项？', { v: ids })).then(() => delCulture(ids).then(() => { this.getList(); this.msgSuccess(this.$t('lims.删除成功')) })).catch(() => {}) }
  }
}
</script>
