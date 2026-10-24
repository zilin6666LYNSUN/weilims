<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item :label="$t('mes.领料单编号')" prop="issueCode">
        <el-input v-model="queryParams.issueCode" :placeholder="$t('mes.请输入领料单编号')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.领料单名称')" prop="issueName">
        <el-input v-model="queryParams.issueName" :placeholder="$t('mes.请输入领料单名称')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.仓库名称')" prop="warehouseName">
        <el-input v-model="queryParams.warehouseName" :placeholder="$t('mes.请输入仓库名称')" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('mes.领料日期')" prop="issueDate">
        <el-date-picker clearable v-model="queryParams.issueDate" type="date" value-format="yyyy-MM-dd"
          :placeholder="$t('mes.请选择领料日期')">
        </el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('mes.单据状态')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="$t('mes.请选择单据状态')" clearable>
          <el-option v-for="dict in dict.type.mes_order_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['mes:wm:issueheader:add']">{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['mes:wm:issueheader:edit']">{{ this.$t('system.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['mes:wm:issueheader:remove']">{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['mes:wm:issueheader:export']">{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="issueheaderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.领料单编号')" align="center" width="150px" prop="issueCode">
        <template slot-scope="scope">
          <el-button type="text" @click="handleView(scope.row)" v-hasPermi="['mes:wm:issueheader:query']">{{
            scope.row.issueCode }}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.领料单名称')" align="center" prop="issueName" :show-overflow-tooltip="true" />
      <el-table-column :label="$t('mes.生产工单')" align="center" prop="workorderCode" />
      <el-table-column :label="$t('mes.客户编号')" align="center" prop="clientCode" />
      <el-table-column :label="$t('lims.客户名称')" align="center" prop="clientName" />
      <el-table-column :label="$t('mes.领料日期')" align="center" prop="issueDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.issueDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.单据状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_order_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-video-play" v-if="scope.row.status == 'PREPARE'"
            @click="handleExecute(scope.row)" v-hasPermi="['mes:wm:issueheader:edit']">{{ $t('mes.执行领出') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" v-if="scope.row.status == 'PREPARE'"
            @click="handleUpdate(scope.row)" v-hasPermi="['mes:wm:issueheader:edit']">{{ $t('system.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.status == 'PREPARE'"
            @click="handleDelete(scope.row)" v-hasPermi="['mes:wm:issueheader:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改生产领料单头对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.领料单编号')" prop="issueCode">
              <el-input v-model="form.issueCode" :placeholder="$t('mes.请输入领料单编号')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label-width="80">
              <el-switch v-model="autoGenFlag" active-color="#13ce66" active-text="自动生成"
                @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view' && form.status == 'PREPARE'">
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.领料单名称')" prop="issueName">
              <el-input v-model="form.issueName" :placeholder="$t('mes.请输入领料单名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.领料日期')" prop="issueDate">
              <el-date-picker clearable v-model="form.issueDate" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择领料日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.领料仓库')">
              <el-cascader v-model="warehouseInfo" :options="warehouseOptions" :props="warehouseProps"
                @change="handleWarehouseChanged">
              </el-cascader>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.单据状态')" prop="status">
              <el-select v-model="form.status" disabled>
                <el-option v-for="dict in dict.type.mes_order_status" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.生产工单')" prop="workorderCode">
              <el-input v-model="form.workorderCode" :placeholder="$t('mes.请选择生产工单')">
                <el-button slot="append" icon="el-icon-search" @click="handleWorkorderSelect"></el-button>
              </el-input>
            </el-form-item>
            <WorkorderSelect ref="woSelect" @onSelected="onWorkorderSelected"></WorkorderSelect>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.客户编号')">
              <el-input v-model="form.clientCode" :placeholder="$t('mes.请选择生产工单')">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.客户名称')">
              <el-input v-model="form.clientName" :placeholder="$t('mes.请选择生产工单')">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="optType != 'view'">
          <el-col :span="8">
            <el-form-item :label="$t('mes.生产任务')" prop="taskCode">
              <el-input v-model="form.taskCode" :placeholder="$t('mes.请选择生产任务')">
                <el-button slot="append" icon="el-icon-search" @click="handleTaskSelect"></el-button>
              </el-input>
            </el-form-item>
            <ProtaskSelect ref="taskSelect" :workorderId="form.workorderId" @onSelected="onTaskSelected">
            </ProtaskSelect>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.任务名称')">
              <el-input v-model="form.taskName" :placeholder="$t('mes.请选择生产任务')">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.工作站')">
              <el-input v-model="form.workstationName" :placeholder="$t('mes.请选择生产任务')">
              </el-input>
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
      <el-divider v-if="form.issueId != null" content-position="center">{{ this.$t('mes.物料信息') }}</el-divider>
      <el-card shadow="always" v-if="form.issueId != null" class="box-card">
        <Issueline ref="line" :issueId="form.issueId" :warehouseId="form.warehouseId" :locationId="form.locationId"
          :areaId="form.areaId" :optType="optType"></Issueline>
      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancel" v-if="optType == 'view' || form.status != 'PREPARE'">{{ this.$t('common.back') }}</el-button>
        <el-button type="primary" @click="submitForm" v-if="form.status == 'PREPARE' && optType != 'view'">{{ this.$t('mes.确') }}
          定</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listIssueheader, getIssueheader, delIssueheader, addIssueheader, updateIssueheader, execute } from "@/api/mes/wm/issueheader";
import WorkstationSelect from "@/components/workstationSelect/simpletableSingle.vue"
import WorkorderSelect from "@/components/workorderSelect/single.vue"
import ProtaskSelect from "@/components/TaskSelect/taskSelectSingle.vue"
import { getTreeList } from "@/api/mes/wm/warehouse"
import { genCode } from "@/api/system/autocode/rule"
import Issueline from "./line.vue";
export default {
  name: "Issueheader",
  dicts: ['mes_order_status'],

  components: { Issueline, WorkstationSelect, WorkorderSelect, ProtaskSelect },
  data() {
    return {
      autoGenFlag: false,
      optType: undefined,
      warehouseInfo: [],
      warehouseOptions: [],
      warehouseProps: {
        multiple: false,
        value: 'pId',
        label: 'pName',
      },
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
      // 生产领料单头表格数据
      issueheaderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        issueCode: null,
        issueName: null,
        workstationId: null,
        workstationCode: null,
        workorderId: null,
        workorderCode: null,
        taskId: null,
        taskCode: null,
        warehouseId: null,
        warehouseCode: null,
        warehouseName: null,
        locationId: null,
        locationCode: null,
        locationName: null,
        areaId: null,
        areaCode: null,
        areaName: null,
        issueDate: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        issueCode: [
          { required: true, message: this.$t('mes.领料单编号不能为空'), trigger: "blur" }
        ],
        issueName: [
          { required: true, message: this.$t('mes.领料单名称不能为空'), trigger: "blur" }
        ],
        issueDate: [
          { required: true, message: this.$t('mes.请指定领出日期'), trigger: "blur" }
        ],
        workorderCode: [
          { required: true, message: this.$t('mes.请指定生产工单'), trigger: "blur" }
        ]
      }
    };
  },

  created() {
    this.getList();
    this.getWarehouseList();
  },

  methods: {
    /** 查询生产领料单头列表 */
    getList() {
      this.loading = true;
      listIssueheader(this.queryParams).then(response => {
        this.issueheaderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getWarehouseList() {
      getTreeList().then(response => {
        if (response.data) {
          this.warehouseOptions = response.data.filter((el) => {
            return el.warehouseCode.indexOf('VIR') == -1;
          });;
        }
        this.warehouseOptions.map(w => {
          w.children.map(l => {
            let lstr = JSON.stringify(l.children).replace(/locationId/g, 'lId').replace(/areaId/g, 'pId').replace(/areaName/g, 'pName');
            l.children = JSON.parse(lstr);
          });

          let wstr = JSON.stringify(w.children).replace(/warehouseId/g, 'wId').replace(/locationId/g, 'pId').replace(/locationName/g, 'pName');
          w.children = JSON.parse(wstr);

        });
        let ostr = JSON.stringify(this.warehouseOptions).replace(/warehouseId/g, 'pId').replace(/warehouseName/g, 'pName');
        this.warehouseOptions = JSON.parse(ostr);
      });
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
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        issueId: null,
        issueCode: null,
        issueName: null,
        workstationId: null,
        workstationCode: null,
        workstationName: null,
        workorderId: null,
        workorderCode: null,
        workorderName: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        taskId: null,
        taskCode: null,
        warehouseId: null,
        warehouseCode: null,
        warehouseName: null,
        locationId: null,
        locationCode: null,
        locationName: null,
        areaId: null,
        areaCode: null,
        areaName: null,
        issueDate: new Date(),
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
      this.ids = selection.map(item => item.issueId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.warehouseInfo = [];
      this.open = true;
      this.title = this.$t('mes.添加生产领料单');
      this.optType = "add";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const issueId = row.issueId || this.ids
      getIssueheader(issueId).then(response => {
        this.form = response.data;
        this.warehouseInfo[0] = response.data.warehouseId;
        this.warehouseInfo[1] = response.data.locationId;
        this.warehouseInfo[2] = response.data.areaId;
        this.open = true;
        this.title = this.$t('mes.修改生产领料单');
        this.optType = "edit";
      });
    },
    // 查询明细按钮操作
    handleView(row) {
      this.reset();
      const issueIds = row.issueId
      getIssueheader(issueIds).then(response => {
        this.form = response.data;
        this.warehouseInfo[0] = response.data.warehouseId;
        this.warehouseInfo[1] = response.data.locationId;
        this.warehouseInfo[2] = response.data.areaId;
        this.open = true;
        this.title = this.$t('mes.查看领料单信息');
        this.optType = "view";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.issueId != null) {
            updateIssueheader(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addIssueheader(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.新增成功'));
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    //执行出库
    handleExecute(row) {
      const issueIds = row.issueId || this.ids;
      this.$modal.confirm(this.$t('mes.确认执行出库？')).then(function () {
        return execute(issueIds)//执行入库
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('mes.出库成功'));
      }).catch(() => { });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const issueIds = row.issueId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除生产领料单头编号为{v}的数据项？', { v: issueIds })).then(function () {
        return delIssueheader(issueIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wm/issueheader/export', {
        ...this.queryParams
      }, `issueheader_${new Date().getTime()}.xlsx`)
    },
    //选择默认的仓库、库区、库位
    handleWarehouseChanged(obj) {
      if (obj != null) {
        this.form.warehouseId = obj[0];
        this.form.locationId = obj[1];
        this.form.areaId = obj[2];
      }
    },
    //选择工作站
    handleWorkstationSelect() {
      this.$refs.wsSelect.showFlag = true;
    },
    onWorkstationSelected(row) {
      if (row != undefined && row != null) {
        this.form.workstationId = row.workstationId;
        this.form.workstationCode = row.workstationCode;
        this.form.workstationName = row.workstationName;
      }
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
        this.form.clientId = row.clientId;
        this.form.clientCode = row.clientCode;
        this.form.clientName = row.clientName;
      }
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag) {
      if (autoGenFlag) {
        genCode('ISSUE_CODE').then(response => {
          this.form.issueCode = response;
        });
      } else {
        this.form.issueCode = null;
      }
    }
  },
  mounted() {
    this.getList();
  }
};
</script>
