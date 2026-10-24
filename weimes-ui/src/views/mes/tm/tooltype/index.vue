<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item :label="$t('mes.类型编码')" prop="toolTypeCode">
        <el-input
          v-model="queryParams.toolTypeCode"
          :placeholder="$t('mes.请输入类型编码')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.类型名称')" prop="toolTypeName">
        <el-input
          v-model="queryParams.toolTypeName"
          :placeholder="$t('mes.请输入类型名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.保养维护类型')" prop="maintenType">
        <el-select v-model="queryParams.maintenType" :placeholder="$t('mes.请选择保养维护类型')" clearable>
          <el-option
            v-for="dict in dict.type.mes_mainten_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['mes:tm:tooltype:add']"
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
          v-hasPermi="['mes:tm:tooltype:edit']"
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
          v-hasPermi="['mes:tm:tooltype:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['mes:tm:tooltype:export']"
        >{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tooltypeList" @selection-change="handleSelectionChange">      
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.类型编码')" align="center" prop="toolTypeCode" >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="handleView(scope.row)"
            v-hasPermi="['mes:tm:tooltype:query']"
          >{{scope.row.toolTypeCode}}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.类型名称')" align="center" prop="toolTypeName" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('mes.是否编码管理')" align="center" prop="codeFlag">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.codeFlag"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.保养维护类型')" align="center" prop="maintenType">
        <template slot-scope="scope">
          <dict-tag v-if="scope.row.codeFlag =='Y'" :options="dict.type.mes_mainten_type" :value="scope.row.maintenType"/>
          <span v-else>{{ "无"  }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.保养周期')" align="center" prop="maintenPeriod" >
        <template slot-scope="scope">
          <span v-if="scope.row.codeFlag =='Y' && scope.row.maintenType =='REGULAR'">{{ scope.row.maintenPeriod +"天"  }}</span>
          <span v-else-if="scope.row.codeFlag =='Y' && scope.row.maintenType =='USAGE'">{{ scope.row.maintenPeriod +"次"  }}</span>
          <span v-else>{{ "无"  }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.备注')" align="center" prop="remark" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:tm:tooltype:edit']"
          >{{ $t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:tm:tooltype:remove']"
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

    <!-- 添加或修改工装夹具类型对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.类型编码')" prop="toolTypeCode">
              <el-input v-model="form.toolTypeCode" :placeholder="$t('mes.请输入类型编码')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-switch v-model="autoGenFlag"
                active-color="#13ce66"
                :active-text="$t('mes.自动生成')"
                @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view'">               
            </el-switch>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.类型名称')" prop="toolTypeName">
              <el-input v-model="form.toolTypeName" :placeholder="$t('mes.请输入类型名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.是否编码管理')" prop="codeFlag">
              <el-radio-group v-model="form.codeFlag" disabled v-if="optType=='view'">
                    <el-radio
                      v-for="dict in dict.type.sys_yes_no"
                      :key="dict.value"
                      :label="dict.value"
                    >{{dict.label}}</el-radio>
                  </el-radio-group>
                  <el-radio-group v-model="form.codeFlag" v-else>
                    <el-radio
                      v-for="dict in dict.type.sys_yes_no"
                      :key="dict.value"
                      :label="dict.value"
                    >{{dict.label}}</el-radio>
                </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.codeFlag=='Y'">
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
          <el-col :span="8" v-if="form.codeFlag=='Y'">
            <el-form-item :label="$t('mes.保养周期')" prop="maintenPeriod">
              <el-input v-model="form.maintenPeriod" :placeholder="$t('mes.请输入保养周期')" />
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
import { listTooltype, getTooltype, delTooltype, addTooltype, updateTooltype } from "@/api/mes/tm/tooltype";
import {genCode} from "@/api/system/autocode/rule"
export default {
  name: "Tooltype",
  dicts: ['mes_mainten_type','sys_yes_no'],
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
      // 工装夹具类型表格数据
      tooltypeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        toolTypeCode: null,
        toolTypeName: null,
        codeFlag: null,
        maintenType: null,
        maintenPeriod: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        toolTypeCode: [
          { required: true, message: this.$t('mes.类型编码不能为空'), trigger: "blur" }
        ],
        toolTypeName: [
          { required: true, message: this.$t('mes.类型名称不能为空'), trigger: "blur" }
        ],
        codeFlag: [
          { required: true, message: this.$t('mes.是否启用不能为空'), trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询工装夹具类型列表 */
    getList() {
      this.loading = true;
      listTooltype(this.queryParams).then(response => {
        this.tooltypeList = response.rows;
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
        toolTypeId: null,
        toolTypeCode: null,
        toolTypeName: null,
        codeFlag: 'Y',
        maintenType: null,
        maintenPeriod: null,
        remark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
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
      this.ids = selection.map(item => item.toolTypeId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加工装夹具类型');
      this.optType = "add";
    },
    // 查询明细按钮操作
    handleView(row){
      this.reset();
      const toolTypeId = row.toolTypeId || this.ids;
      getTooltype(toolTypeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.查看类型信息');
        this.optType = "view";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const toolTypeId = row.toolTypeId || this.ids
      getTooltype(toolTypeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改工装夹具类型');
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.toolTypeId != null) {
            updateTooltype(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addTooltype(this.form).then(response => {
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
      const toolTypeIds = row.toolTypeId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除工装夹具类型编号为{v}的数据项？', { v: toolTypeIds })).then(function() {
        return delTooltype(toolTypeIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('mes/tm/tooltype/export', {
        ...this.queryParams
      }, `tooltype_${new Date().getTime()}.xlsx`)
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag){
      if(autoGenFlag){
        genCode('TOOL_TYPE_CODE').then(response =>{
          this.form.toolTypeCode = response;
        });
      }else{
        this.form.toolTypeCode = null;
      }
    }
  }
};
</script>
