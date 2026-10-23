<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.仪器编号')" prop="instrumentCode"><el-input v-model="queryParams.instrumentCode" :placeholder="$t('lims.请输入')" clearable @keyup.enter.native="handleQuery"/></el-form-item>
      <el-form-item :label="$t('lims.接口类型')" prop="interfaceType">
        <el-select v-model="queryParams.interfaceType" :placeholder="$t('lims.请选择')" clearable>
          <el-option label="RS232" value="RS232"/><el-option label="LAN" value="LAN"/><el-option label="USB" value="USB"/><el-option :label="$t('lims.文件')" value="FILE"/><el-option label="OPC" value="OPC"/><el-option label="MQTT" value="MQTT"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:instrument:acquisition:add']">{{ this.$t('common.add') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:instrument:acquisition:edit']">{{ this.$t('lims.修改') }}</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:instrument:acquisition:remove']">{{ this.$t('common.delete') }}</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.仪器编号')" align="center" prop="instrumentCode" width="120"/>
      <el-table-column :label="$t('lims.接口类型')" align="center" prop="interfaceType" width="100"/>
      <el-table-column :label="$t('lims.数据协议')" align="center" prop="dataProtocol" width="100"/>
      <el-table-column :label="$t('lims.自动导入')" align="center" prop="autoImport" width="80">
        <template slot-scope="scope"><el-tag :type="scope.row.autoImport===1?'success':'info'">{{ scope.row.autoImport===1?this.$t('lims.是'):$t('lims.否') }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('lims.轮询间隔(秒)')" align="center" prop="pollInterval" width="110"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="80">
        <template slot-scope="scope"><el-tag :type="scope.row.status==='ACTIVE'?'success':'info'">{{ scope.row.status==='ACTIVE'?this.$t('lims.启用'):$t('lims.禁用') }}</el-tag></template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:instrument:acquisition:edit']">{{ this.$t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" @click="handleImport(scope.row)" v-hasPermi="['lims:instrument:acquisition:edit']">{{ this.$t('lims.采集数据') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:instrument:acquisition:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.仪器ID')" prop="instrumentId"><el-input v-model="form.instrumentId" :disabled="form.acquisitionId!=null"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.仪器编号')" prop="instrumentCode"><el-input v-model="form.instrumentCode"/></el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item :label="$t('lims.接口类型')" prop="interfaceType">
            <el-select v-model="form.interfaceType"><el-option label="RS232" value="RS232"/><el-option label="LAN" value="LAN"/><el-option label="USB" value="USB"/><el-option :label="$t('lims.文件')" value="FILE"/><el-option label="OPC" value="OPC"/><el-option label="MQTT" value="MQTT"/></el-select>
          </el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="$t('lims.数据协议')" prop="dataProtocol">
            <el-select v-model="form.dataProtocol"><el-option label="ASCII" value="ASCII"/><el-option label="BINARY" value="BINARY"/><el-option label="HL7" value="HL7"/><el-option label="ASTM" value="ASTM"/><el-option label="JSON" value="JSON"/></el-select>
          </el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.连接参数(JSON)')" prop="connectionConfig"><el-input v-model="form.connectionConfig" type="textarea" :rows="3" placeholder='{"host":"192.168.1.100","port":8000}'/></el-form-item>
        <el-form-item :label="$t('lims.解析规则(JSON)')" prop="parsingRules"><el-input v-model="form.parsingRules" type="textarea" :rows="3"/></el-form-item>
        <el-form-item :label="$t('lims.字段映射(JSON)')" prop="fieldMappings"><el-input v-model="form.fieldMappings" type="textarea" :rows="3"/></el-form-item>
        <el-row>
          <el-col :span="8"><el-form-item :label="$t('lims.自动导入')"><el-switch v-model="form.autoImport" :active-value="1" :inactive-value="0"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item :label="$t('lims.轮询间隔(秒)')"><el-input-number v-model="form.pollInterval" :min="0"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item :label="$t('lims.状态')"><el-select v-model="form.status"><el-option :label="$t('lims.启用')" value="ACTIVE"/><el-option :label="$t('lims.禁用')" value="INACTIVE"/></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item :label="$t('lims.备注')" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">{{ this.$t('lims.确 定') }}</el-button><el-button @click="cancel">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>

    <!-- 采集数据对话框 -->
    <el-dialog :title="$t('lims.采集仪器数据')" :visible.sync="importOpen" width="500px" append-to-body>
      <el-form ref="importForm" :model="importForm" :rules="importRules" label-width="100px">
        <el-form-item :label="$t('lims.检验订单ID')" prop="testOrderId"><el-input v-model="importForm.testOrderId" :placeholder="$t('lims.请输入要导入数据的检验订单ID')"/></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitImport">{{ this.$t('lims.开始采集') }}</el-button><el-button @click="importOpen=false">{{ this.$t('lims.取 消') }}</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listAcquisition, getAcquisition, addAcquisition, updateAcquisition, delAcquisition, importInstrumentData } from '@/api/lims/acquisition'

export default {
  name: 'InstrumentAcquisition',
  data() {
    return {
      loading: true, showSearch: true, total: 0, dataList: [], ids: [], single: true, multiple: true,
      queryParams: { pageNum: 1, pageSize: 10, instrumentCode: null, interfaceType: null },
      open: false, title: '', importOpen: false,
      form: {}, importForm: { instrumentId: null, testOrderId: null },
      rules: { instrumentId: [{ required: true, message: this.$t('lims.请输入仪器ID'), trigger: 'blur' }], interfaceType: [{ required: true, message: this.$t('lims.请选择接口类型'), trigger: 'change' }] },
      importRules: { testOrderId: [{ required: true, message: this.$t('lims.请输入检验订单ID'), trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; listAcquisition(this.queryParams).then(res => { this.dataList = res.rows; this.total = res.total; this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.acquisitionId); this.single = selection.length !== 1; this.multiple = !selection.length },
    reset() { this.form = { acquisitionId: null, instrumentId: null, instrumentCode: null, interfaceType: null, connectionConfig: null, dataProtocol: 'ASCII', parsingRules: null, fieldMappings: null, autoImport: 0, pollInterval: 0, status: 'ACTIVE', remark: null } },
    handleAdd() { this.reset(); this.open = true; this.title = this.$t('lims.添加采集配置') },
    handleUpdate(row) { const id = row.acquisitionId || this.ids[0]; getAcquisition(id).then(res => { this.form = res.data; this.open = true; this.title = this.$t('lims.修改采集配置') }) },
    submitForm() { this.$refs.form.validate(valid => { if (valid) { if (this.form.acquisitionId != null) { updateAcquisition(this.form).then(() => { this.$modal.msgSuccess(this.$t('lims.修改成功')); this.open = false; this.getList() }) } else { addAcquisition(this.form).then(() => { this.$modal.msgSuccess(this.$t('lims.新增成功')); this.open = false; this.getList() }) } } }) },
    cancel() { this.open = false; this.reset() },
    handleDelete(row) { const ids = row.acquisitionId || this.ids; this.$modal.confirm(this.$t('lims.确认删除？')).then(() => delAcquisition(ids)).then(() => { this.getList(); this.$modal.msgSuccess(this.$t('lims.删除成功')) }).catch(() => {}) },
    handleImport(row) { this.importForm = { instrumentId: row.instrumentId, testOrderId: null }; this.importOpen = true },
    submitImport() { this.$refs.importForm.validate(valid => { if (valid) { importInstrumentData(this.importForm.instrumentId, this.importForm.testOrderId).then(res => { this.$modal.msgSuccess(res.msg); this.importOpen = false }) } }) }
  }
}
</script>
