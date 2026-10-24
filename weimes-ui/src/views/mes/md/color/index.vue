<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="20" :xs="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
          >
          <el-form-item :label="$t('mes.色号')" prop="colorCode">
            <el-input v-model="queryParams.colorCode" :placeholder="$t('mes.请输入色号')" clearable @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item :label="$t('mes.颜色')" prop="colorName">
            <el-input v-model="queryParams.colorName" :placeholder="$t('mes.请输入颜色')" clearable @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item >
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['md_color:color:add']">{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['md_color:color:edit']">{{ this.$t('system.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['md_color:color:remove']">{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['md_color:color:export']">{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="colorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('mes.色号')" align="center" prop="colorCode" />
      <el-table-column :label="$t('mes.颜色')" align="center" prop="colorName" />
      <el-table-column :label="$t('system.备注')" align="center" prop="remark" />
      <el-table-column :label="$t('system.状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['md_color:color:edit']">{{ $t('system.修改') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['md_color:color:remove']">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改颜色对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item :label="$t('mes.色号')" prop="colorCode">
          <el-input v-model="form.colorCode" :placeholder="$t('mes.请输入色号')" />
        </el-form-item>
        <el-form-item :label="$t('mes.颜色')" prop="colorName">
          <el-input v-model="form.colorName" :placeholder="$t('mes.请输入颜色')" />
        </el-form-item>
        <el-form-item :label="$t('system.备注')" prop="remark">
          <el-input v-model="form.remark" :placeholder="$t('lims.请输入备注')" />
        </el-form-item>
        <el-form-item :label="$t('system.状态')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label
              }}</el-radio>
          </el-radio-group>
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
import { listColor, getColor, delColor, addColor, updateColor } from "@/api/color/color";

export default {
  dicts: ['sys_normal_disable'],
  name: "Color",
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
      // 颜色表格数据
      colorList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        colorCode: null,
        colorName: null,
        remark: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        colorCode: [
          { required: true, message: this.$t('mes.色号不能为空'), trigger: "blur" }
        ], colorName: [
          { required: true, message: this.$t('mes.颜色不能为空'), trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询颜色列表 */
    getList() {
      this.loading = true;
      listColor(this.queryParams).then(response => {
        this.colorList = response.rows;
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
        id: null, colorCode: null, colorName: null, remark: null, status: "0", createTime: null, createBy: null, updateTime: null, updateBy: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加颜色');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getColor(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改颜色');
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateColor(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addColor(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除颜色编号为{v}的数据项？', { v: ids })).then(function () {
        return delColor(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('md_color/color/export', {
        ...this.queryParams
      }, `color_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
