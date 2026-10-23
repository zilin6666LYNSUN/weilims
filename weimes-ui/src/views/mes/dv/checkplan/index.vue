<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('mes.计划编号')" prop="planCode">
        <el-input
          v-model="queryParams.planCode"
          :placeholder="$t('mes.请输入计划编号')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('lims.计划名称')" prop="planName">
        <el-input
          v-model="queryParams.planName"
          :placeholder="$t('lims.请输入计划名称')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('mes.计划类型')" prop="planType">
        <el-select v-model="queryParams.planType" :placeholder="$t('mes.请选择类型')" clearable>
          <el-option
            v-for="dict in dict.type.dv_plan_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('system.状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('system.请选择状态')" clearable>
          <el-option
            v-for="dict in dict.type.mes_order_status"
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
          v-hasPermi="['mes:dv:checkplan:add']"
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
          v-hasPermi="['mes:dv:checkplan:edit']"
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
          v-hasPermi="['mes:dv:checkplan:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['mes:dv:checkplan:export']"
        >{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="checkplanList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('lims.计划编码')" align="center" prop="planCode" >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="handleView(scope.row)"
            v-hasPermi="['mes:dv:checkplan:query']"
          >{{scope.row.planCode}}</el-button>
        </template>
      </el-table-column>      
      <el-table-column :label="$t('lims.计划名称')" align="center" width="200px" prop="planName" />
      <el-table-column :label="$t('mes.计划类型')" align="center" width="120px" prop="planType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dv_plan_type" :value="scope.row.planType"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.开始日期')" align="center" prop="startDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.结束日期')" align="center" prop="endDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.频率')" align="center" prop="cycleType">        
        <template slot-scope="scope">
          {{scope.row.cycleCount}}
          <dict-tag :options="dict.type.mes_cycle_type" :value="scope.row.cycleType"/>
        </template>
      </el-table-column>      
      <el-table-column :label="$t('system.状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_order_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" width="130px" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-if="scope.row.status =='PREPARE'"
            v-hasPermi="['mes:dv:checkplan:edit']"
          >{{ this.$t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-if="scope.row.status =='PREPARE'"
            v-hasPermi="['mes:dv:checkplan:remove']"
          >{{ this.$t('common.delete') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleUpdate(scope.row)"
            v-if="scope.row.status =='FINISHED'"
            v-hasPermi="['mes:dv:checkplan:edit']"
          >{{ this.$t('lims.停用') }}</el-button>
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

    <!-- 添加或修改设备点检计划头对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('lims.计划编码')" prop="planCode">
              <el-input v-model="form.planCode" :placeholder="$t('lims.请输入计划编码')" />
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
            <el-form-item :label="$t('lims.计划名称')" prop="planName">
              <el-input v-model="form.planName" :placeholder="$t('lims.请输入计划名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('mes.计划类型')"  prop="planType" >
                <el-select v-model="form.planType" :placeholder="$t('mes.请选择计划类型')">
                  <el-option
                    v-for="dict in dict.type.dv_plan_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  ></el-option>
                </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.频率')" prop="cycleCount">
              <el-input-number :min="1" :step="1" v-model="form.cycleCount" :placeholder="$t('mes.请输入次数')" />
            </el-form-item>
          </el-col>
          <el-col :span="4" >
            <el-form-item  prop="cycleType" label-width="80">
                <el-select v-model="form.cycleType" :placeholder="$t('mes.请选择频率')">
                  <el-option
                    v-for="dict in dict.type.mes_cycle_type"
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
            <el-form-item :label="$t('system.开始日期')" prop="startDate">
              <el-date-picker clearable
                v-model="form.startDate"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择开始日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('system.结束日期')" prop="endDate">
              <el-date-picker clearable
                v-model="form.endDate"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择结束日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('system.状态')" prop="status">
              <el-select v-model="form.status" disabled>
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
          <el-col :span="24">
            <el-form-item :label="$t('system.备注')" prop="remark">
              <el-input v-model="form.remark" type="textarea" :placeholder="$t('common.inputContent')" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-tabs type="border-card" v-if="form.planId != null">        
        <el-tab-pane :label="$t('mes.设备清单')"> 
          <Checkmachinery ref="machinerylist" :optType="optType" :planId="form.planId" ></Checkmachinery>        
        </el-tab-pane>
        <el-tab-pane :label="form.planType=='CHECK'?this.$t('mes.点检项目'):this.$t('mes.保养项目')">
          <Checksubject ref="subjectlist" :optType="optType" :planId="form.planId" ></Checksubject>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancel" v-if="optType =='view' || form.status !='PREPARE' ">{{ this.$t('common.back') }}</el-button>
        <el-button type="primary" @click="submitForm" v-if="form.status =='PREPARE' && optType !='view' ">{{ this.$t('common.save') }}</el-button>
        <el-button type="success" @click="handleFinish" v-if="form.status =='PREPARE' && optType !='view'  && form.planId !=null">{{ this.$t('lims.启用') }}</el-button>
        <el-button type="success" @click="handleDeFinish" v-if="form.status =='FINISHED' && optType !='view'  && form.planId !=null">{{ this.$t('lims.停用') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCheckplan, getCheckplan, delCheckplan, addCheckplan, updateCheckplan } from "@/api/mes/dv/checkplan";
import Checkmachinery from "./machinery.vue"
import Checksubject from "./subject.vue"
import {genCode} from "@/api/system/autocode/rule"
export default {
  name: "Checkplan",
  dicts: ['mes_cycle_type','mes_order_status','dv_plan_type'],
  components:{Checkmachinery,Checksubject},
  data() {
    return {
      autoGenFlag: false,
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
      // 设备点检计划头表格数据
      checkplanList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        planCode: null,
        planName: null,
        startDate: null,
        endDate: null,        
        cycleType: null,
        cycleCount: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        planCode: [
          { required: true, message: this.$t('mes.计划编码不能为空'), trigger: "blur" }
        ],
        planName: [
          { required: true, message: this.$t('mes.计划名称不能为空'), trigger: "blur" }
        ],
        planType:[
          { required: true, message: this.$t('mes.计划类型不能为空'), trigger: "blur" }
        ],
        cycleType: [
          { required: true, message: this.$t('mes.请选择点检频率'), trigger: "blur" }
        ],
        cycleCount: [
          { required: true, message: this.$t('mes.请输入点检次数'), trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询设备点检计划头列表 */
    getList() {
      this.loading = true;
      listCheckplan(this.queryParams).then(response => {
        this.checkplanList = response.rows;
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
        planId: null,
        planCode: null,
        planName: null,
        planType: null,
        startDate: null,
        endDate: null,
        cycleType: null,
        cycleCount: null,
        status: 'PREPARE',
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
      this.ids = selection.map(item => item.planId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加计划');
      this.optType = "add";
    },
    // 查询明细按钮操作
    handleView(row){
      this.reset();
      const planId = row.planId || this.ids;
      getCheckplan(planId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.查看计划信息');
        this.optType = "view";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const planId = row.planId || this.ids
      getCheckplan(planId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改计划');
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.planId != null) {
            updateCheckplan(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addCheckplan(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.新增成功'));
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleFinish(){
      if(this.form.planId != null && this.form.status =='PREPARE'){
        this.form.status='FINISHED';
        updateCheckplan(this.form).then(response => {                
                this.$modal.msgSuccess(this.$t('mes.已启用'));
                this.open = false;
                this.getList();
        },err =>{
          this.form.status='PREPARE';
        });
      }
    },
    handleDeFinish(){
      if(this.form.planId != null && this.form.status =='FINISHED'){
        this.form.status='PREPARE';
        updateCheckplan(this.form).then(response => {                
                this.$modal.msgSuccess(this.$t('lims.已停用'));
                this.open = false;
                this.getList();
        },err =>{
          this.form.status='FINISHED';
        });
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const planIds = row.planId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除计划编号为{v}的数据项？', { v: planIds })).then(function() {
        return delCheckplan(planIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('dv/checkplan/export', {
        ...this.queryParams
      }, `checkplan_${new Date().getTime()}.xlsx`)
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag){
      if(autoGenFlag){
        genCode('CHECKPLAN_CODE').then(response =>{
          this.form.planCode = response;
        });
      }else{
        this.form.planCode = null;
      }
    }
  }
};
</script>
