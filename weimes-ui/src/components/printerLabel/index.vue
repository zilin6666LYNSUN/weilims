<template>
    <el-button
            size="mini"
            type="text"
            icon="el-icon-printer"
            @click="handlePrint()"
          >{{labelText}}</el-button>
</template>
<script>
import { printBarcodeLabel } from "@/api/print/print";
export default {
    name: 'printLabel',
    data(){
        return {
            
        }
    },
    props:{
        businessId: null,
        businessCode: null,
        businessType: null,
        labelText: {
            type: String,
            default: '打印'
        },
        printerCode: null
    },
    methods:{
        handlePrint(){

            var theCode;
            if(this.printerCode ==null){
                //是否有配置默认的打印机
                const key = 'defaultPrinter';
                let printerJson = localStorage.getItem(key);
                if(printerJson !=null){
                    const printer = JSON.parse(printerJson);
                    theCode = printer.printerCode;
                }else{
                    this.$modal.msgError("请在打印管理-打印机配置功能中指定默认打印机！");
                }
            }else{
                theCode = this.printerCode;
            }

            const param = { businessId: this.businessId, businessCode: this.businessCode, businessType: this.businessType, printerCode: theCode };
            printBarcodeLabel(param).then(response => {
              this.$modal.msgSuccess("打印成功");
            });
        }
    }
}


</script>