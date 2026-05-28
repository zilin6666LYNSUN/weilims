<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="销售订单编码" prop="salesOrderCode" label-width="108px">
        <el-input v-model="queryParams.salesOrderCode" placeholder="请输入销售订单编码" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="订单名称" prop="salesOrderName">
        <el-input v-model="queryParams.salesOrderName" placeholder="请输入订单名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="客户编码" prop="clientCode">
        <el-input v-model="queryParams.clientCode" placeholder="请输入客户编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="客户名称" prop="clientName">
        <el-input v-model="queryParams.clientName" placeholder="请输入客户名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="产品编码" prop="productCode">
        <el-input v-model="queryParams.productCode" placeholder="请输入产品编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="产品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="客户产品名称" prop="clientProductName" label-width="120px">
        <el-input v-model="queryParams.clientProductName" placeholder="请输入客户产品名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="材质" prop="quality">
        <el-input v-model="queryParams.quality" placeholder="请输入材质" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="颜色" prop="colorName">
        <el-select v-model="queryParams.colorName" placeholder="请选择颜色">
          <el-option v-for="item in colorList" :key="item.colorCode" :label="item.colorName"
            :value="item.colorCode"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="交货日期" prop="deliveryDate">
        <el-date-picker clearable v-model="queryParams.deliveryDate" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择交货日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['md:item:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['md:item:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['md:item:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['md:item:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="itemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="明细编码" align="center" prop="salesOrderItemCode" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">{{
            scope.row.salesOrderItemCode
            }}</el-button>
        </template>
      </el-table-column>
      <el-table-column label="订单编码" align="center" prop="salesOrderCode" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleOrderView(scope.row)">{{
            scope.row.salesOrderCode }}</el-button>
        </template>
      </el-table-column>
      <el-table-column label="客户编码" align="center" prop="clientCode" />
      <el-table-column label="订货日期" align="center" prop="salesOrderDate">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.salesOrderDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_order_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="客户名称" align="center" prop="clientName" />
      <el-table-column label="产品编码" align="center" prop="productCode" />
      <el-table-column label="产品名称" align="center" prop="productName" />
      <el-table-column label="产品规格" align="center" prop="productSpec" />
      <el-table-column label="材质" align="center" prop="quality" />
      <el-table-column label="色号" align="center" prop="colorName" />
      <el-table-column label="颜色" align="center" prop="colorName">
        <template slot-scope="scope">
          {{ convertColor(scope.row.colorName) }}
        </template>
      </el-table-column>

      <el-table-column label="单位" align="center" prop="unitOfMeasure">
        <template slot-scope="scope">
          {{ convertUnitmeasure(scope.row.unitOfMeasure) }}
        </template>
      </el-table-column>
      <el-table-column label="交货日期" align="center" prop="deliveryDate">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deliveryDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="客户产品名称" align="center" prop="clientProductName" width="120px" />
      <el-table-column label="订货数量" align="center" prop="salesOrderQuantity" />
      <el-table-column label="库存数量" align="center" prop="stockNum" />
      <el-table-column label="已出货数" align="center" prop="saleQty" />
      <el-table-column label="已退货数" align="center" prop="saleThqty" />
      <el-table-column label="欠数" align="center" prop="oweQty" />
      <el-table-column label="手工消数" align="center" prop="saleSgqty" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="工单ID" align="center" prop="workorderId" />
      <el-table-column label="创建者" align="center" prop="createBy" />
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新者" align="center" prop="updateBy" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['md:item:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['md:item:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 添加或修改订单对话框 -->
    <el-dialog v-if="showClientOrderItem" ref="clientOrderItemDialog" :title="title" :visible.sync="open" width="960px"
      append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="产品编号" prop="productCode">
              <el-input v-model="form.productCode" placeholder="请选择产品">
                <el-button slot="append" @click="handleSelectProduct" icon="el-icon-search"></el-button>
              </el-input>
              <ItemSelect ref="itemSelect" @onSelected="onItemSelected">
              </ItemSelect>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="产品名称" prop="productName">
              <el-input class="normal-input-colour" v-model="form.productName" placeholder="请选择产品" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="产品规格" prop="productSpec">
              <el-input class="normal-input-colour" v-model="form.productSpec" placeholder="请选择产品" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="订单编码" prop="clientOrderCode">
              <el-input v-model="form.salesOrderItemCode" placeholder="请输入订单编码" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="订货日期" prop="orderDate">
              <el-date-picker clearable v-model="form.salesOrderDate" type="date" value-format="yyyy-MM-dd"
                placeholder="请选择订货日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="交货日期" prop="deliveryDate">
              <el-date-picker clearable v-model="form.deliveryDate" type="date" value-format="yyyy-MM-dd"
                placeholder="请选择交货日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="材质" prop="quality">
              <el-input v-model="form.quality" placeholder="请输入材质" />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="色号" prop="colorCode">
              <el-input v-model="form.colorName" placeholder="colorCode" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="颜色" prop="colorName">
              <el-select v-model="form.colorName" placeholder="请选择颜色">
                <el-option v-for="item in colorList" :key="item.colorCode" :label="item.colorName"
                  :value="item.colorCode"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="订货数量" prop="salesOrderQuantity">
              <el-input-number v-model="form.salesOrderQuantity" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="单位" prop="unitOfMeasure">
              <el-select v-model="form.unitOfMeasure">
                <el-option v-for="item in measureOptions" :key="item.measureCode" :label="item.measureName"
                  :value="item.measureCode" :disabled="item.enableFlag == 'N'"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="主单编码" prop="clientOrderCode">
              <el-input v-model="form.clientOrderCode" placeholder="请输入订单编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="客供名称" prop="clientProductName">
              <el-input v-model="form.clientProductName" placeholder="请输入客供名称" /> </el-form-item></el-col>
          <el-col :span="4">
            <el-form-item label="库存数量" prop="stockNum">
              <el-input v-model="form.stockNum" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="已出货数" prop="saleQty">
              <el-input v-model="form.saleQty" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="已退货数" prop="saleThqty">
              <el-input v-model="form.saleThqty" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="手工消数" prop="saleSgqty">
              <el-input v-model="form.saleSgqty" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="工单编码" prop="workorderId">
              <el-input v-model="form.workorderId" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <el-tabs type="border-card" v-if="form.salesOrderId != null">
        <el-tab-pane label="物料需求">
          <SalesItemOrder ref="salesOrderItem" :clientOrderId="form.salesOrderItemId" :optType="optType" />
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="optType !== 'view'">确 定</el-button>
        <el-button type="success" @click="generateWorkOrderOnClick"
          v-if="optType !== 'view' && form.status == 'CONFIRMED' && optType != 'view' && form.salesOrderItemId != null">生成生产工单</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listItem,
  getItem,
  delItem,
  addItem,
  updateItem,
  generateWorkOrder,
} from "@/api/mes/sales/order/item";
import ItemSelect from "@/components/itemSelect/item.vue";
import SalesItemOrder from "./salesOrderItem.vue";
import { mapGetters } from "vuex";
import { totalListColor } from "@/api/color/color";

export default {
  name: "SalesItem",
  components: {
    ItemSelect,
    SalesItemOrder,
  },

  computed: {
    ...mapGetters({
      measureOptions: "unitmeasureList",
    }),
  },
  dicts: ["order_type", "pay_up_type", "currency_type", "mes_order_status"],
  data() {
    return {
      // 颜色数据
      colorList: [],
      optType: undefined,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      codes: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 销售订单产品列表格数据
      itemList: [],
      // 弹出层标题
      title: "",
      //提前渲染
      showClientOrderItem: true,
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productId: null,
        itemCode: null,
        salesOrderId: null,
        productCode: null,
        productName: null,
        productSpec: null,
        quality: null,
        colorName: null,
        unitOfMeasure: null,
        deliveryDate: null,
        clientProductName: null,
        stockNum: null,
        saleQty: null,
        saleThqty: null,
        saleSgqty: null,
        salesOrderCode: null,
        clientCode: null,
        clientName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productId: [
          { required: true, message: "产品id不能为空", trigger: "blur" },
        ],
        productCode: [
          { required: true, message: "产品编码不能为空", trigger: "blur" },
        ],
        productName: [
          { required: true, message: "产品名称不能为空", trigger: "blur" },
        ],
        unitOfMeasure: [
          { required: true, message: "单位不能为空", trigger: "change" },
        ],
        deliveryDate: [
          { required: true, message: "交货日期不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
    this.getColorList();
  },
  methods: {
    /**弹出产品选择页面 */
    handleSelectProduct() {
      this.$refs.itemSelect.showFlag = true;
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
    //生产生产工单
    generateWorkOrderOnClick() {
      let that = this;
      if (that.isEmpty(that.form.salesOrderId)) {
        that.$modal.msgError("请先保存订单信息");
        return;
      }
      that.$modal
        .confirm("是否确认生成生产工单？")
        .then(function () {
          console.log(that.form)
          generateWorkOrder(that.form.salesOrderItemId).then((response) => {
            let workorderCode = response.data.workorderCode;
            let msg = `生成生产工单成功,订单编码:${workorderCode},是否跳转到生产工单页面?`;
            that.open = false;
            that.$modal.confirm(msg).then(() => {
              // 跳转到生产工单页面
              that.$router.push({ path: "/mes/mes/pro/workorder" });
            });
          }).catch(() => {

          });
        });
    },
    /** 查询颜色列表 */
    getColorList() {
      totalListColor().then((response) => {
        this.colorList = response.data;
      });
    },
    convertColor(value) {
      const option = this.colorList.find((o) => o.colorCode === value);
      return option ? option.colorName : "";
    },

    /** 查询销售订单产品列列表 */
    getList() {
      this.loading = true;
      listItem(this.queryParams).then((response) => {
        this.itemList = response.rows;
        this.total = response.total;
        this.loading = false;
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
        salesOrderId: null,
        salesOrderItemId: null,
        salesOrderItemCode: null,
        productId: null,
        productCode: null,
        productName: null,
        productSpec: null,
        quality: null,
        colorCode: null,
        colorName: null,
        unitOfMeasure: null,
        deliveryDate: null,
        clientProductName: null,
        stockNum: null,
        saleQty: null,
        saleThqty: null,
        saleSgqty: null,
        remark: null,
        workorderId: null,
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.salesOrderItemId);
      this.codes = selection.map((item) => item.salesOrderItemCode);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加销售订单产品列";
      this.optType = "add";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const salesOrderItemId = row.salesOrderItemId || this.ids;
      getItem(salesOrderItemId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改销售订单产品列";
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.salesOrderItemId != null) {
            updateItem(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addItem(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const salesOrderItemIds = row.salesOrderItemId || this.ids;
      const salesOrderItemCodes = row.salesOrderItemCode || this.codes;
      this.$modal
        .confirm(
          '是否确认删除销售订单产品列编号为"' +
          salesOrderItemCodes +
          '"的数据项？'
        )
        .then(function () {
          return delItem(salesOrderItemIds);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "md/item/export",
        {
          ...this.queryParams,
        },
        `item_${new Date().getTime()}.xlsx`
      );
    },
    // 查询明细按钮操作
    handleView(row) {
      this.reset();
      const salesOrderItemId = row.salesOrderItemId || this.ids;
      getItem(salesOrderItemId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改销售订单产品列";
        this.optType = "view";
      });
    },
    // 通用跳转函数
    jumpPage(optType, salesOrderId = null) {
      this.$router.push({
        name: 'SalesOrder',
        params: {
          optType: optType,
          salesOrderId: salesOrderId,
        }
      });
    },
    // 查询主单按钮操作
    handleOrderView(row) {
      let salesOrderId = row.salesOrderId
      this.jumpPage('view', salesOrderId);
    },

  },
};
</script>
