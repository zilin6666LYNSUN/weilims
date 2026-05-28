<template>
  <div class="receipt-order-print" ref="receiptOrderPrintRef" hidden="hidden">
    <div class="title">送货单</div>
    <div class="summary">
      <div class="col1">送货单号: {{ row.tranOrderCode }}</div>
      <div class="col1"></div>
      <div class="col1">客户:{{ row.clientName }}</div>
      <div class="col1">日期:{{ parseTime(row.tranDate, "{y}-{m}-{d}") }}</div>
    </div>
    <table class="common-table">
      <tr>
        <th>序号</th>
        <th>订单编码</th>
        <th>品名规格</th>
        <th>物料编码</th>
        <th>单位</th>
        <th>数量</th>
        <th>金额</th>
        <th>备注</th>
      </tr>
      <tr v-for="(it,index) in row.tranOrderLineList " align="center">
        <td>{{ index+1 }}</td>
        <td>{{ it.tranCode || "" }}</td>
        <td>{{ it.clientProductName || it.productName}}
          {{ it.clientSpec || it.productSpec}}</td>
        <td>{{ it.productCode || it.productName }}</td>
        <td>{{ it.unitOfMeasure || "" }}</td>
        <td>{{ it.saleSgqty || "" }}</td>
        <td v-if="$auth.hasPermi('sales:tranOrder:money')">{{ it.totalAmount || "" }}</td>
        <td v-else>{{ }}</td>
        <td>{{ it.remark || "" }}</td>
      </tr>
      <tr>
        <td>合计</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td>{{ row.total || ""}}</td>
        <td v-if="$auth.hasPermi('sales:tranOrder:money')">{{ row.totalAmount || "" }}</td>
        <td v-else>{{ }}</td>
        <td></td>
      </tr>
    </table>
    <div class="foot">
      <div class="col2">备注: {{ row.remark || ""}}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "tranPrint",
  props: ["row"],
  methods: {
    start() {
      this.$print(this.$refs.receiptOrderPrintRef, {}, "A4");
    },
  },
};
</script>

<style lang="stylus" media="print">
@page {
  size: auto;
  margin: 0;
}

@media print {
  * {
    color: #000 !important;
  }

  table {
    width: 100%;
    table-layout: fixed;
    border-collapse: collapse;
    border-spacing: 0;
  }

  table, tbody, thead {
    width: 100% !important;
  }

  .receipt-order-print {
    width: 100% !important;
    font-size: 14px;
  }

  table, table tr th, table tr td {
    border: 0.05rem solid #000;
    font-size: 12px;
  }
}

.receipt-order-print {
  padding: 12px;
  line-height: 1.8;

  .summary {
    display: flex;
    flex-wrap: wrap;

    .col1 {
      width: 50%;
    }
  }

  .title {
    font-size: 18px;
    text-align: center;
  }

  .common-table {
    td, th {
      border-color: black;
    }
  }
  
}
</style>
