import pymysql
import json
import time
from datetime import date, datetime

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

print("=" * 60)
print("LIMS报告系统 - 集成测试数据准备")
print("=" * 60)

# ==========================================
# 1. 准备报告模板数据
# ==========================================
print("\n[1] 准备报告模板数据...")

templates = [
    ("TPL-CMA-001", "CMA食品检验报告模板", "CMA", "FOOD", "适用于CMA食品类检验报告", "V1.0", "A4", "PORTRAIT", 2.0, 2.0, 2.5, 2.5,
     '<div style="text-align:center;font-size:18px;font-weight:bold">检验报告</div><div style="text-align:center;font-size:12px">CMA资质认定</div>',
     '<div style="text-align:center;font-size:10px">第 {page} 页 / 共 {total} 页</div><div>本报告未经本机构书面批准，不得部分复制</div>',
     "受控文件", 1, date.today(), None, "PUBLISHED"),
    ("TPL-CNAS-001", "CNAS药品检验报告模板", "CNAS", "DRUG", "适用于CNAS药品类检验报告", "V1.0", "A4", "PORTRAIT", 2.0, 2.0, 2.5, 2.5,
     '<div style="text-align:center;font-size:18px;font-weight:bold">检验报告</div><div style="text-align:center;font-size:12px">CNAS认可</div>',
     '<div style="text-align:center;font-size:10px">第 {page} 页 / 共 {total} 页</div>',
     "受控文件", 1, date.today(), None, "PUBLISHED"),
    ("TPL-INT-001", "内部检验报告模板", "INTERNAL", "OTHER", "适用于内部质量控制检验报告", "V1.0", "A4", "PORTRAIT", 2.0, 2.0, 2.5, 2.5,
     '<div style="text-align:center;font-size:18px;font-weight:bold">内部检验报告</div>',
     '<div style="text-align:center;font-size:10px">第 {page} 页 / 共 {total} 页</div>',
     None, 0, date.today(), None, "PUBLISHED"),
]

for t in templates:
    try:
        cur.execute("""INSERT INTO lims_report_template 
            (template_code, template_name, template_type, template_category, description, version_number,
             paper_size, orientation, margin_top, margin_bottom, margin_left, margin_right,
             header_content, footer_content, watermark, is_default, effective_date, expiry_date, status, create_by, create_time)
            VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,'admin',now())""", t)
        conn.commit()
        print(f"  [OK] 创建模板: {t[0]} - {t[1]}")
    except Exception as e:
        print(f"  [FAIL] 模板 {t[0]} 创建失败: {e}")

# ==========================================
# 2. 准备仪器采集配置数据
# ==========================================
print("\n[2] 准备仪器采集配置数据...")

acquisitions = [
    (57, "INST001", "LAN", "ASCII",
     '{"host":"192.168.1.100","port":8000,"timeout":30}',
     '{"delimiter":"\\n","encoding":"UTF-8","skip_lines":0}',
     '{"result_name":"testItemName","result_value":"reportedValue","unit":"unit"}',
     1, 60, "ACTIVE", "Agilent 1260 HPLC自动采集配置"),
    (58, "INST002", "LAN", "JSON",
     '{"host":"192.168.1.101","port":8001,"timeout":30}',
     '{"format":"json","data_path":"$.results"}',
     '{"result_name":"itemName","result_value":"value","unit":"unit"}',
     1, 60, "ACTIVE", "Agilent 7890B GC自动采集配置"),
    (59, "INST003", "RS232", "ASCII",
     '{"baud_rate":9600,"data_bits":8,"stop_bits":1,"parity":"NONE","port":"COM3"}',
     '{"delimiter":"\\r\\n","encoding":"UTF-8"}',
     '{"result_name":"name","result_value":"value","unit":"unit"}',
     0, 0, "ACTIVE", "Shimadzu UV-2600i串口采集配置"),
]

for a in acquisitions:
    try:
        cur.execute("""INSERT INTO lims_instrument_data_acquisition
            (instrument_id, instrument_code, interface_type, data_protocol, connection_config, parsing_rules, field_mappings,
             auto_import, poll_interval, status, remark, create_by, create_time)
            VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,'admin',now())""", a)
        conn.commit()
        print(f"  [OK] 创建采集配置: {a[1]} - {a[2]}")
    except Exception as e:
        print(f"  [FAIL] 采集配置 {a[1]} 创建失败: {e}")

# ==========================================
# 3. 更新检验订单状态为APPROVED（以便创建报告）
# ==========================================
print("\n[3] 更新检验订单状态...")

try:
    cur.execute("UPDATE lims_test_order SET status='APPROVED' WHERE test_order_id=13")
    conn.commit()
    print(f"  [OK] 订单13(阿司匹林原料全检) --> APPROVED")
except Exception as e:
    print(f"  [FAIL] 更新失败: {e}")

try:
    cur.execute("UPDATE lims_test_order SET status='APPROVED' WHERE test_order_id=14")
    conn.commit()
    print(f"  [OK] 订单14(阿司匹林片成品全检) --> APPROVED")
except Exception as e:
    print(f"  [FAIL] 更新失败: {e}")

# ==========================================
# 4. 为检验订单13创建检验结果数据（如果不存在）
# ==========================================
print("\n[4] 检查检验结果数据...")

cur.execute("SELECT COUNT(*) FROM lims_test_result WHERE test_order_id=13")
count = cur.fetchone()[0]
if count == 0:
    test_results = [
        (13, 57, "含量测定", "99.5", 98.0, 102.0, "%", "PASS", 0, "HPLC法测定"),
        (13, 57, "有关物质", "0.12", 0.0, 0.5, "%", "PASS", 0, "HPLC法测定"),
        (13, 58, "残留溶剂-甲醇", "523", 0.0, 3000.0, "ppm", "PASS", 0, "GC法测定"),
        (13, 58, "残留溶剂-乙醇", "890", 0.0, 5000.0, "ppm", "PASS", 0, "GC法测定"),
        (13, 59, "干燥失重", "0.3", 0.0, 0.5, "%", "PASS", 0, "UV法测定"),
    ]
    for r in test_results:
        try:
            cur.execute("""INSERT INTO lims_test_result
                (test_order_id, instrument_id, test_item_name, reported_value, lower_limit, upper_limit, unit, result_conclusion, is_oos, remark, create_by, create_time)
                VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,'admin',now())""", r)
            conn.commit()
            print(f"  [OK] 创建检验结果: {r[2]} = {r[3]}")
        except Exception as e:
            print(f"  [FAIL] 检验结果创建失败: {e}")
else:
    print(f"  订单13已有{count}条检验结果")

# ==========================================
# 5. 验证数据完整性
# ==========================================
print("\n[5] 验证数据完整性...")

cur.execute("SELECT COUNT(*) FROM lims_report_template WHERE status='PUBLISHED'")
print(f"  已发布模板: {cur.fetchone()[0]}")

cur.execute("SELECT COUNT(*) FROM lims_instrument_data_acquisition WHERE status='ACTIVE'")
print(f"  活跃采集配置: {cur.fetchone()[0]}")

cur.execute("SELECT COUNT(*) FROM lims_data_validation_rule WHERE is_enabled=1")
print(f"  启用校验规则: {cur.fetchone()[0]}")

cur.execute("SELECT COUNT(*) FROM lims_test_order WHERE status='APPROVED'")
print(f"  已批准检验订单: {cur.fetchone()[0]}")

cur.execute("SELECT COUNT(*) FROM lims_test_result WHERE test_order_id=13")
print(f"  订单13检验结果: {cur.fetchone()[0]}")

cur.close()
conn.close()

print("\n" + "=" * 60)
print("测试数据准备完成！")
print("=" * 60)
