<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item :label="$t('mes.单号')" prop="poOrderNo">
        <el-input
          v-model="queryParams.poOrderNo"
          :placeholder="$t('mes.请输入单号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.供应商编号')" prop="vendorNo" label-width="100px">
        <el-input
          v-model="queryParams.vendorNo"
          :placeholder="$t('mes.请输入供应商编号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.供应商简称')" prop="vendorName" label-width="100px">
        <el-input
          v-model="queryParams.vendorName"
          :placeholder="$t('mes.请输入供应商简称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.落单日期')">
        <el-date-picker
          v-model="daterangePoOrderDate"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          :start-placeholder="$t('system.开始日期')"
          :end-placeholder="$t('system.结束日期')"
        ></el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.采购员')" prop="poOrderUser">
        <el-select v-model="queryParams.poOrderUser" :placeholder="$t('mes.请选择采购员')">
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.送货地址')" prop="tranAddress">
        <el-input
          v-model="queryParams.tranAddress"
          :placeholder="$t('mes.请输入送货地址')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.交货日期')">
        <el-date-picker
          v-model="daterangeTranDate"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          :start-placeholder="$t('system.开始日期')"
          :end-placeholder="$t('system.结束日期')"
        ></el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('system.备注')" prop="remake">
        <el-input
          v-model="queryParams.remake"
          :placeholder="$t('lims.请输入备注')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.开票人')" prop="billUser">
        <el-input
          v-model="queryParams.billUser"
          :placeholder="$t('mes.请输入开票人')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('system.状态')" prop="status">
        <el-select
          v-model="queryParams.status"
          :placeholder="$t('system.请选择状态')"
          clearable
        >
          <el-option :label="$t('mes.请选择字典生成')" value="" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.送审日期')">
        <el-date-picker
          v-model="daterangePartDate"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          :start-placeholder="$t('system.开始日期')"
          :end-placeholder="$t('system.结束日期')"
        ></el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.送审人')" prop="partUser">
        <el-input
          v-model="queryParams.partUser"
          :placeholder="$t('mes.请输入送审人')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.审核日期')">
        <el-date-picker
          v-model="daterangeAuditDate"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          :start-placeholder="$t('system.开始日期')"
          :end-placeholder="$t('system.结束日期')"
        ></el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.审核人')" prop="auditUser">
        <el-input
          v-model="queryParams.auditUser"
          :placeholder="$t('mes.请输入审核人')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.结单日期')">
        <el-date-picker
          v-model="daterangeEndDate"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          :start-placeholder="$t('system.开始日期')"
          :end-placeholder="$t('system.结束日期')"
        ></el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.结单人')" prop="endUser">
        <el-input
          v-model="queryParams.endUser"
          :placeholder="$t('mes.请输入结单人')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >{{ this.$t('system.搜索') }}</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >{{ this.$t('common.reset') }}</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['purchase:purchase_order:add']"
          >{{ this.$t('common.add') }}</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['purchase:purchase_order:edit']"
          >{{ this.$t('system.修改') }}</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['purchase:purchase_order:remove']"
          >{{ this.$t('common.delete') }}</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['purchase:purchase_order:export']"
          >{{ this.$t('common.export') }}</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="purchase_orderList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.更新时间')" align="center" prop="id" />
      <el-table-column :label="$t('mes.单号')" align="center" prop="poOrderNo" />
      <el-table-column :label="$t('mes.供应商编号')" align="center" prop="vendorNo" />
      <el-table-column :label="$t('mes.供应商简称')" align="center" prop="vendorName" />
      <el-table-column
        :label="$t('mes.下单日期')"
        align="center"
        prop="poOrderDate"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.poOrderDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('mes.采购员')"
        align="center"
        prop="poOrderUser"
        width="180"
      >
      </el-table-column>
      <el-table-column :label="$t('mes.送货地址')" align="center" prop="tranAddress" />
      <el-table-column
        :label="$t('mes.交货日期')"
        align="center"
        prop="tranDate"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.tranDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.备注')" align="center" prop="remake" />
      <el-table-column :label="$t('system.状态')" align="center" prop="status" />
      <el-table-column
        :label="$t('mes.送审日期')"
        align="center"
        prop="partDate"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.partDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.送审人')" align="center" prop="partUser" />
      <el-table-column
        :label="$t('mes.审核日期')"
        align="center"
        prop="auditDate"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.审核人')" align="center" prop="auditUser" />
      <el-table-column
        :label="$t('mes.结单日期')"
        align="center"
        prop="endDate"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.结单人')" align="center" prop="endUser" />
      <el-table-column
        :label="$t('common.operation')"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['purchase:purchase_order:edit']"
            >{{ $t('system.修改') }}</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['purchase:purchase_order:remove']"
            >{{ $t('common.delete') }}</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改采购订单对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1500px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="4">
            <el-form-item :label="$t('mes.单号')" prop="poOrderNo">
              <el-input v-model="form.poOrderNo" :placeholder="$t('mes.请输入单号')" />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item label-width="80">
              <el-switch
                v-model="autoGenFlag"
                active-color="#13ce66"
                :active-text="$t('mes.自动生成')"
                @change="handleAutoGenChange(autoGenFlag)"
              >
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item :label="$t('mes.下单日期')" prop="poOrderDate">
              <el-date-picker
                clearable
                v-model="form.poOrderDate"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择下单日期')"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item :label="$t('mes.交货日期')" prop="tranDate">
              <el-date-picker
                clearable
                v-model="form.tranDate"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择交货日期')"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="$t('mes.采购部门')" prop="poOrderDept">
              <el-input v-model="form.poOrderDept" :placeholder="$t('mes.请输入采购员')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="$t('mes.采购员')" prop="poOrderUser">
              <el-input v-model="form.poOrderUser" :placeholder="$t('mes.请输入采购员')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="4">
            <el-form-item
              :label="$t('mes.供应商编号')"
              prop="vendorNo"
              label-width="120px"
            >
              <el-input
                v-model="form.vendorNo"
                :placeholder="$t('mes.供应商编号')"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="$t('lims.供应商')" prop="vendorName">
              <el-input
                v-model="form.vendorName"
                readonly="readonly"
                :placeholder="$t('mes.请选择供应商')"
              >
                <el-button
                  slot="append"
                  @click="handleSelectVendor"
                  icon="el-icon-search"
                ></el-button>
              </el-input>
              <VendorSelect ref="vendorSelect" @onSelected="onVendorSelected" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="$t('mes.结账方式')" prop="payUp">
              <el-select
                v-model="queryParams.payUp"
                :placeholder="$t('mes.请选择结账方式')"
              >
                <el-option
                  v-for="dict in dict.type.pay_up_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.送货地址')" prop="tranAddress">
              <el-input
                v-model="form.tranAddress"
                :placeholder="$t('mes.请输入送货地址')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('system.备注')" prop="remake">
              <el-input v-model="form.remake" :placeholder="$t('lims.请输入备注')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="center">{{ this.$t('mes.采购订单列表信息') }}</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              icon="el-icon-plus"
              size="mini"
              @click="handleAddPurchaseOrderLine"
              >{{ this.$t('lims.添加') }}</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              icon="el-icon-delete"
              size="mini"
              @click="handleDeletePurchaseOrderLine"
              >{{ this.$t('common.delete') }}</el-button
            >
          </el-col>
        </el-row>
        <el-table
          :data="purchaseOrderLineList"
          :row-class-name="rowPurchaseOrderLineIndex"
          @selection-change="handlePurchaseOrderLineSelectionChange"
          ref="purchaseOrderLine"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column
            :label="$t('system.序号')"
            align="center"
            prop="index"
            width="50"
          />
          <el-table-column :label="$t('mes.明细单号')" prop="poOrderLineNo" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.poOrderLineNo"
                :placeholder="$t('mes.请输入明细单号')"
              />
            </template>
          </el-table-column>

          <el-table-column :label="$t('mes.产品编码')" prop="productNo" width="200">
            <template slot-scope="scope">
              <el-input v-model="scope.row.productNo" :placeholder="$t('mes.请选择产品')">
                <el-button
                  slot="append"
                  @click="handleSelectProduct"
                  icon="el-icon-search"
                ></el-button>
              </el-input>
              <ItemSelect ref="itemSelect" @onSelected="onItemSelected">
              </ItemSelect>
            </template>
          </el-table-column>
          <el-table-column :label="$t('lims.产品名称')" prop="productName" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.productName"
                :placeholder="$t('mes.请输入产品名称')"
              />
            </template>
          </el-table-column>

          <el-table-column :label="$t('mes.色号')" prop="colorNo" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.colorNo" :placeholder="$t('mes.请输入色号')" />
            </template>
          </el-table-column>

          <el-table-column :label="$t('mes.颜色')" prop="colorName" width="150">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.colorName"
                :placeholder="$t('mes.请选择颜色')"
                filterable
                :change="changeColor(scope.row)"
              >
                <el-option
                  v-for="item in colorList"
                  :key="item.colorCode"
                  :label="item.colorName"
                  :value="item.colorCode"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>

          <el-table-column :label="$t('lims.单位')" prop="unit" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.unit" :placeholder="$t('lims.请输入单位')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('lims.数量')" prop="qty" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.qty" :placeholder="$t('mes.请输入数量')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.重量')" prop="weight" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.weight" :placeholder="$t('mes.请输入重量')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('lims.单价')" prop="price" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.price" :placeholder="$t('mes.请输入单价')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.折扣')" prop="discount" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.discount" :placeholder="$t('mes.请输入折扣')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.折扣总价')" prop="addMoney" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.addMoney"
                :placeholder="$t('mes.请输入折扣总价')"
              />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.总价')" prop="money" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.money" :placeholder="$t('mes.请输入总价')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.交货日期')" prop="tranDate" width="240">
            <template slot-scope="scope">
              <el-date-picker
                clearable
                v-model="scope.row.tranDate"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择送货日期')"
              />
            </template>
          </el-table-column>
          <el-table-column :label="$t('system.备注')" prop="memo" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.memo" :placeholder="$t('lims.请输入备注')" />
            </template>
          </el-table-column>

          <!-- <el-table-column :label="$t('mes.收货数量')" prop="shQty" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.shQty"
                :placeholder="$t('mes.请输入收货数量')"
              />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.退货数量')" prop="thQty" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.thQty"
                :placeholder="$t('mes.请输入退货数量')"
              />
            </template>
          </el-table-column>
          <el-table-column :label="$t('lims.入库数量')" prop="inQty" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.inQty"
                :placeholder="$t('mes.请输入入库数量')"
              />
            </template>
          </el-table-column> -->
          <!-- <el-table-column :label="$t('system.状态')" prop="status" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.status" :placeholder="$t('system.请选择状态')">
                <el-option :label="$t('mes.请选择字典生成')" value="" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.结账方式')" prop="payUp" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.payUp"
                :placeholder="$t('mes.请输入结账方式')"
              />
            </template>
          </el-table-column> -->
          <!-- <el-table-column :label="$t('mes.客户代号')" prop="cusNo" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.cusNo"
                :placeholder="$t('mes.请输入客户代号')"
              />
            </template>
          </el-table-column> -->
          <!-- <el-table-column :label="$t('mes.实收数量')" prop="ssQty" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.ssQty"
                :placeholder="$t('mes.请输入实收数量')"
              />
            </template>
          </el-table-column> -->
          <!-- <el-table-column :label="$t('mes.总重量')" prop="totalWeight" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.totalWeight"
                :placeholder="$t('mes.请输入总重量')"
              />
            </template>
          </el-table-column> -->
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{ this.$t('system.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listPurchase_order,
  getPurchase_order,
  delPurchase_order,
  addPurchase_order,
  updatePurchase_order,
} from "@/api/mes/sales/purchase/purchase_order";
import { genCode } from "@/api/system/autocode/rule";
import VendorSelect from "@/components/vendorSelect/single.vue";
import { totalListColor } from "@/api/color/color";
import ItemSelect from "@/components/itemSelect/item.vue";

export default {
  name: "Purchase_order",
  dicts: ["pay_up_type"],
  components: { VendorSelect, ItemSelect },
  data() {
    return {
      //自动生成编码
      autoGenFlag: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedPurchaseOrderLine: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 采购订单表格数据
      purchase_orderList: [],
      // 采购订单列表表格数据
      purchaseOrderLineList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 总重量时间范围
      daterangePoOrderDate: [],
      // 总重量时间范围
      daterangeTranDate: [],
      // 总重量时间范围
      daterangePartDate: [],
      // 总重量时间范围
      daterangeAuditDate: [],
      // 总重量时间范围
      daterangeEndDate: [],
      // 总重量时间范围
      daterangeCreateTime: [],
      // 总重量时间范围
      daterangeUpdateTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        poOrderNo: null,
        hwNo: null,
        vendorNo: null,
        poOrderDate: null,
        poOrderUser: null,
        tranAddress: null,
        tranDate: null,
        remake: null,
        billUser: null,
        review: null,
        status: null,
        partDate: null,
        partUser: null,
        auditDate: null,
        auditUser: null,
        endDate: null,
        endUser: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
      },
      // 当前选中行
      currentRow: undefined,
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        poOrderNo: [
          { required: true, message: this.$t('mes.单号不能为空'), trigger: "blur" },
        ],
        vendorNo: [
          { required: true, message: this.$t('mes.供应商编号不能为空'), trigger: "blur" },
        ],
        poOrderUser: [
          { required: true, message: this.$t('mes.采购员不能为空'), trigger: "blur" },
        ],
        tranDate: [
          { required: true, message: this.$t('mes.交货日期不能为空'), trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询采购订单列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (
        null != this.daterangePoOrderDate &&
        "" != this.daterangePoOrderDate
      ) {
        this.queryParams.params["beginPoOrderDate"] =
          this.daterangePoOrderDate[0];
        this.queryParams.params["endPoOrderDate"] =
          this.daterangePoOrderDate[1];
      }
      if (null != this.daterangeTranDate && "" != this.daterangeTranDate) {
        this.queryParams.params["beginTranDate"] = this.daterangeTranDate[0];
        this.queryParams.params["endTranDate"] = this.daterangeTranDate[1];
      }
      if (null != this.daterangePartDate && "" != this.daterangePartDate) {
        this.queryParams.params["beginPartDate"] = this.daterangePartDate[0];
        this.queryParams.params["endPartDate"] = this.daterangePartDate[1];
      }
      if (null != this.daterangeAuditDate && "" != this.daterangeAuditDate) {
        this.queryParams.params["beginAuditDate"] = this.daterangeAuditDate[0];
        this.queryParams.params["endAuditDate"] = this.daterangeAuditDate[1];
      }
      if (null != this.daterangeEndDate && "" != this.daterangeEndDate) {
        this.queryParams.params["beginEndDate"] = this.daterangeEndDate[0];
        this.queryParams.params["endEndDate"] = this.daterangeEndDate[1];
      }
      if (null != this.daterangeCreateTime && "" != this.daterangeCreateTime) {
        this.queryParams.params["beginCreateTime"] =
          this.daterangeCreateTime[0];
        this.queryParams.params["endCreateTime"] = this.daterangeCreateTime[1];
      }
      if (null != this.daterangeUpdateTime && "" != this.daterangeUpdateTime) {
        this.queryParams.params["beginUpdateTime"] =
          this.daterangeUpdateTime[0];
        this.queryParams.params["endUpdateTime"] = this.daterangeUpdateTime[1];
      }
      listPurchase_order(this.queryParams).then((response) => {
        this.purchase_orderList = response.rows;
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
        id: null,
        poOrderNo: null,
        hwNo: null,
        vendorNo: null,
        vendorName: null,
        poOrderDate: null,
        poOrderUser: null,
        deptId: null,
        tranAddress: null,
        tranDate: null,
        remake: null,
        billUser: null,
        review: null,
        status: "0",
        partDate: null,
        partUser: null,
        auditDate: null,
        auditUser: null,
        endDate: null,
        endUser: null,
        attr1: null,
        attr2: null,
        attr3: null,
        attr4: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        deleted: null,
        deleteat: null,
        version: null,
      };
      this.autoGenFlag = false;
      this.purchaseOrderLineList = [];
      this.resetForm("form");
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.daterangePoOrderDate = [];
      this.daterangeTranDate = [];
      this.daterangePartDate = [];
      this.daterangeAuditDate = [];
      this.daterangeEndDate = [];
      this.daterangeCreateTime = [];
      this.daterangeUpdateTime = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
       this.getColorList();
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加采购订单');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getPurchase_order(id).then((response) => {
        this.form = response.data;
        this.purchaseOrderLineList = response.data.purchaseOrderLineList;
        this.open = true;
        this.title = this.$t('mes.修改采购订单');
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          this.form.purchaseOrderLineList = this.purchaseOrderLineList;
          if (this.form.id != null) {
            updatePurchase_order(this.form).then((response) => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addPurchase_order(this.form).then((response) => {
              this.$modal.msgSuccess(this.$t('system.新增成功'));
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除采购订单编号为"' + ids + '"的数据项？')
        .then(function () {
          return delPurchase_order(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess(this.$t('system.删除成功'));
        })
        .catch(() => {});
    },
    /** 采购订单列表序号 */
    rowPurchaseOrderLineIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 采购订单列表添加按钮操作 */
    handleAddPurchaseOrderLine() {
      let obj = {};
      obj.poOrderLineNo = "";
      obj.vendorNo = "";
      obj.vendorName = "";
      obj.productNo = "";
      obj.productName = "";
      obj.colorNo = "";
      obj.colorName = "";
      obj.unit = "";
      obj.qty = "";
      obj.weight = "";
      obj.price = "";
      obj.discount = "";
      obj.addMoney = "";
      obj.money = "";
      obj.tranDate = "";
      obj.memo = "";
      obj.shQty = "";
      obj.thQty = "";
      obj.inQty = "";
      obj.status = "";
      obj.payUp = "";
      obj.cusNo = "";
      obj.ssQty = "";
      obj.deleted = "";
      obj.version = "";
      obj.totalWeight = "";
      this.purchaseOrderLineList.push(obj);
    },
    /** 采购订单列表删除按钮操作 */
    handleDeletePurchaseOrderLine() {
      if (this.checkedPurchaseOrderLine.length == 0) {
        this.$modal.msgError(this.$t('mes.请先选择要删除的采购订单列表数据'));
      } else {
        const purchaseOrderLineList = this.purchaseOrderLineList;
        const checkedPurchaseOrderLine = this.checkedPurchaseOrderLine;
        this.purchaseOrderLineList = purchaseOrderLineList.filter(function (
          item
        ) {
          return checkedPurchaseOrderLine.indexOf(item.index) == -1;
        });
      }
    },
    /** 复选框选中数据 */
    handlePurchaseOrderLineSelectionChange(selection) {
      this.checkedPurchaseOrderLine = selection.map((item) => item.index);
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "purchase/purchase_order/export",
        {
          ...this.queryParams,
        },
        `purchase_order_${new Date().getTime()}.xlsx`
      );
    },
    //供应商选择
    handleSelectVendor() {
      this.$refs.vendorSelect.showFlag = true;
    },
    //供应商选择弹出框
    onVendorSelected(obj) {
      if (obj != undefined && obj != null) {
        this.form.vendorNo = obj.vendorCode;
        this.form.vendorName = obj.vendorName;
      }
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag) {
      if (autoGenFlag) {
        genCode("SALES_ORDER_CODE").then((response) => {
          this.form.poOrderNo = response;
        });
      } else {
        this.form.poOrderNo = null;
      }
    },
    /**行单击 */
    handleRowClick(row, event, column) {
      this.currentRow = row;
    },
    //物料选择弹出框
    onItemSelected(obj) {
      if (obj != undefined && obj != null) {
        // 先删除原有数据
        let index = this.purchaseOrderLineList.indexOf(this.currentRow);
        this.purchaseOrderLineList.splice(index, 1);
        this.currentRow.productId = obj.itemId;
        this.currentRow.productCode = obj.itemCode;
        this.currentRow.productName = obj.itemName;
        this.currentRow.quality = obj.quality;
        this.currentRow.unitOfMeasure = obj.unitOfMeasure;
        // 添加到表格
        this.purchaseOrderLineList.push(this.currentRow);
      }
    },
    handleSelectProduct() {
      this.$refs.itemSelect.showFlag = true;
    },
    /** 查询颜色列表 */
    getColorList() {
      totalListColor().then((response) => {
        this.colorList = response.data;
      });
    },
    //色号
    changeColor(row){
      row.colorNo=row.colorName
    },
  },
};
</script>
