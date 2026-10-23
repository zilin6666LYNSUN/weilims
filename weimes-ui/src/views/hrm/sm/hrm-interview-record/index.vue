<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('hrm.人员编码')" prop="staffCode" v-if="false">
        <el-input v-model="queryParams.staffCode" :placeholder="$t('hrm.请输入员工编码')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('hrm.人员名称')" prop="staffName">
        <el-input v-model="queryParams.staffName" :placeholder="$t('hrm.请输入员工名称')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('hrm.面试官')" prop="interviewerName">
        <el-input v-model="queryParams.interviewerName" :placeholder="$t('hrm.请输入面试官姓名')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['sm:hrminterviewrecord:add']">{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['sm:hrminterviewrecord:edit']">{{ this.$t('system.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['sm:hrminterviewrecord:remove']">{{ this.$t('common.delete') }}</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['sm:hrminterviewrecord:export']">{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="hrmInterviewRecordList" @selection-change="handleSelectionChange"
       :tree-props="{ children: 'children' }" :row-key="getRowKeys">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column :label="$t('hrm.面试记录主键')" align="center" prop="interviewRecordId" v-if="false" />
      <el-table-column :label="$t('hrm.人员编码')" align="center" prop="staffCode" v-if="false" />
      <el-table-column :label="$t('hrm.人员名称')" align="center" prop="staffName" />
      <el-table-column :label="$t('system.联系电话')" align="center" prop="contactPhone" show-overflow-tooltip />
      <el-table-column :label="$t('system.邮箱')" align="center" prop="email" show-overflow-tooltip v-if="false" />
      <el-table-column :label="$t('system.用户性别')" align="center" prop="sex">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.sex" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('hrm.定级')" align="center" prop="rankType" />
      <el-table-column :label="$t('hrm.定级名称')" align="center" prop="rankName" v-if="false" />
      <el-table-column :label="$t('hrm.面试反馈')" show-overflow-tooltip align="center" prop="interviewFeedback" />
      <el-table-column :label="$t('hrm.面试结果')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hrm_staff_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('hrm.面试次数')" align="center" prop="times" />
      <el-table-column :label="$t('hrm.面试时间')" align="center" prop="timeForInterview" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.timeForInterview, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('hrm.面试官')" align="center" prop="interviewerName" />
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.interviewRecordId != null" size="mini" type="text" icon="el-icon-edit"
            @click="handleUpdate(scope.row)" v-hasPermi="['sm:hrminterviewrecord:edit']">{{ this.$t('hrm.反馈') }}</el-button>
          <el-button v-if="scope.row.interviewRecordId != null" size="mini" type="text" icon="el-icon-delete"
            @click="handleDelete(scope.row)" v-hasPermi="['sm:hrminterviewrecord:remove']">{{ this.$t('common.delete') }}</el-button>
          <el-button type="text" size="mini" icon="el-icon-edit" @click="handleAdd(scope.row)"
            v-if="scope.row.interviewRecordId === null">{{ this.$t('hrm.邀请面试') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改面试记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.员工主键id')" prop="staffId" v-if="false">
              <el-input v-model="form.staffId" :placeholder="$t('hrm.请输入员工主键id')" />
            </el-form-item>
            <el-form-item :label="$t('hrm.人员编码')" prop="staffCode">
              <el-input v-model="form.staffCode" :placeholder="$t('hrm.请选择人员')" class="normal-input-colour" disabled>
                <!-- <el-button slot="append" icon="el-icon-search" @click="handleStaffSelect"></el-button> -->
              </el-input>
            </el-form-item>
            <StaffSelect ref="staffSelect" @onSelected="onStaffSelected"></StaffSelect>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.人员名称')" prop="staffName">
              <el-input v-model="form.staffName" :placeholder="$t('hrm.选择人员后展示')" class="normal-input-colour" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.证件信息')" prop="idCard">
              <el-input v-model="form.idCard" :placeholder="$t('hrm.暂无信息')" class="normal-input-colour" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('system.归属部门')" prop="deptId">
              <treeselect v-model="form.deptId" :options="deptOptions" :show-count="true" :placeholder="$t('system.请选择归属部门')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('system.岗位')" prop="postId">
              <el-select v-model="form.postId"  :placeholder="$t('system.请选择岗位')">
                <el-option v-for="item in postOptions" :key="item.postId" :label="item.postName" :value="item.postId"
                  :disabled="item.status == 1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('system.角色')" prop="roleId">
              <el-select v-model="form.roleId"  :placeholder="$t('system.请选择角色')">
                <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId"
                  :disabled="item.status == 1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.面试官')" prop="interviewerName">
              <el-input v-model="form.interviewerName" readonly="readonly" :placeholder="$t('hrm.请选择面试官')">
                <el-button slot="append" @click="handleUserSelect" icon="el-icon-search"></el-button>
              </el-input>
            </el-form-item>
            <UserSingleSelect ref="userSelect" @onSelected="onUserSelected"></UserSingleSelect>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.面试时间')" prop="timeForInterview">
              <el-date-picker clearable v-model="form.timeForInterview" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('hrm.请选择面试时间')">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.面试次数')" prop="times">
              <el-input v-model="form.times" :placeholder="$t('hrm.请输入面试次数')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.定级级别')" prop="rankType">
              <el-input v-model="form.rankType" :placeholder="$t('hrm.请选择职级')">
                <el-button slot="append" icon="el-icon-search" @click="handleRankSelect"></el-button>
              </el-input>
              <RankSelect ref="woSelect" @onSelected="onRankSelected"></RankSelect>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.定级职级')" prop="rankName">
              <el-input v-model="form.rankName" :placeholder="$t('hrm.请选择定级职级层次名称')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.期望薪资')" prop="salaryExpectation">
              <el-input v-model="form.salaryExpectation" type="number" :placeholder="$t('hrm.请输入期望薪资')" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item :label="$t('hrm.面试反馈')" prop="interviewFeedback">
          <el-input v-model="form.interviewFeedback" type="textarea" :placeholder="$t('common.inputContent')" />
        </el-form-item>
        <el-form-item :label="$t('system.备注')" prop="remark">
          <el-input v-model="form.remark" type="textarea" :placeholder="$t('lims.请输入备注')" />
        </el-form-item>
        <el-row>
          <el-col :offset="16" :span="8">
            <el-form-item label-width="120px" :label="$t('hrm.本次面试结果')" prop="status" v-if="form.interviewRecordId !== null">
              <el-select v-model="form.status" :placeholder="$t('hrm.请选择本次面试结果')">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
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
import { listHrmInterviewRecord, getHrmInterviewRecord, delHrmInterviewRecord, addHrmInterviewRecord, updateHrmInterviewRecord } from "@/api/hrm/sm/hrm-interview-record";
import { getUser } from "@/api/system/user";
import { treeselect } from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import StaffSelect from "@/components/StaffSelect/single.vue";
import RankSelect from "@/components/RankSelect/single.vue";
import UserSingleSelect from "@/components/userSelect/single.vue"
export default {
  name: "HrmInterviewRecord",
  components: {
    StaffSelect,
    RankSelect,
    UserSingleSelect,
    Treeselect
  },
  dicts: ['hrm_marital_status', 'sys_user_sex', 'hrm_political_outlook', 'hrm_education', 'hrm_staff_status', 'hrm_ethnicity'],
  data() {
    return {
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
      // 面试记录表格数据
      hrmInterviewRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        staffCode: null, staffName: null, interviewerName: null,
      },
      // 表单参数
      form: {
      },
      statusOptions: [{
        value: '10',
        label: this.$t('hrm.面试')
      }, {
        value: '11',
        label: this.$t('hrm.待复试')
      }, {
        value: '20',
        label: this.$t('hrm.面试通过')
      }, {
        value: '21',
        label: this.$t('hrm.面试失败')
      }, {
        value: '22',
        label: this.$t('hrm.待定')
      },],
      // 表单校验
      rules: {
        staffId: [
          { required: true, message: this.$t('hrm.员工主键id不能为空'), trigger: "blur" }
        ], staffCode: [
          { required: true, message: this.$t('hrm.人员编码不能为空'), trigger: "blur" }
        ], staffName: [
          { required: true, message: this.$t('hrm.人员名称不能为空'), trigger: "blur" }
        ], status: [
          { required: true, message: this.$t('hrm.请选择本轮面试结果'), trigger: "blur" }
        ], timeForInterview: [
          { required: true, message: this.$t('hrm.请选择面试时间'), trigger: "blur" }
        ], times: [
          { required: true, message: this.$t('hrm.请填写面试次数'), trigger: "blur" }
        ], interviewerName: [
          { required: true, message: this.$t('hrm.请选择面试官'), trigger: "blur" }
        ],deptId: [
          { required: true, message: this.$t('system.请选择归属部门'), trigger: "blur" }
        ],postId: [
          { required: true, message: this.$t('hrm.请选择面试岗位'), trigger: ["change","blur"] }
        ],roleId: [
          { required: true, message: this.$t('hrm.请选择面试角色'), trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getTreeselect();
    this.getDeptAndRole();
    this.getList();
  },
  methods: {
    /** 查询面试记录列表 */
    getList() {
      this.loading = true;
      listHrmInterviewRecord(this.queryParams).then(response => {
        this.hrmInterviewRecordList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then(response => {
        this.deptOptions = response.data;
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
        interviewRecordId: null, staffId: null, staffCode: null, staffName: null, rankId: null, rankCode: null, rankType: null, rankName: null, actualSalary: null, salaryExpectation: null, interviewFeedback: null, times: null, timeForInterview: null, interviewerId: null, interviewerName: null, createUserId: null, createBy: null, createTime: null, updateUserId: null, updateBy: null, updateTime: null, status: 0, remark: null, deleted: null, deleteat: null, version: null
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
    //选择人员
    handleStaffSelect() {
      this.$refs.staffSelect.showFlag = true;
    },
    onStaffSelected(row) {
      if (row != undefined && row != null) {
        this.form.staffId = row.staffId;
        this.form.staffName = row.staffName;
        this.form.staffCode = row.staffCode;
        this.form.idCard = row.idCard;
      }
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
    handleUserSelect() {
      this.$refs.userSelect.showFlag = true;
    },
    onUserSelected(row) {
      if (row != null) {
        this.form.interviewerId = row.userId;
        this.form.interviewerName = row.userName;
      }
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.interviewRecordId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      this.form.staffId = row.staffId;
        this.form.staffCode = row.staffCode;
        this.form.staffName = row.staffName;
        this.form.idCard = row.idCard;
        this.open = true;
        this.title = this.$t('hrm.邀请面试');
    },
    getDeptAndRole(){
      getUser().then(response => {
        this.postOptions = response.posts;
        this.roleOptions = response.roles;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const interviewRecordId = row.interviewRecordId || this.ids
      getHrmInterviewRecord(interviewRecordId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('hrm.面试反馈');
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.interviewRecordId != null) {
            updateHrmInterviewRecord(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addHrmInterviewRecord(this.form).then(response => {
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
      const interviewRecordIds = row.interviewRecordId || this.ids;
      this.$modal.confirm(this.$t('hrm.是否确认删除面试记录编号为{v}的数据项？', { v: interviewRecordIds })).then(function () {
        return delHrmInterviewRecord(interviewRecordIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => { });
    },
    //树状唯一列值标识
    getRowKeys(row) {
      //  唯一标识
      return row.staffId;
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('sm/hrm-interview-record/export', {
        ...this.queryParams
      }, `hrm-interview-record_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
