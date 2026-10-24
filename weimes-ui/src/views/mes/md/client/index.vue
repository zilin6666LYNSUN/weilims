<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-row>
        <el-col :span="8">
          <el-form-item :label="$t('lims.客户编码')" prop="clientCode">
            <el-input
              v-model="queryParams.clientCode"
              :placeholder="$t('lims.请输入客户编码')"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="$t('lims.客户名称')" prop="clientName">
            <el-input
              v-model="queryParams.clientName"
              :placeholder="$t('lims.请输入客户名称')"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="$t('mes.客户简称')" prop="clientNick">
            <el-input
              v-model="queryParams.clientNick"
              :placeholder="$t('mes.请输入客户简称')"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
            <el-form-item :label="$t('mes.客户英文名称')" prop="clientEn">
            <el-input
              v-model="queryParams.clientEn"
              :placeholder="$t('mes.请输入客户英文名称')"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="$t('system.是否启用')" prop="enableFlag">
            <el-select
              v-model="queryParams.enableFlag"
              :placeholder="$t('system.是否启用')"
              clearable
              style="width: 215px"
            >
              <el-option
                v-for="dict in dict.type.sys_yes_no"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('system.搜索') }}</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['mes:md:client:add']"
        >{{ this.$t('common.add') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['mes:md:client:edit']"
        >{{ this.$t('system.修改') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['mes:md:client:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['mes:md:client:export']"
        >{{ this.$t('common.export') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="clientList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('lims.客户编码')" align="center" prop="clientCode" >
         <template slot-scope="scope">
                <el-button
                  type="text"
                  @click="handleView(scope.row)"
                  v-hasPermi="['mes:md:client:query']"
                >{{scope.row.clientCode}}</el-button>
          </template>
      </el-table-column>
      <el-table-column :label="$t('lims.客户名称')" width="150px" align="center" prop="clientName" :show-overflow-tooltip="true"/>
      <el-table-column :label="$t('mes.客户简称')" align="center" prop="clientNick" />
      <el-table-column :label="$t('lims.客户类型')" align="center" prop="clientType" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_client_type" :value="scope.row.clientType"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('mes.客户电话')" align="center" prop="tel" />
      <el-table-column :label="$t('lims.联系人')" align="center" prop="contact1" />
      <el-table-column :label="$t('mes.联系人-电话')" align="center" prop="contact1Tel" />
      <el-table-column :label="$t('system.是否启用')" align="center" prop="enableFlag" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.enableFlag"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:md:client:edit']"
          >{{ $t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:md:client:remove']"
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

    <!-- 添加或修改客户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('lims.客户编码')" prop="clientCode">
              <el-input v-model="form.clientCode" :placeholder="$t('lims.请输入客户编码')" />
            </el-form-item>
          </el-col>
            <el-col :span="4">
            <el-form-item  label-width="80">
              <el-switch v-model="autoGenFlag"
                  active-color="#13ce66"
                  :active-text="$t('mes.自动生成')"
                  @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view'">               
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('lims.客户名称')" prop="clientName">
              <el-input v-model="form.clientName" :placeholder="$t('lims.请输入客户名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.客户简称')" prop="clientNick">
              <el-input v-model="form.clientNick" :placeholder="$t('mes.请输入客户简称')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.客户英文名称')" prop="clientEn">
              <el-input v-model="form.clientEn" :placeholder="$t('mes.请输入客户英文名称')" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('lims.客户类型')" prop="clientType">
              <el-select v-model="form.clientType" :placeholder="$t('mes.请选择客户类型')">
              <el-option
                  v-for="dict in dict.type.mes_client_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('mes.客户简介')" prop="clientDes">
              <el-input v-model="form.clientDes" type="textarea" :placeholder="$t('common.inputContent')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('mes.客户地址')" prop="address">
              <el-input v-model="form.address" type="textarea" :placeholder="$t('common.inputContent')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('mes.客户官网地址')" prop="website">
              <el-input v-model="form.website" :placeholder="$t('mes.请输入客户官网地址')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.客户邮箱地址')" prop="email">
              <el-input v-model="form.email" v-if="optType=='view'" />
              <el-input v-model="form.email" :placeholder="$t('mes.请输入客户邮箱地址')" v-else/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('mes.客户电话')" prop="tel">
              <el-input v-model="form.tel" v-if="optType=='view'"  />
              <el-input v-model="form.tel" :placeholder="$t('mes.请输入客户电话')" v-else/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- <el-form-item :label="$t('mes.客户LOGO')" prop="clientLogo">
              <el-input v-model="form.clientLogo" v-if="optType=='view'" />
              <el-input v-model="form.clientLogo" :placeholder="$t('mes.请输入客户LOGO')" v-else/>
            </el-form-item> -->
            <el-form-item :label="$t('system.手机')" prop="contact1Tel">
              <el-input v-model="form.contact1Tel" v-if="optType=='view'" />
              <el-input v-model="form.contact1Tel" :placeholder="$t('mes.请输入联系人1-电话')" v-else />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item :label="$t('mes.业务员')" prop="busMan">
              <el-input v-model="form.salesMan" :placeholder="$t('mes.请输入业务员')" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('mes.跟单')" prop="followerMan">
              <el-input v-model="form.followerMan" :placeholder="$t('mes.请输入跟单')" />
            </el-form-item>
          </el-col>
          <el-col :span="6"
            ><el-form-item :label="$t('mes.结账方式')" prop="payUp">
              <el-select v-model="form.payUp" :placeholder="$t('mes.请选择结账方式')">
                <el-option
                  v-for="dict in dict.type.pay_up_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('mes.币别')" prop="currency">
              <el-select v-model="form.currency" :placeholder="$t('mes.请选择币别')">
                <el-option
                  v-for="dict in dict.type.currency_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="8">
            <el-form-item :label="$t('mes.联系人1')" prop="contact1">
              <el-input v-model="form.contact1" v-if="optType=='view'" />
              <el-input v-model="form.contact1" :placeholder="$t('mes.请输入联系人1')" v-else/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.联系人1-电话')" prop="contact1Tel">
              <el-input v-model="form.contact1Tel" v-if="optType=='view'" />
              <el-input v-model="form.contact1Tel" :placeholder="$t('mes.请输入联系人1-电话')" v-else />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.联系人1-邮箱')" prop="contact1Email">
              <el-input v-model="form.contact1Email" v-if="optType=='view'" />
              <el-input v-model="form.contact1Email" :placeholder="$t('mes.请输入联系人1-邮箱')" v-else/>
            </el-form-item>
          </el-col>
        </el-row> -->
        <!-- <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.联系人2')" prop="contact2">
              <el-input v-model="form.contact2" v-if="optType=='view'" />
              <el-input v-model="form.contact2" :placeholder="$t('mes.请输入联系人2')" v-else />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.联系人2-电话')" prop="contact2Tel">
              <el-input v-model="form.contact2Tel" v-if="optType=='view'" />
              <el-input v-model="form.contact2Tel" :placeholder="$t('mes.请输入联系人2-电话')"  v-else/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.联系人2-邮箱')" prop="contact2Email">
              <el-input v-model="form.contact2Email" v-if="optType=='view'" />
              <el-input v-model="form.contact2Email" :placeholder="$t('mes.请输入联系人2-邮箱')" v-else/>
            </el-form-item>
          </el-col> -->
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('mes.社会信用代码')" prop="creditCode">
              <el-input v-model="form.creditCode" readonly v-if="optType=='view'" />
              <el-input v-model="form.creditCode" :placeholder="$t('mes.请输入统一社会信用代码')" v-else />
            </el-form-item>
          </el-col >
          <el-col :span="12">
            <el-form-item :label="$t('lims.是否有效')" prop="enableFlag">
              <el-radio-group v-model="form.enableFlag" disabled v-if="optType=='view'">
                <el-radio
                  v-for="dict in dict.type.sys_yes_no"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
              <el-radio-group v-model="form.enableFlag" v-else>
                <el-radio
                  v-for="dict in dict.type.sys_yes_no"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>

            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('system.备注')" prop="remark">
              <el-input v-model="form.remark" type="textarea" readonly v-if="optType=='view'" />
              <el-input v-model="form.remark" type="textarea" :placeholder="$t('common.inputContent')" v-else />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancel" v-if="optType =='view'">{{ this.$t('common.back') }}</el-button>
        <el-button type="primary" @click="submitForm" v-else>{{ this.$t('system.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listClient, getClient, delClient, addClient, updateClient } from "@/api/mes/md/client";
import {genCode} from "@/api/system/autocode/rule"
export default {
  name: "Client",
  dicts: ['mes_client_type','sys_yes_no','currency_type','pay_up_type'],
  data() {
    return {
      //自动生成编码
      autoGenFlag:false,
      optType: undefined,
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
      // 客户表格数据
      clientList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        clientCode: null,
        clientName: null,
        clientNick: null,
        clientEn: null,
        clientDes: null,
        clientLogo: null,
        clientType: null,
        address: null,
        website: null,
        email: null,
        tel: null,
        contact1: null,
        contact1Tel: null,
        contact1Email: null,
        contact2: null,
        contact2Tel: null,
        contact2Email: null,
        creditCode: null,
        enableFlag: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        clientCode: [
          { required: true, message: this.$t('mes.客户编码不能为空'), trigger: "blur" }
        ],
        clientName: [
          { required: true, message: this.$t('mes.客户名称不能为空'), trigger: "blur" }
        ],
        email:[
          {
            type: "email",
            message: this.$t('system.请输入正确的邮箱地址'),
            trigger: ["blur", "change"]
          }
        ],
        contact1Email:[
          {
            type: "email",
            message: this.$t('system.请输入正确的邮箱地址'),
            trigger: ["blur", "change"]
          }
        ],
        contact2Email:[
          {
            type: "email",
            message: this.$t('system.请输入正确的邮箱地址'),
            trigger: ["blur", "change"]
          }
        ],
        enableFlag: [
          { required: true, message: this.$t('mes.是否启用不能为空'), trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询客户列表 */
    getList() {
      this.loading = true;
      listClient(this.queryParams).then(response => {
        this.clientList = response.rows;
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
        clientId: null,
        clientCode: null,
        clientName: null,
        clientNick: null,
        clientEn: null,
        clientDes: null,
        clientLogo: null,
        clientType: null,
        address: null,
        website: null,
        email: null,
        tel: null,
        contact1: null,
        contact1Tel: null,
        contact1Email: null,
        contact2: null,
        contact2Tel: null,
        contact2Email: null,
        creditCode: null,
        enableFlag: 'Y',
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
      this.ids = selection.map(item => item.clientId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = this.$t('mes.添加客户');
      this.optType = "add";
    },
    // 查询明细按钮操作
    handleView(row){
      this.reset();
      const clientId = row.clientId || this.ids
      getClient(clientId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.查看客户');
        this.optType = "view";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const clientId = row.clientId || this.ids
      getClient(clientId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('mes.修改客户');
        this.optType = "edit";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.clientId != null) {
            updateClient(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addClient(this.form).then(response => {
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
      const clientIds = row.clientId || this.ids;
      this.$modal.confirm(this.$t('mes.是否确认删除客户编号为{v}的数据项？', { v: clientIds })).then(function() {
        return delClient(clientIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('md/client/export', {
        ...this.queryParams
      }, `client_${new Date().getTime()}.xlsx`)
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag){
      if(autoGenFlag){
        genCode('CLIENT_CODE').then(response =>{
          this.form.clientCode = response;
        });
      }else{
        this.form.clientCode = null;
      }      
    }
  }
};
</script>
