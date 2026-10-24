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
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['sm:hrminterviewrecord:export']">{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="hrmInterviewRecordList" @selection-change="handleSelectionChange"
       :tree-props="{ children: 'children' }" :row-key="getRowKeys">
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
      <el-table-column :label="$t('hrm.面试反馈')" :show-overflow-tooltip="true" align="center" prop="interviewFeedback" />
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
            @click="handleView(scope.row)" v-hasPermi="['sm:hrminterviewrecord:edit']">{{ $t('hrm.查看面试') }}</el-button>
          <el-button type="text" size="mini" icon="el-icon-edit" @click="handleView(scope.row)"
            v-if="scope.row.interviewRecordId === null && scope.row.status === '30'">{{ $t('hrm.查看申请') }}</el-button>
          <el-button type="text" size="mini" icon="el-icon-edit" @click="audit(scope.row)"
            v-if="scope.row.interviewRecordId === null && scope.row.status === '30'">{{ $t('lims.审核') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改面试记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" label-width="80px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.员工主键id')" prop="staffId" v-if="false">
              <el-input v-model="form.staffId" :placeholder="$t('hrm.请输入员工主键id')" />
            </el-form-item>
            <el-form-item :label="$t('hrm.人员编码')" prop="staffCode">
              <el-input v-model="form.staffCode" :placeholder="$t('hrm.请选择人员')" class="normal-input-colour" disabled>
              </el-input>
            </el-form-item>
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
            <el-form-item :label="$t('hrm.面试官')" prop="interviewerName">
              <el-input v-model="form.interviewerName" readonly="readonly" :placeholder="$t('hrm.请选择面试官')">
              </el-input>
            </el-form-item>
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
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.定级职级')" prop="rankName">
              <el-input v-model="form.rankName" :placeholder="$t('hrm.请选择定级职级层次名称')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('hrm.期望薪资')" prop="salaryExpectation">
              <el-input v-model="form.salaryExpectation" :placeholder="$t('hrm.请输入期望薪资')" />
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
              <el-select v-model="form.status" :placeholder="$t('hrm.请选择本次面试结果')" class="no-select-color" disabled>
                <el-option v-for="dict in dict.type.hrm_staff_status" :key="dict.value" :label="dict.label"
                  :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :offset="16" :span="8">
            <el-form-item :label="$t('hrm.最终薪资')" prop="actualSalary" v-if="form.status === '20'">
              <el-input v-model="form.actualSalary" type="number" :placeholder="$t('hrm.请输入最终薪资')" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="success" @click="pass" v-if="this.optType === 'edit'">{{ this.$t('hrm.审核通过') }}</el-button>
        <el-button type="danger" @click="refuse" v-if="this.optType === 'edit'">{{ this.$t('hrm.审核拒绝') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAudit, refuse, pass } from "@/api/hrm/sm/hrm-employment-application";
import { getHrmInterviewRecord } from "@/api/hrm/sm/hrm-interview-record";
export default {
  name: "HrmInterviewAudit",
  components: {
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
      optType: "view",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        staffCode: null, staffName: null, interviewerName: null,
      },
      // 表单参数
      form: {
      },
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询面试记录列表 */
    getList() {
      this.loading = true;
      listAudit(this.queryParams).then(response => {
        this.hrmInterviewRecordList = response.rows;
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.interviewRecordId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.reset();
      let interviewRecordId = row.interviewRecordId;
      if (this.isEmpty(row.interviewRecordId)) {
        // 获取最后一次面试记录
        const latest = this.getLatestData(row);
        interviewRecordId = latest.interviewRecordId;
      }
      getHrmInterviewRecord(interviewRecordId).then(response => {
        this.form = response.data;
        this.optType = "view";
        this.open = true;
        this.title = this.$t('hrm.面试结果');
      });
    },
    /** 审核按钮 */
    audit(row) {
      this.reset();
      let interviewRecordId = row.interviewRecordId;
      if (this.isEmpty(row.interviewRecordId)) {
        // 获取最后一次面试记录
        const latest = this.getLatestData(row);
        interviewRecordId = latest.interviewRecordId;
      }
      getHrmInterviewRecord(interviewRecordId).then(response => {
        this.form = response.data;
        this.form.remark = '';
        this.optType = "edit";
        this.open = true;
        this.title = this.$t('hrm.面试结果');
      });
    },
    /** 审核通过按钮 */
    pass() {
      if (!this.checkData()) {
        return;
      }
      this.$modal
        .confirm(this.$t('hrm.确认审核通过吗？审核通过后即代表员工成功入职，并会自动创建员工账户！'))
        .then(() => {
          pass(this.form)
            .then((response) => {
              this.$modal.msgSuccess(this.$t('hrm.入职成功'));
              this.open = false;
              this.getList();
            })

        })
        .catch(() => {
          this.open = false;
          this.$modal.msgError(this.$t('hrm.未知错误!'));
        });
    },
    /** 审核拒绝按钮 */
    refuse() {
      if (!this.checkData()) {
        return;
      }
    },
    /** 数据校验 */
    checkData() {
      if (this.form.staffId === null) {
        this.$modal.msgSuccess(this.$t('hrm.数据有误，请联系管理员'));
        return false;
      }
      if (this.form.interviewRecordId === null) {
        this.$modal.msgSuccess(this.$t('hrm.数据有误，请联系管理员'));
        return false;
      }
      if (this.isEmpty(this.form.remark)) {
        this.$modal.msgError(this.$t('hrm.请在备注栏中输入审批意见！'));
        return false;
      }
      return true;
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
    },
    getLatestData(row) {
      return row.children.reduce((latest, current) => {
        return new Date(current.timeForInterview) > new Date(latest.timeForInterview) ? current : latest;
      });
    }
  }
};
</script>
