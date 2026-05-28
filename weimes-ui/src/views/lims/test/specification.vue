<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="标准编号" prop="specificationCode"><el-input v-model="queryParams.specificationCode" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="标准名称" prop="specificationName"><el-input v-model="queryParams.specificationName" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:test:spec:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:test:spec:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:test:spec:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="标准编号" align="center" prop="specificationCode" width="140"/>
      <el-table-column label="标准名称" align="center" prop="specificationName" width="180"/>
      <el-table-column label="产品ID" align="center" prop="productId" width="140"/>
      <el-table-column label="版本" align="center" prop="version" width="80"/>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope"><el-tag :type="scope.row.status === 'ENABLED' ? 'success' : 'info'">{{ scope.row.status === 'ENABLED' ? '启用' : '停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160"/>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleViewLines(scope.row)">检测项</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:test:spec:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:test:spec:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="标准编号" prop="specificationCode"><el-input v-model="form.specificationCode" placeholder="请输入"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="标准名称" prop="specificationName"><el-input v-model="form.specificationName" placeholder="请输入"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="产品名称" prop="productId"><el-input v-model="form.productId" placeholder="请输入产品ID"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="版本" prop="version"><el-input v-model="form.version" placeholder="请输入"/></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>

    <!-- 标准行对话框 -->
    <el-dialog title="质量标准检测项" :visible.sync="lineOpen" width="900px" append-to-body>
      <el-row :gutter="10" class="mb8"><el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAddLine">新增检测项</el-button></el-col></el-row>
      <el-table :data="lineList" border>
        <el-table-column label="检测项名称" prop="testItemName" width="160"/>
        <el-table-column label="检测方法ID" prop="methodId" width="120"/>
        <el-table-column label="标准值" prop="specificationValue" width="120"/>
        <el-table-column label="单位" prop="unit" width="80"/>
        <el-table-column label="下限" prop="lowerLimit" width="100"/>
        <el-table-column label="上限" prop="upperLimit" width="100"/>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleEditLine(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="handleDelLine(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 标准行编辑对话框 -->
    <el-dialog :title="lineTitle" :visible.sync="lineEditOpen" width="600px" append-to-body>
      <el-form ref="lineForm" :model="lineForm" :rules="lineRules" label-width="100px">
        <el-form-item label="检测项名称" prop="testItemName"><el-input v-model="lineForm.testItemName"/></el-form-item>
        <el-form-item label="检测方法ID" prop="methodId"><el-input v-model="lineForm.methodId"/></el-form-item>
        <el-row>
          <el-col :span="12"><el-form-item label="标准值" prop="specificationValue"><el-input v-model="lineForm.specificationValue"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="单位" prop="unit"><el-input v-model="lineForm.unit"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="下限" prop="lowerLimit"><el-input v-model="lineForm.lowerLimit"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="上限" prop="upperLimit"><el-input v-model="lineForm.upperLimit"/></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitLineForm">确 定</el-button><el-button @click="lineEditOpen = false">取 消</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listSpecification, getSpecification, addSpecification, updateSpecification, delSpecification, listSpecLines, addSpecLine, updateSpecLine, delSpecLine } from '@/api/lims/testConfig'
export default {
  name: 'LimsSpecification',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, specificationCode: null, specificationName: null },
      form: {}, rules: { specificationCode: [{ required: true, message: '不能为空', trigger: 'blur' }], specificationName: [{ required: true, message: '不能为空', trigger: 'blur' }] },
      lineOpen: false, lineList: [], currentSpecId: null, lineEditOpen: false, lineTitle: '', lineForm: {},
      lineRules: { testItemName: [{ required: true, message: '不能为空', trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listSpecification(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.specificationId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { specificationId: null, specificationCode: null, specificationName: null, productId: null, version: null, remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = '新增质量标准' },
    handleUpdate(row) { this.reset(); getSpecification(row.specificationId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = '修改质量标准' }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.specificationId != null ? updateSpecification : addSpecification)(this.form).then(() => { this.$modal.msgSuccess('操作成功'); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.specificationId || this.ids; this.$modal.confirm('是否确认删除？').then(() => delSpecification(ids)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleViewLines(row) { this.currentSpecId = row.specificationId; listSpecLines(row.specificationId).then(r => { this.lineList = r.data; this.lineOpen = true }) },
    handleAddLine() { this.lineForm = { specificationId: this.currentSpecId, testItemName: null, methodId: null, specificationValue: null, unit: null, lowerLimit: null, upperLimit: null }; this.lineTitle = '新增检测项'; this.lineEditOpen = true },
    handleEditLine(row) { this.lineForm = { ...row }; this.lineTitle = '编辑检测项'; this.lineEditOpen = true },
    handleDelLine(row) { this.$modal.confirm('是否确认删除？').then(() => delSpecLine(row.lineId)).then(() => { this.handleViewLines({ specificationId: this.currentSpecId }); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    submitLineForm() { this.$refs['lineForm'].validate(v => { if (v) { (this.lineForm.lineId != null ? updateSpecLine : addSpecLine)(this.lineForm).then(() => { this.lineEditOpen = false; this.handleViewLines({ specificationId: this.currentSpecId }); this.$modal.msgSuccess('操作成功') }) } }) }
  }
}
</script>
