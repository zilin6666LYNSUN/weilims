<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('mes.盘点单ID')" prop="takingId">
        <el-input
          v-model="queryParams.takingId"
          :placeholder="$t('mes.请输入盘点单ID')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.产品物料ID')" prop="itemId">
        <el-input
          v-model="queryParams.itemId"
          :placeholder="$t('mes.请输入产品物料ID')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.产品物料编码')" prop="itemCode">
        <el-input
          v-model="queryParams.itemCode"
          :placeholder="$t('mes.请输入产品物料编码')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.产品物料名称')" prop="itemName">
        <el-input
          v-model="queryParams.itemName"
          :placeholder="$t('mes.请输入产品物料名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.单位')" prop="unitOfMeasure">
        <el-input
          v-model="queryParams.unitOfMeasure"
          :placeholder="$t('lims.请输入单位')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.单位名称')" prop="unitName">
        <el-input
          v-model="queryParams.unitName"
          :placeholder="$t('mes.请输入单位名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.数量')" prop="quantity">
        <el-input
          v-model="queryParams.quantity"
          :placeholder="$t('mes.请输入数量')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.盘点数量')" prop="takingQuantity">
        <el-input
          v-model="queryParams.takingQuantity"
          :placeholder="$t('mes.请输入盘点数量')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.盘点状态')" prop="takingStatus">
        <el-select v-model="queryParams.takingStatus" :placeholder="$t('mes.请选择盘点状态')" clearable>
          <el-option :label="$t('mes.请选择字典生成')" value="" />
        </el-select>
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
          v-hasPermi="['mes:wm:stocktaking:add']"
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
          v-hasPermi="['mes:wm:stocktaking:edit']"
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
          v-hasPermi="['mes:wm:stocktaking:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['mes:wm:stocktaking:export']"
        >{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stocktakingresultList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.结果ID')" align="center" prop="resultId" />
      <el-table-column :label="$t('mes.盘点单ID')" align="center" prop="takingId" />
      <el-table-column :label="$t('mes.产品物料ID')" align="center" prop="itemId" />
      <el-table-column :label="$t('mes.产品物料编码')" align="center" prop="itemCode" />
      <el-table-column :label="$t('mes.产品物料名称')" align="center" prop="itemName" />
      <el-table-column :label="$t('lims.规格型号')" align="center" prop="specification" />
      <el-table-column :label="$t('lims.单位')" align="center" prop="unitOfMeasure" />
      <el-table-column :label="$t('mes.单位名称')" align="center" prop="unitName" />
      <el-table-column :label="$t('lims.数量')" align="center" prop="quantity" />
      <el-table-column :label="$t('mes.盘点数量')" align="center" prop="takingQuantity" />
      <el-table-column :label="$t('mes.盘点状态')" align="center" prop="takingStatus" />
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:wm:stocktaking:edit']"
          >{{ this.$t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:wm:stocktaking:remove']"
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

    <!-- 添加或修改库存盘点结果对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item :label="$t('mes.盘点单ID')" prop="takingId">
          <el-input v-model="form.takingId" :placeholder="$t('mes.请输入盘点单ID')" />
        </el-form-item>
        <el-form-item :label="$t('mes.产品物料ID')" prop="itemId">
          <el-input v-model="form.itemId" :placeholder="$t('mes.请输入产品物料ID')" />
        </el-form-item>
        <el-form-item :label="$t('mes.产品物料编码')" prop="itemCode">
          <el-input v-model="form.itemCode" :placeholder="$t('mes.请输入产品物料编码')" />
        </el-form-item>
        <el-form-item :label="$t('mes.产品物料名称')" prop="itemName">
          <el-input v-model="form.itemName" :placeholder="$t('mes.请输入产品物料名称')" />
        </el-form-item>
        <el-form-item :label="$t('lims.规格型号')" prop="specification">
          <el-input v-model="form.specification" type="textarea" :placeholder="$t('common.inputContent')" />
        </el-form-item>
        <el-form-item :label="$t('lims.单位')" prop="unitOfMeasure">
          <el-input v-model="form.unitOfMeasure" :placeholder="$t('lims.请输入单位')" />
        </el-form-item>
        <el-form-item :label="$t('mes.单位名称')" prop="unitName">
          <el-input v-model="form.unitName" :placeholder="$t('mes.请输入单位名称')" />
        </el-form-item>
        <el-form-item :label="$t('lims.数量')" prop="quantity">
          <el-input v-model="form.quantity" :placeholder="$t('mes.请输入数量')" />
        </el-form-item>
        <el-form-item :label="$t('mes.盘点数量')" prop="takingQuantity">
          <el-input v-model="form.takingQuantity" :placeholder="$t('mes.请输入盘点数量')" />
        </el-form-item>
        <el-form-item :label="$t('mes.盘点状态')">
          <el-radio-group v-model="form.takingStatus">
            <el-radio label="1">{{ this.$t('mes.请选择字典生成') }}</el-radio>
          </el-radio-group>
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
import { listStocktakingresult, getStocktakingresult, delStocktakingresult, addStocktakingresult, updateStocktakingresult } from "@/api/mes/wm/stocktakingresult";

export default {
  name: "Stocktakingresult",
  props:{
    optType: null,
    takingId: null,
  },
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
      // 库存盘点结果表格数据
      stocktakingresultList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        takingId: this.takingId,        itemId: null,        itemCode: null,        itemName: null,        specification: null,        unitOfMeasure: null,        unitName: null,        quantity: null,        takingQuantity: null,        takingStatus: null,      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        itemId: [
          { required: true, message: this.$t('mes.产品物料ID不能为空'), trigger: "blur" }
        ],        quantity: [
          { required: true, message: this.$t('mes.数量不能为空'), trigger: "blur" }
        ],        takingStatus: [
          { required: true, message: this.$t('mes.盘点状态不能为空'), trigger: "blur" }
        ],      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询库存盘点结果列表 */
    getList() {
      this.loading = true;
      listStocktakingresult(this.queryParams).then(response => {
        this.stocktakingresultList = response.rows;
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
        resultId: null,        takingId: null,        itemId: null,        itemCode: null,        itemName: null,        specification: null,        unitOfMeasure: null,        unitName: null,        quantity: null,        takingQuantity: null,        takingStatus: "0",        remark: null,        attr1: null,        attr2: null,        attr3: null,        attr4: null,        createBy: null,        createTime: null,        updateBy: null,        updateTime: null      };
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
      this.ids = selection.map(item => item.resultId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加库存盘点结果');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const resultId = row.resultId || this.ids
      getStocktakingresult(resultId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改库存盘点结果');
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.resultId != null) {
            updateStocktakingresult(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addStocktakingresult(this.form).then(response => {
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
      const resultIds = row.resultId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除库存盘点结果编号为{v}的数据项？', { v: resultIds })).then(function() {
        return delStocktakingresult(resultIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wm/stocktakingresult/export', {
        ...this.queryParams
      }, `stocktakingresult_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
