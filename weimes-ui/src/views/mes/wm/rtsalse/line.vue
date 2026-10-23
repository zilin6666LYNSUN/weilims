<template>
  <div class="app-container">
    <el-row v-if="optType != 'view'" :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['mes:wm:rtsalse:add']"
        >{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['mes:wm:rtsalse:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="rtsalselineList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />   
      <el-table-column :label="$t('mes.产品编码')" width="120" align="center" prop="itemCode" />
      <el-table-column :label="$t('lims.产品名称')" width="150" align="center" prop="itemName" />
      <el-table-column :label="$t('lims.规格型号')" width="150" align="center" prop="specification" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('lims.单位')" align="center" prop="unitOfMeasure" />
      <el-table-column :label="$t('mes.退货数量')" align="center" prop="quantityRted" />
      <el-table-column :label="$t('lims.批次号')" align="center" prop="batchCode" />
      <el-table-column :label="$t('mes.仓库')" align="center" prop="warehouseName" />
      <el-table-column :label="$t('mes.库区')" align="center" prop="locationName" />
      <el-table-column :label="$t('mes.库位')" align="center" prop="areaName" />
      <el-table-column :label="$t('mes.有效期')" align="center" prop="expireDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expireDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.备注')" align="center" prop="remark" />
      <el-table-column :label="$t('common.operation')" align="center" v-if="optType != 'view'" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:wm:rtsalse:edit']"
          >{{ this.$t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:wm:rtsalse:remove']"
          >{{ this.$t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改产品销售退货行对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">    
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.产品编码')"  prop="itemCode">
              <el-input v-model="form.itemCode" readonly="readonly" :placeholder="$t('mes.请选择产品')" >
                <el-button slot="append" @click="handleSelectProduct" icon="el-icon-search"></el-button>
              </el-input>
            </el-form-item>
            <ItemSelect ref="itemSelect" @onSelected="onItemSelected" > </ItemSelect>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.产品名称')" prop="itemName">
              <el-input v-model="form.itemName" :placeholder="$t('mes.请输入产品物料名称')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.单位')" prop="unitOfMeasure">
              <el-input v-model="form.unitOfMeasure" :placeholder="$t('lims.请输入单位')" />
            </el-form-item>
          </el-col>
        </el-row>   
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('lims.规格型号')" prop="specification">
              <el-input v-model="form.specification" type="textarea" :placeholder="$t('common.inputContent')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.退货数量')" prop="quantityRted">
              <el-input v-model="form.quantityRted" :placeholder="$t('mes.请输入退货数量')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.批次号')" prop="batchCode">
              <el-input v-model="form.batchCode" :placeholder="$t('lims.请输入批次号')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.入库仓库')" prop="warehouseId">
              <el-cascader v-model="warehouseInfo"
                :options="warehouseOptions"
                :props="warehouseProps"
                @change="handleWarehouseChanged"
              >                  
              </el-cascader>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{ this.$t('system.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRtsalseline, getRtsalseline, delRtsalseline, addRtsalseline, updateRtsalseline } from "@/api/mes/wm/rtsalseline";
import ItemSelect  from "@/components/itemSelect/single.vue";
import {getTreeList} from "@/api/mes/wm/warehouse"
export default {
  name: "Rtsalseline",
  components: {ItemSelect},
  props: {
    rtId: null,
    optType: null,
  },
  data() {
    return {
      warehouseInfo:[],
      warehouseOptions:[],
      warehouseProps:{
        multiple: false,
        value: 'pId',
        label: 'pName',
        checkStrictly: true
      },
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
      // 产品销售退货行表格数据
      rtsalselineList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        rtId: this.rtId,
        itemId: null,
        itemCode: null,
        itemName: null,
        specification: null,
        unitOfMeasure: null,
        quantityRted: null,
        batchCode: null,
        warehouseId: null,
        warehouseCode: null,
        warehouseName: null,
        locationId: null,
        locationCode: null,
        locationName: null,
        areaId: null,
        areaCode: null,
        areaName: null,
        expireDate: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        itemId: [
          { required: true, message: this.$t('mes.产品不能为空'), trigger: "blur" }
        ],
        warehouseId: [
          { required: true, message: this.$t('mes.请选择入库仓库'), trigger: "blur" }
        ],
        quantityRted: [
          { required: true, message: this.$t('mes.退货数量不能为空'), trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getWarehouseList();
  },
  methods: {
    /** 查询产品销售退货行列表 */
    getList() {
      this.loading = true;
      listRtsalseline(this.queryParams).then(response => {
        this.rtsalselineList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getWarehouseList(){
      getTreeList().then( response =>{        
        if(response.data){
          this.warehouseOptions = response.data.filter((el) =>{
              return el.warehouseCode.indexOf('VIR') == -1;
          });;
        }
        this.warehouseOptions.map(w =>{
          w.children.map(l =>{
                  let lstr =JSON.stringify(l.children).replace(/locationId/g,'lId').replace(/areaId/g, 'pId').replace(/areaName/g,'pName');                  
                  l.children = JSON.parse(lstr);
          });
            
          let wstr = JSON.stringify(w.children).replace(/warehouseId/g,'wId').replace(/locationId/g, 'pId').replace(/locationName/g,'pName');  
          w.children =  JSON.parse(wstr); 

        });
        let ostr=JSON.stringify(this.warehouseOptions).replace(/warehouseId/g,'pId').replace(/warehouseName/g, 'pName');
        this.warehouseOptions = JSON.parse(ostr);
      });
    },
    //选择默认的仓库、库区、库位
    handleWarehouseChanged(obj){      
      if(obj !=null){
        this.form.warehouseId = obj[0];
        this.form.locationId = obj[1];
        this.form.areaId = obj[2];
      }
    },
    handleSelectProduct(){
      this.$refs.itemSelect.showFlag = true;
    },
    //物料选择弹出框
    onItemSelected(obj){
        if(obj != undefined && obj != null){
          this.form.itemId = obj.itemId;
          this.form.itemCode = obj.itemCode;
          this.form.itemName = obj.itemName;
          this.form.specification = obj.specification;
          this.form.unitOfMeasure = obj.unitOfMeasure;  
        }
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        lineId: null,
        rtId: this.rtId,
        itemId: null,
        itemCode: null,
        itemName: null,
        specification: null,
        unitOfMeasure: null,
        quantityRted: null,
        batchCode: null,
        warehouseId: null,
        warehouseCode: null,
        warehouseName: null,
        locationId: null,
        locationCode: null,
        locationName: null,
        areaId: null,
        areaCode: null,
        areaName: null,
        expireDate: null,
        remark: null,
        attr1: null,
        attr2: null,
        attr3: null,
        attr4: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
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
      this.ids = selection.map(item => item.lineId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加产品销售退货行');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const lineId = row.lineId || this.ids
      getRtsalseline(lineId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改产品销售退货行');
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.lineId != null) {
            updateRtsalseline(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addRtsalseline(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.新增成功'));
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const lineIds = row.lineId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除产品销售退货行编号为{v}的数据项？', { v: lineIds })).then(function() {
        return delRtsalseline(lineIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wm/rtsalseline/export', {
        ...this.queryParams
      }, `rtsalseline_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
