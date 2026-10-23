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
      <el-form-item :label="$t('mes.跟单')" prop="followerMan">
        <el-input
          v-model="queryParams.followerMan"
          :placeholder="$t('mes.请输入跟单')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.业务员')" prop="busMan">
        <el-input
          v-model="queryParams.busMan"
          :placeholder="$t('mes.请输入业务员')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.送货类别')" prop="tranOrderType">
        <el-select
          v-model="queryParams.tranOrderType"
          :placeholder="$t('mes.请选择送货类别')"
          clearable
        >
          <el-option
            v-for="dict in dict.type.tran_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.送货日期')" prop="tranDate">
        <el-date-picker
          clearable
          v-model="queryParams.tranDate"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择送货日期')"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.送货单号')" prop="tranOrderCode">
        <el-input
          v-model="queryParams.tranOrderCode"
          :placeholder="$t('mes.请输入送货单号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.仓库名称')" prop="warehouseName">
        <el-input
          v-model="queryParams.warehouseName"
          :placeholder="$t('mes.请输入仓库名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.销售订单编码')" prop="salesOrderCode">
        <el-input
          v-model="queryParams.salesOrderCode"
          :placeholder="$t('mes.请输入销售订单编码')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item :label="$t('mes.产品编码')" prop="productCode">
        <el-input
          v-model="queryParams.productCode"
          :placeholder="$t('mes.请输入产品编码')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.产品名称')" prop="productName">
        <el-input
          v-model="queryParams.productName"
          :placeholder="$t('mes.请输入产品名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.质量等级')" prop="level">
        <el-select
          v-model="queryParams.level"
          :placeholder="$t('mes.请选择质量等级')"
          clearable
        >
          <el-option
            v-for="dict in dict.type.level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.颜色')" prop="colorName">
        <el-select v-model="queryParams.colorName" :placeholder="$t('mes.请选择颜色')">
          <el-option
            v-for="item in colorList"
            :key="item.colorCode"
            :label="item.colorName"
            :value="item.colorCode"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('lims.单位')" prop="unitOfMeasure">
        <el-select
          v-model="queryParams.unitOfMeasure"
          :placeholder="$t('mes.请选择单位')"
          clearable
        >
          <el-option :label="$t('mes.请选择字典生成')" value="" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.客户品名')" prop="clientProductName">
        <el-input
          v-model="queryParams.clientProductName"
          :placeholder="$t('mes.请输入客户品名')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.客户颜色')" prop="clientColor">
        <el-input
          v-model="queryParams.clientColor"
          :placeholder="$t('mes.请输入客户颜色')"
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
          <el-option
            v-for="dict in dict.type.mes_order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['md:line:add']"
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
          v-hasPermi="['md:line:edit']"
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
          v-hasPermi="['md:line:remove']"
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
          v-hasPermi="['md:line:export']"
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
      :data="lineList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />

      <el-table-column :label="$t('mes.送货单号')" align="center" prop="tranCode">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">{{
            scope.row.tranCode
          }}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.订单编码')" align="center" prop="salesOrderCode" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleTranOrderView(scope.row)">{{
             scope.row.tranOrderCode}}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.客户编号')" align="center" prop="clientCode" />
      <el-table-column :label="$t('mes.客户简称')" align="center" prop="clientName" />
      <el-table-column :label="$t('mes.跟单')" align="center" prop="followerMan" />
      <el-table-column :label="$t('mes.业务员')" align="center" prop="busMan" />
      <el-table-column :label="$t('mes.送货类别')" align="center" prop="tranOrderType">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.tran_type"
            :value="scope.row.tranOrderType"
          />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('mes.送货日期')"
        align="center"
        prop="tranDate"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.tranDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.仓库名称')" align="center" prop="warehouseName" />
      <el-table-column
        :label="$t('mes.销售订单编码')"
        align="center"
        prop="salesOrderCode"
      />
      <el-table-column :label="$t('mes.产品编码')" align="center" prop="productCode" />
      <el-table-column :label="$t('lims.产品名称')" align="center" prop="productName" />
      <el-table-column :label="$t('mes.产品规格')" align="center" prop="productSpec" />
      <el-table-column :label="$t('system.状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.mes_order_status"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.材质')" align="center" prop="quality" />
      <el-table-column :label="$t('mes.质量等级')" align="center" prop="level">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.level" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.颜色')" align="center" prop="colorName">
        <template slot-scope="scope">
          {{ convertColor(scope.row.colorName) }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.单位')" align="center" prop="unitOfMeasure" />
      <el-table-column :label="$t('mes.订单数')" align="center" prop="saleQty" />
      <el-table-column :label="$t('mes.备品数')" align="center" prop="saleThqty" />
      <el-table-column :label="$t('lims.单价')" align="center" prop="amount" />
      <el-table-column :label="$t('mes.出货数')" align="center" prop="saleSgqty" />
      <el-table-column :label="$t('mes.重量')" align="center" prop="weight" />
      <el-table-column :label="$t('mes.含税')" align="center" prop="tax" />
      <el-table-column :label="$t('mes.金额')" align="center" prop="totalAmount" />
      <el-table-column :label="$t('mes.附加费')" align="center" prop="extra" />
      <el-table-column :label="$t('system.备注')" align="center" prop="remark" />
      <el-table-column :label="$t('mes.件数')" align="center" prop="picNum" />
      <el-table-column label="sku" align="center" prop="sku" />
      <el-table-column :label="$t('mes.客户规格')" align="center" prop="clientSpec" />
      <el-table-column
        :label="$t('mes.客户品名')"
        align="center"
        prop="clientProductName"
      />
      <el-table-column :label="$t('mes.客户颜色')" align="center" prop="clientColor" />
      <el-table-column
        :label="$t('mes.生产工单编码')"
        align="center"
        prop="workorderCode"
      />

      <el-table-column :label="$t('system.创建者')" align="center" prop="createBy" />
      <el-table-column
        :label="$t('system.创建时间')"
        align="center"
        prop="createTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.更新者')" align="center" prop="updateBy" />
      <el-table-column
        :label="$t('mes.更新时间')"
        align="center"
        prop="updateTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
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
            v-hasPermi="['md:line:edit']"
            >{{ this.$t('system.修改') }}</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['md:line:remove']"
            >{{ this.$t('common.delete') }}</el-button
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

    <!-- 添加或修改销售送货单列对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item :label="$t('mes.跟单')" prop="followerMan">
          <el-input v-model="form.followerMan" :placeholder="$t('mes.请输入跟单')" />
        </el-form-item>
        <el-form-item :label="$t('mes.业务员')" prop="busMan">
          <el-input v-model="form.busMan" :placeholder="$t('mes.请输入业务员')" />
        </el-form-item>
        <el-form-item :label="$t('mes.送货类别')" prop="tranOrderType">
          <el-select v-model="form.tranOrderType" :placeholder="$t('mes.请选择送货类别')">
            <el-option
              v-for="dict in dict.type.tran_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('mes.送货日期')" prop="tranDate">
          <el-date-picker
            clearable
            v-model="form.tranDate"
            type="date"
            value-format="yyyy-MM-dd"
            :placeholder="$t('mes.请选择送货日期')"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item :label="$t('mes.送货单号')" prop="tranOrderCode">
          <el-input v-model="form.tranOrderCode" :placeholder="$t('mes.请输入送货单号')" />
        </el-form-item>

        <el-form-item :label="$t('mes.仓库编码')" prop="warehouseCode">
          <el-input v-model="form.warehouseCode" :placeholder="$t('mes.请输入仓库编码')" />
        </el-form-item>
        <el-form-item :label="$t('mes.仓库名称')" prop="warehouseName">
          <el-input v-model="form.warehouseName" :placeholder="$t('mes.请输入仓库名称')" />
        </el-form-item>

        <el-form-item :label="$t('mes.销售订单编码')" prop="salesOrderCode">
          <el-input
            v-model="form.salesOrderCode"
            :placeholder="$t('mes.请输入销售订单编码')"
          />
        </el-form-item>

        <el-form-item :label="$t('mes.产品编码')" prop="productCode">
          <el-input v-model="form.productCode" :placeholder="$t('mes.请输入产品编码')" />
        </el-form-item>
        <el-form-item :label="$t('lims.产品名称')" prop="productName">
          <el-input v-model="form.productName" :placeholder="$t('mes.请输入产品名称')" />
        </el-form-item>
        <el-form-item :label="$t('mes.产品规格')" prop="productSpec">
          <el-input
            v-model="form.productSpec"
            type="textarea"
            :placeholder="$t('common.inputContent')"
          />
        </el-form-item>
        <el-form-item :label="$t('mes.材质')" prop="quality">
          <el-input v-model="form.quality" :placeholder="$t('mes.请输入材质')" />
        </el-form-item>
        <el-form-item :label="$t('mes.质量等级')" prop="level">
          <el-select v-model="form.level" :placeholder="$t('mes.请选择质量等级')">
            <el-option
              v-for="dict in dict.type.level"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('mes.颜色')" prop="colorName">
          <el-select v-model="form.colorName" :placeholder="$t('mes.请选择颜色')">
            <el-option
              v-for="item in colorList"
              :key="item.colorCode"
              :label="item.colorName"
              :value="item.colorCode"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('lims.单位')" prop="unitOfMeasure">
          <el-select v-model="form.unitOfMeasure">
            <el-option
              v-for="item in measureOptions"
              :key="item.measureCode"
              :label="item.measureName"
              :value="item.measureCode"
              :disabled="item.enableFlag == 'N'"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('mes.订单数')" prop="saleQty">
          <el-input v-model="form.saleQty" :placeholder="$t('mes.请输入订单数')" />
        </el-form-item>
        <el-form-item :label="$t('mes.备品数')" prop="saleThqty">
          <el-input v-model="form.saleThqty" :placeholder="$t('mes.请输入备品数')" />
        </el-form-item>
        <el-form-item :label="$t('lims.单价')" prop="amount">
          <el-input v-model="form.amount" :placeholder="$t('mes.请输入单价')" />
        </el-form-item>
        <el-form-item :label="$t('mes.出货数')" prop="saleSgqty">
          <el-input v-model="form.saleSgqty" :placeholder="$t('mes.请输入出货数')" />
        </el-form-item>
        <el-form-item :label="$t('mes.重量')" prop="weight">
          <el-input v-model="form.weight" :placeholder="$t('mes.请输入重量')" />
        </el-form-item>
        <el-form-item :label="$t('mes.含税')" prop="tax">
          <el-input v-model="form.tax" :placeholder="$t('mes.请输入含税')" />
        </el-form-item>
        <el-form-item :label="$t('mes.金额')" prop="totalAmount">
          <el-input v-model="form.totalAmount" :placeholder="$t('mes.请输入金额')" />
        </el-form-item>
        <el-form-item :label="$t('mes.附加费')" prop="extra">
          <el-input v-model="form.extra" :placeholder="$t('mes.请输入附加费')" />
        </el-form-item>
        <el-form-item :label="$t('system.备注')" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :placeholder="$t('common.inputContent')"
          />
        </el-form-item>
        <el-form-item :label="$t('mes.件数')" prop="picNum">
          <el-input v-model="form.picNum" :placeholder="$t('mes.请输入件数')" />
        </el-form-item>
        <el-form-item label="sku" prop="sku">
          <el-input v-model="form.sku" :placeholder="$t('mes.请输入sku')" />
        </el-form-item>
        <el-form-item :label="$t('mes.客户规格')" prop="clientSpec">
          <el-input
            v-model="form.clientSpec"
            type="textarea"
            :placeholder="$t('common.inputContent')"
          />
        </el-form-item>
        <el-form-item :label="$t('mes.客户品名')" prop="clientProductName">
          <el-input
            v-model="form.clientProductName"
            :placeholder="$t('mes.请输入客户品名')"
          />
        </el-form-item>
        <el-form-item :label="$t('mes.客户颜色')" prop="clientColor">
          <el-input v-model="form.clientColor" :placeholder="$t('mes.请输入客户颜色')" />
        </el-form-item>
        <el-form-item :label="$t('mes.工单ID')" prop="workorderId">
          <el-input v-model="form.workorderId" :placeholder="$t('mes.请输入工单ID')" />
        </el-form-item>
        <el-form-item :label="$t('mes.生产工单编码')" prop="workorderCode">
          <el-input
            v-model="form.workorderCode"
            :placeholder="$t('mes.请输入生产工单编码')"
          />
        </el-form-item>
        <el-form-item :label="$t('system.状态')" prop="status" v-show="false">
          <el-select v-model="form.status" :placeholder="$t('system.请选择状态')">
            <el-option
              v-for="dict in dict.type.mes_order_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
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
  listLine,
  getLine,
  delLine,
  addLine,
  updateLine,
} from "@/api/mes/sales/tran/line";
import { totalListColor } from "@/api/color/color";
import { mapGetters } from "vuex";
export default {
  name: "TranLine",
  dicts: ["level", "tran_type", "mes_order_status"],
  computed: {
    ...mapGetters({
      measureOptions: "unitmeasureList",
    }),
  },
  data() {
    return {
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
      // 销售送货单列表格数据
      lineList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 颜色数据
      colorList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tranOrderLineId: null,
        tranOrderId: null,
        followerMan: null,
        busMan: null,
        tranOrderType: null,
        tranDate: null,
        tranOrderCode: null,
        warehouseId: null,
        warehouseCode: null,
        warehouseName: null,
        salesOrderId: null,
        salesOrderCode: null,
        productId: null,
        productCode: null,
        productName: null,
        productSpec: null,
        quality: null,
        level: null,
        colorName: null,
        unitOfMeasure: null,
        saleQty: null,
        saleThqty: null,
        amount: null,
        saleSgqty: null,
        weight: null,
        tax: null,
        totalAmount: null,
        extra: null,
        picNum: null,
        sku: null,
        clientSpec: null,
        clientProductName: null,
        clientColor: null,
        workorderCode: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        followerMan: [
          { required: true, message: this.$t('mes.跟单不能为空'), trigger: "blur" },
        ],
        busMan: [
          { required: true, message: this.$t('mes.业务员不能为空'), trigger: "blur" },
        ],
        tranOrderType: [
          { required: true, message: this.$t('mes.送货类别不能为空'), trigger: "change" },
        ],
        tranDate: [
          { required: true, message: this.$t('mes.送货日期不能为空'), trigger: "blur" },
        ],
        tranOrderCode: [
          { required: true, message: this.$t('mes.送货单号不能为空'), trigger: "blur" },
        ],
        warehouseCode: [
          { required: true, message: this.$t('mes.仓库编码不能为空'), trigger: "blur" },
        ],
        warehouseName: [
          { required: true, message: this.$t('mes.仓库名称不能为空'), trigger: "blur" },
        ],
        salesOrderCode: [
          { required: true, message: this.$t('mes.销售订单编码不能为空'), trigger: "blur" },
        ],
        productId: [
          { required: true, message: this.$t('mes.产品id不能为空'), trigger: "blur" },
        ],
        productCode: [
          { required: true, message: this.$t('mes.产品编码不能为空'), trigger: "blur" },
        ],
        productName: [
          { required: true, message: this.$t('mes.产品名称不能为空'), trigger: "blur" },
        ],
        colorName: [
          { required: true, message: this.$t('mes.颜色不能为空'), trigger: "blur" },
        ],
        unitOfMeasure: [
          { required: true, message: this.$t('mes.单位不能为空'), trigger: "change" },
        ],
      },
    };
  },
  created() {
    this.getList();
    this.getColorList();
  },
  methods: {
    /** 查询销售送货单列列表 */
    getList() {
      this.loading = true;
      listLine(this.queryParams).then((response) => {
        this.lineList = response.rows;
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
        tranOrderLineId: null,
        tranOrderId: null,
        followerMan: null,
        busMan: null,
        tranOrderType: null,
        tranDate: null,
        tranOrderCode: null,
        warehouseId: null,
        warehouseCode: null,
        warehouseName: null,
        salesOrderId: null,
        salesOrderCode: null,
        productId: null,
        productCode: null,
        productName: null,
        productSpec: null,
        quality: null,
        level: null,
        colorName: null,
        unitOfMeasure: null,
        saleQty: null,
        saleThqty: null,
        amount: null,
        saleSgqty: null,
        weight: null,
        tax: null,
        totalAmount: null,
        extra: null,
        remark: null,
        picNum: null,
        sku: null,
        clientSpec: null,
        clientProductName: null,
        clientColor: null,
        workorderId: null,
        workorderCode: null,
        status: null,
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
      this.ids = selection.map((item) => item.tranOrderLineId);
      this.codes= selection.map((item) => item.tranOrderCode);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加销售送货单列');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const tranOrderLineId = row.tranOrderLineId || this.ids;
      getLine(tranOrderLineId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改销售送货单列');
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
    // 查询明细按钮操作
    handleView(row) {
      this.reset();
      const tranOrderLineId = row.tranOrderLineId || this.ids;
      getLine(tranOrderLineId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.销售送货列详情');
        this.optType = "view";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.tranOrderLineId != null) {
            updateLine(this.form).then((response) => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addLine(this.form).then((response) => {
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
      const tranOrderLineIds = row.tranOrderLineId || this.ids;
      const tranOrderLineCodes = row.tranOrderCode || this.codes;
      this.$modal
        .confirm(
          '是否确认删除销售送货单号为"' + tranOrderLineCodes + '"的数据项？'
        )
        .then(function () {
          return delLine(tranOrderLineIds);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess(this.$t('system.删除成功'));
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "md/line/export",
        {
          ...this.queryParams,
        },
        `line_${new Date().getTime()}.xlsx`
      );
    },
     // 通用跳转函数
    jumpPage(optType, tranOrderId = null) {
    this.$router.push({
      name: 'TranOrder',
      params: {
        optType: optType,
        tranOrderId: tranOrderId,
      }
    });
    },
      // 查询主单按钮操作
    handleTranOrderView(row) {
      let tranOrderId = row.tranOrderId
      this.jumpPage('view', tranOrderId);
    },
  },
};
</script>
