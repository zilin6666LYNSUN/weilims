<template>
  <el-dialog :title="itemOrProduct === 'product' ? '产品选择' : '物料选择'" v-if="showFlag" :visible.sync="showFlag"
    :close-on-click-modal="false" :modal=false width="80%" center>
    <el-row :gutter="20">
      <!--物料数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
          label-width="68px">
          <el-form-item :label="itemOrProduct === 'product' ? '产品编码' : '物料编码'" prop="itemCode">
            <el-input v-model="queryParams.itemCode" placeholder="请输入编码" clearable style="width: 240px"
              @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item :label="itemOrProduct === 'product' ? '产品名称' : '物料名称'" prop="itemName">
            <el-input v-model="queryParams.itemName" placeholder="请输入名称" clearable style="width: 240px"
              @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="itemList" highlight-current-row @current-change="handleCurrent">
          <el-table-column type="index" width="50">
          </el-table-column>
          <el-table-column :label="itemOrProduct === 'product' ? '产品编码' : '物料编码'" align="left" prop="itemCode"
            v-if="columns[0].visible">
            <template slot-scope="scope">
              <el-input align="center" type="text" v-model="scope.row.itemCode" v-show="scope.row.isEditor" />
              <span v-show="!scope.row.isEditor">{{ scope.row.itemCode }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="itemOrProduct === 'product' ? '产品名称' : '物料名称'" align="left" prop="itemName"
            v-if="columns[1].visible" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-input align="center" type="text" v-model="scope.row.itemName" v-show="scope.row.isEditor" />
              <span v-show="!scope.row.isEditor">{{ scope.row.itemName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="规格型号" align="left" prop="specification" v-if="columns[2].visible"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-input align="center" type="text" v-model="scope.row.specification" v-show="scope.row.isEditor" />
              <span v-show="!scope.row.isEditor">{{ scope.row.specification }}</span>
            </template>
          </el-table-column>
          <el-table-column label="单位" align="left" prop="unitOfMeasure" v-if="columns[3].visible"
            :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-select size="mini" v-model="scope.row.unitOfMeasure" placeholder="请选择单位" v-show="scope.row.isEditor">
                <el-option v-for="item in measureOptions" :key="item.measureCode" :label="item.measureName"
                  :value="item.measureCode" :disabled="item.enableFlag == 'N'"></el-option>
              </el-select>
              <el-select class="no-border-select" size="mini" disabled v-model="scope.row.unitOfMeasure"
                placeholder="请选择单位" v-show="!scope.row.isEditor">
                <el-option v-for="item in measureOptions" :key="item.measureCode" :label="item.measureName"
                  :value="item.measureCode" :disabled="item.enableFlag == 'N'"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" align="center">
            <template slot-scope="scope">
              <el-button type="text" icon="el-icon-edit" @click="edit(scope.row)"
                v-show="!scope.row.isEditor">编辑</el-button>
              <el-button type="text" icon="el-icon-delete" @click="del(scope.row)"
                v-show="!scope.row.isEditor">删除</el-button>
              <el-button type="text" icon="el-icon-add" @click="save(scope.row)"
                v-show="scope.row.isEditor">保存</el-button>
              <el-button type="text" icon="el-icon-add" @click="cancel(scope.row)"
                v-show="scope.row.isEditor">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>
    <el-row>
      <el-button @click="addRow()" :disabled="btnDisabled" type="primary" icon="CirclePlus">
        {{ itemOrProduct === 'product' ? '新增产品' : '新增物料' }}</el-button>
    </el-row>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="confirmSelect">确 定</el-button>
      <el-button @click="confirmCancel">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { listMdItemProduct, getMdItem, delMdItem, addItemProduct, updateMdItem } from "@/api/mes/md/mdItem";
import { genCode } from "@/api/system/autocode/rule"
import { mapGetters } from "vuex";

export default {
  name: "MdItemSingle",
  dicts: ['sys_yes_no', 'mes_item_product'],
  props: {
    itemOrProduct: {
      type: String,
      default: 'product'
    }
  },
  computed: {
    ...mapGetters({
      measureOptions: 'unitmeasureList'
    })
  },
  data() {
    return {
      showFlag: false,
      // 选中数组
      selectedItemId: undefined,
      currentRow: undefined,
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 物料产品表格数据
      itemList: null,
      // 部门树选项
      itemTypeOptions: undefined,
      // 部门名称
      itemTypeName: undefined,
      defaultProps: {
        children: "children",
        label: "label"
      },
      btnDisabled: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        itemCode: undefined,
        itemName: undefined,
        itemTypeId: 0
      },
      // 列信息
      columns: [
        { key: 0, label: `产品编码`, visible: true },
        { key: 1, label: `产品名称`, visible: true },
        { key: 2, label: `规格型号`, visible: true },
        { key: 3, label: `单位`, visible: true },
        { key: 4, label: `物料/产品`, visible: true },
        { key: 5, label: `物料分类`, visible: true },
        { key: 6, label: `创建时间`, visible: true }
      ]
    };
  },
  watch: {
    // 根据名称筛选分类树
    itemTypeName(val) {
      this.$refs.tree.filter(val);
    }
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询物料编码列表 */
    getList() {
      this.loading = true;
      listMdItemProduct(this.queryParams, this.itemOrProduct).then(response => {
        this.itemList = response.rows;
        this.itemList.forEach(element => {
          this.$set(element, 'isEditor', false)
        });
        this.total = response.total;
        this.loading = false;
      }
      );
    },
    addRow() {
      let editList = this.itemList.filter(item => {
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
      this.btnDisabled = true;
      let codeType = 'ITEM_CODE';
      // 如果当前是产品,那么生成时编码取产品编码规则
      if(this.itemOrProduct === 'product'){
        codeType = 'PRODUCT_CODE';
      }
      // 自动生成编码
      genCode(codeType).then(response => {
        var row = {
          itemId: undefined,
          itemCode: response,
          itemName: undefined,
          specification: undefined,
          unitOfMeasure: undefined,
          itemOrProduct: undefined,
          itemTypeId: undefined,
          itemTypeName: undefined,
          createTime: undefined,
          createBy: undefined,
          updateTime: undefined,
          updateBy: undefined,
          remark: undefined,
          isEditor: true
        };
        this.itemList.push(row);
      }).catch(() => { });

    },
    save(row) {
      if (row.itemId === undefined) {
        addItemProduct(row, this.itemOrProduct).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.getList();
          this.btnDisabled = false;
        });
      } else {
        updateMdItem(row).then(response => {
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
      this.$modal.confirm('确认删除数据项？').then(function () {
        return delMdItem(row.itemId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    cancel(row) {
      let editList = this.itemList.filter(item => {
        return item.isEditor === true
      });
      if (editList !== undefined) {
        this.$confirm('有未保存的数据,确定取消吗', "提示", { confirmButtonText: '确定', type: 'info' })
          .then(
            () => {
              this.btnDisabled = false;
              row.isEditor = false;
            }
          )
      }
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.itemTypeId = data.id;
      this.handleQuery();
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
    handleCurrent(row) {
      if (row === undefined || row.itemId === undefined) {
        return;
      }
      this.currentRow = row;
    },
    handleRowDbClick(row) {
      if (row === undefined || row.itemId === undefined) {
        this.$notify({
          title: '提示',
          type: 'warning',
          message: '所选数据不符合规则,请重新选择!'
        });
        return;
      }
      this.currentRow = row;
      this.$emit('onSelected', this.currentRow);
      this.clearEmptyData();
      this.showFlag = false;
    },
    // 单选选中数据
    handleRowChange(row) {
      if (row) {
        this.currentRow = row;
      }
    },
    //确定选中
    confirmSelect() {
      if (this.currentRow === undefined || this.currentRow.itemId === undefined) {
        this.$notify({
          title: '提示',
          type: 'warning',
          message: '请至少选择一条数据!'
        });
        return;
      }
      this.$emit('onSelected', this.currentRow);
      this.clearEmptyData();
      this.showFlag = false;
    },
    /** 取消按钮 */
    confirmCancel() {
      this.clearEmptyData();
      this.showFlag = false;
    },
    /** 清理表格中的空数据 */
    clearEmptyData() {
      let rows = this.itemList.filter(item => {
        return item.itemId !== undefined && item.itemId !== '' && item.itemId !== null;
      });
      this.itemList = rows;
    }
  }
};
</script>