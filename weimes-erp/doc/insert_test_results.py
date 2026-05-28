import pymysql
from datetime import date

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

print("[4] Insert test results for order 13...")

cur.execute("SELECT COUNT(*) FROM lims_test_result WHERE test_order_id=13")
count = cur.fetchone()[0]
if count == 0:
    test_results = [
        (13, 57, "含量测定", "99.5", 98.0, 102.0, "98.0~102.0%", "PASS", 0, "HPLC法测定"),
        (13, 57, "有关物质", "0.12", 0.0, 0.5, "<=0.5%", "PASS", 0, "HPLC法测定"),
        (13, 58, "残留溶剂-甲醇", "523", 0.0, 3000.0, "<=3000ppm", "PASS", 0, "GC法测定"),
        (13, 58, "残留溶剂-乙醇", "890", 0.0, 5000.0, "<=5000ppm", "PASS", 0, "GC法测定"),
        (13, 59, "干燥失重", "0.3", 0.0, 0.5, "<=0.5%", "PASS", 0, "UV法测定"),
    ]
    for r in test_results:
        try:
            cur.execute("""INSERT INTO lims_test_result
                (test_order_id, instrument_id, test_item_name, reported_value, lower_limit, upper_limit, specification_val, result_conclusion, is_oos, remark, create_by, create_time)
                VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,'admin',now())""", r)
            conn.commit()
            print(f"  [OK] {r[2]} = {r[3]}")
        except Exception as e:
            print(f"  [FAIL] {r[2]}: {e}")
else:
    print(f"  Already {count} results for order 13")

cur.execute("SELECT COUNT(*) FROM lims_test_result WHERE test_order_id=13")
print(f"  Total results for order 13: {cur.fetchone()[0]}")

cur.close()
conn.close()
