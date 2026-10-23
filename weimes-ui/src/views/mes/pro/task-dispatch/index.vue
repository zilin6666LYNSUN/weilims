<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="$t('lims.任务名称')" prop="taskName">
        <el-input v-model="queryParams.taskName" :placeholder="$t('mes.请输入任务名称')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.生产工单编号')" prop="workorderCode" label-width="110px">
        <el-input v-model="queryParams.workorderCode" :placeholder="$t('mes.请输入生产工单编号')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.工单名称')" prop="workorderName">
        <el-input v-model="queryParams.workorderName" :placeholder="$t('mes.请输入工单名称')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.工序名称')" prop="processName">
        <el-input v-model="queryParams.processName" :placeholder="$t('mes.请输入工序名称')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="multiple" @click="handleUser2Select"
          v-hasPermi="['pro:taskdispatch:addAssignUsers']">{{ this.$t('mes.批量编辑指派人') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['pro:taskdispatch:removeAssignUsers']">{{ this.$t('mes.重置派单人') }}</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['pro:taskdispatch:export']">{{ this.$t('common.export') }}</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="taskDispatchList" @select="handleSelectRow" @select-all="handleSelectAll"
      @selection-change="handleSelectionChange" :row-key="getRowKeys" default-expand-all
      :tree-props="{ children: 'childTasks' }" ref="taskTable">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.任务ID')" align="center" prop="taskId" v-if="false" />
      <el-table-column :label="$t('mes.生产工单编号')" align="center" prop="workorderCode" width="180" show-overflow-tooltip sortable />
      <el-table-column :label="$t('mes.工单名称')" align="center" prop="workorderName" width="130" show-overflow-tooltip sortable />
      <!-- <el-table-column :label="$t('mes.任务编号')" align="center" prop="taskCode" /> -->
      <el-table-column :label="$t('lims.任务名称')" align="center" prop="taskName" width="180" show-overflow-tooltip sortable />
      <el-table-column :label="$t('mes.工作站名称')" align="center" prop="workstationName" width="130" show-overflow-tooltip sortable />
      <el-table-column :label="$t('mes.工序名称')" align="center" prop="processName" width="100" sortable />
      <!--  <el-table-column :label="$t('mes.工艺编号')" align="center" prop="routeCode" show-overflow-tooltip/> -->
      <el-table-column :label="$t('mes.产品物料名称')" align="center" prop="itemName" width="130" show-overflow-tooltip sortable />
      <!--  <el-table-column :label="$t('lims.客户名称')" align="center" prop="clientName" /> -->
      <el-table-column :label="$t('mes.开始生产时间')" align="center" prop="startTime" width="130" sortable>
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.完成生产时间')" align="center" prop="endTime" width="130" sortable>
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column :label="$t('mes.生产状态')" align="center" prop="status" /> -->
      <el-table-column :label="$t('mes.派单人id')" align="center" prop="taskUserId" v-if="false" />
      <el-table-column :label="$t('mes.派单人名称')" align="center" prop="taskBy" sortable width="150" />
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" slot="append" @click="handleUser2Select(scope.row)"
            v-hasPermi="['pro:taskdispatch:addAssignUsers']"
            v-if="scope.row.taskUserId != null && scope.row.status != 'FINISHED'">{{ this.$t('system.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" slot="append" @click="handleUser2Select(scope.row)"
            v-hasPermi="['pro:taskdispatch:addAssignUsers']" v-if="scope.row.status === 'PREPARE'">{{ this.$t('mes.指派用户') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleDelete(scope.row)"
            v-hasPermi="['pro:taskdispatch:removeAssignUsers']"
            v-if="scope.row.taskUserId != null && scope.row.status != 'FINISHED'">{{ this.$t('mes.重置派单人') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <UserSingleSelect ref="user2Select" @onSelected="onUser2Selected"></UserSingleSelect>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

  </div>
</template>

<script>
import { listTaskDispatch, getTaskDispatch, updateTaskDispatch, removeAssignUsers } from "@/api/mes/pro/task-dispatch";
import UserSingleSelect from "@/components/userSelect/single.vue"
export default {
  name: "TaskDispatch",
  components: {
    UserSingleSelect
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      //最终结果主键id单个/多个
      taskIds: null,
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
      // 生产任务表格数据
      taskDispatchList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskCode: null,
        taskName: null,
        workorderId: null,
        workorderCode: null,
        workorderName: null,
        workstationId: null,
        workstationCode: null,
        workstationName: null,
        routeId: null,
        routeCode: null,
        processId: null,
        processCode: null,
        processName: null,
        itemId: null,
        itemCode: null,
        itemName: null,
        quantityChanged: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        clientNick: null,
        startTime: null,
        duration: null,
        endTime: null,
        colorCode: null,
        requestDate: null,
        status: null,
        updateUserId: null,
        taskUserId: null,
        taskBy: null
      },
      // 表单参数
      form: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询生产任务列表 */
    getList() {
      this.loading = true;
      listTaskDispatch(this.queryParams).then(response => {
        this.taskDispatchList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    /**
     * 单选某行触发
     */
    handleSelectRow(selection, row) {
      // 判断是否是父级节点
      if (this.isEmpty(row.parentId)) {
        let children = row.childTasks || [];
        children.forEach(child => {
          if (selection.includes(row)) { 
            this.$refs.taskTable.toggleRowSelection(child, true); }
          else {
            this.$refs.taskTable.toggleRowSelection(child, false);
          }
        });
      }
    },
    /**
     * 全选触发
     */
    handleSelectAll(selection) {
      // 获取当前表格的所有行
      let fullData = this.$refs.taskTable.store.states.data;
      // 如果当前选中的行数为0，则取消全选
      if (selection.length === 0) {
        this.$refs.taskTable.store.states.selection = [];
        return;
      }
      // 遍历所有的行数据，找出子节点并添加到选项中
      fullData.forEach(item => {
        let children = item.childTasks || [];
        children.forEach(child => {
          if (selection.includes(item)) {
            this.$refs.taskTable.toggleRowSelection(child, true);
          } else {
            this.$refs.taskTable.toggleRowSelection(child, false);
          }
        });
      });
    },
    // 表单重置
    reset() {
      this.form = {
        taskId: null,
        taskCode: null,
        taskName: null,
        workorderId: null,
        workorderCode: null,
        workorderName: null,
        workstationId: null,
        workstationCode: null,
        workstationName: null,
        routeId: null,
        routeCode: null,
        processId: null,
        processCode: null,
        processName: null,
        itemId: null,
        itemCode: null,
        itemName: null,
        specification: null,
        unitOfMeasure: null,
        quantity: null,
        quantityProduced: null,
        quantityQuanlify: null,
        quantityUnquanlify: null,
        quantityChanged: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        clientNick: null,
        startTime: null,
        duration: null,
        endTime: null,
        colorCode: null,
        requestDate: null,
        status: "0",
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        createUserId: null,
        updateUserId: null,
        taskUserId: null,
        taskBy: null
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
    //树状唯一列值标识
    getRowKeys(row) {
      //  唯一标识
      return row.taskId;
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      // 过滤掉父级节点
      selection = selection.filter(item => this.isNotEmpty(item.parentId))
      this.ids = selection.map(item => item.taskId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 重置派单人员按钮操作 */
    handleDelete(row) {
      const taskIds = row.taskId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认清空当前任务派单人？')).then(function () {
        return removeAssignUsers(taskIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('mes.清空成功'));
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pro/task-dispatch/export', {
        ...this.queryParams
      }, `task-dispatch_${new Date().getTime()}.xlsx`)
    },
    //点击人员选择按钮
    handleUser2Select(row) {
      this.taskIds = row.taskId || this.ids;
      this.$refs.user2Select.showFlag = true;
    },
    //人员选择返回
    onUser2Selected(row) {
      this.form.taskUserId = row.userId;
      this.form.taskBy = row.userName;
      if (this.taskIds.length > 0) {
        const query = {
          taskIds: this.taskIds,
          taskUserId: this.form.taskUserId,
          taskBy: this.form.taskBy
        };
        // 将单个参数 taskIds 转换为数组形式
        query.taskIds = Array.isArray(query.taskIds) ? query.taskIds : [query.taskIds];
        updateTaskDispatch(query).then(response => {
          if (response.msg != null && response.msg != '') {
            this.$modal.msgError(response.msg);
          } else {
            this.$modal.msgSuccess(this.$t('mes.派单成功！'));
          }
          this.getList();
        })
      }

    }
  }
};
</script>
