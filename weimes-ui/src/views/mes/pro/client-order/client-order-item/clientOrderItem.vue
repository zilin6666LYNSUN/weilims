<template>
  <div class="app-container">
    <el-row>
      <el-table v-loading="loading" :data="clientOrderItemList" :highlight-current-row="true"
        @row-click="handleRowClick" :row-class-name="stockStatus">
        <el-table-column type="expand" width="25px">
          <template slot-scope="scope">
            <el-form label-position="left" inline>
              <el-form-item label="物料编码:">
                <span>{{ scope.row.itemCode }}</span>
              </el-form-item>
              <el-form-item label="物料名称:" style="margin-left: 20px;">
                <span>{{ scope.row.itemName }}</span>
              </el-form-item>
              <el-form-item label="物料规格:" style="margin-left: 20px;">
                <span>{{ scope.row.specification }}</span>
              </el-form-item>
              <el-form-item label="所在仓库:" style="margin-left: 20px;">
                <span>{{ scope.row.warehouseName }}</span>
              </el-form-item>
              <el-form-item label="仓库编码:" style="margin-left: 20px;">
                <span>{{ scope.row.warehouseCode }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column label="层级" align="center" prop="level">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.mes_item_level" :value="scope.row.level" />
          </template>
        </el-table-column>
        <el-table-column label="添加物料" align="center">
          <template slot-scope="scope">
            <el-button :disabled="scope.row.tableBtn" @click="handleSelectItem"
              icon="el-icon-circle-plus-outline"></el-button>
            <ItemSelect ref="itemSelect" itemOrProduct="item" @onSelected="onItemSelected"> </ItemSelect>
          </template>
        </el-table-column>
        <el-table-column label="物料名称" align="center" prop="itemName">
        </el-table-column>
        <el-table-column label="需求数量" align="center" prop="quantity">
          <template slot-scope="scope">
            <el-input align="center" type="text" v-model="scope.row.quantity" v-show="scope.row.isEditor" />
            <span v-show="!scope.row.isEditor">{{ scope.row.quantity }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存量" align="center" prop="quantityOnhand">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="top">
              <p align="center">{{ scope.row.quantityStatus }}</p>
              <div slot="reference" class="name-wrapper">
                <el-tag size="small">{{ scope.row.quantityOnhand }}</el-tag>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="单位" align="center" prop="unitOfMeasure">
          <template slot-scope="scope">
            {{ convertUnitmeasure(scope.row.unitOfMeasure) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" v-if="optType !== 'view'">
          <template slot-scope="scope">
            <el-button type="text" @click="edit(scope.row)" v-show="!scope.row.isEditor">编辑</el-button>
            <el-button type="text" @click="del(scope.row)" v-show="!scope.row.isEditor">删除</el-button>
            <el-button type="text" @click="save(scope.row)" v-show="scope.row.isEditor">保存</el-button>
            <el-button type="text" @click="cancel(scope.row)" v-show="scope.row.isEditor">取消</el-button>
          </template>

        </el-table-column>
      </el-table>
    </el-row>
    <el-row style="margin-top: 20px">
      <el-button @click="addRow()" :disabled="btnDisabled" type="primary" icon="CirclePlus"
        v-if="optType !== 'view'">新增物料</el-button>
    </el-row>
  </div>
</template>
<style>
.el-table .warning-row {
  background: rgb(253, 230, 233);
}
</style>
<script>
import { listClientOrderItem, getClientOrderItem, delClientOrderItem, addClientOrderItem, updateClientOrderItem, getClientOrderItemLevel } from "@/api/mes/pro/client-order-item";
import ItemSelect from "@/components/itemSelect/item.vue"

export default {
  name: "ClientOrderItem",
  components: {
    ItemSelect
  },
  dicts: ['mes_item_level'],
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
      // 客户订单材料表格数据
      clientOrderItemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      btnDisabled: false,
      // 当前选中行
      currentRow: undefined,
      // 表单参数
      form: {}
    };
  },
  props: {
    clientOrderId: undefined,
    optType: undefined
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询客户订单材料列表 */
    getList() {
      this.loading = true;
      this.queryParams.clientOrderId = this.clientOrderId;
      // this.queryParams.clientOrderId = '1787471074665963521';
      listClientOrderItem(this.queryParams).then(response => {
        this.clientOrderItemList = response.rows;
        this.total = response.total;
        this.clientOrderItemList.forEach(element => {
          this.$set(element, 'isEditor', false)
          this.$set(element, 'tableBtn', true)
        });
        this.btnDisabled = false;
        this.loading = false;
      });
    },
    /**库存状态改变行颜色 */
    stockStatus(data, rowIndex) {
      if (data.row.quantity > data.row.quantityOnhand) {
        data.row.quantityStatus = "库存不足";
        return "warning-row";
      } else if (this.isEmpty(data.row.quantityOnhand)) {
        data.row.quantityStatus = "";
      } else {
        data.row.quantityStatus = "库存充足";
      }
    },
    quantityChange(value) {
    },
    /**行单击 */
    handleRowClick(row, event, column) {
      this.currentRow = row;
    },
    /**选择物料 */
    handleSelectItem() {
      this.$refs.itemSelect.showFlag = true;
    },
    //物料选择弹出框
    onItemSelected(obj) {
      if (obj != undefined && obj != null) {
        // 先删除原有数据
        let index = this.clientOrderItemList.indexOf(this.currentRow);
        this.clientOrderItemList.splice(index, 1);
        this.currentRow.itemId = obj.itemId;
        this.currentRow.itemCode = obj.itemCode;
        this.currentRow.itemName = obj.itemName;
        this.currentRow.specification = obj.specification;
        this.currentRow.unitOfMeasure = obj.unitOfMeasure;
        this.currentRow.isEditor = true;
        this.currentRow.tableBtn = false;
        // 添加到表格
        this.clientOrderItemList.push(this.currentRow);
      }
    },
    /**添加行 */
    addRow() {
      let editList = this.clientOrderItemList.filter(item => {
        return item.isEditor === true
      });
      if (editList.length !== 0) {
        this.$notify({
          title: '提示',
          type: 'warning',
          message: '每次只能修改一条数据,请保存后再添加'
        });
        return;
      }
      // 判空校验
      // if (this.isEmpty(this.clientOrderId)) {
      //   this.$notify({
      //     title: '提示',
      //     type: 'warning',
      //     message: '参数有误,请联系管理员'
      //   });
      //   return;
      // }
      // 只能添加四层校验 
      let isExist = this.clientOrderItemList.find(item => {
        return item.level === '4'
      });
      if (this.isNotEmpty(isExist)) {
        this.$notify({
          title: '提示',
          type: 'warning',
          message: '当前只支持添加数据到四层'
        });
        return;
      }

      this.btnDisabled = true
      getClientOrderItemLevel(this.clientOrderId).then(response => {

        // getClientOrderItemLevel('1787471074665963521').then(response => {
        let emptyRow = {
          level: response.data,
          orderItemId: undefined,
          clientOrderId: undefined,
          itemId: undefined,
          itemOrProduct: undefined,
          quantity: undefined,
          unitOfMeasure: undefined,
          isEditor: false,
          tableBtn: false
        };
        this.clientOrderItemList.push(emptyRow);
      })
    },
    save(row) {
      if (row.orderItemId === undefined) {
        row.clientOrderId = this.clientOrderId;
        // row.clientOrderId = '1787471074665963521';
        addClientOrderItem(row).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.getList();
          this.btnDisabled = false;
        });
      } else {
        updateClientOrderItem(row).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.getList();
          this.btnDisabled = false;
        });
      }
    },
    edit(row) {
      row.isEditor = true;
    },
    del(row) {
      // 为空代表还没有入库的数据,直接刷新
      if (this.isEmpty(row.orderItemId)) {
        this.getList();
        return;
      }
      this.$modal.confirm('是否确认删除物料"' + row.itemName + '"的数据项？').then(function () {
        return delClientOrderItem(row.orderItemId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    cancel(row) {
      let editList = this.clientOrderItemList.filter(item => {
        return item.isEditor === true
      });
      if (editList !== undefined) {
        this.$confirm('有未保存的数据,确定取消吗', "提示", { confirmButtonText: '确定', type: 'info' })
          .then(
            () => {
              this.btnDisabled = false;
              this.getList();
            }
          )
      }
    }
  }
};
</script>
