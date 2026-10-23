<template>
    <div class="app-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.检验单编号')" prop="ipqcCode">
              <el-input v-model="form.ipqcCode" :placeholder="$t('mes.请输入检验单编号')" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item  label-width="80">
              <el-switch v-model="autoGenFlag"
                  active-color="#13ce66"
                  :active-text="$t('mes.自动生成')"
                  @change="handleAutoGenChange(autoGenFlag)" v-if="optType != 'view' && form.status =='PREPARE'">               
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('mes.检验单名称')" prop="ipqcName">
              <el-input v-model="form.ipqcName" :placeholder="$t('mes.请输入检验单名称')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('lims.检验类型')" prop="ipqcType">
              <el-select v-model="form.ipqcType" :placeholder="$t('mes.请选择检验类型')">
                <el-option
                  v-for="dict in dict.type.mes_ipqc_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.工单编码')" prop="workorderCode">
              <el-input v-if="form.ipqcId == null" v-model="form.workorderCode" :placeholder="$t('mes.请选择生产工单')" >
              </el-input>
              <!--保存过则不允许修改工单，需要修改则删除重做-->
              <el-input v-else v-model="form.workorderCode" >              
              </el-input>
            </el-form-item>              
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.工单名称')" prop="workorderName">
              <el-input v-model="form.workorderName" readonly="readonly" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('mes.工作站编号')" prop="workstationCode">
              <el-input v-model="form.workstationCode" :placeholder="$t('mes.请输入工作站编号')" >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.工作站名称')" prop="workstationName">
              <el-input v-model="form.workstationName" readonly="readonly"  />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.检测数量')" prop="quantityCheck">
              <el-input v-model="form.quantityCheck" :placeholder="$t('mes.请输入检测数量')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
            <el-col :span="8">
            <el-form-item :label="$t('mes.产品编码')" prop="itemCode">
                <el-input v-model="form.itemCode" :placeholder="$t('mes.请输入产品编码')" />
            </el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item :label="$t('lims.产品名称')" prop="itemName">
                <el-input v-model="form.itemName" :placeholder="$t('mes.请输入产品名称')" />
            </el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item :label="$t('lims.单位')" prop="unitOfMeasure">
                <el-input v-model="form.unitOfMeasure" :placeholder="$t('lims.请输入单位')" />
            </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24">
            <el-form-item :label="$t('lims.规格型号')" prop="specification">
                <el-input v-model="form.specification" type="textarea" :placeholder="$t('common.inputContent')" />
            </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="8">
            <el-form-item :label="$t('mes.工序编码')" prop="processCode">
                <el-input v-model="form.processCode" :placeholder="$t('mes.请输入工序编码')" />
            </el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item :label="$t('mes.工序名称')" prop="processName">
                <el-input v-model="form.processName" :placeholder="$t('mes.请输入工序名称')" />
            </el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item :label="$t('mes.任务编号')" prop="taskCode">
                <el-input v-model="form.taskCode" :placeholder="$t('mes.请输入任务编号')" />
            </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="8">
            <el-form-item :label="$t('lims.任务名称')" prop="taskName">
                <el-input v-model="form.taskCode" :placeholder="$t('mes.请输入任务编号')" />
            </el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item :label="$t('mes.不合格数')" prop="quantityUnqualified">
                <el-input v-model="form.quantityUnqualified" :placeholder="$t('mes.请输入不合格数')" />
            </el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item :label="$t('mes.合格品数量')" prop="quantityQualified">
                <el-input v-model="form.quantityQualified" :placeholder="$t('mes.请输入合格品数量')" />
            </el-form-item>
            </el-col>
        </el-row>        
        <el-row>
            <el-col :span="8">
            <el-form-item :label="$t('mes.致命缺陷数量')" prop="crQuantity">
                <el-input v-model="form.crQuantity" :placeholder="$t('mes.请输入致命缺陷数量')" />
            </el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item :label="$t('mes.严重缺陷数量')" prop="majQuantity">
                <el-input v-model="form.majQuantity" :placeholder="$t('mes.请输入严重缺陷数量')" />
            </el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item :label="$t('mes.轻微缺陷数量')" prop="minQuantity">
                <el-input v-model="form.minQuantity" :placeholder="$t('mes.请输入轻微缺陷数量')" />
            </el-form-item>
            </el-col>
        </el-row>

        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('lims.检测日期')" prop="inspectDate">
              <el-date-picker clearable
                v-model="form.inspectDate"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('mes.请选择检测日期')">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.检测结果')" prop="checkResult">
              <el-select v-model="form.checkResult" :placeholder="$t('mes.请选择检验结果')">
                <el-option
                  v-for="dict in dict.type.mes_qc_result"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('mes.检测人员')" prop="inspector">
              <el-input v-model="form.inspector" :placeholder="$t('mes.请输入检测人员')" />
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
      <el-divider v-if="form.ipqcId !=null" content-position="center">{{ this.$t('lims.检测项') }}</el-divider> 
      <el-card shadow="always" v-if="form.ipqcId !=null" class="box-card">
          <Ipqcline ref=line :ipqcId="form.ipqcId" :optType="optType"></Ipqcline>
      </el-card>
      <el-form label-width="100px">
        <el-form-item style="text-align: center;margin-left:-120px;margin-top:30px;">
            <el-button type="primary" @click="cancel" v-if="optType =='view' || form.status !='PREPARE' ">{{ this.$t('common.back') }}</el-button>
            <el-button type="primary" @click="submitForm" v-if="form.status =='PREPARE' && optType !='view' ">{{ this.$t('mes.保 存') }}</el-button>
            <el-button type="success" @click="handleFinish" v-if="form.status =='PREPARE' && optType !='view'  && form.ipqcId !=null">{{ this.$t('lims.完成') }}</el-button>
            <el-button @click="cancel">{{ this.$t('system.取 消') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
</template>
<script>
import { listIpqc, getIpqc, delIpqc, addIpqc, updateIpqc } from "@/api/mes/qc/ipqc";
import {genCode} from "@/api/system/autocode/rule"
import WorkorderSelect from "@/components/workorderSelect/single.vue"
import WorkstationSelect from "@/components/workstationSelect/simpletableSingle.vue"
import Ipqcline from "../ipqc/line.vue"
export default {
    name: "IpqcAdd",
    components: {WorkorderSelect,WorkstationSelect,Ipqcline},
    dicts: ['mes_ipqc_type','mes_qc_result','mes_order_status'],
    props: {
        workOrderId: null,
        workOrderCode: null,
        workOrderName: null,
        workstationId: null,
        workstationCode: null,
        workstationName: null,
        taskId: null,
        taskCode: null,
        taskName: null,
        itemId: null,
        itemCode: null,
        itemName: null,        
        quantityCheck: null
    },
    data() {
        return {
            autoGenFlag: false,
            optType: 'add',
            // 表单参数
            form: {},
            // 表单校验
            rules: {
                ipqcCode: [
                { required: true, message: this.$t('mes.请输入或生产检验单编号'), trigger: "blur" }
                ],
                ipqcType: [
                { required: true, message: this.$t('mes.请选择检验类型'), trigger: "change" }
                ],

                workorderCode: [
                { required: true, message: this.$t('mes.请选择生产工单'), trigger: "blur" }
                ],

                workstationCode: [
                { required: true, message: this.$t('mes.请选择工作站'), trigger: "blur" }
                ],
            
                quantityCheck: [
                { required: true, message: this.$t('mes.检测数量不能为空'), trigger: "blur" }
                ],

                quantityUnqualified: [
                { required: true, message: this.$t('mes.不合格品数量不能为空'), trigger: "blur" }
                ],

                quantityQualified: [
                { required: true, message: this.$t('mes.合格品数量不能为空'), trigger: "blur" }
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
                ipqcId: null,
                ipqcCode: null,
                ipqcName: null,
                ipqcType: null,
                templateId: null,
                sourceDocId: this.$route.params.sourceDocId,
                sourceDocType: this.$route.params.sourceDocType,
                sourceDocCode: this.$route.params.sourceDocCode,
                sourceLineId: this.$route.params.sourceLineId,
                workorderId: this.$route.params.workOrderId,
                workorderCode: this.$route.params.workOrderCode,
                workorderName: this.$route.params.workOrderName,
                taskId: this.$route.params.taskId,
                taskCode: this.$route.params.taskCode,
                taskName: this.$route.params.taskName,
                workstationId: this.$route.params.workstationId,
                workstationCode: this.$route.params.workstationCode,
                workstationName: this.$route.params.workstationName,
                processId: null,
                processCode: null,
                processName: null,
                itemId: this.$route.params.itemId,
                itemCode: this.$route.params.itemCode,
                itemName: this.$route.params.itemName,
                specification: this.$route.params.specification,
                unitOfMeasure: this.$route.params.unitOfMeasure,
                quantityCheck: this.$route.params.quantityUncheck,
                quantityUnqualified: null,
                quantityQualified: null,
                crRate: null,
                majRate: null,
                minRate: null,
                crQuantity: null,
                majQuantity: null,
                minQuantity: null,
                checkResult: null,
                inspectDate: new Date(),
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
                    if (this.form.ipqcId != null) {
                        updateIpqc(this.form).then(response => {
                            this.$modal.msgSuccess(this.$t('system.修改成功'));
                        });
                    } else {
                        addIpqc(this.form).then(response => {
                            this.$modal.msgSuccess(this.$t('system.新增成功'));
                            this.form.ipqcId = response.data; //将返回的ID保存
                        });
                    }
                }
            });
        },
        //点击完成
        handleFinish(){
            let that = this;
            if(this.form.checkResult == null){
                this.$modal.msgError(this.$t('mes.请选择检测结果！'));
                return;
            }

            this.$refs["form"].validate(valid => {
                    if (valid) {
                        this.$modal.confirm(this.$t('mes.是否完成检验单编制？【完成后将不能更改】')).then(function(){
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
                genCode('IPQC_CODE').then(response =>{
                this.form.ipqcCode = response;
                });
            }else{
                this.form.ipqcCode = null;
            }
        }
    }
}
</script>