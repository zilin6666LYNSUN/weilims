<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8" v-if="optType !='view'">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['mes:md:mditem:add']"
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
          v-hasPermi="['mes:md:mditem:remove']"
        >{{ this.$t('common.delete') }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="printerconfigList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('print.打印机编号')" align="center" prop="printerCode" :show-overflow-tooltip="true" >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="handleView(scope.row)"
            v-hasPermi="['print:printerconfig:query']"
          >{{scope.row.printerCode}}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('print.是否默认')" align="center" prop="defaultFlag">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.defaultFlag"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('print.打印机类型')" align="center" prop="printerType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_printer_type" :value="scope.row.printerType"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('print.打印机名称')" align="center" prop="printerName" />
      <el-table-column :label="$t('print.品牌')" align="center" prop="brand" />
      <el-table-column :label="$t('lims.型号')" align="center" prop="printerModel" />
      <el-table-column :label="$t('print.连接类型')" align="center" prop="connectionType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_conn_type" :value="scope.row.connectionType"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('print.打印机IP')" align="center" prop="printerIp" />
      <el-table-column :label="$t('print.打印机端口')" align="center" prop="printerPort" />
      <el-table-column :label="$t('system.启用状态')" align="center" prop="enableFlag">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.enableFlag"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('print.打印机状态')" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mes_printer_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" align="center" class-name="small-padding fixed-width" v-if="optType !='view'">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['mes:md:mditem:edit']"
          >{{ $t('system.修改') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['mes:md:mditem:remove']"
          >{{ $t('common.delete') }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-star-off"
            @click="handleDefault(scope.row)"
            v-if="scope.row.defaultFlag != 'Y'"
            v-hasPermi="['print:printerconfig:list']"
          >{{ $t('print.设置为默认') }}</el-button>
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

    <!-- 添加或修改产品BOM关系对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="960px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="16">
            <el-row>
              <el-col :span="16">
                <el-form-item :label="$t('print.打印机编号')" prop="printerCode">
                  <el-input v-model="form.printerCode" :placeholder="$t('print.请输入打印机编号')" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item  label-width="80">
                  <el-switch v-model="autoGenFlag"
                             active-color="#13ce66"
                             :active-text="$t('print.自动生成')"
                             @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view'">
                  </el-switch>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item :label="$t('print.打印机名称')" prop="printerName">
                  <el-select v-model="form.printerName" :placeholder="$t('print.请选择打印机')">
                    <el-option
                      v-for="item in printerList"
                      :key="item.name"
                      :label="item.displayName"
                      :value="item.name"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item :label="$t('print.打印机类型')" prop="printerType">
                  <el-select v-model="form.printerType" :placeholder="$t('print.请选择打印机类型')">
                    <el-option
                      v-for="dict in dict.type.mes_printer_type"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item :label="$t('system.启用状态')" prop="enableFlag">
                  <el-radio-group v-model="form.enableFlag">
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
              <el-col :span="12">
                <el-form-item :label="$t('print.品牌')" prop="brand">
                  <el-input v-model="form.brand" :placeholder="$t('print.请输入品牌')" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item :label="$t('lims.型号')" prop="printerModel">
                  <el-input v-model="form.printerModel" :placeholder="$t('print.请输入型号')" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item :label="$t('print.打印机IP')" prop="printerIp">
                  <el-input v-model="form.printerIp" :placeholder="$t('print.请输入打印机IP')" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item :label="$t('print.打印机端口')" prop="printerPort">
                  <el-input v-model="form.printerPort" :placeholder="$t('print.请输入打印机端口')" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('print.图片URL')" prop="printerUrl">
              <ImageUpload :limit="1" :value="form.printerUrl" :fileSize="5" @onUploaded="handleImgUplaoded" @onRemoved="handleImgRemoved" ></ImageUpload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('print.连接类型')">
              <el-radio-group v-model="form.connectionType">
                <el-radio
                  v-for="dict in dict.type.mes_conn_type"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('print.打印机状态')">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.mes_printer_status"
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
              <el-input v-model="form.remark" type="textarea" :placeholder="$t('common.inputContent')" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="optType !='view'">{{ this.$t('system.确 定') }}</el-button>
        <el-button @click="cancel">{{ this.$t('print.关 闭') }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listPrinterconfig, getPrinterconfig, delPrinterconfig, addPrinterconfig, updatePrinterconfig, setDefaultPrinter } from "@/api/print/printerconfig";
import {genCode} from "@/api/system/autocode/rule"
import {hiprint} from 'vue-plugin-hiprint'

export default {
  name: "PrintConfig",
  dicts: ['mes_printer_status', 'mes_printer_type', 'mes_conn_type','sys_yes_no'],
  data() {
    return {
      //自动生成编码
      autoGenFlag:false,
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
      // 打印机配置表格数据
      printerconfigList: [],
      // 打印机列表
      printerList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        printerType: null,    printerCode: null,    printerName: null,        brand: null,        printerModel: null,        connectionType: null,        printerUrl: null,        printerIp: null,        printerPort: null,        clientSid: null,        clientIp: null,        clientPort: null,        enableFlag: null,        status: null,      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        printerCode: [
          { required: true, message: this.$t('print.打印机编号不能为空'), trigger: "blur" }
        ],

        printerName: [
          { required: true, message: this.$t('print.打印机名称不能为空'), trigger: "blur" }
        ],

        printerIp: [
          { required: true, message: this.$t('print.打印机IP地址不能为空'), trigger: "blur" }
        ],
        printerPort: [
          { required: true, message: this.$t('print.打印机端口地址不能为空'), trigger: "blur" }
        ],
        remark: [
          { max: 250, message: this.$t('print.长度必须小于250个字符'), trigger: 'blur' }
        ]
      }
    };
  },
  props: {
    optType: undefined,
    client: undefined
  },
  mounted() {
    this.queryParams.clientId = this.client.clientId;
    this.getList()
    // 获取打印机列表
    const printIP = this.client.clientIp + ":" + this.client.clientPort
    if (this.client.clientToken) {
      hiprint.hiwebSocket.setHost(printIP, this.client.clientToken)
    } else {
      hiprint.hiwebSocket.setHost(printIP)
    }
  },
  methods: {
    //图片上传成功
    handleImgUplaoded(imgUrl){
      this.form.printerUrl = imgUrl;
    },
    //图片移除
    handleImgRemoved(imgUrl){
      this.form.printerUrl = null;
    },
    handleDownloadClient(){
      const resource = "http://101.43.244.58:9090/api/v1/buckets/printtool/objects/download?prefix=UHJpbnQuZXhl";
      this.$download.saveAs(resource,'client');
    },
    /**
     * 将指定打印机作为默认打印机，存储在浏览器本地
     * @param row
     */
    handleDefault(row){
      setDefaultPrinter(row).then(res => {
        if (res.code == 200) {
          this.getList();
          this.$modal.msgSuccess(this.$t('print.设置成功!'));
        }
      })
    },
    /** 查询打印机配置列表 */
    getList() {
      this.loading = true;
      listPrinterconfig(this.queryParams).then(response => {
        this.printerconfigList = response.rows;
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
        printerId: null,  printerCode: null,   printerType: null,        printerName: null,        brand: null,        printerModel: null,        connectionType: "0",        printerUrl: null,        printerIp: null,        printerPort: null,        clientSid: null,        clientIp: null,        clientPort: null,        enableFlag: 'Y',        status: "READY",        remark: null,        attr1: null,        attr2: null,        attr3: null,        attr4: null,        createBy: null,        createTime: null,        updateBy: null,        updateTime: null      };
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
      this.ids = selection.map(item => item.printerId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.printerList = hiprint.hiwebSocket.printerList
      this.reset();
      this.open = true;
      this.title = this.$t('print.添加打印机配置');
      this.optType = 'add';
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const printerId = row.printerId || this.ids
      getPrinterconfig(printerId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('print.修改打印机配置');
        this.optType = 'edit';
      });
    },
    /** 修改按钮操作 */
    handleView(row) {
      this.printerList = hiprint.hiwebSocket.printerList
      this.reset();
      const printerId = row.printerId || this.ids
      getPrinterconfig(printerId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = this.$t('print.查看打印机配置');
        this.optType = 'view';
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.clientId = this.client.clientId;
          if (this.form.printerId != null) {
            updatePrinterconfig(this.form).then(response => {
              this.$modal.msgSuccess(this.$t('system.修改成功'));
              this.open = false;
              this.getList();
            });
          } else {
            addPrinterconfig(this.form).then(response => {
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
      const printerIds = row.printerId || this.ids;
      this.$modal.confirm(this.$t('print.是否确认删除打印机配置编号为{v}的数据项？', { v: printerIds })).then(function() {
        return delPrinterconfig(printerIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(this.$t('system.删除成功'));
      }).catch(() => {});
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag){
      if(autoGenFlag){
        genCode('PRINTER_CODE').then(response =>{
          this.form.printerCode = response;
        });
      }else{
        this.form.printerCode = null;
      }
    }
  }
};
</script>
