import pymysql
conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()
for t in ['lims_specification_line', 'lims_specification']:
    try:
        cur.execute(f"SHOW COLUMNS FROM {t}")
        columns = [(r[0], r[1]) for r in cur.fetchall()]
        print(f"{t}:")
        for c in columns:
            print(f"  {c[0]}: {c[1]}")
    except Exception as e:
        print(f"{t}: ERROR - {e}")
conn.close()
