<template>
  <div class="app-container">
    
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['mes:dv:repair:add']"
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
          v-hasPermi="['mes:dv:repair:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="repairlineList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.项目名称')" align="center" prop="subjectName" />
      <el-table-column :label="$t('mes.故障描述')" align="center" prop="malfunction" >
        <template slot-scope="scope">
          <el-input type="textarea">{{scope.row.malfunction}}</el-input>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.故障描述资源')" align="center" prop="malfunctionUrl" >
        
      </el-table-column>
      <el-table-column :label="$t('mes.维修情况')" align="center" prop="repairDes" />
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:dv:repair:edit']"
          >{{ $t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:dv:repair:remove']"
          >{{ $t('common.delete') }}</el-button>
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

    <!-- 添加或修改设备维修单行对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">       
        <el-form-item :label="$t('mes.项目名称')" prop="subjectName">
          <el-input v-model="form.subjectName" :placeholder="$t('mes.请输入项目名称')" />
        </el-form-item>        
        <el-form-item :label="$t('mes.故障描述')" prop="malfunction">
          <el-input v-model="form.malfunction" type="textarea" :placeholder="$t('common.inputContent')" />
        </el-form-item>
        <el-form-item :label="$t('mes.故障描述资源')" prop="malfunctionUrl">
          <el-input type="textarea" v-model="form.malfunctionUrl" :placeholder="$t('mes.请输入故障描述资源')" />
        </el-form-item>
        <el-form-item :label="$t('mes.维修情况')" prop="repairDes">
          <el-input  v-model="form.repairDes" type="textarea" :placeholder="$t('common.inputContent')" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{ this.$t('system.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRepairline, getRepairline, delRepairline, addRepairline, updateRepairline } from "@/api/mes/dv/repairline";

export default {
  name: "Repairline",
  props: {
    repairId: null,
    optType: undefined
  },
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
      // 设备维修单行表格数据
      repairlineList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        repairId: this.repairId,
        subjectId: null,
        subjectCode: null,
        subjectName: null,
        subjectType: null,
        subjectContent: null,
        subjectStandard: null,
        malfunction: null,
        malfunctionUrl: null,
        repairDes: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        malfunction: [
          { required: true, message: this.$t('mes.故障描述不能为空'), trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询设备维修单行列表 */
    getList() {
      this.loading = true;
      listRepairline(this.queryParams).then(response => {
        this.repairlineList = response.rows;
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
        lineId: null,
        repairId: this.repairId,
        subjectId: null,
        subjectCode: null,
        subjectName: null,
        subjectType: null,
        subjectContent: null,
        subjectStandard: null,
        malfunction: null,
        malfunctionUrl: null,
        repairDes: null,
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
      this.ids = selection.map(item => item.lineId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加设备维修单行');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const lineId = row.lineId || this.ids
      getRepairline(lineId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改设备维修单行');
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.lineId != null) {
            updateRepairline(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addRepairline(this.form).then(response => {
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
      const lineIds = row.lineId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除设备维修单行编号为{v}的数据项？', { v: lineIds })).then(function() {
        return delRepairline(lineIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('dv/repairline/export', {
        ...this.queryParams
      }, `repairline_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
