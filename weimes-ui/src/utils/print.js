import { Message } from "element-ui";
import {getDefaultPrinter} from "../api/print/printerconfig";

export const PRINT_DICT = {
  PRINT_CLIENT_IP: "127.0.0.8:17521",
  PRINT_CLIENT_TOKEN: "123456",
}

/**
 * 直接打印（使用Hiprint客户端打印）
 * @param printTemplate 打印模板
 * @param  printData 打印数据
 * @param  hiprintTemplate hiprint模板
 */
export function print(printTemplate, printData, hiprintTemplate) {
  if (hiprintTemplate) {
    try {
      // 将打印模板封装
      hiprintTemplate.update(JSON.parse(printTemplate));
      if (window.hiwebSocket.opened) {
        // 根据配置好的客户端查询相应的打印机
        const client = JSON.parse(localStorage.getItem("defaultClient"))
        getDefaultPrinter(client.clientId).then(res => {
          if (res.code == 200) {
            console.log(res.data)
            // hiprintTemplate.print2(printData, {printer: res.data.printerName, title: 'hiprint测试打印'});
            Message.success("打印成功")
          }
        }).catch(err => {
          Message.error("没有设置默认打印机")
        })
        return
      }
      Message.error('客户端未连接,无法直接打印')
    } catch (e) {
      Message.error(`更新失败: ${e}`)
    }
  }
}
