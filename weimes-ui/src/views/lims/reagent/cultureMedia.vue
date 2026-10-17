<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="培养基名称" prop="mediaName"><el-input v-model="queryParams.mediaName" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="状态" prop="status"><el-select v-model="queryParams.status" placeholder="全部" clearable><el-option label="在库" value="IN_STOCK"/><el-option label="已领用" value="ISSUED"/><el-option label="已报废" value="SCRAPPED"/></el-select></el-form-item>
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
      <el-table-column label="培养基编码" align="center" prop="mediaCode" width="120"/>
      <el-table-column label="培养基名称" align="center" prop="mediaName" width="140"/>
      <el-table-column label="类型" align="center" prop="mediaType" width="110"/>
      <el-table-column label="生产商" align="center" prop="manufacturer" width="120"/>
      <el-table-column label="批次号" align="center" prop="lotNumber" width="120"/>
      <el-table-column label="制备日期" align="center" prop="preparationDate" width="110"/>
      <el-table-column label="灭菌方式" align="center" prop="sterilizationMethod" width="100"/>
      <el-table-column label="无菌检查" align="center" prop="sterilityTest" width="90"/>
      <el-table-column label="促生长试验" align="center" prop="growthPromotionTest" width="110"/>
      <el-table-column label="有效期至" align="center" prop="expireDate" width="110"/>
      <el-table-column label="存储条件" align="center" prop="storageCondition" width="100"/>
      <el-table-column label="状态" align="center" prop="status" width="80"/>
      <el-table-column label="操作" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:reagent:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:reagent:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12"><el-form-item label="培养基编码" prop="mediaCode"><el-input v-model="form.mediaCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="培养基名称" prop="mediaName"><el-input v-model="form.mediaName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="类型" prop="mediaType"><el-input v-model="form.mediaType" placeholder="如营养琼脂/沙氏/麦康凯"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="生产商" prop="manufacturer"><el-input v-model="form.manufacturer"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="批次号" prop="lotNumber"><el-input v-model="form.lotNumber"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="制备日期" prop="preparationDate"><el-date-picker v-model="form.preparationDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="灭菌方式" prop="sterilizationMethod"><el-input v-model="form.sterilizationMethod" placeholder="如121℃ 15min"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="有效期至" prop="expireDate"><el-date-picker v-model="form.expireDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="8"><el-form-item label="无菌检查" prop="sterilityTest"><el-switch v-model="form.sterilityTest" :active-value="1" :inactive-value="0"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="促生长试验" prop="growthPromotionTest"><el-switch v-model="form.growthPromotionTest" :active-value="1" :inactive-value="0"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="促生长结果" prop="growthTestResult"><el-input v-model="form.growthTestResult"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="存储条件" prop="storageCondition"><el-input v-model="form.storageCondition" placeholder="如2-8°C"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态" prop="status"><el-select v-model="form.status"><el-option label="在库" value="IN_STOCK"/><el-option label="已领用" value="ISSUED"/><el-option label="已报废" value="SCRAPPED"/></el-select></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCultureMedia, getCultureMedia, addCultureMedia, updateCultureMedia, delCultureMedia } from '@/api/lims/reagent'

export default {
  name: 'CultureMedia',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, mediaName: undefined, status: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { mediaCode: [{ required: true, message: '培养基编码不能为空', trigger: 'blur' }], mediaName: [{ required: true, message: '培养基名称不能为空', trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listCultureMedia(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {} },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.mediaId); this.single = selection.length !== 1; this.multiple = !selection.length },
    resetForm(ref) { if (this.$refs[ref]) this.$refs[ref].resetFields() },
    handleAdd() { this.reset(); this.open = true; this.title = '新增培养基' },
    handleUpdate(row) { this.reset(); const id = row.mediaId || this.ids[0]; getCultureMedia(id).then(response => { this.form = response.data; this.open = true; this.title = '修改培养基' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.mediaId) { updateCultureMedia(this.form).then(() => { this.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addCultureMedia(this.form).then(() => { this.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleDelete(row) { const ids = row.mediaId || this.ids; this.$modal.confirm('是否确认删除培养基编号为"' + ids + '"的数据项？').then(() => delCultureMedia(ids).then(() => { this.getList(); this.msgSuccess('删除成功') })).catch(() => {}) }
  }
}
</script>
