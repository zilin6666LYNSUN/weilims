<template>
  <div :class="className" :style="{ height: height, width: width }">

  </div>
</template>

<script>
import echarts from 'echarts'
import { listWmstock, getWmstock, delWmstock, addWmstock, updateWmstock, materielCount } from "@/api/mes/wm/wmstock";
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'
import Result from '../mes/wm/stocktaking/result.vue';
import { create } from 'sortablejs';

const animationDuration = 6000

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    selectedValues: {
      type: String,
      default: ''
    },//物料编码
    dateTime: {
      type: String,
      default: null
    },//当前时间
  },
  data() {
    return {
      chart: null,
      // 查询参数
      queryParams: {
        itemCode: null,
        recptDate: null,
      },
      resultData: [],//饼状图最终数据
    }
  },
  watch: {
    dateTime(newVal) {
      this.initChart();
    },
    selectedValues(newVal) {
      this.initChart();
    }
  },
  created() {
    this.initChart();
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    //调用查询物料总量-大屏
    initChart() {
      //获取物料数量
      this.chart = echarts.init(this.$el, 'macarons');
      this.queryParams.recptDate = new Date();//初始化赋值时间 
      this.queryParams.itemCode = 'YCL00111';// 初始化赋值编号
      //实际值
      if (this.dateTime != null && this.dateTime != '') {
        this.queryParams.recptDate = this.dateTime
      }
      if (this.selectedValues != null && this.selectedValues != '') {
        this.queryParams.itemCode = this.selectedValues
      }

      //调取方法赋值饼状图内容
      materielCount(this.queryParams).then(response => {
        this.resultData = response.data.map((item) => ({
          value: item.quantity_onhand,
          name: item.warehouse_name
        }))
      })


      this.chart.setOption({
        title: {
          text: '物料信息',
          subtext: '数量',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '数量',
            type: 'pie',
            radius: '50%',
            data: this.resultData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
    }
  }
}
</script>
