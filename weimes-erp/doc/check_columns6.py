import pymysql
conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()
for t in ['lims_reagent', 'lims_test_order', 'lims_test_result']:
    try:
        cur.execute(f"SHOW COLUMNS FROM {t}")
        columns = [r[0] for r in cur.fetchall()]
        print(f"{t} ({len(columns)}列):")
        print(f"  {columns}")
    except Exception as e:
        print(f"{t}: ERROR - {e}")
conn.close()
