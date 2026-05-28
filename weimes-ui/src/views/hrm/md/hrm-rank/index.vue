<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="职级编码" prop="rankCode">
        <el-input v-model="queryParams.rankCode" placeholder="请输入职级编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="职级层次" prop="rankType" >
        <el-input v-model="queryParams.rankType" placeholder="请输入职级层次" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="职级层次名称" prop="rankName" label-width="110px">
        <el-input v-model="queryParams.rankName" placeholder="请输入职级层次名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="薪资范围" prop="salaryRangeMin">
        <el-input v-model="queryParams.salaryRangeMin" placeholder="请输入下限" clearable @keyup.enter.native="handleQuery"
          style="width: 120px;"  /> ~
        <el-input style="width: 120px;" v-model="queryParams.salaryRangeMax" placeholder="请输入上限" clearable
          @keyup.enter.native="handleQuery"  />
      </el-form-item>
      <!--   <el-form-item label="薪资范围上限" prop="salaryRangeMax">
        
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['md:hrmrank:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['md:hrmrank:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['md:hrmrank:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['md:hrmrank:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="hrmRankList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="职级主键id" align="center" prop="rankId" v-if="false" />
      <el-table-column label="职级编码" align="center" prop="rankCode" />
      <el-table-column label="职级层次" align="center" prop="rankType" />
      <el-table-column label="职级层次名称" align="center" prop="rankName" />
      <el-table-column label="薪资范围（元）" align="center" >
        <el-table-column label="薪资范围下限" align="center" prop="salaryRangeMin" />
        <el-table-column label="薪资范围上限" align="center" prop="salaryRangeMax" />
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['md:hrmrank:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['md:hrmrank:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改职级管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="职级编码" prop="rankCode">
              <el-input v-model="form.rankCode" placeholder="请输入职级编码" />
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item label-width="80">
              <el-switch v-model="autoGenFlag" active-color="#13ce66" active-text="自动生成"
                @change="handleAutoGenChange(autoGenFlag)" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="职级层次" prop="rankType" label-width="110px">
              <el-input v-model="form.rankType" placeholder="请输入职级层次" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="职级层次名称" prop="rankName" label-width="110px">
              <el-input v-model="form.rankName" placeholder="请输入职级层次名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="薪资范围下限" prop="salaryRangeMin" label-width="120px">
              <el-input v-model="form.salaryRangeMin" placeholder="请输入薪资范围下限" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="薪资范围上限" prop="salaryRangeMax" label-width="120px">
              <el-input v-model="form.salaryRangeMax" placeholder="请输入薪资范围上限" />
            </el-form-item>
          </el-col>
        </el-row>



      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { genCode } from '@/api/system/autocode/rule'
import { listHrmRank, getHrmRank, delHrmRank, addHrmRank, updateHrmRank } from "@/api/hrm/md/hrm-rank";
export default {
  name: "HrmRank",
  data() {
    return {
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
      // 职级管理表格数据
      hrmRankList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        rankCode: null,rankType: null, rankName: null, salaryRangeMin: null, salaryRangeMax: null, createUserId: null, updateUserId: null, deleted: null, deleteat: null, version: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        rankCode: [
          { required: true, message: "职级编码不能为空", trigger: "blur" }
        ], 
        rankType: [
          { required: true, message: "职级层次不能为空", trigger: "blur" }
        ], 
        rankName: [
          { required: true, message: "职级层次名称不能为空", trigger: "blur" }
        ], 
        salaryRangeMin: [
          { required: true, message: "薪资范围下限不能为空", trigger: "blur" }
        ], 
        salaryRangeMax: [
          { required: true, message: "薪资范围上限不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {

    //字段验证
    verification() {
      let check = (!this.queryParams.salaryRangeMin && !this.queryParams.salaryRangeMax) || (this.queryParams.salaryRangeMin && this.queryParams.salaryRangeMax);
      if (!check) {
        this.$message.warning("薪资范围不为空");
      }
      if (this.queryParams.salaryRangeMin > this.queryParams.salaryRangeMax) {
        this.$message.warning("起始数不能大于结束数");
        return false;
      } else {
        return true;
      }
    },
    /** 查询职级管理列表 */
    getList() {
      this.loading = true;
      listHrmRank(this.queryParams).then(response => {
        this.hrmRankList = response.rows;
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
        rankId: null, rankCode: null, rankType: null,rankName: null, salaryRangeMin: null, salaryRangeMax: null, createUserId: null, createBy: null, updateUserId: null, updateBy: null, createTime: null, updateTime: null, deleted: null, deleteat: null, version: null, remark: null
      };
      this.autoGenFlag = false;
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      //薪资范围检验
      this.verification();
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.rankId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag) {
      if (autoGenFlag) {
        genCode("RANK_CODE").then(response => {
          this.form.rankCode = response
        })
      } else {
        this.form.rankCode = null
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加职级管理";
      this.optType = 'add';
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const rankId = row.rankId || this.ids;
      this.optType = 'edit';
      getHrmRank(rankId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改职级管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.rankId != null) {
            updateHrmRank(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addHrmRank(this.form).then(response => {
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
      const rankIds = row.rankId || this.ids;
      //this.$modal.confirm('是否确认删除职级管理编号为"' + rankIds + '"的数据项？').then(function () {
      this.$modal.confirm('是否确认删除所选职级管理的数据项？').then(function () {
        return delHrmRank(rankIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('md/hrm-rank/export', {
        ...this.queryParams
      }, `hrm-rank_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>