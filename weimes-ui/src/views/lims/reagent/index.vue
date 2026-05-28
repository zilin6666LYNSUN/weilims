<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="试剂编号" prop="reagentCode"><el-input v-model="queryParams.reagentCode" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="试剂名称" prop="reagentName"><el-input v-model="queryParams.reagentName" placeholder="请输入" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item label="试剂类型" prop="reagentType">
        <el-select v-model="queryParams.reagentType" placeholder="请选择" clearable>
          <el-option label="一般试剂" value="GENERAL"/><el-option label="危险试剂" value="HAZARDOUS"/><el-option label="标准品" value="STANDARD"/><el-option label="培养基" value="CULTURE"/>
        </el-select>
      </el-form-item>
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
      <el-table-column label="试剂编号" align="center" prop="reagentCode" width="130"/>
      <el-table-column label="试剂名称" align="center" prop="reagentName" width="160"/>
      <el-table-column label="CAS号" align="center" prop="casNumber" width="130"/>
      <el-table-column label="试剂类型" align="center" prop="reagentType" width="100">
        <template slot-scope="scope">{{ { GENERAL: '一般', HAZARDOUS: '危险', STANDARD: '标准品', CULTURE: '培养基' }[scope.row.reagentType] }}</template>
      </el-table-column>
      <el-table-column label="生产商" align="center" prop="manufacturer" width="120"/>
      <el-table-column label="危险等级" align="center" prop="hazardLevel" width="100"/>
      <el-table-column label="存储条件" align="center" prop="storageCondition" width="100"/>
      <el-table-column label="状态" align="center" prop="status" width="80"/>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleStockLog(scope.row)">出入库</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:reagent:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:reagent:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="试剂编号" prop="reagentCode"><el-input v-model="form.reagentCode"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="试剂名称" prop="reagentName"><el-input v-model="form.reagentName"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="CAS号" prop="casNumber"><el-input v-model="form.casNumber"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="试剂类型" prop="reagentType"><el-select v-model="form.reagentType"><el-option label="一般试剂" value="GENERAL"/><el-option label="危险试剂" value="HAZARDOUS"/><el-option label="标准品" value="STANDARD"/><el-option label="培养基" value="CULTURE"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="生产商" prop="manufacturer"><el-input v-model="form.manufacturer"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="危险等级" prop="hazardLevel"><el-input v-model="form.hazardLevel" placeholder="如1/2/3级"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="存储条件" prop="storageCondition"><el-input v-model="form.storageCondition" placeholder="如2-8°C"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态" prop="status"><el-select v-model="form.status"><el-option label="启用" value="ENABLED"/><el-option label="停用" value="DISABLED"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row><el-col :span="24"><el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item></el-col></el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button></div>
    </el-dialog>

    <!-- 出入库对话框 -->
    <el-dialog title="试剂出入库" :visible.sync="stockOpen" width="500px" append-to-body>
      <el-form ref="stockForm" :model="stockForm" :rules="stockRules" label-width="100px">
        <el-form-item label="操作类型" prop="operationType"><el-select v-model="stockForm.operationType"><el-option label="入库" value="IN"/><el-option label="出库" value="OUT"/></el-select></el-form-item>
        <el-form-item label="数量" prop="quantity"><el-input-number v-model="stockForm.quantity" :min="0.01" :precision="2"/></el-form-item>
        <el-form-item label="原因" prop="reason"><el-input v-model="stockForm.reason" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitStockForm">确 定</el-button><el-button @click="stockOpen = false">取 消</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import { listReagent, getReagent, addReagent, updateReagent, delReagent, addStockLog } from '@/api/lims/reagent'
export default {
  name: 'LimsReagent',
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0, dataList: [], title: '', open: false,
      queryParams: { pageNum: 1, pageSize: 10, reagentCode: null, reagentName: null, reagentType: null },
      form: {}, rules: { reagentCode: [{ required: true, message: '不能为空', trigger: 'blur' }], reagentName: [{ required: true, message: '不能为空', trigger: 'blur' }] },
      stockOpen: false, currentReagentId: null, stockForm: { operationType: 'IN', quantity: null, reason: null },
      stockRules: { operationType: [{ required: true, message: '不能为空', trigger: 'change' }], quantity: [{ required: true, message: '不能为空', trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listReagent(this.queryParams).then(r => { this.dataList = r.rows; this.total = r.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(s) { this.ids = s.map(i => i.reagentId); this.single = s.length !== 1; this.multiple = !s.length },
    reset() { this.form = { reagentId: null, reagentCode: null, reagentName: null, casNumber: null, reagentType: 'GENERAL', manufacturer: null, hazardLevel: null, storageCondition: null, status: 'ENABLED', remark: null }; this.resetForm('form') },
    handleAdd() { this.reset(); this.open = true; this.title = '新增试剂' },
    handleUpdate(row) { this.reset(); getReagent(row.reagentId || this.ids[0]).then(r => { this.form = r.data; this.open = true; this.title = '修改试剂' }) },
    submitForm() { this.$refs['form'].validate(v => { if (v) { (this.form.reagentId != null ? updateReagent : addReagent)(this.form).then(() => { this.$modal.msgSuccess('操作成功'); this.open = false; this.getList() }) } }) },
    handleDelete(row) { const ids = row.reagentId || this.ids; this.$modal.confirm('是否确认删除？').then(() => delReagent(ids)).then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {}) },
    cancel() { this.open = false; this.reset() },
    handleStockLog(row) { this.currentReagentId = row.reagentId; this.stockForm = { reagentId: row.reagentId, operationType: 'IN', quantity: null, reason: null }; this.stockOpen = true },
    submitStockForm() { this.$refs['stockForm'].validate(v => { if (v) { addStockLog(this.stockForm).then(() => { this.$modal.msgSuccess('操作成功'); this.stockOpen = false; this.getList() }) } }) }
  }
}
</script>
