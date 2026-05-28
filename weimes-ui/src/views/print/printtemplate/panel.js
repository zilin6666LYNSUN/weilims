export default {
  panels: [
    {
      index: 0,
      height: 297,
      width: 210,
      paperHeader: 0,
      paperFooter: 842,
      printElements: [
        {
          options: {
            left: 20,
            top: 20,
            height: 56,
            width: 555,
            field: "table1",
            tableHeaderRepeat: "none",
            tableBorder: "noBorder",
            tableBodyRowBorder: "noBorder",
            tableBodyCellBorder: "noBorder",
            gridColumns: 2,
            gridColumnsGutter: 3,
            fields: [
              {
                text: "商品名称",
                field: "name",
              },
              {
                text: "商品编码",
                field: "code",
              },
            ],
            columns: [
              [
                {
                  title: "商品名称",
                  field: "name",
                  checked: true,
                  renderFormatter:
                      "function(value,row,index,options){console.log('row',row);return `${row.barcode||'test'}`}",
                      // "function(value,row,index,options){console.log('row',row);return `商品名：${row.name || '测试商品名'}<br>条码：${row.barcode || '123456'}`}",
                },
                {
                  title: "商品条码",
                  field: "barcode",
                  checked: true,
                  tableTextType: "barcode",
                },
              ],
            ],
          },
          printElementType: {
            title: "表格",
            type: "table",
          },
        },
      ],
      paperNumberLeft: 565.5,
      paperNumberTop: 819,
    },
  ],
}
