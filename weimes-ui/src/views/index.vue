<template>
  <div class="dashboard-editor-container">

    <panel-group @handleSetLineChartData="handleSetLineChartData" />
    <el-row>
      <BaseChart chartType="line" :chartData="lineData" :showArea="true" :showLegend="true" :loading="chartLoading" />
    </el-row>
    <!-- <el-row>
      <BaseChart chartType="bar" :chartData="barData" :loading="chartLoading" />
      <BaseChart chartType="pie" :chartData="pieData" :loading="chartLoading" />
    </el-row>
    <el-row style="background:#fff;padding:16px 16px 0; margin-bottom:32px;">
      <line-chart :chart-data="lineChartData" />
    </el-row> -->
    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <raddar-chart />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart />
        </div>
        <!-- 
          <div class="block">
            <span class="demonstration">请选择时间：</span>
            <el-date-picker v-model="dateTime" type="month" format="yyyy-MM" value-format="yyyy-MM" default-value
              class="item-date-picker" />
          </div>
          <div class="item-select-all">
            <el-select v-model="values" filterable placeholder="请选择物料" class="item-select" >
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </div>
          <MaterielPieChart :date-time="dateTime" :selected-values="values" /> -->
        <!-- <pie-chart /> -->
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart />
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="32" style="padding:16px 16px 0;margin-bottom:32px;">
      <el-card>
        <div slot="header" class="clearfix">
          <span>生产进度</span>
        </div>
        <el-table v-loading="loading" :data="workorderList" row-key="workorderId" default-expand-all
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
          <el-table-column label="工单编码" width="180" prop="workorderCode">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="handleView(scope.row)"
                v-hasPermi="['mes:pro:workorder:query']">{{ scope.row.workorderCode }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="订单编号" width="140" align="center" prop="sourceCode" />
          <el-table-column label="客户名称" align="center" prop="clientName" :show-overflow-tooltip="true" />
          <el-table-column label="产品编号" width="120" align="center" prop="productCode" />
          <el-table-column label="产品名称" width="250px" align="center" prop="productName" :show-overflow-tooltip="true" />
          <el-table-column label="规格型号" align="center" prop="productSpc" :show-overflow-tooltip="true" />
          <el-table-column label="单位" align="center" prop="unitOfMeasure">
            <template slot-scope="scope">
              {{ convertUnitmeasure(scope.row.unitOfMeasure) }}
            </template>
          </el-table-column>
          <el-table-column label="生产进度" align="center" width="200px">
            <template slot-scope="scope">
              <el-progress :text-inside="true" :stroke-width="20"
                :percentage="parseFloat(((scope.row.quantityProduced / scope.row.quantity * 100).toFixed(2)))"></el-progress>
            </template>
          </el-table-column>
          <el-table-column label="需求日期" align="center" prop="requestDate" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.requestDate, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-row>

  </div>
</template>

<script>
import { getLoginDataByTimeRange } from "@/api/system/dashboard.js"
import { listWorkorder } from "@/api/mes/pro/workorder";
import BarChart from './dashboard/BarChart';
import LineChart from './dashboard/LineChart';
import PanelGroup from './dashboard/PanelGroup';
import PieChart from './dashboard/PieChart';
import RaddarChart from './dashboard/RaddarChart';
import MaterielPieChart from './dashboard/MaterielPieChart';
import BaseChart from "@/components/Charts/BaseChart.vue";

const lineChartData = {
  newVisitis: {
    expectedData: [100, 120, 161, 134, 105, 160, 165],
    actualData: [120, 82, 91, 154, 162, 140, 145]
  },
  messages: {
    expectedData: [200, 192, 120, 144, 160, 130, 140],
    actualData: [180, 160, 151, 106, 145, 150, 130]
  },
  purchases: {
    expectedData: [80, 100, 121, 104, 105, 90, 100],
    actualData: [120, 90, 100, 138, 142, 130, 130]
  },
  shoppings: {
    expectedData: [130, 140, 141, 142, 145, 150, 160],
    actualData: [120, 82, 91, 154, 162, 140, 130]
  }
}

export default {
  name: 'Index',
  components: {
    PanelGroup,
    LineChart,
    RaddarChart,
    PieChart,
    BarChart,
    MaterielPieChart,
    BaseChart
  },
  data() {
    return {
      lineData: {
        // xAxisData: ['Mon', 'Tue', 'Wed', 'Thu'],
        // series: [
        //   { name: '访问量', data: [100, 200, 150, 300], color: '#1abc9c' }
        // ]
      },
      barData: {
        xAxisData: ['苹果', '香蕉', '橘子'],
        series: [
          { name: '销量', data: [30, 80, 45], color: '#3498db' }
        ]
      },
      pieData: {
        series: [
          {
            name: '访问来源',
            data: [
              { value: 1048, name: '搜索引擎' },
              { value: 735, name: '直接访问' },
              { value: 580, name: '邮件营销' },
              { value: 484, name: '联盟广告' },
              { value: 300, name: '视频广告' }
            ],
            colors: ['#5470C6', '#91CC75', '#FAC858', '#EE6666', '#73C0DE']
          }
        ]
      },
      chartLoading: false,
      loading: true,
      queryParams: { status: 'CONFIRMED' },
      workorderList: [],
      lineChartData: lineChartData.newVisitis,
      dateTime: null,//选择日期
      options: [],
      values: ''//下拉内容
    }
  },
  created() {
    this.getList();
    this.$store.dispatch('getUnitmeasureList');
    this.getLoginDataByTimeRange('week');
  },
  methods: {
    getList() {
      this.loading = true;
      listWorkorder(this.queryParams).then(response => {
        this.workorderList = response.rows;
        this.loading = false;
      });
    },
    handleSetLineChartData(type) {
      this.getLoginDataByTimeRange(type);
    },
    getLoginDataByTimeRange(type) {
      this.loading = true;
      getLoginDataByTimeRange(type).then(response => {
        this.loading = false;
        this.lineData = response.data;
        // this.lineData = {
        //   "series": [
        //     {
        //       "name": "访问量",
        //       "count": null,
        //       "data": [
        //         0,
        //         0,
        //         0,
        //         3,
        //         2,
        //         0,
        //         3
        //       ],
        //       "color": "#1abc9c"
        //     }
        //   ],
        //   "xaxisData": [
        //     "2025-04-04",
        //     "2025-04-05",
        //     "2025-04-06",
        //     "2025-04-07",
        //     "2025-04-08",
        //     "2025-04-09",
        //     "2025-04-10"
        //   ]
        // }
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

.item-select {
  //width: 130px;
}

.item-date-picker {
  position: relative;
  width: 114px;
}

.item-select-all {
  // position: relative;
  // float: right;
  margin-right: 0px;
}

@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
