<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-row>
        <el-col :span="8">
          <el-form-item :label="$t('mes.工装夹具编码')" prop="toolCode">
            <el-input
              v-model="queryParams.toolCode"
              :placeholder="$t('mes.请输入工装夹具编码')"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="$t('mes.工装夹具名称')" prop="toolName">
            <el-input
              v-model="queryParams.toolName"
              :placeholder="$t('mes.请输入工装夹具名称')"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="$t('mes.工装夹具类型')" prop="toolTypeId">
            <el-select v-model="form.toolTypeId" :placeholder="$t('mes.请选择类型')">
              <el-option
                v-for="dict in toolTypeOptions"
                :key="dict.toolTypeId"
                :label="dict.toolTypeName"
                :value="dict.toolTypeId"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="6">
          <el-form-item :label="$t('mes.品牌')" label-width="60px" prop="brand">
            <el-input
              v-model="queryParams.brand"
              :placeholder="$t('mes.请输入品牌')"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item :label="$t('lims.型号')" label-width="60px" prop="spec">
            <el-input
              v-model="queryParams.spec"
              :placeholder="$t('mes.请输入型号')"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item :label="$t('system.状态')" label-width="60px" prop="status">
            <el-select v-model="queryParams.status" :placeholder="$t('system.请选择状态')" clearable>
              <el-option
                v-for="dict in dict.type.mes_tool_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['mes:tm:tool:add']"
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
          v-hasPermi="['mes:tm:tool:edit']"
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
          v-hasPermi="['mes:tm:tool:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['mes:tm:tool:export']"
        >{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="toolList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.编号')" align="center" prop="toolCode" >
      </el-table-column>
      <el-table-column :label="$t('mes.名称')" align="center" prop="toolName" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="handleView(scope.row)"
            v-hasPermi="['mes:tm:tool:query']"
          >{{scope.row.toolName}}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.品牌')" align="center" prop="brand" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('lims.型号')" align="center" prop="spec" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('system.类型')" align="center" prop="toolTypeName" />
      <el-table-column :label="$t('mes.库存数量')" align="center" prop="quantity" />
      <el-table-column :label="$t('mes.可用数量')" align="center" prop="quantityAvail" />
      <el-table-column :label="$t('mes.保养维护类型')" width="100px" align="center" prop="maintenType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_mainten_type" :value="scope.row.maintenType"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.下次保养周期')" width="100px" align="center" prop="nextMaintenPeriod" />
      <el-table-column :label="$t('mes.下次保养日期')" align="center" prop="nextMaintenDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.nextMaintenDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_tool_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:tm:tool:edit']"
          >{{ this.$t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:tm:tool:remove']"
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

    <!-- 添加或修改工装夹具清单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('mes.工装夹具类型')" prop="toolTypeId">
              <el-select v-model="form.toolTypeId" @change="onToolTypeChanged" :placeholder="$t('mes.请选择类型')">
                <el-option
                  v-for="dict in toolTypeOptions"
                  :key="dict.toolTypeId"
                  :label="dict.toolTypeName"
                  :value="dict.toolTypeId"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.工装夹具名称')" prop="toolName">
              <el-input v-model="form.toolName" :placeholder="$t('mes.请输入工装夹具名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('mes.品牌')" prop="brand">
              <el-input v-model="form.brand" :placeholder="$t('mes.请输入品牌')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.型号')" prop="spec">
              <el-input v-model="form.spec" :placeholder="$t('mes.请输入型号')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item  :label="$t('mes.工装夹具编号')" prop="toolCode">
              <el-input v-model="form.toolCode" :placeholder="$t('mes.请输入工装夹具编号')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item  label-width="80">
              <el-switch v-model="autoGenFlag"
                  active-color="#13ce66"
                  :active-text="$t('mes.自动生成')"
                  @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view'">               
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item  :label="$t('lims.数量')" prop="quantity">
              <el-input v-if="form.codeFlag=='Y'" readonly v-model="form.quantity" />
              <el-input-number :min="0" @blur="onQuantityChanged" v-else v-model="form.quantity" :placeholder="$t('mes.请输入数量')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('mes.可用数量')" prop="quantityAvail">
              <el-input v-model="form.quantityAvail" readonly :placeholder="$t('mes.请输入可用数量')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.保养维护类型')" prop="maintenType">
              <el-select v-model="form.maintenType" :placeholder="$t('mes.请选择保养维护类型')">
                <el-option
                  v-for="dict in dict.type.mes_mainten_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.maintenType == 'REGULAR'" :label="$t('mes.下一次保养日期')" prop="nextMaintenDate">
                <el-date-picker clearable
                  v-model="form.nextMaintenDate"
                  type="date"
                  value-format="yyyy-MM-dd"
                  :placeholder="$t('mes.请选择下一次保养日期')">
                </el-date-picker>
            </el-form-item>
            <el-form-item v-else :label="$t('mes.下一次保养周期')" prop="nextMaintenPeriod">
              <el-input v-model="form.nextMaintenPeriod" :placeholder="$t('mes.请输入下一次保养周期')" />
            </el-form-item >
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.状态')">
              <el-radio-group disabled="disabled" v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.mes_tool_status"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
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
        <el-button type="primary" @click="submitForm" v-else>{{ this.$t('system.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTool, getTool, delTool, addTool, updateTool } from "@/api/mes/tm/tool";
import { listAllTooltype } from "@/api/mes/tm/tooltype"
import {genCode} from "@/api/system/autocode/rule"
export default {
  name: "Tool",
  dicts: ['mes_tool_status', 'mes_mainten_type'],
  data() {
    return {
      //自动生成编码
      autoGenFlag:false,
      optType: undefined,
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
      // 工装夹具清单表格数据
      toolList: [],
      //类型清单
      toolTypeOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        toolCode: null,
        toolName: null,
        brand: null,
        spec: null,
        toolTypeId: null,
        toolTypeCode: null,
        toolTypeName: null,
        quantity: null,
        quantityAvail: null,
        maintenType: null,
        nextMaintenPeriod: null,
        nextMaintenDate: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        toolName: [
          { required: true, message: this.$t('mes.工装夹具名称不能为空'), trigger: "blur" }
        ],
        toolTypeId: [
          { required: true, message: this.$t('mes.工装夹具类型不能为空'), trigger: "blur" }
        ],
        quantity: [
          { required: true, message: this.$t('mes.数量不能为空'), trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getTypeList();
  },
  methods: {
    /** 查询工装夹具清单列表 */
    getList() {
      this.loading = true;
      listTool(this.queryParams).then(response => {
        this.toolList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getTypeList(){
      listAllTooltype().then(response =>{
        this.toolTypeOptions = response.data;
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
        toolId: null,
        toolCode: null,
        toolName: null,
        brand: null,
        spec: null,
        toolTypeId: null,
        toolTypeCode: null,
        toolTypeName: null,
        quantity: null,
        quantityAvail: null,
        maintenType: 'REGULAR',
        nextMaintenPeriod: null,
        nextMaintenDate: null,
        status: "STORE",
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      };
      this.autoGenFlag = false;
      this.resetForm("form");
    },
    onToolTypeChanged(){
      var types = this.toolTypeOptions.filter((item) =>{
        if(item.toolTypeId == this.form.toolTypeId){
          return item;
        }
      });
      if(types !==null && types.length>0){
        if(types[0].codeFlag == 'Y'){
          //如果是单独编码，则数量固定为1
          this.form.quantity=1;
          this.form.quantityAvail=1;
          this.form.codeFlag ='Y';
          return;
        }
      }
      this.form.codeFlag = 'N';
    },
    onQuantityChanged(){
      this.form.quantityAvail = this.form.quantity;
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
      this.ids = selection.map(item => item.toolId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加工装夹具清单');
      this.optType = "add";
    },
    // 查询明细按钮操作
    handleView(row){
      this.reset();
      const toolId = row.toolId || this.ids;
      getTool(toolId).then(response => {
        this.form = response.data;        
        this.open = true;
        this.title = this.$t('mes.查看工装夹具信息');
        this.optType = "view";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const toolId = row.toolId || this.ids
      getTool(toolId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改工装夹具清单');
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.toolId != null) {
            updateTool(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addTool(this.form).then(response => {
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
      const toolIds = row.toolId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除工装夹具清单编号为{v}的数据项？', { v: toolIds })).then(function() {
        return delTool(toolIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('tm/tool/export', {
        ...this.queryParams
      }, `tool_${new Date().getTime()}.xlsx`)
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag){
      if(autoGenFlag){
        genCode('TOOL_CODE').then(response =>{
          this.form.toolCode = response;
        });
      }else{
        this.form.toolCode = null;
      }
    }
  }
};
</script>
