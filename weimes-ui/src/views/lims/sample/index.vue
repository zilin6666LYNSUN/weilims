<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item :label="$t('lims.样品编号')" prop="sampleCode">
        <el-input v-model="queryParams.sampleCode" :placeholder="$t('lims.请输入样品编号')" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item :label="$t('lims.样品名称')" prop="sampleName">
        <el-input v-model="queryParams.sampleName" :placeholder="$t('lims.请输入样品名称')" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item :label="$t('lims.样品类型')" prop="sampleType">
        <el-select v-model="queryParams.sampleType" :placeholder="$t('lims.请选择')" clearable>
          <el-option v-for="dict in sampleTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('lims.批号')" prop="batchNo">
        <el-input v-model="queryParams.batchNo" :placeholder="$t('lims.请输入批号')" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item :label="$t('lims.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('lims.请选择')" clearable>
          <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['lims:sample:add']">{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['lims:sample:edit']">{{ this.$t('lims.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['lims:sample:remove']">{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="sampleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column :label="$t('lims.样品编号')" align="center" prop="sampleCode" width="140"/>
      <el-table-column :label="$t('lims.样品名称')" align="center" prop="sampleName" width="160"/>
      <el-table-column :label="$t('lims.样品类型')" align="center" prop="sampleType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="sampleTypeOptions" :value="scope.row.sampleType"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.批号')" align="center" prop="batchNo" width="120"/>
      <el-table-column :label="$t('lims.产品名称')" align="center" prop="productName" width="140"/>
      <el-table-column :label="$t('lims.供应商')" align="center" prop="vendorName" width="140"/>
      <el-table-column :label="$t('lims.接收数量')" align="center" prop="quantityReceived" width="90"/>
      <el-table-column :label="$t('lims.状态')" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.优先级')" align="center" prop="priority" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.priority === 'URGENT'" type="warning">{{ $t('lims.加急') }}</el-tag>
          <el-tag v-else-if="scope.row.priority === 'EMERGENCY'" type="danger">{{ $t('lims.紧急') }}</el-tag>
          <el-tag v-else>{{ $t('lims.普通') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.创建时间')" align="center" prop="createTime" width="160"/>
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['lims:sample:edit']">{{ $t('lims.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-check" @click="handleReceive(scope.row)" v-if="scope.row.status === '待接收'" v-hasPermi="['lims:sample:receive']">{{ $t('lims.接收') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['lims:sample:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('lims.样品编号')" prop="sampleCode">
              <el-input v-model="form.sampleCode" :placeholder="$t('lims.请输入样品编号')"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.样品名称')" prop="sampleName">
              <el-input v-model="form.sampleName" :placeholder="$t('lims.请输入样品名称')"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('lims.样品类型')" prop="sampleType">
              <el-select v-model="form.sampleType" :placeholder="$t('lims.请选择样品类型')">
                <el-option v-for="dict in sampleTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.批号')" prop="batchNo">
              <el-input v-model="form.batchNo" :placeholder="$t('lims.请输入批号')"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('lims.规格型号')" prop="specification">
              <el-input v-model="form.specification" :placeholder="$t('lims.请输入规格型号')"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.接收数量')" prop="quantityReceived">
              <el-input-number v-model="form.quantityReceived" :precision="4" :min="0"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('lims.单位')" prop="unitOfMeasure">
              <el-input v-model="form.unitOfMeasure" :placeholder="$t('lims.请输入单位')"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.优先级')" prop="priority">
              <el-select v-model="form.priority" :placeholder="$t('lims.请选择优先级')">
                <el-option :label="$t('lims.普通')" value="NORMAL"/>
                <el-option :label="$t('lims.加急')" value="URGENT"/>
                <el-option :label="$t('lims.紧急')" value="EMERGENCY"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('lims.效期')" prop="expireDate">
              <el-date-picker v-model="form.expireDate" type="date" value-format="yyyy-MM-dd" :placeholder="$t('lims.选择效期')"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.存储条件')" prop="storageCondition">
              <el-input v-model="form.storageCondition" :placeholder="$t('lims.如2-8°C/室温/避光')"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('lims.备注')" prop="remark">
              <el-input v-model="form.remark" type="textarea" :placeholder="$t('lims.请输入备注')"/>
            </el-form-item>
          </el-col>
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
import { listSample, getSample, addSample, updateSample, delSample, receiveSample } from '@/api/lims/sample'

export default {
  name: 'LimsSample',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      sampleList: [],
      title: '',
      open: false,
      sampleTypeOptions: [
        { value: '原料', label: this.$t('lims.原料') },
        { value: '中间体', label: this.$t('lims.中间体') },
        { value: '成品', label: this.$t('lims.成品') },
        { value: '稳定性', label: this.$t('lims.稳定性') },
        { value: '环境', label: this.$t('lims.环境') },
        { value: '水系统', label: this.$t('lims.水系统') },
        { value: '临床', label: this.$t('lims.临床') }
      ],
      statusOptions: [
        { value: '待接收', label: this.$t('lims.待接收') },
        { value: '已接收', label: this.$t('lims.已接收') },
        { value: '检测中', label: this.$t('lims.检测中') },
        { value: '检测完成', label: this.$t('lims.检测完成') },
        { value: '留样', label: this.$t('lims.留样') },
        { value: '已处置', label: this.$t('lims.已处置') }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        sampleCode: null,
        sampleName: null,
        sampleType: null,
        batchNo: null,
        status: null
      },
      form: {},
      rules: {
        sampleCode: [{ required: true, message: this.$t('lims.样品编号不能为空'), trigger: 'blur' }],
        sampleName: [{ required: true, message: this.$t('lims.样品名称不能为空'), trigger: 'blur' }],
        sampleType: [{ required: true, message: this.$t('lims.样品类型不能为空'), trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSample(this.queryParams).then(response => {
        this.sampleList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.sampleId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    reset() {
      this.form = {
        sampleId: null, sampleCode: null, sampleName: null, sampleType: null,
        sampleSource: null, batchNo: null, specification: null, quantityReceived: null,
        unitOfMeasure: null, priority: 'NORMAL', storageCondition: null, remark: null
      }
      this.resetForm('form')
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = this.$t('lims.新增样品')
    },
    handleUpdate(row) {
      this.reset()
      const sampleId = row.sampleId || this.ids[0]
      getSample(sampleId).then(response => {
        this.form = response.data
        this.open = true
        this.title = this.$t('lims.修改样品')
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.sampleId != null) {
            updateSample(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('lims.修改成功'));
              this.open = false
              this.getList()
            })
          } else {
            addSample(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('lims.新增成功'));
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const sampleIds = row.sampleId || this.ids
      this.$modal.confirm(this.$t('lims.是否确认删除？')).then(() => {
        return delSample(sampleIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess(this.$t('lims.删除成功'));
      }).catch(() => {})
    },
    handleReceive(row) {
      this.$modal.confirm(this.$t('lims.是否确认接收该样品？')).then(() => {
        return receiveSample(row.sampleId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess(this.$t('lims.接收成功'));
      }).catch(() => {})
    },
    getStatusType(status) {
      const map = { '待接收': 'info', '已接收': '', '检测中': 'warning', '检测完成': 'success', '留样': '', '已处置': 'info' }
      return map[status] || ''
    },
    cancel() {
      this.open = false
      this.reset()
    }
  }
}
</script>
