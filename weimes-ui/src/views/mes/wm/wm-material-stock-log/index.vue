<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="编码" prop="itemCode">
        <el-input v-model="queryParams.itemCode" placeholder="请输入产品或物料编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="itemName">
        <el-input v-model="queryParams.itemName" placeholder="请输入产品或物料名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="变化类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="请选择变化类型" clearable>
          <el-option v-for="dict in dict.type.log_change_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="单据编号" prop="sourceDocCode">
        <el-input v-model="queryParams.sourceDocCode" placeholder="请输入单据编号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="单据名称" prop="sourceDocName">
        <el-input v-model="queryParams.sourceDocName" placeholder="请输入单据名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="单据类型" prop="sourceDocType">
        <el-select v-model="queryParams.sourceDocType" placeholder="请选择单据类型" clearable>
          <el-option v-for="dict in dict.type.source_doc_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="工单编号" prop="workorderCode">
        <el-input v-model="queryParams.workorderCode" placeholder="请输入生产工单编号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="工单名称" prop="workorderName">
        <el-input v-model="queryParams.workorderName" placeholder="请输入生产工单名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="操作人" prop="operationBy">
        <el-input v-model="queryParams.operationBy" placeholder="请输入操作人姓名" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="操作时间" prop="operationTime">
        <el-date-picker clearable v-model="queryParams.operationTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择操作时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wm:wmmaterialstocklog:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="wmMaterialStockLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="产品物料编码" align="center" prop="itemCode" />
      <el-table-column label="产品物料名称" align="center" prop="itemName" />
      <el-table-column label="变化类型" align="center" prop="changeType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.log_change_type" :value="scope.row.changeType" />
        </template>
      </el-table-column>
      <el-table-column label="变化前数量" align="center" prop="beforeQuantity" />
      <el-table-column label="变化后数量" align="center" prop="afterQuantity" />
      <el-table-column label="变化数量" align="center" prop="changeQuantity" />
      <el-table-column label="单据编号" align="center" prop="sourceDocCode" />
      <el-table-column label="单据名称" align="center" prop="sourceDocName" />
      <el-table-column label="单据类型" align="center" prop="sourceDocType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.source_doc_type" :value="scope.row.sourceDocType" />
        </template>
      </el-table-column>
      <el-table-column label="生产工单编号" align="center" prop="workorderCode" />
      <el-table-column label="生产工单名称" align="center" prop="workorderName" />
      <el-table-column label="仓库" align="center" prop="warehouseName" />
      <el-table-column label="库区" align="center" prop="locationName" />
      <el-table-column label="库位" align="center" prop="areaName" />
      <el-table-column label="操作人姓名" align="center" prop="operationBy" />
      <el-table-column label="操作时间" align="center" prop="operationTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.operationTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="变化描述" align="center" prop="description" /> -->
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

  </div>
</template>

<script>
import { listWmMaterialStockLog } from "@/api/mes/wm/wm-material-stock-log";

export default {
  name: "WmMaterialStockLog",
  dicts: ['source_doc_type', 'log_change_type'],
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
      // 库存变化日志表格数据
      wmMaterialStockLogList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        itemCode: null, itemName: null, changeType: null, sourceDocCode: null, sourceDocName: null, sourceDocType: null, workorderCode: null, workorderName: null, operationBy: null, operationTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询库存变化日志列表 */
    getList() {
      this.loading = true;
      listWmMaterialStockLog(this.queryParams).then(response => {
        this.wmMaterialStockLogList = response.rows;
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
        logId: null, materialStockId: null, itemId: null, itemCode: null, itemName: null, changeType: null, beforeQuantity: null, afterQuantity: null, changeQuantity: null, sourceDocId: null, sourceDocCode: null, sourceDocName: null, sourceDocType: null, sourceLineId: null, workorderId: null, workorderCode: null, workorderName: null, operationUserId: null, operationBy: null, operationTime: null, description: null, createUserId: null, createBy: null, createTime: null, updateUserId: null, updateBy: null, updateTime: null, remark: null, deleted: null, deleteat: null, version: null
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
      this.ids = selection.map(item => item.logId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wm/wm-material-stock-log/export', {
        ...this.queryParams
      }, `wm-material-stock-log_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
