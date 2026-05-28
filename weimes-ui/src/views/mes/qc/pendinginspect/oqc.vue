<template>
    <div class="app-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="检验单编号" prop="oqcCode">
              <el-input v-model="form.oqcCode" placeholder="请输入出货检验单编号" />
            </el-form-item>
          </el-col>

          <el-col :span="4">
            <el-form-item  label-width="80">
              <el-switch v-model="autoGenFlag"
                  active-color="#13ce66"
                  active-text="自动生成"
                  @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view' && form.status =='PREPARE'" >               
              </el-switch>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="检验单名称" prop="oqcName">
              <el-input v-model="form.oqcName" placeholder="请输入内容" />
            </el-form-item>
          </el-col>          
        </el-row>

        <el-row>
          <el-col :span="8">
            <el-form-item label="产品编码" prop="itemCode">
              <el-input v-if="form.oqcId ==null " readonly v-model="form.itemCode" placeholder="请选择产品" >
                <el-button slot="append" @click="handleSelectProduct" icon="el-icon-search"></el-button>
              </el-input>
              <!--如果已经保存过，则产品不允许再修改，需要修改就删除重做-->
              <el-input v-else v-model="form.itemCode"/>                
              <ItemSelect ref="itemSelect" @onSelected="onItemSelected" > </ItemSelect>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="产品名称" prop="itemName">
              <el-input readonly="readonly" v-model="form.itemName"></el-input>            
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="单位" prop="unitOfMeasure">
              <el-input readonly="readonly" v-model="form.unitOfMeasure"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="规格型号" prop="specification">
              <el-input readonly="readonly" v-model="form.specification" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="8">
            <el-form-item label="客户编号" prop="clientCode">
              <el-input v-model="form.clientCode" readonly placeholder="请输选择客户" >
                <el-button slot="append" @click="handleSelectClient" icon="el-icon-search"></el-button>
              </el-input>
            </el-form-item>
            <ClientSelect ref="clientSelect" @onSelected="onClientSelected" > </ClientSelect>
          </el-col>

          <el-col :span="8">
            <el-form-item label="客户名称" prop="clientName">
              <el-input readonly="readonly" v-model="form.clientName"  />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="批次号" prop="batchCode">
              <el-input v-model="form.batchCode" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="8">
            <el-form-item label="发货数量" prop="quantityOut">
              <el-input-number :min="1" v-model="form.quantityOut" placeholder="请输入发货数量" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="检测数量" prop="quantityCheck">
              <el-input-number :min="1" v-model="form.quantityCheck" placeholder="请输入本次检测数量" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="不合格数量" prop="quantityUnqualified">
              <el-input-number :min="0" readonly="readonly" v-model="form.quantityUnqualified" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="出货日期" prop="outDate">
              <el-date-picker clearable
                v-model="form.outDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择出货日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="检测日期" prop="inspectDate">
              <el-date-picker clearable
                v-model="form.inspectDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择检测日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="检测结果" prop="checkResult">
              <el-select v-model="form.checkResult" placeholder="请选择检测结果">
                <el-option
                  v-for="dict in dict.type.mes_qc_result"
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
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-collapse accordion>
          <el-collapse-item title="结果统计">
            <el-row>
              <el-col :span="8">
                <el-form-item label="致命缺陷数量" prop="crQuantity">
                  <el-input readonly="readonly" v-model="form.crQuantity" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="严重缺陷数量" prop="majQuantity">
                  <el-input readonly="readonly" v-model="form.majQuantity" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="轻微缺陷数量" prop="minQuantity">
                  <el-input readonly="readonly" v-model="form.minQuantity" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="致命缺陷率" prop="crQuantity">
                  <el-input readonly="readonly" v-model="form.crRate" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="严重缺陷率" prop="majRate">
                  <el-input readonly="readonly" v-model="form.majRate" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="轻微缺陷率" prop="minRate">
                  <el-input readonly="readonly" v-model="form.minRate" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>        
      </el-form>
      <el-divider v-if="form.oqcId !=null" content-position="center">检测项</el-divider> 
      <el-card shadow="always" v-if="form.oqcId !=null" class="box-card">
          <Oqcline ref=line :oqcId="form.oqcId" :optType="optType"></Oqcline>
      </el-card>
    </div>
</template>
<script>
import {genCode} from "@/api/system/autocode/rule";
import Oqcline from "../oqc/line.vue";
export default {
  name: "Oqc",
  dicts: ['mes_order_status','mes_qc_result'],
  components: {
    Oqcline
  },
  data(){
    return {
        //自动生成编码
        autoGenFlag:false,
        optType: undefined,
        // 表单参数
      form: {},
      // 表单校验
      rules: {
        oqcCode: [
          { required: true, message: "出货检验单编号不能为空", trigger: "blur" }
        ],
        clientCode: [
          { required: true, message: "客户不能为空", trigger: "blur" }
        ],       
        itemCode: [
          { required: true, message: "产品不能为空", trigger: "blur" }
        ],
        quantityOut: [
          { required: true, message: "发货数量不能为空", trigger: "blur" }
        ],
        quantityCheck: [
          { required: true, message: "本次检测数量不能为空", trigger: "blur" }
        ],
        outDate: [
          { required: true, message: "请选择出货日期", trigger: "blur" }
        ],
        inspectDate: [
          { required: true, message: "请选择检测日期", trigger: "blur" }
        ],
      }
    }
  },
  created() {

  },
  methods: {
    // 取消按钮
    cancel() {

    },
    // 表单重置
    reset() {
      this.form = {
        oqcId: null,
        oqcCode: null,
        oqcName: null,
        templateId: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        batchCode: null,
        itemId: null,
        itemCode: null,
        itemName: null,
        specification: null,
        unitOfMeasure: null,
        quantityMinCheck: null,
        quantityMaxUnqualified: null,
        quantityOut: null,
        quantityCheck: null,
        quantityUnqualified: null,
        quantityQuanlified: null,
        crRate: null,
        majRate: null,
        minRate: null,
        crQuantity: null,
        majQuantity: null,
        minQuantity: null,
        checkResult: null,
        outDate: null,
        inspectDate: null,
        inspector: null,
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
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.oqcId != null) {
            updateOqc(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
            });
          } else {
            addOqc(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
            });
          }
        }
      });
    },
    //点击完成
    handleFinish(){
      let that = this;
      if(this.form.checkResult == null){
        this.$modal.msgError("请选择检测结果！");
        return;
      }

      this.$refs["form"].validate(valid => {
        if (valid) {
          this.$modal.confirm('是否完成出货检验单编制？【完成后将不能更改】').then(function(){
            that.form.status = 'FINISHED';
            that.submitForm();
            that.cancel();
          });
        }
        });
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag){
      if(autoGenFlag){
        genCode('OQC_CODE').then(response =>{
          this.form.oqcCode = response;
        });
      }else{
        this.form.oqcCode = null;
      }
    },
  }

}
</script>