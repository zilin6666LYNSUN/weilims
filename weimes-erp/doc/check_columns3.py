import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

for table in ['lims_instrument', 'lims_sample', 'lims_test_template', 'lims_stability_protocol', 'lims_stability_test', 'lims_audit_trail']:
    try:
        cur.execute(f"SHOW COLUMNS FROM {table}")
        columns = [r[0] for r in cur.fetchall()]
        print(f"{table} ({len(columns)}列):")
        print(f"  {columns}")
    except Exception as e:
        print(f"{table}: ERROR - {e}")

conn.close()
