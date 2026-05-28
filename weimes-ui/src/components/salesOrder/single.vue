<template>
  <el-dialog
    title="销售订单选择"
    v-if="showFlag"
    :visible.sync="showFlag"
    :modal="false"
    width="80%"
    center
  >
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="100px"
    >
      <el-row>
        <el-col :span="6">
          <el-form-item label="销售订单编码" prop="salesOrderCode">
            <el-input
              v-model="queryParams.salesOrderCode"
              placeholder="请输入销售订单编码"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="仓库" prop="warehouseId">
            <el-select
                v-model="queryParams.warehouseId"
                placeholder="请选择仓库"
                clearable
              >
                <el-option
                  v-for="dict in warehouseList"
                  :key="dict.warehosueId"
                  :label="dict.warehouseName"
                  :value="dict.warehouseId"
                />
              </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="客户编码" prop="clientCode">
            <el-input
              v-model="queryParams.clientCode"
              placeholder="请输入客户编码"
              clearable
              @keyup.enter.native="handleQuery"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="客户名称" prop="clientName">
            <el-input
              v-model="queryParams.clientName"
              placeholder="请输入客户名称"
              clearable
              @keyup.enter.native="handleQuery"
              disabled
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="6">
          <el-form-item label="客户PO号" prop="clientPoCode">
            <el-input
              v-model="queryParams.clientPoCode"
              placeholder="请输入客户PO号"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="订货日期" prop="salesOrderDate">
            <el-date-picker
              clearable
              v-model="queryParams.salesOrderDate"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="请选择订货日期"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="交货日期" prop="deliveryDate">
            <el-date-picker
              clearable
              v-model="queryParams.deliveryDate"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="请选择交货日期"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              size="mini"
              @click="handleQuery"
              >搜索</el-button
            >
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
              >重置</el-button
            >
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-table
      v-loading="loading"
      :data="salesItemList"
      @current-change="handleCurrent"
      @row-dblclick="handleRowDbClick"
      @selection-change="handleRowChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="交货日期" align="center" prop="deliveryDate"  width="150"/>
      <el-table-column label="销售订单编码" prop="salesOrderCode"  width="150"/>
      <el-table-column label="生产编号" prop="workorderCode" />
      <el-table-column label="产品编码" align="center" prop="productCode" />
      <el-table-column label="产品名称" align="center" prop="productName" />
      <el-table-column label="产品规格" align="center" prop="productSpec" />
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
      <el-table-column
        label="订货数量"
        align="center"
        prop="salesOrderQuantity"
      />
      <el-table-column label="已出货数" align="center" prop="saleQty" />
      <el-table-column label="已退货数" align="center" prop="saleThqty" />
      <el-table-column label="欠数" align="center" prop="oweQty" />
      <el-table-column label="库存数量" align="center" prop="stockNumber" />

      <!-- <el-table-column label="仓库" align="center" prop="this.q.warehouseId" /> -->
      
      <el-table-column
        label="备注"
        align="center"
        prop="remark"
        :show-overflow-tooltip="true"
      />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="confirmSelect">确 定</el-button>
      <el-button @click="showFlag = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { selectItem } from "@/api/mes/sales/order/item";
import { totalListColor } from "@/api/color/color";
export default {
  name: "SalesItemSelect",
  dicts: ["sys_yes_no", "mes_vendor_level"],

  data() {
    return {
      // 颜色数据
      colorList: [],
      showFlag: false,
      selectedItemId: undefined,
      selectedRow: [],
      // 遮罩层
      loading: true,
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 供应商表格数据
      salesItemList: [],
      //仓库数据
      warehouseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // warehouseId: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        salesOrderCode: null,
        salesOrderName: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        clientPoCode: null,
        salesOrderDate: null,
        deliveryDate: null,
        warehouseId:null,
      },
      // 表单参数
      form: {},
    };
  },
  created() {
    this.getList();
    this.getColorList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      
      if(this.queryParams.warehouseId!=undefined){
        selectItem(this.queryParams).then((response) => {
        this.salesItemList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
      }
      
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
    handleCurrent(row) {
      if (row) {
        this.selectedRow = row;
      }
    },
    // 行双击选中
    handleRowDbClick(row) {
      if (row) {
        this.selectedRow = row;
        this.$emit("onSelected", this.selectedRow);
        this.showFlag = false;
      }
    },
    // 选中数据
    handleRowChange(selection) {
        // this.selectedRow = row;
    this.selectedRow = selection.map((item) => item);
    this.single = selection.length !== 1;
    this.multiple = !selection.length;      
    },
    //确定选中
    confirmSelect() {
      if (this.selectedRow == null || this.selectedRow == 0) {
        this.$notify({
          title: "提示",
          type: "warning",
          message: "请至少选择一条数据!",
        });
        return;
      }
      this.$emit("onSelected", this.selectedRow);
      this.showFlag = false;
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
  },
};
</script>
