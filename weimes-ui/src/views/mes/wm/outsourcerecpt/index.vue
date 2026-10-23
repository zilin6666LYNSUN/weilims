<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item :label="$t('mes.入库单编号')" prop="recptCode">
        <el-input
          v-model="queryParams.recptCode"
          :placeholder="$t('mes.请输入入库单编号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item :label="$t('mes.外协工单编号')" prop="workorderCode">
        <el-input
          v-model="queryParams.workorderCode"
          :placeholder="$t('mes.请输入外协工单编号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item :label="$t('lims.供应商名称')" prop="vendorName">
        <el-input
          v-model="queryParams.vendorName"
          :placeholder="$t('lims.请输入供应商名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item :label="$t('lims.入库日期')" prop="recptDate">
        <el-date-picker clearable
          v-model="queryParams.recptDate"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择入库日期')">
        </el-date-picker>
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
          v-hasPermi="['mes:wm:outsourcerecpt:add']"
        >{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['mes:wm:outsourcerecpt:edit']"
        >{{ this.$t('system.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['mes:wm:outsourcerecpt:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="outsourcerecptList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.入库单编号')" align="center" prop="recptCode" >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="handleView(scope.row)"
            v-hasPermi="['mes:wm:outsourcerecpt:query']"
          >{{scope.row.recptCode}}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.入库单名称')" align="center" prop="recptName" />
      <el-table-column :label="$t('mes.外协工单编号')" align="center" prop="workorderCode" />
      <el-table-column :label="$t('lims.供应商编码')" align="center" prop="vendorCode" />
      <el-table-column :label="$t('lims.供应商名称')" align="center" prop="vendorName" />
      <el-table-column :label="$t('lims.入库日期')" align="center" prop="recptDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.recptDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.单据状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_order_status" :value="scope.row.status"/>
        </template>
      </el-table-column>  
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-video-play"
            v-if="scope.row.status =='PREPARE'"
            @click="handleExecute(scope.row)"
            v-hasPermi="['mes:wm:outsourcerecpt:edit']"
          >{{ this.$t('mes.执行入库') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-if="scope.row.status =='PREPARE'"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:wm:outsourcerecpt:edit']"
          >{{ this.$t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            v-if="scope.row.status =='PREPARE'"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:wm:outsourcerecpt:remove']"
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

    <!-- 添加或修改外协入库单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.入库单编号')" prop="recptCode">
              <el-input v-model="form.recptCode" :placeholder="$t('mes.请输入入库单编号')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item  label-width="80">
              <el-switch v-model="autoGenFlag"
                  active-color="#13ce66"
                  :active-text="$t('mes.自动生成')"
                  @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view' && form.status =='PREPARE'">               
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.入库单名称')" prop="recptName">
              <el-input v-model="form.recptName" :placeholder="$t('mes.请输入入库单名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">            
            <el-form-item :label="$t('lims.入库日期')" prop="recptDate">
              <el-date-picker clearable
                v-model="form.recptDate"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择入库日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.外协工单')" prop="workorderCode">
              <el-input v-model="form.workorderCode" :placeholder="$t('mes.请选择外协工单')" >
                <el-button slot="append" icon="el-icon-search" @click="handleWorkorderSelect"></el-button>
              </el-input>
            </el-form-item>
            <WorkorderSelect ref="woSelect" :workorder="workorder" @onSelected="onWorkorderSelected"></WorkorderSelect>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.单据状态')" prop="status">
              <el-select v-model="form.status" disabled :placeholder="$t('mes.请选择单据状态')">
                <el-option
                  v-for="dict in dict.type.mes_order_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.采购订单号')" prop="poCode">
              <el-input v-model="form.poCode" :placeholder="$t('mes.请输入采购订单号')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.供应商')" prop="vendorName">
              <el-input v-model="form.vendorName" readonly="readonly">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.入库仓库')">
              <el-cascader v-model="warehouseInfo"
                :options="warehouseOptions"
                :props="warehouseProps"
                @change="handleWarehouseChanged"
              >                  
              </el-cascader>
            </el-form-item>
          </el-col>
          <el-col :span="8">

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
      <el-divider v-if="form.recptId !=null" content-position="center">{{ this.$t('mes.物料信息') }}</el-divider> 
        <el-card shadow="always" v-if="form.recptId !=null" class="box-card">
          <Itemrecptline ref=line :recptId="form.recptId" :warehouseId="form.warehouseId" :locationId="form.locationId" :areaId="form.areaId" :optType="optType"></Itemrecptline>
        </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancel" v-if="optType =='view' || form.status !='PREPARE' ">{{ this.$t('common.back') }}</el-button>
        <el-button type="primary" @click="submitForm" v-if="form.status =='PREPARE' && optType !='view' ">{{ this.$t('mes.保 存') }}</el-button>        
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOutsourcerecpt, getOutsourcerecpt, delOutsourcerecpt, addOutsourcerecpt, updateOutsourcerecpt, execute } from "@/api/mes/wm/outsourcerecpt";
import {getTreeList} from "@/api/mes/wm/warehouse"
import {genCode} from "@/api/system/autocode/rule"
import WorkorderSelect from "@/components/workorderSelect/single.vue"
import IqcSelect from "@/components/iqcSelect/single.vue";
import Itemrecptline from "./line.vue";
export default {
  name: "Outsourcerecpt",
  dicts:['mes_order_status'],
  components :{WorkorderSelect,IqcSelect,Itemrecptline},
  data() {
    return {
      //自动生成编码
      autoGenFlag:false,
      optType: undefined,
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
      // 外协入库单表格数据
      outsourcerecptList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //工单弹出框结果过滤参数
      workorder: { workorderType: 'OUTSOURCE'},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        recptCode: null,        recptName: null,        iqcId: null,        iqcCode: null,        workorderId: null,        workorderCode: null,        vendorId: null,        vendorCode: null,        vendorName: null,        vendorNick: null,        warehouseId: null,        warehouseCode: null,        warehouseName: null,        locationId: null,        locationCode: null,        locationName: null,        areaId: null,        areaCode: null,        areaName: null,        recptDate: null,        status: null,      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        recptCode: [
          { required: true, message: this.$t('mes.入库单编号不能为空'), trigger: "blur" }
        ],        recptName: [
          { required: true, message: this.$t('mes.入库单名称不能为空'), trigger: "blur" }
        ],
        workorderCode: [
          { required: true, message: this.$t('mes.请选择外协工单'), trigger: "blur" }
        ],      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询外协入库单列表 */
    getList() {
      this.loading = true;
      listOutsourcerecpt(this.queryParams).then(response => {
        this.outsourcerecptList = response.rows;
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
        recptId: null,
        recptCode: null,
        recptName: null,
        iqcId: null,
        iqcCode: null,
        workorderId: null,
        workorderCode: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        vendorNick: null,
        warehouseId: null,
        warehouseCode: null,
        warehouseName: null,
        locationId: null,
        locationCode: null,
        locationName: null,
        areaId: null, areaCode: null, areaName: null, recptDate: new Date(), 
        status: "PREPARE", remark: null, attr1: null, attr2: null, attr3: null, attr4: null, createBy: null, createTime: null, updateBy: null, updateTime: null
      };
        this.autoGenFlag = false;     
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
      this.ids = selection.map(item => item.recptId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加外协入库单');
      this.optType = "add";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const recptId = row.recptId || this.ids
      getOutsourcerecpt(recptId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改外协入库单');
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.recptId != null) {
            updateOutsourcerecpt(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addOutsourcerecpt(this.form).then(response => {
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
      const recptIds = row.recptId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除外协入库单编号为{v}的数据项？', { v: recptIds })).then(function() {
        return delOutsourcerecpt(recptIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    // 查询明细按钮操作
    handleView(row){
      this.reset();
      const recptIds = row.recptId
      getOutsourcerecpt(recptIds).then(response => {
        this.form = response.data;
        this.warehouseInfo[0] = response.data.warehouseId;    
        this.warehouseInfo[1] = response.data.locationId;    
        this.warehouseInfo[2] = response.data.areaId; 
        this.open = true;
        this.title = this.$t('mes.查看外协入库单信息');
        this.optType = "view";
      });
    },
    //执行入库
    handleExecute(row){
      const recptIds = row.recptId || this.ids;
      this.$modal.confirm(this.$t('mes.确认执行入库？')).then(function() {
        return execute(recptIds)//执行入库
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('lims.入库成功'));
      }).catch(() => {});
    },
    //选择生产工单
    handleWorkorderSelect(){
      this.$refs.woSelect.showFlag = true;
    },
    onWorkorderSelected(row){
      if(row != undefined && row != null){
        this.form.workorderId = row.workorderId;
        this.form.workorderCode = row.workorderCode;
        this.form.workorderName = row.workorderName;
        this.form.poCode = row.sourceCode;
        this.form.vendorId = row.vendorId;
        this.form.vendorCode = row.vendorCode;
        this.form.vendorName = row.vendorName;
      }
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag){
      if(autoGenFlag){
        genCode('OUTSOURCE_RECPT_CODE').then(response =>{
          this.form.recptCode = response;
        });
      }else{
        this.form.recptCode = null;
      }
    }
  }
};
</script>
