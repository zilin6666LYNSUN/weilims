import pymysql
import json

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# 1. 检查报告模板表数据
cur.execute("SELECT COUNT(*) FROM lims_report_template")
print(f"报告模板数量: {cur.fetchone()[0]}")

# 2. 检查校验规则表数据
cur.execute("SELECT COUNT(*) FROM lims_data_validation_rule")
print(f"校验规则数量: {cur.fetchone()[0]}")

# 3. 检查采集配置表数据
cur.execute("SELECT COUNT(*) FROM lims_instrument_data_acquisition")
print(f"采集配置数量: {cur.fetchone()[0]}")

# 4. 检查报告表数据
cur.execute("SELECT COUNT(*) FROM lims_report")
print(f"报告数量: {cur.fetchone()[0]}")

# 5. 检查签章表数据
cur.execute("SELECT COUNT(*) FROM lims_report_signature")
print(f"签章记录数量: {cur.fetchone()[0]}")

# 6. 检查校验日志表数据
cur.execute("SELECT COUNT(*) FROM lims_data_validation_log")
print(f"校验日志数量: {cur.fetchone()[0]}")

# 7. 查看检验订单数据
cur.execute("SELECT test_order_id, test_order_name, status FROM lims_test_order LIMIT 5")
print("\n=== 检验订单 ===")
for row in cur.fetchall():
    print(f"  ID={row[0]}, 名称={row[1]}, 状态={row[2]}")

# 8. 查看检验结果数据
cur.execute("SELECT result_id, test_order_id, test_item_name, reported_value, lower_limit, upper_limit, result_conclusion, is_oos FROM lims_test_result LIMIT 5")
print("\n=== 检验结果 ===")
for row in cur.fetchall():
    print(f"  ID={row[0]}, 订单ID={row[1]}, 项目={row[2]}, 报告值={row[3]}, 下限={row[4]}, 上限={row[5]}, 结论={row[6]}, OOS={row[7]}")

# 9. 查看仪器数据
cur.execute("SELECT instrument_id, instrument_code, instrument_name FROM lims_instrument LIMIT 5")
print("\n=== 仪器 ===")
for row in cur.fetchall():
    print(f"  ID={row[0]}, 编号={row[1]}, 名称={row[2]}")

# 10. 查看用户数据
cur.execute("SELECT user_id, user_name, nick_name FROM sys_user LIMIT 10")
print("\n=== 用户 ===")
for row in cur.fetchall():
    print(f"  ID={row[0]}, 用户名={row[1]}, 昵称={row[2]}")

cur.close()
conn.close()
