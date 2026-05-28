import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

for table in ['lims_test_method', 'lims_test_template']:
    try:
        cur.execute(f"SHOW COLUMNS FROM {table}")
        columns = [r[0] for r in cur.fetchall()]
        print(f"{table} ({len(columns)}列):")
        print(f"  {columns}")
    except Exception as e:
        print(f"{table}: ERROR - {e}")

# 检查 lims_stability 相关表
cur.execute("SHOW TABLES LIKE 'lims_stab%'")
print(f"\nlims_stab* 表: {cur.fetchall()}")

# 检查 lims_audit_trail 表
cur.execute("SHOW TABLES LIKE 'lims_audit%'")
print(f"lims_audit* 表: {cur.fetchall()}")

conn.close()
