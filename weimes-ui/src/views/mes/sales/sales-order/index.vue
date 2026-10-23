<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('mes.订单编码')" prop="salesOrderCode">
        <el-input v-model="queryParams.salesOrderCode" :placeholder="$t('mes.请输入订单编码')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.订单名称')" prop="salesOrderName">
        <el-input v-model="queryParams.salesOrderName" :placeholder="$t('mes.请输入订单名称')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.订单状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('mes.请选择订单状态')" clearable>
          <el-option :label="$t('mes.请选择字典生成')" value="" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('lims.客户编码')" prop="clientCode">
        <el-input v-model="queryParams.clientCode" :placeholder="$t('lims.请输入客户编码')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.客户PO号')" prop="clientPoCode" label-width="120px">
        <el-input v-model="queryParams.clientPoCode" :placeholder="$t('mes.请输入客户PO号')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('lims.客户名称')" prop="clientName">
        <el-input v-model="queryParams.clientName" :placeholder="$t('lims.请输入客户名称')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.订货日期')" prop="salesOrderDate">
        <el-date-picker clearable v-model="queryParams.salesOrderDate" type="date" value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择订货日期')">
        </el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.交货日期')" prop="deliveryDate">
        <el-date-picker clearable v-model="queryParams.deliveryDate" type="date" value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择交货日期')">
        </el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.币别')" prop="currency">
        <el-select v-model="queryParams.currency" :placeholder="$t('mes.请选择币别')">
          <el-option v-for="dict in dict.type.currency_type" :key="dict.value" :label="dict.label"
            :value="dict.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.结账方式')" prop="payUp">
        <el-select v-model="queryParams.payUp" :placeholder="$t('mes.请选择结账方式')">
          <el-option v-for="dict in dict.type.pay_up_type" :key="dict.value" :label="dict.label"
            :value="dict.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.订单类别')" prop="orderType">
        <el-select v-model="queryParams.orderType" :placeholder="$t('mes.请选择订单类别')" clearable>
          <el-option v-for="dict in dict.type.order_type" :key="dict.value" :label="dict.label"
            :value="dict.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.跟单')" prop="followerMan">
        <el-input v-model="queryParams.followerMan" :placeholder="$t('mes.请输入跟单')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.业务员')" prop="salesMan">
        <el-input v-model="queryParams.salesMan" :placeholder="$t('mes.请输入业务员')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.质量要求')" prop="qualityRequirement">
        <el-select v-model="queryParams.qualityRequirement" :placeholder="$t('mes.请选择质量等级')">
          <el-option v-for="dict in dict.type.level" :key="dict.value" :label="dict.label"
            :value="dict.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['sales:order:add']">{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['sales:order:edit']">{{ this.$t('system.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['sales:order:remove']">{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['sales:order:export']">{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.订单编码')" align="center" prop="salesOrderCode">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">{{
            scope.row.salesOrderCode }}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.订单名称')" align="center" prop="salesOrderName" />
      <el-table-column :label="$t('system.状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_order_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.客户编码')" align="center" prop="clientCode" />
      <el-table-column :label="$t('mes.客户PO号')" align="center" prop="clientPoCode" />
      <el-table-column :label="$t('lims.客户名称')" align="center" prop="clientName" />
      <el-table-column :label="$t('mes.订货日期')" align="center" prop="salesOrderDate">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.salesOrderDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.交货日期')" align="center" prop="deliveryDate">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deliveryDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.币别')" align="center" prop="currency">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.currency_type" :value="scope.row.currency" />
        </template>
      </el-table-column>

      <el-table-column :label="$t('mes.结账方式')" align="center" prop="payUp">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pay_up_type" :value="scope.row.payUp" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.订单类别')" align="center" prop="orderType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.order_type" :value="scope.row.orderType" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.跟单')" align="center" prop="followerMan" />
      <el-table-column :label="$t('mes.业务员')" align="center" prop="salesMan" />
      <el-table-column :label="$t('mes.质量要求')" align="center" prop="qualityRequirement">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.level" :value="scope.row.qualityRequirement" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.创建者')" align="center" prop="createBy" />
      <el-table-column :label="$t('system.创建时间')" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.备注')" align="center" prop="remark" />
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['md:order:edit']">{{ this.$t('system.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['md:order:remove']">{{ this.$t('common.delete') }}</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-delete" @click="puMes(scope.row)"
            v-hasPermi="['md:order:push']">{{ this.$t('mes.推送') }}</el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改销售订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="5">
            <el-form-item :label="$t('mes.订单编码')" prop="salesOrderCode">
              <el-input v-model="form.salesOrderCode" :placeholder="$t('mes.请输入订单编码')" />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item label-width="80">
              <el-switch v-model="autoGenFlag" active-color="#13ce66" active-text="自动生成"
                @change="handleAutoGenChange(autoGenFlag)">
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item :label="$t('mes.订单名称')" prop="salesOrderName">
              <el-input v-model="form.salesOrderName" :placeholder="$t('mes.请输入订单名称')" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('lims.客户编码')" prop="clientCode">
              <el-input v-model="form.clientCode" :placeholder="$t('mes.请选择客户')" :disabled="!this.salesOrderItemList == undefined ||
                !this.salesOrderItemList.length <= 0
                ">
                <el-button slot="append" @click="handleSelectClient" icon="el-icon-search" :disabled="!this.salesOrderItemList == undefined ||
                  !this.salesOrderItemList.length <= 0
                  "></el-button>
              </el-input>
              <ClientSelect ref="clientSelect" @onSelected="onClientSelected">
              </ClientSelect>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('lims.客户名称')" prop="clientName">
              <el-input v-model="form.clientName" :placeholder="$t('lims.请输入客户名称')" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item :label="$t('mes.客户PO号')" prop="clientPoCode">
              <el-input v-model="form.clientPoCode" :placeholder="$t('mes.请输入客户PO号')" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('mes.订货日期')" prop="salesOrderDate">
              <el-date-picker clearable v-model="form.salesOrderDate" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择订货日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('mes.交货日期')" prop="deliveryDate">
              <el-date-picker clearable v-model="form.deliveryDate" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择交货日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="4">
            <el-form-item :label="$t('mes.币别')" prop="currency">
              <el-select v-model="form.currency" :placeholder="$t('mes.请选择币别')">
                <el-option v-for="dict in dict.type.currency_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="$t('mes.结账方式')" prop="payUp">
              <el-select v-model="form.payUp" :placeholder="$t('mes.请选择结账方式')">
                <el-option v-for="dict in dict.type.pay_up_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="$t('mes.订单类别')" prop="orderType">
              <el-select v-model="form.orderType" :placeholder="$t('mes.请选择订单类别')">
                <el-option v-for="dict in dict.type.order_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="$t('mes.跟单')" prop="followerMan">
              <el-input v-model="form.followerMan" :placeholder="$t('mes.请输入跟单')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="$t('mes.业务员')" prop="salesMan">
              <el-input v-model="form.salesMan" :placeholder="$t('mes.请输入业务员')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="$t('mes.质量要求')" prop="qualityRequirement">
              <el-select v-model="form.qualityRequirement" :placeholder="$t('mes.请选择质量等级')">
                <el-option v-for="dict in dict.type.level" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item :label="$t('system.备注')" prop="remark">
          <el-input v-model="form.remark" type="textarea" :placeholder="$t('common.inputContent')" />
        </el-form-item>
        <el-divider content-position="center">{{ this.$t('mes.销售订单产品列信息') }}</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddSalesOrderItem"
              v-if="optType != 'view'">{{ this.$t('lims.添加') }}</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteSalesOrderItem"
              v-if="optType != 'view'">{{ this.$t('common.delete') }}</el-button>
          </el-col>
        </el-row>
        <el-table :data="salesOrderItemList" :row-class-name="rowSalesOrderItemIndex"
          @selection-change="handleSalesOrderItemSelectionChange" @row-click="handleRowClick" v-loading="loading">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column :label="$t('system.序号')" align="center" prop="index" width="50" />
          <el-table-column :label="$t('mes.产品编码')" prop="productCode" width="200">
            <template slot-scope="scope">
              <el-input v-model="scope.row.productCode" :placeholder="$t('mes.请选择产品')">
                <el-button slot="append" @click="handleSelectProduct" icon="el-icon-search"></el-button>
              </el-input>
              <ItemSelect ref="itemSelect" @onSelected="onItemSelected">
              </ItemSelect>
            </template>
          </el-table-column>
          <el-table-column :label="$t('lims.产品名称')" prop="productName" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.productName" :placeholder="$t('mes.请输入产品名称')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('lims.规格')" prop="quality" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.productSpec" :placeholder="$t('lims.请输入规格')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.材质')" prop="quality" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.quality" :placeholder="$t('mes.请输入材质')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.颜色')" prop="colorName" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.colorName" :placeholder="$t('mes.请选择颜色')" filterable :change="changeColor(scope.row)">
                <el-option v-for="item in colorList" :key="item.colorCode" :label="item.colorName"
                  :value="item.colorCode"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.订货数量')" prop="salesOrderQuantity" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.salesOrderQuantity" :placeholder="$t('mes.请输入订货数量')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('lims.单位')" prop="unitOfMeasure" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.unitOfMeasure">
                <el-option v-for="item in measureOptions" :key="item.measureCode" :label="item.measureName"
                  :value="item.measureCode"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('lims.单价')" prop="amount" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.amount" :placeholder="$t('lims.单价')" :change="changeAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.金额')" prop="totalAmount" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.totalAmount" :placeholder="$t('mes.金额')" disabled />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.订货日期')" prop="salesOrderDate" width="240">
            <template slot-scope="scope">
              <el-date-picker clearable v-model="scope.row.salesOrderDate" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择订货日期')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.交货日期')" prop="deliveryDate" width="240">
            <template slot-scope="scope">
              <el-date-picker clearable v-model="scope.row.deliveryDate" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择交货日期')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.客户产品名称')" prop="clientProductName" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.clientProductName" :placeholder="$t('mes.请输入客户产品名称')" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.库存数量')" prop="stockNum" width="150" v-if="optType == 'view'">
            <template slot-scope="scope">
              <el-input v-model="scope.row.stockNum" :placeholder="$t('mes.请输入库存数量')" disabled />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.已出货数')" prop="saleQty" width="150" v-if="optType == 'view'">
            <template slot-scope="scope">
              <el-input v-model="scope.row.saleQty" :placeholder="$t('mes.请输入已出货数')" disabled />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.已退货数')" prop="saleThqty" width="150" v-if="optType == 'view'">
            <template slot-scope="scope">
              <el-input v-model="scope.row.saleThqty" :placeholder="$t('mes.请输入已退货数')" disabled />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.欠数')" prop="oweQty" width="150" v-if="optType == 'view'">
            <template slot-scope="scope">
              <el-input v-model="scope.row.oweQty" :placeholder="$t('mes.欠数')" disabled />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.手工消数')" prop="saleSgqty" width="150" v-if="optType == 'view'">
            <template slot-scope="scope">
              <el-input v-model="scope.row.saleSgqty" :placeholder="$t('mes.请输入手工消数')" disabled />
            </template>
          </el-table-column>
          <el-table-column :label="$t('mes.工单编码')" prop="workorderCode" width="150" v-if="optType == 'view'">
            <template slot-scope="scope">
              <el-input v-model="scope.row.workorderCode" disabled />
            </template>
          </el-table-column>
          <el-table-column :label="$t('system.创建时间')" prop="createTime" width="240" v-if="optType == 'view'">
            <template slot-scope="scope">
              <el-date-picker disabled clearable v-model="scope.row.createTime" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择创建时间')" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm"
          v-if="form.status == 'PREPARE' && optType != 'view'">{{ this.$t('common.save') }}</el-button>
        <el-button type="success" @click="handleConfirm('CONFIRMED')"
          v-if="form.status == 'PREPARE' && optType != 'view' && form.salesOrderId != null">{{ this.$t('lims.确认') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
        <el-button type="primary" @click="handleSubmit"
          v-if="form.status == 'CONFIRMED' && optType != 'view' && form.salesOrderId != null">{{ this.$t('mes.提交审批') }}</el-button>
        <el-button type="success" @click="handleExecute"
          v-if="form.status == 'APPROVING' && form.salesOrderId != null">{{ this.$t('mes.审批通过') }}</el-button>
        <el-button type="danger" @click="handleReject"
          v-if="form.status == 'APPROVING' && form.salesOrderId != null">{{ this.$t('mes.审批不通过') }}</el-button>
        <el-button type="primary" @click="handleConfirm('APPROVING')"
          v-if="form.status == 'REFUSE' && optType != 'view' && form.salesOrderId != null">{{ this.$t('mes.重新提交') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listOrder,
  getOrder,
  delOrder,
  addOrder,
  updateOrder,
  execute,
  pushMes,
  refuse,
  delOrderItem,
} from "@/api/mes/sales/order/order";
import ItemSelect from "@/components/itemSelect/item.vue";
import ClientSelect from "@/components/clientSelect/single.vue";
import { mapGetters } from "vuex";
import { genCode } from "@/api/system/autocode/rule";
import { totalListColor } from "@/api/color/color";


export default {
  name: "SalesOrder",
  dicts: [
    "order_type",
    "pay_up_type",
    "currency_type",
    "level",
    "mes_order_status",
  ],
  components: {
    ItemSelect,
    ClientSelect,
  },

  data() {
    return {
      //自动生成编码
      autoGenFlag: false,
      optType: undefined,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      //编码
      codes: [],
      //产品列id
      itemIds: [],
      // 子表选中数据
      checkedSalesOrderItem: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 销售订单表格数据
      orderList: [],
      // 销售订单产品列表格数据
      salesOrderItemList: [],
      // 颜色数据
      colorList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        salesOrderCode: null,
        salesOrderName: null,
        status: null,
        clientCode: null,
        clientPoCode: null,
        clientName: null,
        salesOrderDate: null,
        deliveryDate: null,
        salesOrderQuantity: null,
        currency: null,
        payUp: null,
        orderType: null,
        followerMan: null,
        salesMan: null,
        qualityRequirement: null,
      },
      // 当前选中行
      currentRow: undefined,
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        salesOrderCode: [
          { required: true, message: this.$t('mes.订单编码不能为空'), trigger: "blur" },
        ],
        clientId: [
          { required: true, message: this.$t('mes.客户id不能为空'), trigger: "blur" },
        ],
        clientName: [
          { required: true, message: this.$t('mes.客户名称不能为空'), trigger: "blur" },
        ],
        salesOrderDate: [
          { required: true, message: this.$t('mes.订货日期不能为空'), trigger: "blur" },
        ],
        deliveryDate: [
          { required: true, message: this.$t('mes.交货日期不能为空'), trigger: "blur" },
        ],
        orderType: [
          { required: true, message: this.$t('mes.订单类别不能为空'), trigger: "blur" },
        ],
      },
    };
  },
  computed: {
    ...mapGetters({
      measureOptions: "unitmeasureList",
    }),
  },
  created() {
    this.handleRouteChange();
    this.getList();

  },
  watch: {
    '$route'(to, from) {
      this.handleRouteChange();
    }
  },
  methods: {
    /** 查询颜色列表 */
    getColorList() {
      totalListColor().then((response) => {
        this.colorList = response.data;
      });
    },

    /** 查询销售订单列表 */
    getList() {
      this.loading = true;
      listOrder(this.queryParams).then((response) => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    handleSelectClient() {
      this.$refs.clientSelect.showFlag = true;
    },
    //客户选择弹出框
    onClientSelected(obj) {
      if (obj != undefined && obj != null) {
        this.form.clientId = obj.clientId;
        this.form.clientCode = obj.clientCode;
        this.form.clientName = obj.clientName;

        this.form.currency = obj.currency;
        this.form.payUp = obj.payUp;
        this.form.followerMan = obj.followerMan;
        this.form.salesMan = obj.salesMan;
      }
    },
    /**行单击 */
    handleRowClick(row, event, column) {
      this.currentRow = row;
    },
    //计算金额
    changeAmount(row) {
      if (
        (row.salesOrderQuantity != null ||
          row.salesOrderQuantity != undefined) &&
        (row.amount != null || row.amount != undefined)
      ) {
        row.totalAmount = row.amount * row.salesOrderQuantity;
      }
    },
    //色号
    changeColor(row) {
      row.colorCode = row.colorName
    },
    //物料选择弹出框
    onItemSelected(obj) {
      if (obj != undefined && obj != null) {
        // 先删除原有数据
        let index = this.salesOrderItemList.indexOf(this.currentRow);
        this.salesOrderItemList.splice(index, 1);
        this.currentRow.productId = obj.itemId;
        this.currentRow.productCode = obj.itemCode;
        this.currentRow.productName = obj.itemName;
        this.currentRow.quality = obj.quality;
        this.currentRow.unitOfMeasure = obj.unitOfMeasure;
        // 添加到表格
        this.salesOrderItemList.push(this.currentRow);
      }
    },
    handleSelectProduct() {
      this.$refs.itemSelect.showFlag = true;
    },
    // 表单重置
    reset() {
      this.form = {
        salesOrderId: null,
        salesOrderCode: null,
        salesOrderName: null,
        status: "PREPARE",
        clientId: null,
        clientCode: null,
        clientPoCode: null,
        clientName: null,
        salesOrderDate: null,
        deliveryDate: null,
        salesOrderQuantity: null,
        currency: null,
        payUp: null,
        orderType: null,
        followerMan: null,
        salesMan: null,
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
        remark: null,
      };
      this.autoGenFlag = false;
      this.salesOrderItemList = [];
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
      this.ids = selection.map((item) => item.salesOrderId);
      this.codes = selection.map((item) => item.salesOrderCode);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.getColorList();
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加销售订单');
      this.optType = "add";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const salesOrderId = row.salesOrderId || this.ids;
      getOrder(salesOrderId).then((response) => {
        this.form = response.data;
        this.salesOrderItemList = response.data.salesOrderItemList;
        this.open = true;
        this.title = this.$t('mes.修改销售订单');
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          this.form.salesOrderItemList = this.salesOrderItemList;
          if (this.form.salesOrderId != null) {
            updateOrder(this.form).then((response) => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addOrder(this.form).then((response) => {
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
      const salesOrderIds = row.salesOrderId || this.ids;
      const salesOrders = row.salesOrderCode || this.codes;
      this.$modal
        .confirm('是否确认删除销售订单编号为"' + salesOrders + '"的数据项？')
        .then(function () {
          return delOrder(salesOrderIds);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess(this.$t('system.删除成功'));
        })
        .catch(() => { });
    },
    // 确认/重新提交
    handleConfirm(status) {
      let that = this;
      that.$modal.confirm("是否确认？【确认后将不能更改】").then(function () {
        if (that.salesOrderItemList.length == 0) {
          return that.$modal.msgError("当前订单下没有产品！请先添加产品。");
        }
        if (status == "CONFIRMED") {
          refuse(that.form.salesOrderId).then((response) => {
            that.$modal.msgSuccess(response.msg);
            that.open = false;
            that.getList();
          });
        }
        if (status == "APPROVING") {
          that.handleSubmit();
        }
      });
    },
    //提交审批
    handleSubmit() {
      refuse(this.form.salesOrderId).then((response) => {
        this.$modal.msgSuccess(response.msg);
        this.open = false;
        this.getList();
      });
    },
    //审批通过执行
    handleExecute() {
      let that = this;
      const salesOrderId = that.form.salesOrderId;
      that.$modal
        .confirm("通过后会生成生产工单,是否确认通过？")
        .then(function () {
          execute(salesOrderId).then((response) => {
            that.$modal.msgSuccess(response.msg);
            that.open = false;
            that.getList();
          });
        });
    },
    //执行推送
    puMes(row) {
      let that = this;
      const salesOrderId = row.salesOrderId;
      that.$modal
        .confirm("请确认工单数量,是否确认推送生成MES工单？")
        .then(function () {
          pushMes(salesOrderId).then((response) => {
            that.$modal.msgSuccess(response.msg);
            that.open = false;
            that.getList();
          });
        });
    },
    //审批不通过，拒绝
    handleReject() {
      refuse(this.form.salesOrderId).then((response) => {
        this.$modal.msgSuccess(response.msg);
        this.open = false;
        this.getList();
      });
    },
    /** 销售订单产品列序号 */
    rowSalesOrderItemIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 销售订单产品列添加按钮操作 */
    handleAddSalesOrderItem() {
      if (this.form.clientId == null || this.form.clientId == undefined) {
        this.$modal.msgError(this.$t('mes.请先选择客户'));
        return;
      }
      let obj = {};
      obj.salesOrderItemId = "";
      (obj.clientId = this.form.clientId),
        (obj.clientCode = this.form.clientCode),
        (obj.clientName = this.form.clientName),
        (obj.productId = "");
      obj.productCode = "";
      obj.productName = "";
      obj.productSpec = "";
      obj.quality = "";
      obj.colorCode = "";
      obj.colorName = "";
      obj.unitOfMeasure = "";
      obj.salesOrderQuantity = "";
      obj.deliveryDate = "";
      obj.clientProductName = "";
      obj.stockNum = "";
      obj.saleQty = "";
      obj.saleThqty = "";
      obj.saleSgqty = "";
      obj.amount = "";
      obj.totalAmount = "";
      obj.oweQty = "";
      obj.remark = "";
      obj.workorderId = "";
      obj.createBy = "";
      obj.createTime = "";
      obj.updateBy = "";
      this.salesOrderItemList.push(obj);
    },
    /** 销售订单产品列删除按钮操作 */
    handleDeleteSalesOrderItem() {
      if (this.checkedSalesOrderItem.length == 0) {
        this.$modal.msgError(this.$t('mes.请先选择要删除的销售订单产品列数据'));
      } else {
        const salesOrderItemList = this.salesOrderItemList;
        const checkedSalesOrderItem = this.checkedSalesOrderItem;
        // if(this.itemIds.length>0){
        //   const ids=this.ids.map((item)=>item!=undefined)
        //   if(ids.length>0){
        //     delOrderItem(ids).then((response)=>{
        //    this.$modal.msgSuccess(response.msg);
        //    })
        //   }
        // }
        this.salesOrderItemList = salesOrderItemList.filter(function (item) {
          return checkedSalesOrderItem.indexOf(item.index) == -1;
        });
      }

    },
    /** 复选框选中数据 */
    handleSalesOrderItemSelectionChange(selection) {
      this.checkedSalesOrderItem = selection.map((item) => item.index);
      this.itemIds = [];
      for (let i = 0; i < selection.length; i++) {
        const item = selection[i];
        if (item.salesOrderItemId !== null && item.salesOrderItemId !== undefined && item.salesOrderItemId !== '') {
          this.itemIds.push(item.salesOrderItemId);
        }
      }
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "md/order/export",
        {
          ...this.queryParams,
        },
        `order_${new Date().getTime()}.xlsx`
      );
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag) {
      if (autoGenFlag) {
        genCode("SALES_ORDER_CODE").then((response) => {
          this.form.salesOrderCode = response;
        });
      } else {
        this.form.salesOrderCode = null;
      }
    },
    // 查询明细按钮操作
    handleView(row) {
      this.reset();
      const salesOrderId = row.salesOrderId || this.ids;
      getOrder(salesOrderId).then((response) => {
        this.form = response.data;
        this.salesOrderItemList = response.data.salesOrderItemList;
        this.open = true;
        this.title = this.$t('menu.销售订单详情');
        this.optType = "view";
      });
    },
    //动态加载数据
    handleRouteChange() {
      this.optType = this.$route.params.optType;
      const salesOrderId = this.$route.params.salesOrderId;
      if (this.optType === "view") {
        this.reset();
        getOrder(salesOrderId).then((response) => {
          this.form = response.data;
          this.salesOrderItemList = response.data.salesOrderItemList;
          this.open = true;
          this.title = this.$t('menu.销售订单详情');
          this.optType = "view";
        });
      }
    },
    activated() {
      this.optType = this.$route.params.optType
      this.handleRouteChange();
    },
  },
};
</script>
