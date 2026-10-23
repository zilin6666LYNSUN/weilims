<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('mes.班组类型')" prop="calendarType">
        <el-select v-model="queryParams.calendarType" :placeholder="$t('mes.请选择班组类型')">
          <el-option
            v-for="dict in dict.type.mes_calendar_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
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
      <el-form-item :label="$t('system.开始日期')" prop="startDate">
        <el-date-picker clearable
          v-model="queryParams.startDate"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择开始日期')">
        </el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('system.结束日期')" prop="endDate">
        <el-date-picker clearable
          v-model="queryParams.endDate"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择结束日期')">
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
          v-hasPermi="['mes:cal:calplan:add']"
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
          v-hasPermi="['mes:cal:calplan:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['mes:cal:calplan:export']"
        >{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="calplanList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />      
      <el-table-column :label="$t('mes.计划编号')" align="center" prop="planCode" >
          <template slot-scope="scope">
                <el-button              
                  type="text"
                  @click="handleView(scope.row)"
                  v-hasPermi="['mes:cal:calplan:query']"
                >{{scope.row.planCode}}</el-button>
          </template>
      </el-table-column>
      <el-table-column :label="$t('lims.计划名称')" width="200px" align="center" prop="planName" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('mes.班组类型')" align="center" prop="calendarType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_calendar_type" :value="scope.row.calendarType"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.开始日期')" align="center" prop="startDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('system.结束日期')" align="center" prop="endDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.轮班方式')" align="center" prop="shiftType" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_shift_type" :value="scope.row.shiftType"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.倒班方式')" align="center" prop="shiftMethod" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_shift_method" :value="scope.row.shiftMethod"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.单据状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_order_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width" >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-if="scope.row.status =='PREPARE'"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:cal:calplan:edit']"
          >{{ this.$t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            v-if="scope.row.status =='PREPARE'"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:cal:calplan:remove']"
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

    <!-- 添加或修改排班计划对话框 -->
    <el-dialog :title="title" v-loading="formLoading" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.计划编号')" prop="planCode">
              <el-input v-model="form.planCode" :placeholder="$t('mes.请输入计划编号')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item  label-width="80">
              <el-switch v-model="autoGenFlag"
                  active-color="#13ce66"
                  :active-text="$t('mes.自动生成')"
                  @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view'" >               
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
            <el-form-item :label="$t('mes.班组类型')" prop="calendarType">
              <el-select v-model="form.calendarType" :placeholder="$t('mes.请选择班组类型')">
                <el-option
                  v-for="dict in dict.type.mes_calendar_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('mes.轮班方式')">
              <el-radio-group v-model="form.shiftType">
                <el-radio
                  v-for="dict in dict.type.mes_shift_type"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.shiftType !='SINGLE'" :span="6">
            <el-form-item :label="$t('mes.倒班方式')" prop="shiftMethod">
              <el-select style="width:100px" v-model="form.shiftMethod" :placeholder="$t('mes.请选择倒班方式')">
                <el-option
                  v-for="dict in dict.type.mes_shift_method"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="form.shiftMethod =='DAY' && form.shiftType !='SINGLE'" :span="6">
            <el-form-item label-width="20" prop="shiftCount">
              <el-input-number :min="1"  controls-position="right" v-model="form.shiftCount" >
              </el-input-number>
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
        <el-tab-pane :label="$t('mes.班次')">
          <Shift ref="shiftTab" :planId="form.planId" :optType="optType"></Shift>
        </el-tab-pane>
        <el-tab-pane :label="$t('mes.班组')">
          <Team  ref="teamTab" :planId="form.planId" :calendarType="form.calendarType" :optType="optType"></Team>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancel" v-if="optType =='view' || form.status !='PREPARE'">{{ this.$t('common.back') }}</el-button>
        <el-button type="primary" @click="submitForm" v-if="form.status =='PREPARE' && optType !='view' ">{{ this.$t('system.确 定') }}</el-button>
        <el-button type="success" @click="handleFinish" v-if="form.status =='PREPARE' && optType !='view'  && form.planId !=null">{{ this.$t('lims.完成') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCalplan, getCalplan, delCalplan, addCalplan, updateCalplan } from "@/api/mes/cal/calplan";
import Shift from "./shift";
import Team  from "./team";
import {genCode} from "@/api/system/autocode/rule"
export default {
  name: "Calplan",
  dicts: ['mes_shift_method','mes_shift_type','mes_calendar_type','mes_order_status'],
  components: {Shift,Team},
  data() {
    return {
      //自动生成编码
      autoGenFlag:false,
      optType: undefined,
      // 遮罩层
      loading: true,
      formLoading: false,
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
      // 排班计划表格数据
      calplanList: [],
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
        calendarType:null,
        startDate: null,
        endDate: null,
        shiftType: null,
        shiftMethod: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        planCode: [
          { required: true, message: this.$t('mes.计划编号不能为空'), trigger: "blur" }
        ],
        planName: [
          { required: true, message: this.$t('mes.计划名称不能为空'), trigger: "blur" }
        ],
        calendarType:[
          { required: true, message: this.$t('mes.请选择班组类型'), trigger: "blur" }
        ],
        startDate: [
          { required: true, message: this.$t('mes.开始日期不能为空'), trigger: "blur" }
        ],
        endDate: [
          { required: true, message: this.$t('mes.结束日期不能为空'), trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询排班计划列表 */
    getList() {
      this.loading = true;
      listCalplan(this.queryParams).then(response => {
        this.calplanList = response.rows;
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
        calendarType:null,
        startDate: null,
        endDate: null,
        shiftType: 'SHIFT_TWO',
        shiftMethod: 'MONTH',
        shiftCount: 1,
        status: "PREPARE",
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
      this.ids = selection.map(item => item.planId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加排班计划');
      this.optType = "add";
    },
    // 查询明细按钮操作
    handleView(row){
      this.reset();
      const planId = row.planId || this.ids
      getCalplan(planId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.查看排班计划');
        this.optType = "view";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const planId = row.planId || this.ids
      getCalplan(planId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改排班计划');
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {      
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.planId != null) {
            updateCalplan(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addCalplan(this.form).then(response => {
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
      const planIds = row.planId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除排班计划编号为{v}的数据项？', { v: planIds })).then(function() {
        return delCalplan(planIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    handleFinish(){
      let that = this;
      this.$modal.confirm(this.$t('mes.是否完成计划编制？【完成后将不能更改】')).then(function(){
        that.form.status = 'CONFIRMED';
        that.$refs["form"].validate(valid => {
        if (valid) {
          if (that.form.planId != null) {
            updateCalplan(that.form).then(response => {
              that.$modal.msgSuccess("已完成");
              that.open = false;
              that.getList();
              that.formLoading = false;
            },err =>{
              that.form.status = 'PREPARE';
              that.formLoading = false;
            });
          }
        }
      });
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('cal/calplan/export', {
        ...this.queryParams
      }, `calplan_${new Date().getTime()}.xlsx`)
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag){
      if(autoGenFlag){
        genCode('CAL_PLAN_CODE').then(response =>{
          this.form.planCode = response;
        });
      }else{
        this.form.planCode = null;
      }
    },
  }
};
</script>
