<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="工号" prop="staffCode"><el-input v-model="queryParams.staffCode" placeholder="请输入工号" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="姓名" prop="staffName"><el-input v-model="queryParams.staffName" placeholder="请输入姓名" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="部门" prop="deptName"><el-input v-model="queryParams.deptName" placeholder="请输入部门" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:staff:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:staff:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:staff:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="工号" align="center" prop="staffCode" width="110"/>
      <el-table-column label="姓名" align="center" prop="staffName" width="100"/>
      <el-table-column label="部门" align="center" prop="deptName" width="130"/>
      <el-table-column label="岗位" align="center" prop="position" width="120"/>
      <el-table-column label="电话" align="center" prop="phone" width="120"/>
      <el-table-column label="邮箱" align="center" prop="email" width="150"/>
      <el-table-column label="入职日期" align="center" prop="hireDate" width="110"/>
      <el-table-column label="状态" align="center" prop="status" width="80"/>
      <el-table-column label="操作" align="center" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:staff:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:staff:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="工号" prop="staffCode"><el-input v-model="form.staffCode" placeholder="请输入工号"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="姓名" prop="staffName"><el-input v-model="form.staffName" placeholder="请输入姓名"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="部门" prop="deptName"><el-input v-model="form.deptName" placeholder="请输入部门"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="岗位" prop="position"><el-input v-model="form.position" placeholder="请输入岗位"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="电话" prop="phone"><el-input v-model="form.phone" placeholder="请输入电话"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="邮箱" prop="email"><el-input v-model="form.email" placeholder="请输入邮箱"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="入职日期" prop="hireDate"><el-date-picker v-model="form.hireDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择入职日期" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态" prop="status"><el-select v-model="form.status" placeholder="请选择状态"><el-option label="在职" value="ACTIVE"/><el-option label="离职" value="INACTIVE"/></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注"/></el-form-item>
        <el-divider content-position="left">检测授权</el-divider>
        <el-table :data="testAuthList" size="mini">
          <el-table-column label="检测项目" align="center" prop="testItemName"/>
          <el-table-column label="授权日期" align="center" prop="authorizedDate"/>
          <el-table-column label="到期日期" align="center" prop="expireDate"/>
          <el-table-column label="操作" align="center" width="90">
            <template slot-scope="scope"><el-button size="mini" type="text" icon="el-icon-delete" @click="testAuthList.splice(scope.$index, 1)">删除</el-button></template>
          </el-table-column>
        </el-table>
        <el-form-item label="新增授权">
          <el-input v-model="testAuthTemp.testItemName" placeholder="检测项目名称" style="width:200px"/>
          <el-date-picker v-model="testAuthTemp.authorizedDate" type="date" value-format="yyyy-MM-dd" placeholder="授权日期" style="width:150px"/>
          <el-date-picker v-model="testAuthTemp.expireDate" type="date" value-format="yyyy-MM-dd" placeholder="到期日期" style="width:150px"/>
          <el-button size="mini" type="primary" @click="addTestAuth">添加</el-button>
        </el-form-item>
        <el-divider content-position="left">仪器授权</el-divider>
        <el-table :data="instAuthList" size="mini">
          <el-table-column label="仪器名称" align="center" prop="instrumentName"/>
          <el-table-column label="授权日期" align="center" prop="authorizedDate"/>
          <el-table-column label="到期日期" align="center" prop="expireDate"/>
          <el-table-column label="操作" align="center" width="90">
            <template slot-scope="scope"><el-button size="mini" type="text" icon="el-icon-delete" @click="instAuthList.splice(scope.$index, 1)">删除</el-button></template>
          </el-table-column>
        </el-table>
        <el-form-item label="新增授权">
          <el-input v-model="instAuthTemp.instrumentName" placeholder="仪器名称" style="width:200px"/>
          <el-date-picker v-model="instAuthTemp.authorizedDate" type="date" value-format="yyyy-MM-dd" placeholder="授权日期" style="width:150px"/>
          <el-date-picker v-model="instAuthTemp.expireDate" type="date" value-format="yyyy-MM-dd" placeholder="到期日期" style="width:150px"/>
          <el-button size="mini" type="primary" @click="addInstAuth">添加</el-button>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStaff, getStaff, addStaff, updateStaff, delStaff } from '@/api/lims/training'

export default {
  name: 'LimsStaff',
  data() {
    return {
      loading: true, showSearch: true, dataList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, staffCode: undefined, staffName: undefined, deptName: undefined },
      ids: [], single: true, multiple: true, open: false, title: '',
      form: {}, rules: { staffCode: [{ required: true, message: '工号不能为空', trigger: 'blur' }], staffName: [{ required: true, message: '姓名不能为空', trigger: 'blur' }] },
      testAuthList: [], testAuthTemp: { testItemName: '', authorizedDate: '', expireDate: '' },
      instAuthList: [], instAuthTemp: { instrumentName: '', authorizedDate: '', expireDate: '' }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listStaff(this.queryParams).then(response => { this.dataList = response.rows; this.total = response.total; this.loading = false }) },
    reset() { this.form = {}; this.testAuthList = []; this.instAuthList = [] },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.staffId); this.single = selection.length !== 1; this.multiple = !selection.length },
    handleAdd() { this.reset(); this.open = true; this.title = '新增人员档案' },
    handleUpdate(row) { this.reset(); const id = row.staffId || this.ids[0]; getStaff(id).then(response => { this.form = response.data; this.open = true; this.title = '修改人员档案' }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { const data = Object.assign({}, this.form); data.testAuthList = this.testAuthList; data.instrumentAuthList = this.instAuthList; if (this.form.staffId != null) { updateStaff(data).then(() => { this.msgSuccess('修改成功'); this.open = false; this.getList() }) } else { addStaff(data).then(() => { this.msgSuccess('新增成功'); this.open = false; this.getList() }) } } }) },
    handleDelete(row) { const ids = row.staffId || this.ids; this.$confirm('确认删除选中的数据项?', '警告', { type: 'warning' }).then(() => delStaff(ids).then(() => { this.getList(); this.msgSuccess('删除成功') })).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    addTestAuth() { if (this.testAuthTemp.testItemName) { this.testAuthList.push(Object.assign({}, this.testAuthTemp)); this.testAuthTemp = { testItemName: '', authorizedDate: '', expireDate: '' } } },
    addInstAuth() { if (this.instAuthTemp.instrumentName) { this.instAuthList.push(Object.assign({}, this.instAuthTemp)); this.instAuthTemp = { instrumentName: '', authorizedDate: '', expireDate: '' } } }
  }
}
</script>
