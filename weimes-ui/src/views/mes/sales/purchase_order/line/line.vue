<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('mes.明细单号')" prop="poOrderLineNo">
        <el-input
          v-model="queryParams.poOrderLineNo"
          :placeholder="$t('mes.请输入明细单号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.采购单号')" prop="poOrderNo">
        <el-input
          v-model="queryParams.poOrderNo"
          :placeholder="$t('lims.请输入采购单号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.物料编号')" prop="productNo">
        <el-input
          v-model="queryParams.productNo"
          :placeholder="$t('mes.请输入物料编号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.物料名称')" prop="productName">
        <el-input
          v-model="queryParams.productName"
          :placeholder="$t('lims.请输入物料名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.色号')" prop="colorNo">
        <el-input
          v-model="queryParams.colorNo"
          :placeholder="$t('mes.请输入色号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.颜色名称')" prop="colorName">
        <el-input
          v-model="queryParams.colorName"
          :placeholder="$t('mes.请输入颜色名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.单位')" prop="unit">
        <el-input
          v-model="queryParams.unit"
          :placeholder="$t('lims.请输入单位')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.数量')" prop="qty">
        <el-input
          v-model="queryParams.qty"
          :placeholder="$t('mes.请输入数量')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.重量')" prop="weight">
        <el-input
          v-model="queryParams.weight"
          :placeholder="$t('mes.请输入重量')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.单价')" prop="price">
        <el-input
          v-model="queryParams.price"
          :placeholder="$t('mes.请输入单价')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.折扣')" prop="discount">
        <el-input
          v-model="queryParams.discount"
          :placeholder="$t('mes.请输入折扣')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.折扣总价')" prop="addMoney">
        <el-input
          v-model="queryParams.addMoney"
          :placeholder="$t('mes.请输入折扣总价')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.总价')" prop="money">
        <el-input
          v-model="queryParams.money"
          :placeholder="$t('mes.请输入总价')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.送货日期')" prop="tranDate">
        <el-date-picker clearable
          v-model="queryParams.tranDate"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择送货日期')">
        </el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.收货数量')" prop="shQty">
        <el-input
          v-model="queryParams.shQty"
          :placeholder="$t('mes.请输入收货数量')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.退货数量')" prop="thQty">
        <el-input
          v-model="queryParams.thQty"
          :placeholder="$t('mes.请输入退货数量')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.入库数量')" prop="inQty">
        <el-input
          v-model="queryParams.inQty"
          :placeholder="$t('mes.请输入入库数量')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('system.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('system.请选择状态')" clearable>
          <el-option :label="$t('mes.请选择字典生成')" value="" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.结账方式')" prop="payUp">
        <el-input
          v-model="queryParams.payUp"
          :placeholder="$t('mes.请输入结账方式')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.客户代号')" prop="cusNo">
        <el-input
          v-model="queryParams.cusNo"
          :placeholder="$t('mes.请输入客户代号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.实收数量')" prop="ssQty">
        <el-input
          v-model="queryParams.ssQty"
          :placeholder="$t('mes.请输入实收数量')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.逻辑删除字段 0:未删除 1:已删除')" prop="deleted">
        <el-select v-model="queryParams.deleted" :placeholder="$t('mes.请选择逻辑删除字段 0:未删除 1:已删除')" clearable>
          <el-option :label="$t('mes.请选择字典生成')" value="" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.乐观锁')" prop="version">
        <el-input
          v-model="queryParams.version"
          :placeholder="$t('mes.请输入乐观锁')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.总重量')" prop="totalWeight">
        <el-input
          v-model="queryParams.totalWeight"
          :placeholder="$t('mes.请输入总重量')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
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
          v-hasPermi="['purchase:purchase_line:add']"
        >{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['purchase:purchase_line:edit']"
        >{{ this.$t('system.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['purchase:purchase_line:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['purchase:purchase_line:export']"
        >{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="purchase_lineList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column :label="$t('mes.明细单号')" align="center" prop="poOrderLineNo" />
      <el-table-column :label="$t('lims.采购单号')" align="center" prop="poOrderNo" />
      <el-table-column :label="$t('mes.物料编号')" align="center" prop="productNo" />
      <el-table-column :label="$t('lims.物料名称')" align="center" prop="productName" />
      <el-table-column :label="$t('mes.色号')" align="center" prop="colorNo" />
      <el-table-column :label="$t('mes.颜色名称')" align="center" prop="colorName" />
      <el-table-column :label="$t('lims.单位')" align="center" prop="unit" />
      <el-table-column :label="$t('lims.数量')" align="center" prop="qty" />
      <el-table-column :label="$t('mes.重量')" align="center" prop="weight" />
      <el-table-column :label="$t('lims.单价')" align="center" prop="price" />
      <el-table-column :label="$t('mes.折扣')" align="center" prop="discount" />
      <el-table-column :label="$t('mes.折扣总价')" align="center" prop="addMoney" />
      <el-table-column :label="$t('mes.总价')" align="center" prop="money" />
      <el-table-column :label="$t('mes.送货日期')" align="center" prop="tranDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.tranDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.备注')" align="center" prop="memo" />
      <el-table-column :label="$t('mes.收货数量')" align="center" prop="shQty" />
      <el-table-column :label="$t('mes.退货数量')" align="center" prop="thQty" />
      <el-table-column :label="$t('lims.入库数量')" align="center" prop="inQty" />
      <el-table-column :label="$t('system.状态')" align="center" prop="status" />
      <el-table-column :label="$t('mes.结账方式')" align="center" prop="payUp" />
      <el-table-column :label="$t('mes.客户代号')" align="center" prop="cusNo" />
      <el-table-column :label="$t('mes.实收数量')" align="center" prop="ssQty" />
      <el-table-column :label="$t('mes.逻辑删除字段 0:未删除 1:已删除')" align="center" prop="deleted" />
      <el-table-column :label="$t('mes.乐观锁')" align="center" prop="version" />
      <el-table-column :label="$t('mes.总重量')" align="center" prop="totalWeight" />
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['purchase:purchase_line:edit']"
          >{{ this.$t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['purchase:purchase_line:remove']"
          >{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改采购订单列表对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item :label="$t('mes.明细单号')" prop="poOrderLineNo">
          <el-input v-model="form.poOrderLineNo" :placeholder="$t('mes.请输入明细单号')" />
        </el-form-item>
        <el-form-item :label="$t('lims.采购单号')" prop="poOrderNo">
          <el-input v-model="form.poOrderNo" :placeholder="$t('lims.请输入采购单号')" />
        </el-form-item>
        <el-form-item :label="$t('mes.物料编号')" prop="productNo">
          <el-input v-model="form.productNo" :placeholder="$t('mes.请输入物料编号')" />
        </el-form-item>
        <el-form-item :label="$t('lims.物料名称')" prop="productName">
          <el-input v-model="form.productName" :placeholder="$t('lims.请输入物料名称')" />
        </el-form-item>
        <el-form-item :label="$t('mes.色号')" prop="colorNo">
          <el-input v-model="form.colorNo" :placeholder="$t('mes.请输入色号')" />
        </el-form-item>
        <el-form-item :label="$t('mes.颜色名称')" prop="colorName">
          <el-input v-model="form.colorName" :placeholder="$t('mes.请输入颜色名称')" />
        </el-form-item>
        <el-form-item :label="$t('lims.单位')" prop="unit">
          <el-input v-model="form.unit" :placeholder="$t('lims.请输入单位')" />
        </el-form-item>
        <el-form-item :label="$t('lims.数量')" prop="qty">
          <el-input v-model="form.qty" :placeholder="$t('mes.请输入数量')" />
        </el-form-item>
        <el-form-item :label="$t('mes.重量')" prop="weight">
          <el-input v-model="form.weight" :placeholder="$t('mes.请输入重量')" />
        </el-form-item>
        <el-form-item :label="$t('lims.单价')" prop="price">
          <el-input v-model="form.price" :placeholder="$t('mes.请输入单价')" />
        </el-form-item>
        <el-form-item :label="$t('mes.折扣')" prop="discount">
          <el-input v-model="form.discount" :placeholder="$t('mes.请输入折扣')" />
        </el-form-item>
        <el-form-item :label="$t('mes.折扣总价')" prop="addMoney">
          <el-input v-model="form.addMoney" :placeholder="$t('mes.请输入折扣总价')" />
        </el-form-item>
        <el-form-item :label="$t('mes.总价')" prop="money">
          <el-input v-model="form.money" :placeholder="$t('mes.请输入总价')" />
        </el-form-item>
        <el-form-item :label="$t('mes.送货日期')" prop="tranDate">
          <el-date-picker clearable
            v-model="form.tranDate"
            type="date"
            value-format="yyyy-MM-dd"
            :placeholder="$t('mes.请选择送货日期')">
          </el-date-picker>
        </el-form-item>
        <el-form-item :label="$t('system.备注')" prop="memo">
          <el-input v-model="form.memo" type="textarea" :placeholder="$t('common.inputContent')" />
        </el-form-item>
        <el-form-item :label="$t('mes.收货数量')" prop="shQty">
          <el-input v-model="form.shQty" :placeholder="$t('mes.请输入收货数量')" />
        </el-form-item>
        <el-form-item :label="$t('mes.退货数量')" prop="thQty">
          <el-input v-model="form.thQty" :placeholder="$t('mes.请输入退货数量')" />
        </el-form-item>
        <el-form-item :label="$t('lims.入库数量')" prop="inQty">
          <el-input v-model="form.inQty" :placeholder="$t('mes.请输入入库数量')" />
        </el-form-item>
        <el-form-item :label="$t('system.状态')" prop="status">
          <el-select v-model="form.status" :placeholder="$t('system.请选择状态')">
            <el-option :label="$t('mes.请选择字典生成')" value="" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('mes.结账方式')" prop="payUp">
          <el-input v-model="form.payUp" :placeholder="$t('mes.请输入结账方式')" />
        </el-form-item>
        <el-form-item :label="$t('mes.客户代号')" prop="cusNo">
          <el-input v-model="form.cusNo" :placeholder="$t('mes.请输入客户代号')" />
        </el-form-item>
        <el-form-item :label="$t('mes.实收数量')" prop="ssQty">
          <el-input v-model="form.ssQty" :placeholder="$t('mes.请输入实收数量')" />
        </el-form-item>
        <el-form-item :label="$t('mes.逻辑删除字段 0:未删除 1:已删除')" prop="deleted">
          <el-select v-model="form.deleted" :placeholder="$t('mes.请选择逻辑删除字段 0:未删除 1:已删除')">
            <el-option :label="$t('mes.请选择字典生成')" value="" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('mes.乐观锁')" prop="version">
          <el-input v-model="form.version" :placeholder="$t('mes.请输入乐观锁')" />
        </el-form-item>
        <el-form-item :label="$t('mes.总重量')" prop="totalWeight">
          <el-input v-model="form.totalWeight" :placeholder="$t('mes.请输入总重量')" />
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
import { listPurchase_line, getPurchase_line, delPurchase_line, addPurchase_line, updatePurchase_line } from "@/api/mes/sales/purchase/purchase_line";

export default {
  name: "Purchase_line",
  data() {
    return {
      // 遮罩层
      loading: true,
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
      // 采购订单列表表格数据
      purchase_lineList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        poOrderLineNo: null,        poOrderNo: null,        productNo: null,        productName: null,        colorNo: null,        colorName: null,        unit: null,        qty: null,        weight: null,        price: null,        discount: null,        addMoney: null,        money: null,        tranDate: null,        memo: null,        shQty: null,        thQty: null,        inQty: null,        status: null,        payUp: null,        cusNo: null,        ssQty: null,        deleted: null,        version: null,        totalWeight: null      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productNo: [
          { required: true, message: this.$t('mes.物料编号不能为空'), trigger: "blur" }
        ],        qty: [
          { required: true, message: this.$t('mes.数量不能为空'), trigger: "blur" }
        ],        price: [
          { required: true, message: this.$t('mes.单价不能为空'), trigger: "blur" }
        ],        tranDate: [
          { required: true, message: this.$t('mes.送货日期不能为空'), trigger: "blur" }
        ],        status: [
          { required: true, message: this.$t('mes.状态不能为空'), trigger: "change" }
        ],      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询采购订单列表列表 */
    getList() {
      this.loading = true;
      listPurchase_line(this.queryParams).then(response => {
        this.purchase_lineList = response.rows;
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
        id: null,        poOrderLineNo: null,        poOrderNo: null,        productNo: null,        productName: null,        colorNo: null,        colorName: null,        unit: null,        qty: null,        weight: null,        price: null,        discount: null,        addMoney: null,        money: null,        tranDate: null,        memo: null,        shQty: null,        thQty: null,        inQty: null,        status: null,        payUp: null,        cusNo: null,        ssQty: null,        attr1: null,        attr2: null,        attr3: null,        attr4: null,        createBy: null,        createTime: null,        updateBy: null,        updateTime: null,        deleted: null,        deleteat: null,        version: null,        totalWeight: null      };
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加采购订单列表');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPurchase_line(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改采购订单列表');
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePurchase_line(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addPurchase_line(this.form).then(response => {
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
      this.$modal.confirm(this.$t('mes.是否确认删除采购订单列表编号为{v}的数据项？', { v: ids })).then(function() {
        return delPurchase_line(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('purchase/purchase_line/export', {
        ...this.queryParams
      }, `purchase_line_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
