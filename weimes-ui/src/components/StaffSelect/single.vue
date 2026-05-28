<template>
  <el-dialog title="人员选择" v-if="showFlag" :visible.sync="showFlag" :modal=false width="80%" center>
    <el-row :gutter="20">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="员工编码" prop="staffCode">
          <el-input v-model="queryParams.staffCode" placeholder="请输入员工编码" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="员工名称" prop="staffName">
          <el-input v-model="queryParams.staffName" placeholder="请输入员工名称" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option v-for="dict in dict.type.hrm_staff_status" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="hrmStaffList" @current-change="handleCurrent" @row-dblclick="handleRowDbClick" @selection-change="handleSelectionChange">
        <el-table-column  width="55" align="center" >
            <template v-slot="scope">
                <el-radio v-model="selectedId" :label="scope.row.staffId" @change="handleRowChange(scope.row)">{{""}}</el-radio>
            </template>
        </el-table-column>   
        <el-table-column label="员工主键id" align="center" prop="staffId" v-if="false" />
        <el-table-column label="员工编码" align="center" prop="staffCode" width="190" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleProcess(scope.row)" v-hasPermi="['st:hrmstaff:edit']">{{
              scope.row.staffCode }}</el-button>
          </template>
        </el-table-column>
        <el-table-column label="员工名称" align="center" prop="staffName" show-overflow-tooltip />
        <el-table-column label="联系电话" align="center" prop="contactPhone" show-overflow-tooltip />
        <el-table-column label="邮箱" align="center" prop="email" show-overflow-tooltip />
        <el-table-column label="用户性别" align="center" prop="sex">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.sex" />
          </template>
        </el-table-column>
        <el-table-column label="学历" align="center" prop="education">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.hrm_education" :value="scope.row.education" />
          </template>
        </el-table-column>
        <el-table-column label="婚姻状况" align="center" prop="maritalStatus">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.hrm_marital_status" :value="scope.row.maritalStatus" />
          </template>
        </el-table-column>
        <el-table-column label="民族" align="center" prop="ethnicity">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.hrm_ethnicity" :value="scope.row.ethnicity" />
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.hrm_staff_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="出生年月" align="center" prop="birthDate" width="120">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.birthDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="政治面貌" align="center" prop="politicalOutlook">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.hrm_political_outlook" :value="scope.row.politicalOutlook" />
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
        @pagination="getList" />
    </el-row>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="confirmSelect">确 定</el-button>
      <el-button @click="showFlag = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { listTalents } from "@/api/hrm/sm/hrm-staff";
import RankSelect from "@/components/RankSelect/single.vue";

export default {
  name: "StaffSelect",
  components: {
    RankSelect
  },
  dicts: ['hrm_marital_status', 'sys_user_sex', 'hrm_political_outlook', 'hrm_education', 'hrm_staff_status', 'hrm_ethnicity'],
  data() {
    return {
      showFlag:false,
      selectedRow: null,
      // 选中数组
      selectedId: null,
      // 计划编码表格数据
      codeList: [],
      cateProps: {
        value: "value",
        label: "label",
        children: "children",
      },
      //自动生成按钮
      autoGenFlag: false,
      optType: undefined,//标识打开的dialog
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
      // 员工花名册表格数据
      hrmStaffList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        staffCode: null,
        staffName: null,
        contactPhone: null,
        sex: null,
        ethnicity: null,
        birthDate: null,
        email: null,
        maritalStatus: null,
        politicalOutlook: null,
        idCard: null,
        origin: null,
        householdAddress: null,
        documentNumber: null,
        currentAddress: null,
        emergencyContact: null,
        relationship: null,
        emergencyContactPhone: null,
        education: null,
        joinedTime: null,
        leaveTime: null,
        createUserId: null,
        updateUserId: null,
        status: null
      },
      // 表单参数
      form: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询员工花名册列表 */
    getList() {
      this.loading = true;
      listTalents(this.queryParams).then(response => {
        this.hrmStaffList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    //行双击选中
    handleRowDbClick(row){
      if(row){
        this.selectedRow = row;
        this.$emit('onSelected',this.selectedRow);
        this.showFlag = false;
      }
    },
    handleCurrent(row){
        if(row){
            this.selectedRow = row;
        }
    },
    // 单选选中数据
    handleRowChange(row) {
      if(row){
        this.selectedRow = row;
      }
    },
    //确定选中
    confirmSelect(){
        if(this.selectedId == null || this.selectedId == 0){
            this.$notify({
                title:'提示',
                type:'warning',
                message: '请至少选择一条数据!'
            });
            return;
        }
        this.$emit('onSelected',this.selectedRow);
        this.showFlag = false;
    }
  }
};
</script>
