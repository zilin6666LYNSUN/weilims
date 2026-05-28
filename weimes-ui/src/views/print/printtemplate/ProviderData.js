import {hiprint} from 'vue-plugin-hiprint'

// 自定义设计元素1
export const aProvider = function (ops) {
  var addElementTypes = function (context) {
    context.removePrintElementTypes("defaultModule");
    context.addPrintElementTypes(
      "defaultModule",
      [
        new hiprint.PrintElementTypeGroup("平台", [
          // 固定文本
          {
            tid: 'defaultModule.text', title: '固定文本', data: '固定文本', type: 'text',
            options: {
              type: "text",
              testData: '',
              height: 12,
              fontSize: 6.75,
              width: 61.5,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          // 图片
          {tid: 'defaultModule.image', title: '', data: '', type: 'image'},
          // 长文本
          {
            tid: 'defaultModule.longText', title: '长文本', data: '长文本', type: 'longText',
            options: {
              type: "text",
              testData: '',
              height: 12,
              fontSize: 6.75,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          // 日期时间
          {
            tid: 'defaultModule.date', title: '', data: '2020-01-01', type: 'text',
            options: {
              type: "date",
              field: 'date',
              testData: '2020-01-01',
              height: 16,
              fontSize: 6.75,
              fontWeight: "700",
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          //表格
          {
            tid: "defaultModule.table",
            field: "table",
            title: "表格",
            type: "table",
            groupFields: ["name"],
/*            groupFooterFormatter: function(t, e) {
              return "这里自定义统计脚信息"
            },*/
            columns: [
              [{
                title: "行号",
                fixed: false,
                rowspan: 2,
                field: "id",
                width: 70
              }, {
                title: "人员信息",
                colspan: 2
              }, {
                title: "销售统计",
                colspan: 2
              }],
              [{
                title: "姓名",
                align: "left",
                field: "name",
                width: 100
              }, {
                title: "性别",
                field: "gender",
                width: 100
              }, {
                title: "销售数量",
                field: "count",
                width: 100
              }, {
                title: "销售金额",
                field: "amount",
                width: 100
              }]
            ],
            editable: !0,
            columnDisplayEditable: !0,
            columnDisplayIndexEditable: !0,
            columnTitleEditable: !0,
            columnResizable: !0,
            columnAlignEditable: !0,
            isEnableEditField: !0,
            isEnableContextMenu: !0,
            isEnableInsertRow: !0,
            isEnableDeleteRow: !0,
            isEnableInsertColumn: !0,
            isEnableDeleteColumn: !0,
            isEnableMergeCell: !0
          },
          //空白表格
          {
            tid: "defaultModule.emptyTable",
            title: "空白表格",
            type: "table",
            columns: [
              [{
                title: "",
                field: "",
                width: 100
              }, {
                title: "",
                field: "",
                width: 100
              }]
            ]
          }, {
            tid: "defaultModule.html",
            title: "html",
            formatter: function(t, e) {
              return '<div style="height:50pt;width:50pt;background:red;border-radius: 50%;"></div>'
            },
            type: "html"
          },

          // 横线
          {
            tid: 'defaultModule.hline',
            title: '横线',
            type: 'hline',
            options: {
              width: 90,
              height: 9
            }
          },
          // 竖线
          {
            tid: 'defaultModule.vline',
            title: '竖线',
            type: 'vline',
            options: {
              width: 9,
              height: 90
            }
          },
          // 矩形
          {
            tid: 'defaultModule.rect',
            title: '矩形',
            type: 'rect',
            options: {
              width: 66,
              height: 42
            }
          },
          // 椭圆
          {
            tid: 'defaultModule.oval',
            title: '椭圆',
            type: 'oval',
            options: {
              width: 60,
              height: 60
            }
          },
          // 条形码
          {
            tid: 'defaultModule.barcode', title: '条形码', data: '', type: 'text',
            options: {
              type: "barcode",
              field: 'barcode',
              testData: 'test',
              height: 32,
              fontSize: 12,
              lineHeight: 18,
              textType: "barcode"
            }
          },
          // 二维码
          {
            tid: 'defaultModule.qrcode', title: '二维码', data: '', type: 'text',
            options: {
              type: "qrcode",
              field: 'qrcode',
              testData: 'test',
              height: 40,
              width: 40,
              fontSize: 12,
              lineHeight: 18,
              textType: "qrcode"
            }
          },
          // 物料编码
          {
            tid: 'defaultModule.itemCode', title: "物料编码", data: "${itemCode}", type: 'text',
            options: {
              type: "itemCode",
              testData: "${itemCode}",
              height: 12,
              fontSize: 6.75,
              width: 61.5,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          // 物料名称
          {
            tid: 'defaultModule.itemName', title: "物料名称", data: "${itemName}", type: 'text',
            options: {
              type: "itemName",
              testData: "${itemName}",
              height: 12,
              fontSize: 6.75,
              width: 61.5,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          // 工单编码
          {
            tid: 'defaultModule.workorderCode', title: "工单编码", data: "${workorderCode}", type: 'text',
            options: {
              type: "workorderCode",
              testData: "${workorderCode}",
              height: 12,
              fontSize: 6.75,
              width: 61.5,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          // 工单名称
          {
            tid: 'defaultModule.workorderName', title: "工单名称", data: "${workorderName}", type: 'text',
            options: {
              type: "workorderName",
              testData: "${workorderName}",
              height: 12,
              fontSize: 6.75,
              width: 61.5,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          // 供应商编码
          {
            tid: 'defaultModule.vendorCode', title: "供应商编码", data: "${vendorCode}", type: 'text',
            options: {
              type: "vendorCode",
              testData: "${vendorCode}",
              height: 12,
              fontSize: 6.75,
              width: 61.5,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          // 供应商名称
          {
            tid: 'defaultModule.vendorName', title: "供应商名称", data: "${vendorName}", type: 'text',
            options: {
              type: "vendorName",
              testData: "${vendorName}",
              height: 12,
              fontSize: 6.75,
              width: 61.5,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          // 客户编码
          {
            tid: 'defaultModule.clientCode', title: "客户编码", data: "${clientCode}", type: 'text',
            options: {
              type: "clientCode",
              testData: "${clientCode}",
              height: 12,
              fontSize: 6.75,
              width: 61.5,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
          // 客户名称
          {
            tid: 'defaultModule.clientName', title: "客户名称", data: "${clientName}", type: 'text',
            options: {
              type: "clientName",
              testData: "${clientName}",
              height: 12,
              fontSize: 6.75,
              width: 61.5,
              textAlign: "left",
              textContentVerticalAlign: "middle"
            }
          },
        ]),
      ]
    );
  };
  return {
    addElementTypes: addElementTypes
  };
};

export default aProvider()
