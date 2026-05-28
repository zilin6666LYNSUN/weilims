<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="类型编号" prop="typeCode"><el-input v-model="queryParams.typeCode" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="类型名称" prop="typeName"><el-input v-model="queryParams.typeName" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:instrument:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:instrument:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:instrument:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="类型编号" align="center" prop="typeCode" width="140"/>
      <el-table-column label="类型名称" align="center" prop="typeName" width="180"/>
      <el-table-column label="分类" align="center" prop="category" width="120"/>
      <el-table-column label="上级ID" align="center" prop="parentId" width="100"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160"/>
      <el-table-column label="操作" align="center" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:instrument:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:instrument:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="类型编号" prop="typeCode"><el-input v-model="form.typeCode"/></el-form-item>
        <el-form-item label="类型名称" prop="typeName"><el-input v-model="form.typeName"/></el-form-item>
        <el-form-item label="分类" prop="category"><el-input v-model="form.category" placeholder="如色谱/光谱/天平"/></el-form-item>
        <el-form-item label="上级ID" prop="parentId"><el-input v-model="form.parentId" placeholder="0为顶级"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listInstrumentType, getInstrumentType, addInstrumentType, updateInstrumentType, delInstrumentType } from '@/api/lims/instrumentSub'
export default {
  name: 'LimsInstrumentType',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, typeCode: null, typeName: null },
      form: {}, rules: { typeCode: [{ required: true, message: '不能为空', trigger: 'blur' }], typeName: [{ required: true, message: '不能为空', trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listInstrumentType(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.typeId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { typeId: null, typeCode: null, typeName: null, category: null, parentId: 0 }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = '新增仪器类型' },
    handleUpdate(row) { this.reset(); const typeId = row.typeId || this.ids[0]; getInstrumentType(typeId).then(r => { this.form = r.data; this.open = true; this.title = '修改仪器类型' }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.typeId != null ? updateInstrumentType : addInstrumentType)(this.form).then(() => { this.$modal.msgSuccess('操作成功'); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.typeId || this.ids; this.$modal.confirm('是否确认删除？').then(() => delInstrumentType(ids)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    cancel() { this.open = false; this.reset() }
  }
}
</script>
