<template>
    <el-button
            size="mini"
            type="text"
            icon="el-icon-printer"
            @click="handlePrint()"
          >{{ labelText || this.$t('common.print') }}</el-button>
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
            default: ''
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
                    this.$modal.msgError(this.$t("common.setDefaultPrinter"));
                }
            }else{
                theCode = this.printerCode;
            }

            const param = { businessId: this.businessId, businessCode: this.businessCode, businessType: this.businessType, printerCode: theCode };
            printBarcodeLabel(param).then(response => {
              this.$modal.msgSuccess(this.$t("common.printSuccess"));
            });
        }
    }
}


</script>