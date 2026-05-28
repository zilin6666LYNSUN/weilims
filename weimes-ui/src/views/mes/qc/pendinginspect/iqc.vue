<template>
    <div class="app-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="检验单编号" prop="iqcCode">
              <el-input v-model="form.iqcCode" placeholder="请输入来料检验单编号" />
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
          <el-col :span="9">
            <el-form-item label="检验单名称" prop="iqcName">
              <el-input v-model="form.iqcName" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="3" align="middle">
              <el-image fit="contain" v-if="form.checkResult == 'ACCEPT'" :src="acceptImg" />
              <el-image fit="contain" v-else-if="form.checkResult == 'REJECT'" :src="rejectImg" />
              <el-image fit="contain" v-else :src="prepareImg" />
          </el-col>
        </el-row>
        <el-divider content-position="center">物料与供应商</el-divider>
        <el-row>
          <el-col :span="8">
            <el-form-item label="产品物料编码" prop="itemCode">
              <el-input v-if="form.iqcId ==null " v-model="form.itemCode" readonly placeholder="请选择物料" >
                <el-button slot="append" @click="handleSelectProduct" icon="el-icon-search"></el-button>
              </el-input>
              <!--如果已经保存过，则产品不允许再修改，需要修改就删除重做-->
              <el-input v-else v-model="form.itemCode">                
              </el-input>
            </el-form-item>
            <ItemSelect ref="itemSelect" @onSelected="onItemSelected" > </ItemSelect>
          </el-col>
          <el-col :span="8">
            <el-form-item label="产品物料名称" prop="itemName">
              <el-input v-model="form.itemName" readonly="readonly" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="unitOfMeasure">
              <el-input v-model="form.unitOfMeasure" readonly="readonly" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="供应商编码" prop="vendorCode">
              <el-input v-model="form.vendorCode" readonly placeholder="请选择供应商" >
                <el-button slot="append" @click="handleSelectVendor" icon="el-icon-search"></el-button>
              </el-input>
            </el-form-item>
            <VendorSelect ref="vendorSelect" @onSelected="onVendorSelected" />
          </el-col>
          <el-col :span="8">
            <el-form-item label="供应商简称" prop="vendorNick">
              <el-input v-model="form.vendorNick" readonly="readonly" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="供应商批次号" prop="vendorBatch">
              <el-input v-model="form.vendorBatch" placeholder="请输入供应商批次号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="center">检测情况</el-divider>
        <el-row>
          <el-col :span="8">
            <el-form-item label="本次接收数量" prop="quantityRecived">
              <el-input-number :min="1" v-model="form.quantityRecived" placeholder="请输入本次接收数量" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="本次检测数量" prop="quantityCheck">
              <el-input :min="1" v-model="form.quantityCheck" placeholder="请输入本次检测数量" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="不合格数量" prop="quantityUnqualified">
              <el-input-number :min="0" v-model="form.quantityUnqualified" placeholder="请输入不合格数" />
            </el-form-item>
          </el-col>
        </el-row>
            <el-row>
          <el-col :span="8">
            <el-form-item label="来料日期" prop="reciveDate">
              <el-date-picker clearable
                v-model="form.reciveDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择来料日期">
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
                <el-form-item label="致命缺陷率" prop="crRate">
                  <el-input v-model="form.crRate" readonly="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="严重缺陷率" prop="majRate">
                  <el-input v-model="form.majRate" readonly="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="轻微缺陷率" prop="minRate">
                  <el-input v-model="form.minRate" readonly="readonly" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="致命缺陷数量" prop="crQuantity">
                  <el-input v-model="form.crQuantity" readonly="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="严重缺陷数量" prop="majQuantity">
                  <el-input v-model="form.majQuantity" readonly="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="轻微缺陷数量" prop="minQuantity">
                  <el-input v-model="form.minQuantity" readonly="readonly" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>       
        <el-divider v-if="form.iqcId !=null" content-position="center">检测项</el-divider> 
        <el-card shadow="always" v-if="form.iqcId !=null" class="box-card">
          <IqcLine ref=line :iqcId="form.iqcId" :optType="optType"></IqcLine>
        </el-card>
      </el-form>
      <el-form label-width="100px">
      <el-form-item style="text-align: center;margin-left:-120px;margin-top:30px;">
        <el-button type="primary" @click="cancel" v-if="optType =='view' || form.status !='PREPARE' ">返回</el-button>
        <el-button type="primary" @click="submitForm" v-if="form.status =='PREPARE' && optType !='view' ">保 存</el-button>
        <el-button type="success" @click="handleFinish" v-if="form.status =='PREPARE' && optType !='view'  && form.iqcId !=null">完成</el-button>
        <el-button @click="cancel">取 消</el-button>
      </el-form-item>
    </el-form>
    </div>
</template>

<script>
import { listIqc, getIqc, delIqc, addIqc, updateIqc } from "@/api/mes/qc/iqc";
import ItemSelect  from "@/components/itemSelect/single.vue";
import VendorSelect from "@/components/vendorSelect/single.vue";
import IqcLine from "../iqc/iqcline.vue";
import {genCode} from "@/api/system/autocode/rule"
export default {
    name: "pendingIqc",
    components: {ItemSelect,VendorSelect,IqcLine},
    dicts: ['mes_ipqc_type','mes_qc_result','mes_order_status'],
    data(){
        return {
            // 表单参数
            form: {},
            // 表单校验
            rules: {
                ipqcCode: [
                { required: true, message: "请输入或生产检验单编号", trigger: "blur" }
                ],
                ipqcType: [
                { required: true, message: "请选择检验类型", trigger: "change" }
                ],

                workorderCode: [
                { required: true, message: "请选择生产工单", trigger: "blur" }
                ],

                workstationCode: [
                { required: true, message: "请选择工作站", trigger: "blur" }
                ],
            
                quantityCheck: [
                { required: true, message: "检测数量不能为空", trigger: "blur" }
                ],

                quantityUnqualified: [
                { required: true, message: "不合格品数量不能为空", trigger: "blur" }
                ],

                quantityQualified: [
                { required: true, message: "合格品数量不能为空", trigger: "blur" }
                ]

            }
        }
    },
    created() {
      this.reset();
    },
    methods: {
      // 取消按钮
      cancel() {
        const obj = { path: "/mes/qc/pendinginspect" };
        this.$tab.closeOpenPage(obj);
      },
      // 表单重置
      reset() {
        this.form = {
          iqcId: null,
          iqcCode: null,
          iqcName: null,
          templateId: null,
          sourceDocId: this.$route.params.sourceDocId,
          sourceDocType: this.$route.params.sourceDocType,
          sourceDocCode: this.$route.params.sourceDocCode,
          sourceLineId: this.$route.params.sourceLineId,
          vendorId: this.$route.params.vendorClientId,
          vendorCode: this.$route.params.vendorClientCode,
          vendorName: this.$route.params.vendorClientName,
          vendorNick: this.$route.params.vendorClientNick,
          vendorBatch: this.$route.params.batchCode,
          itemId: this.$route.params.itemId,
          itemCode: this.$route.params.itemCode,
          itemName: this.$route.params.itemName,
          specification: this.$route.params.specification,
          unitOfMeasure: this.$route.params.unitOfMeasure,
          quantityMinCheck: null,
          quantityMaxUnqualified: null,
          quantityRecived: this.$route.params.quantityUncheck,
          quantityCheck: null,
          quantityUnqualified: null,
          crRate: null,
          majRate: null,
          minRate: null,
          crQuantity: null,
          majQuantity: null,
          minQuantity: null,
          checkResult: null,
          reciveDate: this.$route.params.recordTime,
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
            if (this.form.iqcId != null) {
              updateIqc(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
              });
            } else {
              addIqc(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.form.iqcId=response.data;
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
            this.$modal.confirm('是否完成来料检验单编制？【完成后将不能更改】').then(function(){
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
          genCode('QC_IQC_CODE').then(response =>{
            this.form.iqcCode = response;
          });
        }else{
          this.form.iqcCode = null;
        }
      },
    }
}


</script>