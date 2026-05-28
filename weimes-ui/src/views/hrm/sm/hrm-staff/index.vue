<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工编码" prop="staffCode">
        <el-input v-model="queryParams.staffCode" placeholder="请输入员工编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="员工名称" prop="staffName">
        <el-input v-model="queryParams.staffName" placeholder="请输入员工名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!--  <el-form-item label="用户性别" prop="sex">
        <el-select v-model="queryParams.sex" placeholder="请选择性别" clearable style="width: 120px;">
          <el-option v-for="dict in dict.type.sys_user_sex" 
                      :key="dict.value" 
                      :label="dict.label"
                      :value="dict.value"/>
        </el-select>
      </el-form-item> -->
      <el-form-item label="学历" prop="education">
        <el-select v-model="queryParams.education" placeholder="请选择学历" clearable style="width: 120px;">
          <el-option v-for="dict in dict.type.hrm_education" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="入职日期" prop="joinedTime">
        <el-date-picker clearable v-model="queryParams.joinedTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择入职日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="离职日期" prop="leaveTime">
        <el-date-picker clearable v-model="queryParams.leaveTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择离职日期">
        </el-date-picker>
      </el-form-item> -->

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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['st:hrmstaff:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-if="false" type="success" plain icon="el-icon-edit" size="mini" :disabled="single"
          @click="handleUpdate" v-hasPermi="['st:hrmstaff:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['st:hrmstaff:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['st:hrmstaff:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="hrmStaffList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
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
      <!--   <el-table-column label="职级主键id" align="center" prop="rankId" v-if="false" />
      <el-table-column label="职级编码" align="center" prop="rankCode" show-overflow-tooltip  width="100"/>
      <el-table-column label="职级层次" align="center" prop="rankType" />
      <el-table-column label="职级层次名称" align="center" prop="rankName" show-overflow-tooltip  width="100"/>
      <el-table-column label="籍贯" align="center" prop="origin" show-overflow-tooltip />
      <el-table-column label="户口详细地址" align="center" prop="householdAddress" show-overflow-tooltip width="120" />
      <el-table-column label="现住址" align="center" prop="currentAddress" show-overflow-tooltip width="120" />
      <el-table-column label="紧急联系人" align="center" prop="emergencyContact" width="100" />
      <el-table-column label="关系" align="center" prop="relationship" />
      <el-table-column label="紧急联系电话" align="center" prop="emergencyContactPhone" show-overflow-tooltip width="100" />
      <el-table-column label="创建者id" align="center" prop="createUserId" v-if="false" />
      <el-table-column label="修改者id" align="center" prop="updateUserId" v-if="false" />
      <el-table-column label="创建者人" align="center" prop="createBy" />
      <el-table-column label="修改者人" align="center" prop="updateBy" /> 
       <el-table-column label="入职日期" align="center" prop="joinedTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.joinedTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="离职日期" align="center" prop="leaveTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.leaveTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['st:hrmstaff:edit']">修改</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-thumb" @click="handleProcess(scope.row)"
            v-hasPermi="['st:hrmstaff:edit']" v-if="scope.row.status === '0'">邀请面试</el-button> -->
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['st:hrmstaff:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改员工花名册对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="员工编码" prop="staffCode">
              <el-input v-model="form.staffCode" placeholder="请输入员工编码" />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item v-if="form.status !== '0' && optType !== 'process'" label-width="60">
              <el-switch v-model="autoGenFlag" active-color="#13ce66" active-text="自动生成"
                @change="handleAutoGenChange(autoGenFlag)" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="员工名称" prop="staffName">
              <el-input v-model="form.staffName" placeholder="请输入员工名称" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="学历" prop="education">
              <el-select v-model="form.education" placeholder="请选学历" 婚姻状况>
                <el-option v-for="dict in dict.type.hrm_education" :key="dict.value" :label="dict.label"
                  :value="dict.value" />
              </el-select>
              <!-- <el-input v-model="form.education" placeholder="请输入学历" /> -->
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="职级编码" prop="rankCode">
              <el-input v-model="form.rankCode" placeholder="请选择职级">
                <el-button slot="append" icon="el-icon-search" @click="handleRankSelect"></el-button>
              </el-input>
            </el-form-item>
            <RankSelect ref="woSelect" @onSelected="onRankSelected"></RankSelect>
          </el-col>
          <el-col :span="7">
            <el-form-item label="职级名称" prop="rankName">
              <el-input v-model="form.rankName" placeholder="请输入职级名称" readonly="readonly" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="职级层次" prop="rankType">
              <el-input v-model="form.rankType" placeholder="请输入职级层次" readonly="readonly" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="薪资范围" prop="salaryRangeMin">
              <el-input style="width: 120px;" v-model="form.salaryRangeMin" placeholder="薪资上限" readonly="readonly" />
              <b>~</b>
              <el-input style="width: 120px;" v-model="form.salaryRangeMax" placeholder="薪资下限" readonly="readonly" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="实际薪资" prop="actualSalary">
              <el-input v-model="form.actualSalary" placeholder="请输入实际薪资" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择性别">
                <el-option v-for="dict in dict.type.sys_user_sex" :key="dict.value" :label="dict.label"
                  :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="民族" prop="ethnicity">
              <el-select v-model="form.ethnicity" placeholder="请选择民族" filterable>
                <el-option v-for="dict in dict.type.hrm_ethnicity" :key="dict.value" :label="dict.label"
                  :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="出生年月" prop="birthDate">
              <el-date-picker clearable v-model="form.birthDate" type="date" value-format="yyyy-MM-dd"
                placeholder="请选择出生年月">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="婚姻状况">
              <el-select v-model="form.maritalStatus" placeholder="请选择婚姻状况">
                <el-option v-for="dict in dict.type.hrm_marital_status" :key="dict.value" :label="dict.label"
                  :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="政治面貌" prop="politicalOutlook">
              <el-select v-model="form.politicalOutlook" placeholder="请选政治面貌">
                <el-option v-for="dict in dict.type.hrm_political_outlook" :key="dict.value" :label="dict.label"
                  :value="dict.value" />
              </el-select>
              <!--   <el-input v-model="form.politicalOutlook" placeholder="请输入政治面貌" /> -->
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="籍贯" prop="origin">
              <el-input v-model="form.origin" placeholder="请输入籍贯" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="户口详细地址" prop="householdAddress" v-model="form.householdAddress">
              <!--  <el-input " placeholder="请输入户口详细地址" /> -->
              <el-col :span="10">
                <el-cascader style="width: 100%" clearable size="large" :options="regionDatas" ref="cascaderAddr"
                  :props="cateProps" v-model="form.houseAddr1" @change="handleHouseholdAddr" />
              </el-col>
              <el-col :offset="1" :span="12">
                <el-input v-model="form.houseAddr2" placeholder="请输入详细地址" clearable />
              </el-col>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="现住址" prop="currentAddress">
              <!-- <el-input v-model="form.currentAddress" placeholder="请输入现住址" /> -->
              <el-col :span="10">
                <el-cascader style="width: 100%" clearable size="large" :options="regionDatas" ref="cascaderAddr"
                  :props="cateProps" v-model="form.currentAddr1" @change="handleCurrentAddr" />
              </el-col>
              <el-col :offset="1" :span="12">
                <el-input v-model="form.currentAddr2" placeholder="请输入详细地址" clearable />
              </el-col>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="紧急联系电话" prop="emergencyContactPhone">
              <el-input v-model="form.emergencyContactPhone" placeholder="请输入紧急联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="关系" prop="relationship">
              <el-input v-model="form.relationship" placeholder="请输入关系" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="8">
            <el-form-item label="入职日期" prop="joinedTime" v-if="form.status === '3' || form.status === '4'">
              <el-date-picker clearable v-model="form.joinedTime" type="date" value-format="yyyy-MM-dd"
                placeholder="请选择入职日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="离职日期" prop="leaveTime" v-if="form.status === '3' || form.status === '4'">
              <el-date-picker clearable v-model="form.leaveTime" type="date" value-format="yyyy-MM-dd"
                placeholder="请选择离职日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row> -->
        <!--  <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="optType === 'edit' || optType === 'add'">确 定</el-button>
        <!-- <el-button type="warning" @click="interview(form)" v-if="form.status === '0' && optType == 'process'">邀请面试</el-button> -->
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { delHrmStaff, addHrmStaff, updateHrmStaff, listTalents, getTalent } from "@/api/hrm/sm/hrm-staff";
import { genCode } from '@/api/system/autocode/rule';
import RankSelect from "@/components/RankSelect/single.vue";

import {
  codeToText,
  pcaTextArr,
} from "element-china-area-data";


export default {
  name: "HrmStaff",
  components: {
    RankSelect
  },
  dicts: ['hrm_marital_status', 'sys_user_sex', 'hrm_political_outlook', 'hrm_education', 'hrm_staff_status', 'hrm_ethnicity'],
  data() {
    return {
      // 计划编码表格数据
      codeList: [],
      regionDatas: pcaTextArr,
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
      form: {},
      // 表单校验
      rules: {
        staffCode: [
          { required: true, message: '请输入员工编码', trigger: 'blur' }
        ],
        staffName: [
          { required: true, message: '请输入员工名称', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3456789]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        email: [
          { required: false, message: '邮箱格式不正确', trigger: 'blur' },
          { pattern: /^[\w\.-]+@[a-zA-Z\d\.-]+\.[a-zA-Z]{2,}$/, message: '邮箱格式不正确', trigger: 'blur' }
        ],
        /* education: [
          { required: true, message: '请选择学历', trigger: 'change' }
        ],
        sex: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
       
        ethnicity: [
          { required: true, message: '请选择民族', trigger: 'change' }
        ],
        maritalStatus: [
          { required: true, message: '请选择婚姻状况', trigger: 'change' }
        ],
        birthDate: [
          { type: 'date', required: true, message: '请选择出生年月', trigger: 'change' }
        ],
        politicalOutlook: [
          { required: true, message: '请选择政治面貌', trigger: 'change' }
        ],
        origin: [
          { required: true, message: '请输入籍贯', trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
        ],
        householdAddress: [
          { required: true, message: '请输入户口详细地址', trigger: 'blur' },
          { validator: this.validateHouseholdAddress, trigger: 'blur' }
        ],
        currentAddress: [
          { required: true, message: '请输入现住址', trigger: 'blur' }
        ],
        emergencyContactPhone: [
          { pattern: /^1[3456789]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ] */
      }
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
    //选择职级
    handleRankSelect() {
      this.$refs.woSelect.showFlag = true;
    },
    onRankSelected(row) {
      if (row != undefined && row != null) {
        this.form.rankId = row.rankId;
        this.form.rankCode = row.rankCode;
        this.form.rankName = row.rankName;
        this.form.salaryRangeMin = row.salaryRangeMin;
        this.form.salaryRangeMax = row.salaryRangeMax;
        this.form.rankType = row.rankType
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
        staffId: null,
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
        createBy: null,
        updateUserId: null,
        updateBy: null,
        createTime: null,
        updateTime: null,
        status: 0,
        houseAddr1: [],
        houseAddr2: "",
        currentAddr1: [],
        currentAddr2: "",
        rankCode: null,
        rankId: null,
        rankName: null,
        salaryRangeMax: null,
        salaryRangeMin: null
      };
      this.autoGenFlag = false;
      this.resetForm("form");
    },
    //级联选择器详细数据 户口地址
    handleHouseholdAddr(value) {
      this.form.houseAddr1 = value;
      this.form.houseAddr1.map((item) => (name += codeToText[item] + "")); //将省市区三个拼接一起 
      //好😴 想💤 🐱‍🐉🐱‍👓🎉 下班 出来干什么 栓q 又出来        
      if (value.length == 0) {
        this.form.houseAddr2 = '';
      }
    },
    //级联选择器详细数据 现住址
    handleCurrentAddr(value) {
      if (value == null)
        return;
      this.form.currentAddr1 = value;
      if (value.length == 0) {
        this.form.currentAddr2 = '';
      }
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
      this.ids = selection.map(item => item.staffId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag) {
      if (autoGenFlag) {
        genCode("STAFFE_CODE").then(response => {
          this.form.staffCode = response
        })
      } else {
        this.form.staffCode = null
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加员工花名册";
      this.optType = 'add';
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const staffId = row.staffId || this.ids
      getTalent(staffId).then(response => {
        this.form = response.data;
        if (response.data.householdAddress != null) {
          //解析数据库数据户口详情地址逗号分隔
          let arrayHouseAdr = response.data.householdAddress.split(',');
          this.$set(this.form, 'houseAddr2', arrayHouseAdr[arrayHouseAdr.length - 1]);
          this.$set(this.form, 'houseAddr1', arrayHouseAdr.slice(0, arrayHouseAdr.length - 1));
        }
        if (response.data.currentAddress != null) {
          //现住址逗号分隔
          let arrayCurrentAdr = response.data.currentAddress.split(',');
          this.$set(this.form, 'currentAddr2', arrayCurrentAdr[arrayCurrentAdr.length - 1]);
          this.$set(this.form, 'currentAddr1', arrayCurrentAdr.slice(0, arrayCurrentAdr.length - 1));
        }
        this.open = true;
        this.title = "修改员工花名册";
        this.optType = 'edit'
      });
    },

    /** 修改按钮操作 */
    handleProcess(row) {
      this.reset();
      const staffId = row.staffId || this.ids
      getTalent(staffId).then(response => {
        this.form = response.data;
        if (response.data.householdAddress != null) {
          //解析数据库数据户口详情地址逗号分隔
          let arrayHouseAdr = response.data.householdAddress.split(',');
          this.$set(this.form, 'houseAddr2', arrayHouseAdr[arrayHouseAdr.length - 1]);
          this.$set(this.form, 'houseAddr1', arrayHouseAdr.slice(0, arrayHouseAdr.length - 1));
        }
        if (response.data.currentAddress != null) {
          //现住址逗号分隔
          let arrayCurrentAdr = response.data.currentAddress.split(',');
          this.$set(this.form, 'currentAddr2', arrayCurrentAdr[arrayCurrentAdr.length - 1]);
          this.$set(this.form, 'currentAddr1', arrayCurrentAdr.slice(0, arrayCurrentAdr.length - 1));
        }
        this.open = true;
        this.title = "审批员工花名册";
        this.optType = 'process'
      });
    },
    /**
     * 提取所有的地址集处理数据赋值
     */
    setAddressList() {
      this.codeList.push(this.form); //将新增数据push到dataList数组中
      this.codeList.forEach((element) => {
        //将地址前部分逗号分隔
        if (this.form.houseAddr1 != null && this.form.houseAddr1.length > 0) {
          element.householdAddress = this.form.houseAddr1.join(",") + ',' + this.form.houseAddr2; // 将级联选择器的地址和input里的详细地址合并赋值给table数据表中
        }
        if (this.form.currentAddr1 != null && this.form.currentAddr1.length > 0) {
          element.currentAddress = this.form.currentAddr1.join(",") + ',' + this.form.currentAddr2; // 将级联选择器的地址和input里的详细地址合并赋值给table数据表中
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.staffId != null) {
            this.setAddressList();
            updateHrmStaff(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            this.setAddressList();
            addHrmStaff(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const staffIds = row.staffId || this.ids;
      this.$modal.confirm('是否确认删除员工花名册编号为"' + staffIds + '"的数据项？').then(function () {
        return delHrmStaff(staffIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/hrm/st/hrm-staff/export', {
        ...this.queryParams
      }, `hrm-staff_${new Date().getTime()}.xlsx`)
    },
    /**自定义校验规则 */
    validateHouseholdAddress(rule, value, callback) {
      if (!value || value.trim() === '') {
        callback(new Error('请输入户口详细地址'));
      } else {
        // 校验成功
        callback();
      }
    },
  }
};
</script>
