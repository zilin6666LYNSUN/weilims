<template>
  <div class="app-container">

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-if="optType != 'view'"
          v-hasPermi="['mes:wm:rtissue:add']" >{{ this.$t('common.add') }}</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="rtissueList" @selection-change="handleSelectionChange">
      <el-table-column :label="$t('mes.退料单编号')" width="140px" align="center" prop="rtCode">
        <template slot-scope="scope">
          <el-button type="text" @click="handleView(scope.row)" v-hasPermi="['mes:wm:rtissue:query']">{{
            scope.row.rtCode }}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.退料单名称')" align="center" prop="rtName" :show-overflow-tooltip="true" />
      <el-table-column :label="$t('mes.生产工单')" align="center" prop="workorderCode" />
      <el-table-column :label="$t('mes.报工单编号')" width="150px" align="center" prop="recordId" v-if="showColumn" />
      <el-table-column :label="$t('mes.退料日期')" align="center" prop="rtDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.rtDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.单据状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_order_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" width="200px" align="center" class-name="small-padding fixed-width" v-if="optType != 'view'">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-video-play" v-if="scope.row.status == 'PREPARE'"
            @click="handleExecute(scope.row)" v-hasPermi="['mes:wm:rtissue:edit']">{{ $t('mes.执行退料') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" v-if="scope.row.status == 'PREPARE'"
            @click="handleUpdate(scope.row)" v-hasPermi="['mes:wm:rtissue:edit']">{{ $t('system.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.status == 'PREPARE'"
            @click="handleDelete(scope.row)" v-hasPermi="['mes:wm:rtissue:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改生产退料单头对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.退料单编号')" prop="rtCode">
              <el-input v-model="form.rtCode" :placeholder="$t('mes.请输入退料单编号')" />
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
            <el-form-item :label="$t('mes.退料单名称')" prop="rtName">
              <el-input v-model="form.rtName" :placeholder="$t('mes.请输入退料单名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.生产工单')" prop="workorderCode">
              <el-input v-model="form.workorderCode" :placeholder="$t('mes.请输入生产工单')" readonly="readonly">
                <el-button slot="append" icon="el-icon-search"></el-button>
              </el-input>
            </el-form-item>
            <WorkorderSelect ref="woSelect" @onSelected="onWorkorderSelected"></WorkorderSelect>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.报工编号')" prop="recordId" v-if="showColumn">
              <el-input v-model="form.recordId" :placeholder="$t('mes.请输入报工编号')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.退料日期')" prop="rtDate">
              <el-date-picker clearable v-model="form.rtDate" type="date" value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择退料日期')">
              </el-date-picker>
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
      <el-divider v-if="form.rtId != null" content-position="center">{{ this.$t('mes.物料信息') }}</el-divider>
      <el-card shadow="always" v-if="form.rtId != null" class="box-card">
        <Rtissueline :rtId="form.rtId" :optType="optType" :workorderCode="form.workorderCode"></Rtissueline>
      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancel" v-if="optType == 'view' || form.status != 'PREPARE'">{{ this.$t('common.back') }}</el-button>
        <el-button type="primary" @click="submitForm"
          v-if="form.status == 'PREPARE' && optType != 'view'">{{ this.$t('common.save') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRtissue, getRtissue, delRtissue, addRtissue, updateRtissue, execute } from "@/api/mes/wm/rtissue";
import { listWorkorder } from "@/api/mes/pro/workorder";
import WorkorderSelect from "@/components/workorderSelect/single.vue"

import Rtissueline from "./line.vue";
import { getTreeList } from "@/api/mes/wm/warehouse"
import { genCode } from "@/api/system/autocode/rule"
export default {
  name: "Rtissue",
  dicts: ['mes_order_status'],
  components: {
    Rtissueline, WorkorderSelect
  },
  props:{
    recordId: {
      type: String,
      default: null
    },
    workorderCode: {
      type: String,
      default: null
    },
    optType: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      autoGenFlag: false,
      showColumn: false,//隐藏列
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
      // 生产退料单头表格数据
      rtissueList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        rtCode: null,
        rtName: null,
        recordId: null,
        workorderId: null,
        workorderCode: null,
        warehouseId: null,
        warehouseCode: null,
        warehouseName: null,
        locationId: null,
        locationCode: null,
        locationName: null,
        areaId: null,
        areaCode: null,
        areaName: null,
        rtDate: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        rtCode: [
          { required: true, message: this.$t('mes.退料单编号不能为空'), trigger: "blur" }
        ],
        workorderCode: [
          { required: true, message: this.$t('mes.请选择要退料的生产工单'), trigger: "blur" }
        ],
        recordId: [
          { required: true, message: this.$t('mes.报工编号不能为空'), trigger: 'blur' }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getWarehouseList();
  },
  methods: {
    /* 生产工单 */
    listWorkorder() {
      // this.queryParams.workorderCode = this.$route.params.workorderCodeResult
      this.queryParams.workorderCode = this.workorderCode;
      listWorkorder(this.queryParams).then(response => {
        const a = response.rows[0].workorderId
        this.form.workorderId = response.rows[0].workorderId
      });
    },
    /** 查询生产退料单头列表 */
    getList() {
      this.loading = true;
      let result = this.recordId;
      if (result == null) {
        this.queryParams.recordId = -1;//初始化 假设新增为保存 退料单中不存在记录id 
      } else {
        this.queryParams.recordId = result;
      }

      listRtissue(this.queryParams).then(response => {
        this.rtissueList = response.rows;
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
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        rtId: null,
        rtCode: null,
        rtName: null,
        workorderId: null,
        workorderCode: null,
        warehouseId: null,
        warehouseCode: null,
        warehouseName: null,
        recordId: null,
        locationId: null,
        locationCode: null,
        locationName: null,
        areaId: null,
        areaCode: null,
        areaName: null,
        rtDate: new Date(),
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
      this.ids = selection.map(item => item.rtId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.warehouseInfo = [];
      this.open = true;
      this.title = this.$t('mes.添加生产退料单头');
      this.optType = "add";
      // this.form.recordId = this.$route.params.recordId;
      // this.form.workorderCode = this.$route.params.workorderCodeResult
      this.form.recordId = this.recordId;
      this.form.workorderCode = this.workorderCode;
      this.listWorkorder();
    },
    // 查询明细按钮操作
    handleView(row) {
      this.reset();
      const rtIds = row.rtId
      getItemrecpt(rtIds).then(response => {
        this.form = response.data;
        this.warehouseInfo[0] = response.data.warehouseId;
        this.warehouseInfo[1] = response.data.locationId;
        this.warehouseInfo[2] = response.data.areaId;
        this.open = true;
        this.title = this.$t('mes.查看生产退料单信息');
        this.optType = "view";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const rtId = row.rtId || this.ids
      getRtissue(rtId).then(response => {
        this.form = response.data;
        this.warehouseInfo[0] = response.data.warehouseId;
        this.warehouseInfo[1] = response.data.locationId;
        this.warehouseInfo[2] = response.data.areaId;
        this.open = true;
        this.title = this.$t('mes.修改生产退料单头');
        this.optType = "edit";
      });
    },
    // 查询明细按钮操作
    handleView(row) {
      this.reset();
      const rtId = row.rtId
      getRtissue(rtId).then(response => {
        this.form = response.data;
        this.warehouseInfo[0] = response.data.warehouseId;
        this.warehouseInfo[1] = response.data.locationId;
        this.warehouseInfo[2] = response.data.areaId;
        this.open = true;
        this.title = this.$t('mes.查看退料单信息');
        this.optType = "view";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.rtId != null) {
            updateRtissue(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addRtissue(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.新增成功'));
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    //执行退料
    handleExecute(row) {
      const rtIds = row.rtId || this.ids;
      this.$modal.confirm(this.$t('mes.确认执行退料？')).then(function () {
        return execute(rtIds)//执行退料
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('mes.退料成功'));
      }).catch(() => { });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const rtIds = row.rtId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除生产退料单头编号为{v}的数据项？', { v: rtIds })).then(function () {
        return delRtissue(rtIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wm/rtissue/export', {
        ...this.queryParams
      }, `rtissue_${new Date().getTime()}.xlsx`)
    },
    //选择默认的仓库、库区、库位
    handleWarehouseChanged(obj) {
      if (obj != null) {
        this.form.warehouseId = obj[0];
        this.form.locationId = obj[1];
        this.form.areaId = obj[2];
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
      }
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag) {
      if (autoGenFlag) {
        genCode('RTISSUE_CODE').then(response => {
          this.form.rtCode = response;
        });
      } else {
        this.form.rtCode = null;
      }
    },
    mounted() {
      this.getList();
    }
  }
};
</script>