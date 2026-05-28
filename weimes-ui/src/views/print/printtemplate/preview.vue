<template>
  <el-dialog :visible.sync="visible" v-loading="spinning" :maskClosable="false"
             @close="hideModal" :width="`${80}vw`">
    <div v-show="isMultiPanel" id="template-preview-printPagination" style="margin: 14px 0 0 10px;"></div>
    <el-row>
      <el-col :span="18">
        <el-card class="card-design">
          <div id="template-preview"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div id="template-preview-setting"></div>
        </el-card>
      </el-col>
    </el-row>
    <template slot="title">
      <div>模板预览</div>
    </template>
    <template slot="footer">
      <el-button key="close" type="info" @click="hideModal">关闭</el-button>
      <el-button key="pdf" type="info" @click="toPdf">pdf打印</el-button>
    </template>
  </el-dialog>
</template>

<script>
import { hiprint } from 'vue-plugin-hiprint';
import printData from "./print-data";
import qrCodes from "./qrCodeData";
export default {
  name: "templatePreview",
  data() {
    return {
      visible: false,
      spinning: true,
      waitShowPrinter: false,
      template: null,
      isMultiPanel: true,
      hiprintTemplate: {},
      name: '名称',
      json: {},
      printData: {},
      extendCss: ''
    }
  },
  methods: {
    hideModal() {
      this.visible = false;
      $("link[media=print]").empty();
    },
    async show(template) {
      try {
        if (!template || !template.getJson) {
          throw new Error('无效的模板数据');
        }
        
        this.initializeTemplate(template);
        await this.delay(200);
        
        const templateJson = template.getJson();
        if (!templateJson || !templateJson.panels) {
          throw new Error('模板数据格式错误');
        }
        
        await this.setupHiprintTemplate(template);
        this.isMultiPanel = templateJson.panels.length > 1;
        console.log('isMultiPanel', this.isMultiPanel);
        this.spinning = false;
      } catch (error) {
        console.error('Error in template preview:', error);
        this.$message.error('模板预览失败: ' + error.message);
        this.spinning = false;
        this.visible = false;
      }
    },
    initializeTemplate(template) {
      this.visible = true;
      this.template = template;
      this.name = template.name;
      this.json = template.json;
      this.printData = template.printData;
      this.extendCss = template.extendCss || "";
      if (this.extendCss.length > 1) {
        $(this.extendCss).appendTo($("link[media=print]"));
      }
      this.hiprintTemplate = new hiprint.PrintTemplate({
        template: template.getJson(),
        settingContainer: '#template-preview-setting',
        paginationContainer: '#template-preview-printPagination'
      });
    },
    async setupHiprintTemplate(template) {
      $('#template-preview').empty();
      this.hiprintTemplate.design($('#template-preview'));
    },
    delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    print() {
      this.performPrintOperation(this.hiprintTemplate.print, () => {
        this.waitShowPrinter = false;
      });
    },
    toPdf() {
      console.log('--view topdf---');
      this.hiprintTemplate.toPdf(qrCodes, '打印预览');
    },
    print2() {
      if (hiprint.hiwebSocket.opened) {
        this.performPrintOperation(this.hiprintTemplate.print2, undefined, { printer: '', title: this.name });
      } else {
        this.$message.error('请先连接直接打印客户端');
      }
    },
    performPrintOperation(printMethod, callback, options = {}) {
      this.waitShowPrinter = true;
      const methodOptions = {
        ...options,
        styleHandler: () => this.extendCss
      };
      printMethod.call(this.hiprintTemplate, this.printData, methodOptions, callback);
    }
  }
}
</script>

<style scoped>
.card-design {
  overflow: hidden;
  overflow-x: auto;
  overflow-y: auto;
}

/deep/ .el-dialog__body {
  padding: 0px;
}

/deep/ .el-dialog__content {
  margin-bottom: 24px;
}


tbody .hiprint-printElement-tableTarget-border-all{
  border-top: 1px solid !important;
  border-bottom: 1px solid !important;
}
td{
  border: 1px solid ;
}
.drag_item_box {
  height: 100%;
  padding: 6px;
}

.drag_item_box > div {
  height: 100%;
  width: 100%;
  background-color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
}



.drag_item_box > div > a {
  text-align: center;
  text-decoration-line: none;
}

.drag_item_box > div > a > .draggable-item-svg {
  width: 50px;
  height: 50px;
}


.drag_item_box > div > a > span {
  font-size: 28px;
}

.drag_item_box > div > a > p {
  margin: 0;
}

.drag_item_title {
  font-size: 16px;
  padding: 12px 6px 0 6px;
  font-weight: bold;
}



.card-design {
  width: 900px;
  height: 1200px;
  overflow: hidden;
  overflow-x: auto;
  overflow-y: auto;
}

</style>
