<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('mes.条码类型')" prop="barcodeType">
        <el-select v-model="queryParams.barcodeType" :placeholder="$t('mes.请选择条码类型')">
          <el-option
            v-for="dict in dict.type.mes_barcode_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('mes.业务编码')" prop="bussinessCode">
        <el-input
          v-model="queryParams.bussinessCode"
          :placeholder="$t('mes.请输入业务编码')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.业务名称')" prop="bussinessName">
        <el-input
          v-model="queryParams.bussinessName"
          :placeholder="$t('mes.请输入业务名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['mes:wm:barcode:add']"
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
          v-hasPermi="['mes:wm:barcode:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button
          type="success"
          icon="el-icon-s-tools"
          size="mini"
          @click="handleConfig"
          v-hasPermi="['mes:wm:barcode:edit']"
        >{{ this.$t('mes.条码设置') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-printer"
          size="mini"
          @click="handleBatchPrint"
          v-hasPermi="['mes:wm:barcode:view']"
        >{{ this.$t('mes.批量打印') }}</el-button>
      </el-col>
      <Barcodeconfig ref="barcodeconfig"></Barcodeconfig>
      <BarcodeBatchPrint ref="barcodebatchprint"></BarcodeBatchPrint>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="barcodeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />     
      <el-table-column :label="$t('mes.条码')" align="center">
        <template slot-scope="scope">
            <el-image @click="handleView(scope.row)" class="barcodeClass" fit="scale-down" :src="scope.row.barcodeUrl">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
        </template>
      </el-table-column> 
      <el-table-column :label="$t('mes.条码格式')" align="center" prop="barcodeFormart">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_barcode_formart" :value="scope.row.barcodeFormart"/>
        </template>
      </el-table-column>          
      <el-table-column :label="$t('mes.条码类型')" align="center" prop="barcodeType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_barcode_type" :value="scope.row.barcodeType"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.条码内容')" align="center" prop="barcodeContent" />
      <el-table-column :label="$t('mes.业务编码')" align="center" prop="bussinessCode" />
      <el-table-column :label="$t('mes.业务名称')" align="center" prop="bussinessName" />
      <el-table-column :label="$t('mes.是否生效')" align="center" prop="enableFlag" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.enableFlag"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:wm:barcode:edit']"
          >{{ $t('common.edit') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:wm:barcode:remove']"
          >{{ $t('common.delete') }}</el-button>
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

    <!-- 添加或修改条码清单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.条码格式')" prop="barcodeFormart">          
              <el-select v-model="form.barcodeFormart" :placeholder="$t('mes.请选择条码格式')">
                <el-option
                  v-for="dict in dict.type.mes_barcode_formart"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.条码类型')" prop="barcodeType">
              <el-select v-model="form.barcodeType" :placeholder="$t('mes.请选择条码类型')">
                <el-option
                  v-for="dict in dict.type.mes_barcode_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('system.是否启用')">
              <el-radio-group v-model="form.enableFlag" disabled v-if="optType=='view'">
                <el-radio
                  v-for="dict in dict.type.sys_yes_no"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
              <el-radio-group v-model="form.enableFlag" v-else>
                <el-radio
                  v-for="dict in dict.type.sys_yes_no"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <!--根据不同的条码类型展示不同的业务内容选择 start-->
        <!--物料产品-->
        <el-row v-if="form.barcodeType=='ITEM'">
          <el-col :span="12">
            <el-form-item :label="$t('mes.物料编码')"  prop="bussinessCode">
              <el-input v-model="form.bussinessCode" readonly="readonly" :placeholder="$t('mes.请选择物料编码')" >
                <el-button slot="append" @click="handleSelectProduct" icon="el-icon-search"></el-button>
              </el-input>
            </el-form-item>
            <ItemSelect ref="itemSelect" @onSelected="onItemSelected" > </ItemSelect>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.物料名称')" prop="bussinessName">
              <el-input v-model="form.bussinessName" readonly="readonly" />
            </el-form-item>
          </el-col>
        </el-row>
        <!--仓库-->
        <el-row v-if="form.barcodeType=='WAREHOUSE'">
          <el-col :span="24">
            <el-form-item :label="$t('mes.储位')" prop="warehouseId">
              <!-- <el-cascader v-model="warehouseInfo"
                :options="warehouseOptions"
                :props="warehouseProps"
                @change="handleWarehouseChanged"
              >                  
              </el-cascader> -->
              <el-cascader v-model="warehouseInfo"
                ref="warehouseRef"
                :options="warehouseOptions"
                :props="warehouseProps"
                @change="handleWarehouseChanged"
              >                  
              </el-cascader>
            </el-form-item>
          </el-col>
        </el-row>

        <!--箱条码-->
        <el-row v-if="form.barcodeType=='BOX'">
          <el-col :span="12">
            <el-form-item :label="$t('mes.装箱单号')"  prop="packageCode">
              <el-input v-model="form.bussinessCode" readonly="readonly" :placeholder="$t('mes.请选择装箱单')" >
                <el-button slot="append" @click="handleSelectPackage" icon="el-icon-search"></el-button>
              </el-input>
            </el-form-item>
            <PackageSelectSingle ref="packageSelect" :status="'FINISHED'" @onSelected="onPackageSelected"></PackageSelectSingle>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.客户名称')" prop="bussinessName">
              <el-input v-model="form.bussinessName" readonly="readonly" />
            </el-form-item>
          </el-col>
        </el-row>
        <!--供应商-->
        <el-row v-else-if="form.barcodeType=='VENDOR'">
          <el-col :span="12">
            <el-form-item :label="$t('mes.供应商编号')" prop="bussinessCode">
              <el-input v-model="form.bussinessCode" readonly="readonly" :placeholder="$t('mes.请选择供应商')" >
                <el-button slot="append" @click="handleSelectVendor" icon="el-icon-search"></el-button>
              </el-input>
              <VendorSelect ref="vendorSelect" @onSelected="onVendorSelected" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.供应商名称')" prop="bussinessName">
              <el-input v-model="form.bussinessName" readonly="readonly" >
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 工作站 -->
        <el-row v-else-if="form.barcodeType=='WORKSTATION'">
          <el-col :span="12">
            <el-form-item :label="$t('mes.工作站')" prop="bussinessCode">
              <el-input v-model="form.bussinessCode" :placeholder="$t('mes.请选择工作站')" >
                <el-button slot="append" icon="el-icon-search" @click="handleWorkstationSelect"></el-button>
              </el-input>
            </el-form-item>
            <WorkstationSelect ref="wsSelect"  @onSelected="onWorkstationSelected"> </WorkstationSelect>      
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.工作站名称')" prop="bussinessName">
              <el-input v-model="form.bussinessName" readonly="readonly" >
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 库存 -->
        <el-row v-else-if="form.barcodeType=='STOCK'">
          <el-col :span="8">
            <el-form-item :label="$t('mes.物资编码')" prop="bussinessCode">
              <el-input v-model="form.bussinessCode" :placeholder="$t('mes.请选择库存记录')" >
                <el-button slot="append" icon="el-icon-search" @click="handleMaterialStockSelect"></el-button>
              </el-input>
            </el-form-item>
            <StockSelect ref="stockSelect"  @onSelected="onMaterialStockSelected"> </StockSelect>      
          </el-col>
          <el-col :span="16">
            <el-form-item :label="$t('mes.物资信息')" prop="bussinessName">
              <el-input v-model="form.bussinessName" readonly="readonly" ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!--根据不同的条码类型展示不同的业务内容选择 end-->
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('mes.条码内容')">
              <el-input v-model="form.barcodeContent" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('mes.条码')" prop="url">
              <el-image class="barcodeFormClass" :src="form.barcodeUrl">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('system.备注')" prop="remark">
              <el-input v-model="form.remark" type="textarea" :placeholder="$t('common.inputContent')" />
            </el-form-item>   
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancel" v-if="optType =='view'">{{ this.$t('common.back') }}</el-button>
        <el-button type="primary" @click="submitForm" v-if="optType !='view' ">{{ this.$t('mes.保 存') }}</el-button>        
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBarcode, getBarcode, delBarcode, addBarcode, updateBarcode } from "@/api/mes/wm/barcode";
import ItemSelect  from "@/components/itemSelect/single.vue";
import VendorSelect from "@/components/vendorSelect/single.vue";
import PackageSelectSingle from "@/components/package/single.vue";
import WorkstationSelect from "@/components/workstationSelect/simpletableSingle.vue"
import StockSelect from "@/components/stockSelect/single.vue"
import Barcodeconfig from "./config.vue"
import BarcodeBatchPrint from "./batchprint.vue"
import {getTreeList} from "@/api/mes/wm/warehouse"
export default {
  name: "Barcode",
  dicts: ['mes_barcode_type','mes_barcode_formart','sys_yes_no'],
  components: {
    ItemSelect,VendorSelect,PackageSelectSingle,Barcodeconfig,WorkstationSelect,StockSelect,BarcodeBatchPrint
  },
  data() {
    return {
      warehouseInfo:[],
      warehouseOptions:[],
      warehouseProps:{
        multiple: false,
        value: 'pId',
        label: 'pName',
      },
      optType: null,
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
      // 条码清单表格数据
      barcodeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        barcodeFormart: null,
        barcodeType: null,
        barcodeContent: null,
        bussinessId: null,
        bussinessCode: null,
        bussinessName: null,
        barcodeUrl: null,
        enableFlag: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        barcodeFormart: [
          { required: true, message: this.$t('mes.条码格式不能为空'), trigger: "blur" }
        ],
        barcodeType: [
          { required: true, message: this.$t('mes.条码类型不能为空'), trigger: "change" }
        ],
        barcodeContent: [
          { required: true, message: this.$t('mes.产品物料ID不能为空'), trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getWarehouseList();
  },
  methods: {
    /** 查询条码清单列表 */
    getList() {
      this.loading = true;
      listBarcode(this.queryParams).then(response => {
        this.barcodeList = response.rows;
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
        barcodeId: null,
        barcodeFormart: null,
        barcodeType: null,
        barcodeContent: null,
        bussinessId: null,
        bussinessCode: null,
        bussinessName: null,
        barcodeUrl: null,
        enableFlag: 'Y',
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
      this.ids = selection.map(item => item.barcodeId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加条码');
      this.optType = "add";
    },
    handleView(row){
      this.reset();
      const barcodeId = row.barcodeId
      getBarcode(barcodeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.查看条码');
        this.optType = "view";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const barcodeId = row.barcodeId || this.ids
      getBarcode(barcodeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改条码');
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.barcodeId != null) {
            updateBarcode(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addBarcode(this.form).then(response => {
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
      const barcodeIds = row.barcodeId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除条码清单编号为{v}的数据项？', { v: barcodeIds })).then(function() {
        return delBarcode(barcodeIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wm/barcode/export', {
        ...this.queryParams
      }, `barcode_${new Date().getTime()}.xlsx`)
    },
    //物料选择
    handleSelectProduct(){
      this.$refs.itemSelect.showFlag = true;
    },
    //物料选择弹出框
    onItemSelected(obj){
        if(obj != undefined && obj != null){
          this.form.bussinessId = obj.itemId;
          this.form.bussinessCode = obj.itemCode;
          this.form.bussinessName = obj.itemName;
          this.form.barcodeContent= "".concat(this.form.barcodeType,'-',this.form.bussinessCode);
        }
    },
    //选择仓库、库区、库位
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
    handleWarehouseChanged(obj){
      const checkedNode = this.$refs['warehouseRef'].getCheckedNodes();
      if(obj !=null){
        this.form.bussinessId = checkedNode[0].data.pId;
        this.form.bussinessCode = checkedNode[0].data.areaCode;
        this.form.bussinessName = checkedNode[0].data.pName;
        this.form.barcodeContent = "".concat(this.form.barcodeType,'-',this.form.bussinessCode);
      }
    },
    //装箱单选择
    handleSelectPackage(){
      this.$refs.packageSelect.showFlag=true;
    },
    /**选择装箱单返回 */
    onPackageSelected(obj){
      if(obj != undefined && obj != null){
          this.form.bussinessId = obj.packageId;
          this.form.bussinessCode = obj.packageCode;
          this.form.bussinessName = obj.clientName;
          this.form.barcodeContent= "".concat(this.form.barcodeType,'-',this.form.bussinessCode);
        }
    },
    /**选择工作站 */
    handleWorkstationSelect(){
      this.$refs.wsSelect.showFlag = true;
    },
    /**工作站选择返回 */
    onWorkstationSelected(obj){
      if(obj != undefined && obj != null){
          this.form.bussinessId = obj.workstationId;
          this.form.bussinessCode = obj.workstationCode;
          this.form.bussinessName = obj.workstationName;
          this.form.barcodeContent= "".concat(this.form.barcodeType,'-',this.form.bussinessCode);
        }
    },
    /**
     * 选择库存 
     */
     handleMaterialStockSelect(){
        this.$refs.stockSelect.showFlag = true;
     },

     /**库存选择返回 */
     onMaterialStockSelected(obj){
      if(obj != undefined && obj != null){
          this.form.bussinessId = obj.materialStockId;
          this.form.bussinessCode = obj.itemCode;
          this.form.bussinessName = "".concat(obj.itemName,'|',obj.specification,'|',obj.vendorName,'|',obj.batchCode);
          this.form.barcodeContent= "".concat(this.form.barcodeType,'-',this.form.bussinessId);
        }
     },
    handleConfig(){
      this.$refs.barcodeconfig.showFlag = true;
    },

    handleBatchPrint(){
      this.$refs.barcodebatchprint.showFlag = true;
    }

  }
};
</script>
<style scoped>
  .barcodeClass {
    width: 100px;
    height: 200px;
  }
  .barcodeFormClass {
    width: 200px;
    height: 200px;
  }

</style>
