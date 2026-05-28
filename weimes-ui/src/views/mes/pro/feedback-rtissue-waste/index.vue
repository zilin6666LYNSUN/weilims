<template>
  <div>
    <el-row>
      <div>
        <div class="feedback">
          <p class="feedback-p">
            <span class="feedback-s"></span>
            <span class="feedback-message">生产报工</span>
          </p>
          <p v-if="optTypeFlag" class="warning-text">*注：保存之后才可新增退料以及废料</p>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="feedback-form">
            <el-row>
              <el-col :span="5">
                <el-form-item label="报工类型" prop="feedbackType">
                  <el-select v-model="form.feedbackType" placeholder="请选择报工类型">
                    <el-option v-for="dict in dict.type.mes_feedback_type" :key="dict.value" :label="dict.label"
                      :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item label="生产工单" prop="workorderCode">
                  <el-input v-model="form.workorderCode" placeholder="请选择生产工单">
                    <el-button slot="append" icon="el-icon-search" @click="handleWorkorderSelect"></el-button>
                  </el-input>
                </el-form-item>
                <WorkorderSelect ref="woSelect" @onSelected="onWorkorderSelected"></WorkorderSelect>
              </el-col>
              <el-col :span="7">
                <el-form-item label="生产任务" prop="taskCode">
                  <el-input v-model="form.taskCode" placeholder="请选择生产任务">
                    <el-button slot="append" icon="el-icon-search" @click="handleTaskSelect"></el-button>
                  </el-input>
                </el-form-item>
                <ProtaskSelect ref="taskSelect" :workorderId="form.workorderId" @onSelected="onTaskSelected">
                </ProtaskSelect>
              </el-col>
              <el-col :span="4">
                <el-form-item label="单位" prop="unitOfMeasure">
                  <el-input v-model="form.unitOfMeasure" readonly="readonly" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="5">
                <el-form-item label="产品编码" prop="itemCode">
                  <el-input v-model="form.itemCode" readonly="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="产品名称" prop="itemName">
                  <el-input v-model="form.itemName" readonly="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="报工人" prop="nickName">
                  <el-input v-model="form.nickName" placeholder="请选择报工人">
                    <el-button slot="append" @click="handleUserSelect" icon="el-icon-search"></el-button>
                  </el-input>
                </el-form-item>
                <UserSingleSelect ref="userSelect" @onSelected="onUserSelected"></UserSingleSelect>
              </el-col>

              <el-col :span="6">
                <el-form-item label="审核人" prop="recordNick">
                  <el-input v-model="form.recordNick" placeholder="请选择审核人">
                    <el-button slot="append" @click="handleUser2Select" icon="el-icon-search"></el-button>
                  </el-input>
                </el-form-item>
                <UserSingleSelect ref="user2Select" @onSelected="onUser2Selected"></UserSingleSelect>
              </el-col>
            </el-row>
            <el-row v-if="form.isCheck == 'N'">
              <el-col :span="4">
                <el-form-item label="报工数量" prop="quantityFeedback">
                  <el-input readonly="readonly" v-model="form.quantityFeedback" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="合格品数量" prop="quantityQualified">
                  <el-input-number :min="0" @change="handleQuantityChanged" v-model="form.quantityQualified"
                    placeholder="请输入合格品数量" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="不良品数量" prop="quantityUnquanlified">
                  <el-input-number :min="0" @change="handleQuantityChanged" v-model="form.quantityUnquanlified"
                    placeholder="请输入不良品数量" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row v-else>
              <el-col :span="5">
                <el-form-item label="报工数量" prop="quantityFeedback">
                  <el-input readonly="readonly" v-model="form.quantityFeedback" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="待检测数量" prop="quantityUncheck">
                  <el-input-number :min="0" @change="handleQuantityChanged" v-model="form.quantityUncheck"
                    placeholder="请输入待检测数量" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="规格型号" prop="specification">
                  <el-input v-model="form.specification" type="textarea" readonly="readonly" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm"
              v-if="form.status == 'PREPARE' && optType != 'view'">保存</el-button>
            <el-button type="warning" @click="handleSubmit"
              v-if="form.status == 'PREPARE' && optType != 'view' && form.recordId != null">提交审批</el-button>
            <el-button type="sucess" @click="handleExecute"
              v-if="form.status == 'APPROVING' && form.recordId != null">审批通过</el-button>
            <el-button type="danger" @click="handleReject"
              v-if="form.status == 'APPROVING' && form.recordId != null">审批不通过</el-button>
            <el-button type="primary" @click="cancel">返回</el-button>
          </div>
        </div>
      </div>
    </el-row>
    <el-row>
      <el-tabs type="border-card" class="tab-panes" v-if="this.form.recordId !== null">
        <el-tab-pane label="生产退料">
          <Rtissue :recordId="this.form.recordId" :workorderCode="this.form.workorderCode" :optType="optType"></Rtissue>
        </el-tab-pane>
        <el-tab-pane label="生产废料">
          <WmWasteForm :recordId="this.form.recordId" :workorderCode="this.form.workorderCode" :optType="optType">
          </WmWasteForm>
        </el-tab-pane>
      </el-tabs>
    </el-row>
  </div>
</template>
<script>
import WmWasteForm from './waste/index.vue';
import Rtissue from './rtissue/index.vue';
import { getFeedback, delFeedback, addFeedback, updateFeedback, execute, refuse } from "@/api/mes/pro/feedback";
import WorkorderSelect from "@/components/workorderSelect/single.vue"
import WorkstationSelect from "@/components/workstationSelect/simpletableSingle.vue"
import UserSingleSelect from "@/components/userSelect/single.vue"
import ProtaskSelect from "@/components/TaskSelect/taskSelectSingle.vue"

export default {
  name: 'FeedbackRtissueWaste',
  components: {
    WmWasteForm, Rtissue, WorkorderSelect, WorkstationSelect, UserSingleSelect, ProtaskSelect
  },
  dicts: ['mes_order_status', 'mes_feedback_type'],
  data() {
    return {
      optTypeFlag: true, //标签栏开关
      optType: undefined,
      saveFlag: true,
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
      // 生产报工记录表格数据
      feedbackList: [],
      // 查询参数
      // 表单参数
      form: {
        workorderCode: '',
        recordId: null
      },
      // 表单校验
      rules: {
        feedbackType: [
          { required: true, message: "报工类型不能为空", trigger: "change" }
        ],
        taskCode: [
          { required: true, message: "请选择生产任务", trigger: "blur" }
        ],
        workorderCode: [
          { required: true, message: "生产工单不能为空", trigger: "blur" }
        ],
        nickName: [
          { required: true, message: "请选择报工人", trigger: "blur" }
        ],
        feedbackTime: [
          { required: true, message: "请选择报工时间", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.optTypeFlag = this.$route.params.optType === 'add'
    this.handleRouteChange();
  },
  beforeRouteLeave(to, from, next) {
    // if (this.saveFlag) {
    //   // 关闭当前页面标签
    //   this.$store.dispatch('tagsView/delView', from).then(() => {
    //     next();
    //   });
    // } else {
    //   this.$modal.confirm('离开页面后不会保存现有未保存的数据，确认离开吗？(点击确认离开当前页面，点击取消停留在当前页面。)').then(function () {
    //   }).then(() => {
    //     // 关闭当前页面标签
    //     this.$store.dispatch('tagsView/delView', from).then(() => {
    //       next();
    //     });
    //   }).catch(() => {
    //     next(false); // 阻止离开
    //   });
    // }
    next();
  },
  watch: {
    form: {
      deep: true, // 深度监听
      handler(newVal) {
        this.saveFlag = false;
      }
    }
  },
  methods: {
    //动态加载数据
    handleRouteChange() {
      this.optType = this.$route.params.optType;
      this.form.workorderCode = this.$route.params.workorderCodeResult;
      if (this.optType === "add") {
        this.reset();
      }
      if (this.optType === "edit" || this.optType === "view") {
        getFeedback(this.$route.params.recordId).then(response => {
          this.form = response.data;
        });
      }
    },
    // 返回取消按钮
    cancel() {
      this.reset();
      let backPage = { path: '/mes/mes/pro/feedback', name: 'Feedback' }
      this.$tab.closeOpenPage(backPage).then(res => { this.$tab.refreshPage(backPage) });
    },
    // 表单重置
    reset() {
      this.form = {
        recordId: null,
        feedbackType: null,
        workstationId: null,
        workstationCode: null,
        workstationName: null,
        routeId: null,
        routeCode: null,
        processId: null,
        processCode: null,
        processName: null,
        workorderId: null,
        workorderCode: null,
        workorderName: null,
        clientOrderCode: null,
        taskId: null,
        taskCode: null,
        itemId: null,
        itemCode: null,
        itemName: null,
        specification: null,
        quantity: 0,
        quantityUncheck: 0,
        quantityFeedback: 0,
        quantityQualified: 0,
        quantityUnquanlified: 0,
        userName: null,
        userId: null,
        nickName: null,
        feedbackChannel: null,
        feedbackTime: new Date(),
        recordUser: null,
        recordUserId: null,
        recordNick: null,
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
    handleQuantityChanged() {
      this.form.quantityFeedback = this.form.quantityQualified + this.form.quantityUnquanlified + this.form.quantityUncheck;
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.recordId != null) {
            updateFeedback(this.form).then(response => {
              this.saveFlag = true;
              this.$modal.msgSuccess("修改成功");
            })
          } else {
            addFeedback(this.form).then(response => {
              this.form.recordId = response.data;
              this.saveFlag = true;
              this.$modal.msgSuccess("新增成功");
            });
          }
        }
      });
    },
    handleSubmit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.status = "APPROVING";
          if (this.form.recordId != null) {
            updateFeedback(this.form).then(response => {
              this.$modal.msgSuccess("提交成功");
              this.cancel();
            });
          }
        }
      });
    },
    //审批通过执行
    handleExecute() {
      const recordIds = this.form.recordId;
      this.$modal.confirm('确认执行报工？').then(function () {
        return execute(recordIds)//执行报工
      }).then(() => {
        this.$modal.msgSuccess("执行成功");
        this.cancel()
      }).catch(() => { });
    },
    //审批不通过，拒绝
    handleReject() {
      if (this.form.recordId != null) {
        refuse(this.form.recordId).then(response => {
          this.$modal.msgSuccess("提交成功");
          this.cancel();
        });
      } else {
        this.$modal.msgError("提交失败，请联系管理员");
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const recordIds = row.recordId || this.ids;
      this.$modal.confirm('是否确认删除生产报工记录编号为"' + recordIds + '"的数据项？').then(function () {
        return delFeedback(recordIds);
      }).then(() => {
        this.$modal.msgSuccess("删除成功");

      }).catch(() => { });
    },
    //选择生产工单
    handleWorkorderSelect() {
      this.$refs.woSelect.showFlag = true;
    },
    onWorkorderSelected(row) {
      if (row != undefined && row != null) {
        this.form.workorderId = row.workorderId;
        this.form.workorderCode = row.workorderCode;
        this.form.workorderName = row.workorderName;
        this.form.clientOrderCode = row.clientOrderCode;
        this.form.itemId = row.productId;
        this.form.itemCode = row.productCode;
        this.form.itemName = row.productName;
        this.form.specification = row.productSpc;
        this.form.unitOfMeasure = row.unitOfMeasure;
      }
    },
    handleTaskSelect() {
      this.$refs.taskSelect.showFlag = true;
      this.$refs.taskSelect.getList();
    },
    onTaskSelected(row) {

      if (row != undefined && row != null) {
        this.form.taskId = row.taskId;
        this.form.taskCode = row.taskCode;
        this.form.taskName = row.taskName;
        this.form.workstationId = row.workstationId;
        this.form.workstationCode = row.workstationCode;
        this.form.workstationName = row.workstationName;
        this.form.processId = row.processId;
        this.form.processCode = row.processCode;
        this.form.processName = row.processName;
        this.form.routeId = row.routeId;
        this.form.routeCode = row.routeCode;
        this.form.isCheck = row.isCheck;
        if (row.isCheck == 'N') {
          this.form.quantityUncheck = 0;
        } else {
          this.form.quantityQualified = 0;
          this.form.quantityUnquanlified = 0;
        }
      }
    },
    //点击人员选择按钮
    handleUserSelect() {
      this.$refs.userSelect.showFlag = true;
    },
    //人员选择返回
    onUserSelected(row) {
      this.form.userId = row.userId;
      this.form.nickName = row.nickName;
      this.form.userName = row.userName;
    },
    //点击人员选择按钮
    handleUser2Select() {
      this.$refs.user2Select.showFlag = true;
    },
    //人员选择返回
    onUser2Selected(row) {
      this.form.recordUserId = row.userId;
      this.form.recordUser = row.userName;
      this.form.recordNick = row.nickName;
    },
  }
};
</script>
<style scoped>
.tab-panes {
  width: 98%;
  margin: -15px auto;
  /* 上下外边距为20px，左右外边距为auto，实现居中 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  /* 淡灰色阴影 */
  padding: 20px;
  /* 内边距 */
  background-color: #fff;
  /* 白色背景 */
  border-radius: 5px;
}

.warning-text {
  color: rgb(0, 153, 255);
  font-style: italic;
  /* 斜体 */
  position: absolute;
  right: 20px;
  top: 20px;
  background-color: #e0f7ff;
  padding: 10px;
  border: 1px solid rgb(0, 153, 255);
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 255, 0.15);
  transform: rotate(-5deg);
}

.feedback {
  /* height: 10%; */
  width: 98%;
  margin: 20px auto;
  /* 上下外边距为20px，左右外边距为auto，实现居中 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  /* 淡灰色阴影 */
  padding: 20px;
  /* 内边距 */
  background-color: #fff;
  /* 白色背景 */
  border-radius: 5px;
  /* 圆角 */
}

.feedback-p {
  width: 100%;
  height: 25px;
  line-height: 25px;
  position: relative;
}

.feedback-p .feedback-s {
  width: 5px;
  height: 30px;
  background: #068ffa;
  display: inline-block;
  border-radius: 10px;
}

.feedback-p .feedback-message {
  font-weight: bold;
  display: inline-block;
  position: absolute;
  left: 12px;
  font-size: 1.1rem;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  /* Adjust the gap between buttons as needed */
  padding-top: 15px;
  /* Add padding to separate the buttons from the form */
}

.feedback-form {
  padding-top: 20px;
}
</style>