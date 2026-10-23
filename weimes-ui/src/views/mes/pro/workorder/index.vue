<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('mes.工单编码')" prop="workorderCode">
        <el-input v-model="queryParams.workorderCode" :placeholder="$t('mes.请输入工单编码')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.工单名称')" prop="workorderName">
        <el-input v-model="queryParams.workorderName" :placeholder="$t('mes.请输入工单名称')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.来源单据')" prop="sourceCode">
        <el-input v-model="queryParams.sourceCode" :placeholder="$t('mes.请输入来源单据')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item :label="$t('mes.产品编号')" prop="productCode">
        <el-input v-model="queryParams.productCode" :placeholder="$t('mes.请输入产品编号')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('lims.产品名称')" prop="productName">
        <el-input v-model="queryParams.productName" :placeholder="$t('mes.请输入产品名称')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item :label="$t('lims.客户编码')" prop="clientCode">
        <el-input v-model="queryParams.clientCode" :placeholder="$t('lims.请输入客户编码')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('lims.客户名称')" prop="clientName">
        <el-input v-model="queryParams.clientName" :placeholder="$t('lims.请输入客户名称')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.工单类型')" prop="workorderType">
        <el-input v-model="queryParams.workorderType" :placeholder="$t('mes.请选择工单类型')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.需求日期')" prop="requestDate">
        <el-date-picker clearable v-model="queryParams.requestDate" type="date" value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择需求日期')">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['mes:pro:workorder:add']">{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['mes:pro:workorder:edit']">{{ this.$t('system.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['mes:pro:workorder:remove']">{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['mes:pro:workorder:export']">{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="workorderList" row-key="workorderId" default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
      <el-table-column :label="$t('mes.工单编码')" width="180" prop="workorderCode">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)" v-hasPermi="['mes:pro:workorder:query']">{{
            scope.row.workorderCode }}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.工单名称')" width="200" align="center" prop="workorderName" :show-overflow-tooltip="true" />
      <el-table-column :label="$t('mes.工单类型')" align="center" prop="workorderType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_workorder_type" :value="scope.row.workorderType" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.工单来源')" align="center" prop="orderSource">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_workorder_sourcetype" :value="scope.row.orderSource" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.订单编号')" width="140" align="center" prop="sourceCode" />
      <el-table-column :label="$t('mes.产品编号')" width="120" align="center" prop="productCode" />
      <el-table-column :label="$t('lims.产品名称')" width="200" align="center" prop="productName" :show-overflow-tooltip="true" />
      <el-table-column :label="$t('lims.规格型号')" align="center" prop="productSpc" :show-overflow-tooltip="true" />
      <el-table-column :label="$t('lims.单位')" align="center" prop="unitOfMeasure">
        <template slot-scope="scope">
          {{ convertUnitmeasure(scope.row.unitOfMeasure) }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.工单数量')" align="center" prop="quantity" />
      <el-table-column :label="$t('mes.调整数量')" align="center" prop="quantityChanged" />
      <el-table-column :label="$t('mes.已生产数量')" align="center" width="100px" prop="quantityProduced" />
      <el-table-column :label="$t('lims.批次号')" align="center" width="100px" prop="batchCode" />
      <el-table-column :label="$t('lims.客户编码')" align="center" prop="clientCode" />
      <el-table-column :label="$t('lims.客户名称')" align="center" prop="clientName" :show-overflow-tooltip="true" />
      <el-table-column :label="$t('mes.需求日期')" align="center" prop="requestDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.requestDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.单据状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_order_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" width="150px" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" v-if="scope.row.status == 'PREPARE'"
            @click="handleUpdate(scope.row)" v-hasPermi="['mes:pro:workorder:edit']">{{ this.$t('system.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus"
            v-if="scope.row.status == 'CONFIRMED' && scope.row.workorderType == 'SELF'" @click="handleAdd(scope.row)"
            v-hasPermi="['mes:pro:workorder:update']">{{ this.$t('common.add') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-circle-check" v-if="scope.row.status == 'CONFIRMED'"
            @click="handleFinish(scope.row)" v-hasPermi="['mes:pro:workorder:update']">{{ this.$t('lims.完成') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.status == 'PREPARE'"
            @click="handleDelete(scope.row)" v-hasPermi="['mes:pro:workorder:remove']">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改生产工单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.工单编号')" prop="workorderCode">
              <el-input v-model="form.workorderCode" :placeholder="$t('mes.请输入工单编号')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label-width="80">
              <el-switch v-model="autoGenFlag" active-color="#13ce66" active-text="自动生成"
                @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view' && form.status == 'PREPARE'">
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.工单名称')" prop="workorderName">
              <el-input v-model="form.workorderName" :placeholder="$t('mes.请输入工单名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('lims.来源类型')" prop="orderSource">
              <el-radio-group v-model="form.orderSource" disabled v-if="optType == 'view'">
                <el-radio v-for="dict in dict.type.mes_workorder_sourcetype" :key="dict.value" :label="dict.value">{{
                  dict.label }}</el-radio>
              </el-radio-group>
              <el-radio-group v-model="form.orderSource" v-else>
                <el-radio v-for="dict in dict.type.mes_workorder_sourcetype" :key="dict.value" :label="dict.value">{{
                  dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.orderSource == 'ORDER'">
            <el-form-item :label="$t('mes.订单编号')" prop="sourceCode">
              <el-input v-model="form.sourceCode" :placeholder="$t('mes.请输入订单编号')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.单据状态')" prop="status">
              <el-select v-model="form.status" disabled :placeholder="$t('mes.请选择单据状态')">
                <el-option v-for="dict in dict.type.mes_order_status" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.工单类型')" prop="workorderType">
              <el-select v-model="form.workorderType" :placeholder="$t('mes.请选择类型')">
                <el-option v-for="dict in dict.type.mes_workorder_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.产品编号')" prop="productCode">
              <el-input v-model="form.productCode" :placeholder="$t('mes.请选择产品')">
                <el-button slot="append" @click="handleSelectProduct" icon="el-icon-search"></el-button>
              </el-input>
              <ItemSelect ref="itemSelect" @onSelected="onItemSelected"> </ItemSelect>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.产品名称')" prop="productName">
              <el-input v-model="form.productName" :placeholder="$t('mes.请选择产品')" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('lims.规格型号')" prop="productSpc">
              <el-input v-model="form.productSpc" :placeholder="$t('mes.请选择产品')" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.单位')" prop="unitOfMeasure">
              <el-input v-model="form.unitOfMeasure" :placeholder="$t('mes.请选择产品')" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.工单数量')" prop="quantity">
              <el-input-number :min="1" v-model="form.quantity" :placeholder="$t('mes.请输入生产数量')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.需求日期')" prop="requestDate">
              <el-date-picker clearable v-model="form.requestDate" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择需求日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.批次号')" prop="batchCode">
              <el-input v-model="form.batchCode" :placeholder="$t('lims.请输入批次号')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.orderSource == 'ORDER'">
          <el-col :span="12">
            <el-form-item :label="$t('lims.客户编码')" prop="clientCode">
              <el-input v-model="form.clientCode" :placeholder="$t('mes.请选择客户')">
                <el-button slot="append" @click="handleSelectClient" icon="el-icon-search"></el-button>
              </el-input>
              <ClientSelect ref="clientSelect" @onSelected="onClientSelected"> </ClientSelect>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.客户名称')" prop="clientName">
              <el-input v-model="form.clientName" readonly="readonly" :placeholder="$t('lims.请输入客户名称')" />
            </el-form-item>
          </el-col>
          <el-col></el-col>
        </el-row>
        <el-row v-if="form.workorderType == 'OUTSOURCE' || form.workorderType == 'PURCHASE'">
          <el-col :span="12">
            <el-form-item :label="$t('lims.供应商编码')" prop="vendorCode">
              <el-input v-model="form.vendorCode" :placeholder="$t('mes.请选择供应商')">
                <el-button slot="append" @click="handleSelectVendor" icon="el-icon-search"></el-button>
              </el-input>
              <VendorSelect ref="vendorSelect" @onSelected="onVendorSelected" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.供应商名称')" prop="vendorName">
              <el-input v-model="form.vendorName" readonly="readonly" :placeholder="$t('mes.请选择供应商')" />
            </el-form-item>
          </el-col>
          <el-col></el-col>
        </el-row>
        <el-row>
          <el-col></el-col>
          <el-col></el-col>
          <el-col></el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('system.备注')" prop="remark">
              <el-input v-model="form.remark" type="textarea" :placeholder="$t('common.inputContent')" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-tabs type="border-card" v-if="form.workorderId != null">
        <el-tab-pane :label="$t('mes.BOM组成')">
          <Workorderbom ref="bomlist" :optType="optType" :workorder="form" @handleAddSub="handleSubAdd"></Workorderbom>
        </el-tab-pane>
        <!-- <el-tab-pane :label="$t('mes.物料需求')">
          <ClientOrderItem ref="clientOrderItem" :optType="optType" :clientOrderId="form.clientOrderId" />
        </el-tab-pane> -->
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancel" v-if="optType == 'view' || form.status != 'PREPARE'">{{ this.$t('common.back') }}</el-button>
        <el-button type="primary" @click="submitForm" v-if="form.status == 'PREPARE' && optType != 'view'">{{ this.$t('mes.保') }}
          存</el-button>
        <el-button type="success" @click="handleConfirm"
          v-if="form.status == 'PREPARE' && optType != 'view' && form.workorderId != null">{{ this.$t('mes.确 认') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listWorkorder, getWorkorder, delWorkorder, addWorkorder, updateWorkorder, dofinish, getWorkTaskStatus } from "@/api/mes/pro/workorder";
import Workorderbom from "./bom/bom.vue";
import WorkorderItemList from "./items/item.vue";
import ItemSelect from "@/components/itemSelect/single.vue";
import ClientOrderItem from "@/views/mes/pro/client-order/client-order-item/clientOrderItem.vue"
import ClientSelect from "@/components/clientSelect/single.vue";
import VendorSelect from "@/components/vendorSelect/single.vue";
import { genCode } from "@/api/system/autocode/rule"
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "Workorder",
  dicts: ['mes_order_status', 'mes_workorder_sourcetype', 'mes_workorder_type'],
  components: {
    Treeselect,
    ItemSelect,
    ClientSelect,
    VendorSelect,
    ClientOrderItem,
    Workorderbom,
    WorkorderItemList
  },
  data() {
    return {
      //自动生成编码
      autoGenFlag: false,
      optType: undefined,
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 非单个禁用
      single: true,
      // 总条数
      total: 0,
      // 非多个禁用
      multiple: true,
      // 生产工单表格数据
      workorderList: [],
      // 生产工单树选项
      workorderOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        workorderCode: null,
        workorderName: null,
        orderSource: null,
        sourceCode: null,
        productId: null,
        productCode: null,
        productName: null,
        productSpc: null,
        unitOfMeasure: null,
        quantity: null,
        quantityProduced: null,
        quantityChanged: null,
        quantityScheduled: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        requestDate: null,
        parentId: null,
        ancestors: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        workorderCode: [
          { required: true, message: this.$t('mes.工单编码不能为空'), trigger: "blur" }
        ],
        workorderName: [
          { required: true, message: this.$t('mes.工单名称不能为空'), trigger: "blur" }
        ],
        workorderType: [
          { required: true, message: this.$t('mes.请选择生产工单类型'), trigger: "blur" }
        ],
        orderSource: [
          { required: true, message: this.$t('mes.来源类型不能为空'), trigger: "blur" }
        ],
        productId: [
          { required: true, message: this.$t('mes.产品不能为空'), trigger: "blur" }
        ],
        productCode: [
          { required: true, message: this.$t('mes.产品编号不能为空'), trigger: "blur" }
        ],
        productName: [
          { required: true, message: this.$t('mes.产品名称不能为空'), trigger: "blur" }
        ],
        quantity: [
          { required: true, message: this.$t('mes.生产数量不能为空'), trigger: "blur" }
        ],
        requestDate: [
          { required: true, message: this.$t('mes.需求日期不能为空'), trigger: "blur" }
        ],
      }
    };
  },
  beforeRouteEnter(to, from, next) {
    next(v => {
      v.getList()
    })
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询生产工单列表 */
    getList() {
      this.loading = true;
      listWorkorder(this.queryParams).then(response => {
        this.workorderList = this.handleTree(response.rows, "workorderId", "parentId");
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 转换生产工单数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.workorderId,
        label: node.workorderName,
        children: node.children
      };
    },
    /** 查询生产工单下拉树结构 */
    getTreeselect() {
      listWorkorder().then(response => {
        this.workorderOptions = [];
        const data = { workorderId: 0, workorderName: '顶级节点', children: [] };
        data.children = this.handleTree(response.data, "workorderId", "parentId");
        this.workorderOptions.push(data);
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        workorderId: null,
        workorderCode: null,
        workorderName: null,
        workorderType: 'SELF',
        orderSource: null,
        sourceCode: null,
        productId: null,
        productCode: null,
        productName: null,
        productSpc: null,
        unitOfMeasure: null,
        quantity: null,
        quantityProduced: null,
        quantityChanged: null,
        quantityScheduled: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        requestDate: null,
        parentId: null,
        status: "PREPARE",
        remark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      };
      this.autoGenFlag = false;
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    //从BOM行中直接新增
    handleSubAdd(row) {
      this.open = false;
      this.reset();
      this.getTreeselect();
      if (row != null && row.workorderId) {
        this.form = row;
        this.form.parentId = row.workorderId;
        this.form.workorderId = null;
        this.form.workorderCode = null;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = this.$t('mes.添加生产工单');
      this.optType = "add";
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.workorderId) {
        this.form.parentId = row.workorderId;
        this.form.orderSource = row.orderSource;
        this.form.sourceCode = row.sourceCode;
        this.form.clientId = row.clientId;
        this.form.clientCode = row.clientCode;
        this.form.clientName = row.clientName;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = this.$t('mes.添加生产工单');
      this.optType = "add";
    },
    // 查询明细按钮操作
    handleView(row) {
      this.reset();
      this.getTreeselect();
      const workorderId = row.workorderId || this.ids;
      getWorkorder(workorderId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.查看工单信息');
        this.optType = "view";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      if (row != null) {
        this.form.parentId = row.workorderId;
      }
      getWorkorder(row.workorderId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改生产工单');
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.workorderId != null) {
            updateWorkorder(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              // this.$refs["bomlist"].getList();
              this.getList();
            });
          } else {
            addWorkorder(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.新增成功'));
              this.open = false;
              this.form.workorderId = response.data;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm(this.$t('mes.是否确认删除生产工单编号为{v}的数据项？', { v: row.workorderId })).then(function () {
        return delWorkorder(row.workorderId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => { });
    },
    handleSelectProduct() {
      this.$refs.itemSelect.showFlag = true;
    },
    handleSelectClient() {
      this.$refs.clientSelect.showFlag = true;
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('mes/pro/workorder/export', {
        ...this.queryParams
      }, `workorder_${new Date().getTime()}.xlsx`)
    },
    handleConfirm() {
      let that = this;
      this.$modal.confirm(this.$t('mes.是确认完成工单编制？【确认后将不能更改】')).then(function () {
        that.form.status = 'CONFIRMED';
        that.submitForm();
      });
    },
    handleFinish(row) {
      const workorderIds = row.workorderId || this.ids;
      // 提示字符串
      let tipString;

      // 查询当前工单下是否存在未完成的生产任务
      getWorkTaskStatus(workorderIds).then(response => {
        const count = response.data;

        // 根据返回的任务数量决定提示信息
        if (count > 0) {
          tipString = '当前工单下存在未完成生产任务，确认完成工单？一旦完成，此工单将无法继续报工！';
        } else {
          tipString = '确认完成工单？一旦完成，此工单将无法继续报工！';
        }
        // 弹出确认对话框
        return this.$modal.confirm(tipString);
      }).then(() => {
        // 用户确认后执行完成工单操作
        return dofinish(workorderIds); // 完成工单
      }).then(() => {
        // 完成工单成功后刷新列表并显示成功消息
        this.getList();
        this.$modal.msgSuccess(this.$t('mes.更改成功'));
      }).catch(() => {});
    },
    //物料选择弹出框
    onItemSelected(obj) {
      if (obj != undefined && obj != null) {
        this.form.productId = obj.itemId;
        this.form.productCode = obj.itemCode;
        this.form.productName = obj.itemName;
        this.form.productSpc = obj.specification;
        this.form.unitOfMeasure = obj.unitOfMeasure;
      }
    },
    //客户选择弹出框
    onClientSelected(obj) {
      if (obj != undefined && obj != null) {
        this.form.clientId = obj.clientId;
        this.form.clientCode = obj.clientCode;
        this.form.clientName = obj.clientName;
      }
    },
    //供应商选择
    handleSelectVendor() {
      this.$refs.vendorSelect.showFlag = true;
    },
    //供应商选择弹出框
    onVendorSelected(obj) {
      if (obj != undefined && obj != null) {
        this.form.vendorId = obj.vendorId;
        this.form.vendorCode = obj.vendorCode;
        this.form.vendorName = obj.vendorName;
      }
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag) {
      if (autoGenFlag) {
        genCode('WORKORDER_CODE').then(response => {
          this.form.workorderCode = response;
        });
      } else {
        this.form.workorderCode = null;
      }
    }
  }
};
</script>
