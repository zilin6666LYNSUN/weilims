<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('mes.失败时间')" prop="failureTime">
        <el-date-picker clearable v-model="queryParams.failureTime" type="date" value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择失败时间')">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wm:wmlogfailure:export']">{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="wmLogFailureList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column :label="$t('mes.失败记录ID')" align="center" prop="failureId" /> -->
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline >
            <el-form-item :label="$t('mes.日志写入失败原因')">
              <span>{{ props.row.failureReason }}</span>
            </el-form-item>
            <el-form-item :label="$t('mes.失败时的原始日志数据')">
              <span>{{ props.row.logData }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.日志写入失败原因')" show-overflow-tooltip="true" align="center" prop="failureReason" />
      <el-table-column :label="$t('mes.失败时间')" align="center" prop="failureTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.failureTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.失败时的原始日志数据')" show-overflow-tooltip="true" align="center" prop="logData" />
      <!-- <el-table-column :label="$t('mes.创建人ID')" align="center" prop="createUserId" /> -->
      <el-table-column :label="$t('mes.创建人')" align="center" prop="createBy" />
      <el-table-column :label="$t('system.创建时间')" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.更新人')" align="center" prop="updateBy" />
      <el-table-column :label="$t('mes.更新时间')" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script>
import { listWmLogFailure, getWmLogFailure } from "@/api/mes/wm/wm-log-failure";

export default {
  name: "WmLogFailure",
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
      // 日志写入失败记录表格数据
      wmLogFailureList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        failureTime: null,
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
    /** 查询日志写入失败记录列表 */
    getList() {
      this.loading = true;
      listWmLogFailure(this.queryParams).then(response => {
        this.wmLogFailureList = response.rows;
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
        failureId: null, logId: null, materialStockId: null, failureReason: null, failureTime: null, logData: null, createUserId: null, createBy: null, createTime: null, updateUserId: null, updateBy: null, updateTime: null, remark: null, deleted: null, deleteat: null, version: null
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
      this.ids = selection.map(item => item.failureId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wm/wm-log-failure/export', {
        ...this.queryParams
      }, `wm-log-failure_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
