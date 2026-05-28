<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="物料分类" prop="itemType">
        <el-select v-model="queryParams.itemTypeId" placeholder="请选择物料分类" clearable filterable>
          <el-option v-for="item in itemTypeList" :key="item.itemTypeId" :label="item.itemTypeName"
            :value="item.itemTypeId" />
        </el-select>
      </el-form-item>
      <el-form-item label="计划编号" prop="planCode">
        <el-input v-model="queryParams.planCode" placeholder="请输入计划编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="计划名称" prop="planName">
        <el-input v-model="queryParams.planName" placeholder="请输入计划名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker clearable v-model="queryParams.startDate" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择开始日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker clearable v-model="queryParams.endDate" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择结束日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>


  </div>
</template>

<script>
import { getStock, getItemType } from "@/api/mes/analytics/analytics";
export default {
  name: "AnalyticsStock",
  components: {},
  data() {
    return {
      // 查询参数
      queryParams: {
        itemTypeId:''
      },
      itemTypeList: [],
      showSearch: true
    };
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      this.getList();
      this.getItemTypeList();
    },
    /** 查询排班计划列表 */
    getList() {
      this.loading = true;
      getStock(this.queryParams).then(response => {
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    getItemTypeList() {
      getItemType().then(res => {
        this.itemTypeList = res.data
        this.itemTypeList.unshift({ itemTypeId: '', itemTypeName: '全部' })
      })
    }
  }
};
</script>
