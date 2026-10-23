<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('mes.订单编码')" prop="clientOrderCode">
        <el-input v-model="queryParams.clientOrderCode" :placeholder="$t('mes.请输入订单编码')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('lims.客户名称')" prop="clientName">
        <el-input v-model="queryParams.clientName" :placeholder="$t('lims.请输入客户名称')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.订货日期')">
        <el-date-picker v-model="daterangeOrderDate" style="width: 240px" value-format="yyyy-MM-dd" type="daterange"
          range-separator="-" :start-placeholder="$t('system.开始日期')" :end-placeholder="$t('system.结束日期')"></el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.交货日期')">
        <el-date-picker v-model="daterangeDeliveryDate" style="width: 240px" value-format="yyyy-MM-dd" type="daterange"
          range-separator="-" :start-placeholder="$t('system.开始日期')" :end-placeholder="$t('system.结束日期')"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['pro:clientorder:add']">{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['pro:clientorder:edit']">{{ this.$t('system.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['pro:clientorder:remove']">{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['pro:clientorder:export']">{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="clientOrderList" tooltip-effect="light"
      @selection-change="handleSelectionChange" :showOverflowTooltip="true">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column :label="$t('mes.订单编码')" align="center" prop="clientOrderCode">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)" v-hasPermi="['pro:clientorder:query']">{{
            scope.row.clientOrderCode }}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.客户名称')" align="center" prop="clientName" />
      <el-table-column :label="$t('mes.订货日期')" align="center" prop="orderDate">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.orderDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.交货日期')" align="center" prop="deliveryDate">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deliveryDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.规格型号')" align="center" prop="spec" />
      <el-table-column :label="$t('mes.订货数量')" align="center" prop="orderQuantity" />
      <el-table-column :label="$t('lims.单位')" align="left" prop="unitOfMeasure" width="80px">
        <template slot-scope="scope">
            {{ convertUnitmeasure(scope.row.unitOfMeasure) }}
          </template>
      </el-table-column>
      <el-table-column :label="$t('mes.订单状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_client_order_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.质量要求')" align="center" prop="qualityRequirement" show-overflow-tooltip />
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['pro:clientorder:edit']" v-if="scope.row.status !== 'WORK_ORDER_FINISHED'">{{ this.$t('system.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['pro:clientorder:remove']" v-if="scope.row.status !== 'WORK_ORDER_FINISHED'">{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改客户订单对话框 -->
    <el-dialog v-if="showClientOrderItem" ref="clientOrderItemDialog" :title="title" :visible.sync="open" width="960px"
      append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
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
              <el-input class="normal-input-colour" v-model="form.productName" :placeholder="$t('mes.请选择产品')" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.产品规格')" prop="productSpec">
              <el-input class="normal-input-colour" v-model="form.productSpec" :placeholder="$t('mes.请选择产品')" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.订单编码')" prop="clientOrderCode">
              <el-input v-model="form.clientOrderCode" :placeholder="$t('mes.请输入订单编码')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.客户编码')" prop="clientCode">
              <el-input v-model="form.clientCode" :placeholder="$t('mes.请选择客户')">
                <el-button slot="append" @click="handleSelectClient" icon="el-icon-search"></el-button>
              </el-input>
              <ClientSelect ref="clientSelect" @onSelected="onClientSelected"> </ClientSelect>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.客户名称')" prop="clientName">
              <el-input v-model="form.clientName" readonly="readonly" :placeholder="$t('lims.请输入客户名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('mes.订货日期')" prop="orderDate">
              <el-date-picker clearable v-model="form.orderDate" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择订货日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.交货日期')" prop="deliveryDate">
              <el-date-picker clearable v-model="form.deliveryDate" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择交货日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="7">
            <el-form-item :label="$t('mes.订货数量')" prop="orderQuantity">
              <el-input-number v-model="form.orderQuantity" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item :label="$t('lims.单位')" prop="measureName">
              <el-select v-model="form.unitOfMeasure">
                <el-option v-for="item in measureOptions" :key="item.measureCode" :label="item.measureName"
                  :value="item.measureCode" :disabled="item.enableFlag == 'N'"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.规格型号')" prop="spec">
              <el-input v-model="form.spec" :placeholder="$t('lims.请输入规格型号')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item :label="$t('mes.质量要求')" prop="qualityRequirement">
            <el-input type="textarea" v-model="form.qualityRequirement" :placeholder="$t('mes.请输入质量要求')" />
          </el-form-item>
        </el-row>
      </el-form>
      <el-tabs type="border-card" v-if="form.clientOrderId != null">
        <el-tab-pane :label="$t('mes.物料需求')">
          <ClientOrderItem ref="clientOrderItem" :clientOrderId="form.clientOrderId" :optType="optType" />
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="optType !== 'view'">{{ this.$t('system.确 定') }}</el-button>
        <el-button :disabled="form.clientOrderId === null || form.status === 'WORK_ORDER_FINISHED'" type="success"
          @click="generateWorkOrderOnClick" v-if="optType !== 'view'">{{ this.$t('mes.生成生产工单') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addClientOrder,
  delClientOrder,
  getClientOrder,
  listClientOrder,
  updateClientOrder,
  generateWorkOrder
} from "@/api/mes/pro/client-order";
import ItemSelect from "@/components/itemSelect/item.vue"
import ClientSelect from "@/components/clientSelect/single.vue";
import ClientOrderItem from "./client-order-item/clientOrderItem.vue"
import { mapGetters } from "vuex";
export default {
  name: "ClientOrder",
  dicts: ['mes_client_order_status'],
  components: {
    ItemSelect,
    ClientSelect,
    ClientOrderItem
  },
  computed: {
    ...mapGetters({
      measureOptions: 'unitmeasureList'
    })
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      optType: undefined,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 客户订单表格数据
      clientOrderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 质量要求时间范围
      daterangeOrderDate: [],
      // 质量要求时间范围
      daterangeDeliveryDate: [],
      // 提前渲染使用
      showClientOrderItem: true,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        clientOrderCode: null,
        clientName: null,
        orderDate: null,
        deliveryDate: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        clientOrderCode: [
          { required: true, message: this.$t('mes.订单编码不能为空'), trigger: "blur" },
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
        clientName: [
          { required: true, message: this.$t('mes.客户名称不能为空'), trigger: "blur" },
        ],
        orderDate: [
          { required: true, message: this.$t('mes.订货日期不能为空'), trigger: "blur" },
        ],
        deliveryDate: [
          { required: true, message: this.$t('mes.交货日期不能为空'), trigger: "blur" },
        ],
        orderQuantity: [
          { required: true, message: this.$t('mes.订货数量不能为空'), trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询客户订单列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeOrderDate && "" != this.daterangeOrderDate) {
        this.queryParams.params["beginOrderDate"] = this.daterangeOrderDate[0];
        this.queryParams.params["endOrderDate"] = this.daterangeOrderDate[1];
      }
      if (
        null != this.daterangeDeliveryDate &&
        "" != this.daterangeDeliveryDate
      ) {
        this.queryParams.params["beginDeliveryDate"] =
          this.daterangeDeliveryDate[0];
        this.queryParams.params["endDeliveryDate"] =
          this.daterangeDeliveryDate[1];
      }
      listClientOrder(this.queryParams).then((response) => {
        this.clientOrderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    generateWorkOrderOnClick() {
      if (this.isEmpty(this.form.clientOrderId)) {
        this.$modal.msgError(this.$t('mes.请先保存订单信息'));
        return;
      }
      this.$modal
        .confirm('操作生成生产工单后,当前订单会锁定,不能继续修改,是否确认生成订单?')
        .then(() => {
          generateWorkOrder(this.form.clientOrderId)
            .then((response) => {
              let workOrderCode = response.data.workorderCode;
              let msg = `生成生产工单成功,订单编码:${workOrderCode},是否跳转到生产工单页面?`
              this.open = false;
              this.$modal.confirm(msg)
                .then(() => {
                  // 跳转到生产工单页面
                  this.$router.push({ path: '/mes/mes/pro/workorder' });
                });
            });
        })
        .catch(() => {
          this.open = false;
          this.$modal.msgError(this.$t('mes.未知错误!'));
         });
    },
    // 查询明细按钮操作
    handleView(row) {
      this.reset();
      getClientOrder(row.clientOrderId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.查看订单信息');
        this.optType = "view";
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    handleSelectProduct() {
      this.$refs.itemSelect.showFlag = true;
    },
    handleSelectClient() {
      this.$refs.clientSelect.showFlag = true;
    },
    //物料选择弹出框
    onItemSelected(obj) {
      if (obj != undefined && obj != null) {
        this.form.productId = obj.itemId;
        this.form.productCode = obj.itemCode;
        this.form.productName = obj.itemName;
        this.form.productSpec = obj.specification;
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
    // 表单重置
    reset() {
      this.form = {
        clientOrderId: null,
        clientOrderCode: null,
        productId: null,
        productName: null,
        productCode: null,
        productSpec: null,
        unitOfMeasure: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        orderDate: null,
        deliveryDate: null,
        spec: null,
        orderQuantity: null,
        measureId: null,
        measureCode: null,
        measureName: null,
        qualityRequirement: null,
        attr1: null,
        attr2: null,
        attr3: null,
        attr4: null,
        createUserId: null,
        createBy: null,
        createTime: null,
        updateUserId: null,
        updateBy: null,
        updateTime: null,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.daterangeOrderDate = [];
      this.daterangeDeliveryDate = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.clientOrderId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加客户订单');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const clientOrderId = row.clientOrderId || this.ids;
      getClientOrder(clientOrderId).then((response) => {
        this.form = response.data;
        this.optType = 'edit';
        this.open = true;
        this.title = this.$t('mes.修改客户订单');
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.clientOrderId != null) {
            updateClientOrder(this.form).then((response) => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              // this.open = false;
              this.getList();
            });
          } else {
            addClientOrder(this.form).then((response) => {
              this.$modal.msgSuccess(this.$t('system.新增成功'));
              // this.open = false;
              this.form.clientOrderId = response.data;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const clientOrderIds = row.clientOrderId || this.ids;
      this.$modal
        .confirm('是否确认删除客户订单编号为"' + row.clientOrderCode + '"的数据项？')
        .then(function () {
          return delClientOrder(clientOrderIds);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess(this.$t('system.删除成功'));
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "pro/client-order/export",
        {
          ...this.queryParams,
        },
        `client-order_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
