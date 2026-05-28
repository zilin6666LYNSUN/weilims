<template>
  <el-dialog title="职级选择" v-if="showFlag" :visible.sync="showFlag" :modal=false width="80%" center>
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="职级编码" prop="rankCode">
        <el-input v-model="queryParams.rankCode" placeholder="请输入职级编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="职级层次" prop="rankType">
        <el-input v-model="queryParams.rankType" placeholder="请输入职级层次" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="职级层次名称" prop="rankName" label-width="110px">
        <el-input v-model="queryParams.rankName" placeholder="请输入职级层次名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="薪资范围" prop="salaryRangeMin">
        <el-input v-model="queryParams.salaryRangeMin" placeholder="请输入下限" clearable @keyup.enter.native="handleQuery"
          style="width: 120px;" /> ~
        <el-input style="width: 120px;" v-model="queryParams.salaryRangeMax" placeholder="请输入上限" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!--   <el-form-item label="薪资范围上限" prop="salaryRangeMax">
        
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="hrmRankList" @current-change="handleCurrent" @row-dblclick="handleRowDbClick">
      <el-table-column width="55" align="center">
        <template v-slot="scope">
          <el-radio v-model="selectedRankId" :label="scope.row.rankId"
            @change="handleRowChange(scope.row)">{{ "" }}</el-radio>
        </template>
      </el-table-column>
     <!--  <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="职级主键id" align="center" prop="rankId" v-if="false" />
      <el-table-column label="职级编码" align="center" prop="rankCode" />
      <el-table-column label="职级层次" align="center" prop="rankType" />
      <el-table-column label="职级层次名称" align="center" prop="rankName" />
      <el-table-column label="薪资范围（元）" align="center">
        <el-table-column label="薪资范围下限" align="center" prop="salaryRangeMin" />
        <el-table-column label="薪资范围上限" align="center" prop="salaryRangeMax" />
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="confirmSelect">确 定</el-button>
      <el-button @click="showFlag = false">取 消</el-button>
    </div>
  </el-dialog>

</template>
<script>
import { listHrmRank } from "@/api/hrm/md/hrm-rank";
export default {
  name: 'RankSelectSingle',
  components: {},
  data() {
    return {
      showFlag: false,
      // 遮罩层
      loading: true,
      // 选中数组
      selectedRankId: undefined,
      selectedRows: [],
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
      // 职级管理表格数据
      hrmRankList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        rankCode: null, rankType: null, rankName: null, salaryRangeMin: null, salaryRangeMax: null, createUserId: null, updateUserId: null, deleted: null, deleteat: null, version: null,
      },
      // 表单参数
      form: {},
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询生产工单列表 */
    getList() {
      this.loading = true;
      listHrmRank(this.queryParams).then(response => {
        this.hrmRankList = response.rows;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleCurrent(row) {
      if (row) {
        this.selectedRows = row;
      }
    },
    // 单选选中数据
    handleRowChange(row) {
      if (row) {
        this.selectedRows = row;
      }
    },
    //双击选中
    handleRowDbClick(row) {
      if (row) {
        this.selectedRows = row;
        this.$emit('onSelected', this.selectedRows);
        this.showFlag = false;
      }
    },
    //确定选中
    confirmSelect() {
      if (this.selectedRankId == null || this.selectedRankId == 0) {
        this.$notify({
          title: '提示',
          type: 'warning',
          message: '请至少选择一条数据!'
        });
        return;
      }
      this.$emit('onSelected', this.selectedRows);
      this.showFlag = false;
    }
  }
}
</script>