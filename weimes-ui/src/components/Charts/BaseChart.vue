<template>
  <div :class="className" :style="{ height, width, position: 'relative' }">
    <!-- 加载状态 -->
    <div v-if="loading" class="chart-overlay">
      {{ this.$t('common.loading') }}
    </div>

    <!-- 无数据提示 -->
    <div v-else-if="noData" class="chart-overlay">
      {{ this.$t('common.noData') }}
    </div>
  </div>
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons')
import resize from '@/views/dashboard/mixins/resize.js'

export default {
  name: 'BaseChart',
  mixins: [resize],
  props: {
    className: { type: String, default: 'chart' },
    width: { type: String, default: '100%' },
    height: { type: String, default: '400px' },
    chartType: { type: String, default: 'line' },
    chartData: { type: Object, required: true },
    showLegend: { type: Boolean, default: true },
    showArea: { type: Boolean, default: false },
    smooth: { type: Boolean, default: true },
    loading: { type: Boolean, default: false }
  },
  data() {
    return {
      chart: null
    }
  },
  computed: {
    noData() {
      if (this.loading) return false
      const { chartType, chartData } = this
      if (chartType === 'pie') {
        return !chartData?.series?.[0]?.data?.length
      } else {
        return !chartData?.xAxisData?.length || !chartData?.series?.some(s => s.data?.length)
      }
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val);
      }
    },
    loading(val) {
      if (!val && !this.noData) {
        this.setOptions(this.chartData)
      }
    },
    '$i18n.locale'() {
      if (this.chart && !this.noData) {
        this.setOptions(this.chartData)
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      // if (!this.noData && !this.loading) {
        
      // }
    },
    setOptions(data) {
      const { chartType } = this
      if (!this.chart) return
      if (chartType === 'pie') {
        this.setPieChart(data)
      } else {
        this.setXYChart(data)
      }
    },
    setXYChart({ xAxisData = [], series = [] }) {
      const displayName = (name) => {
        const map = { '访问量': 'dashboard.visits' }
        return map[name] ? this.$t(map[name]) : name
      }
      const option = {
        tooltip: { trigger: 'axis' },
        legend: this.showLegend ? { data: series.map(s => displayName(s.name)) } : null,
        grid: { left: 10, right: 10, bottom: 20, top: 30, containLabel: true },
        xAxis: {
          type: 'category',
          data: xAxisData,
          boundaryGap: this.chartType === 'bar'
        },
        yAxis: { type: 'value' },
        series: series.map(s => ({
          name: displayName(s.name),
          type: this.chartType,
          data: s.data,
          smooth: this.chartType === 'line' ? this.smooth : false,
          animationDuration: 3000,
          itemStyle: {
            color: s.color,
            areaStyle: this.showArea && this.chartType === 'line'
              ? { opacity: 0.2, color: s.color }
              : undefined
          },
          lineStyle: { width: 2, color: s.color },
          symbol: 'circle',
          symbolSize: 6
        }))
      }

      this.chart.setOption(option)
    },
    setPieChart({ series = [] }) {
      const option = {
        tooltip: { trigger: 'item' },
        legend: this.showLegend ? { orient: 'vertical', left: 'left' } : null,
        series: [
          {
            name: series[0]?.name || '',
            type: 'pie',
            radius: '60%',
            center: ['50%', '50%'],
            data: series[0]?.data || [],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            itemStyle: {
              color: (params) => {
                return series[0]?.colors?.[params.dataIndex] || undefined
              }
            }
          }
        ]
      }

      this.chart.setOption(option)
    }
  }
}
</script>

<style scoped>
.chart-overlay {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 9;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 16px;
}
</style>