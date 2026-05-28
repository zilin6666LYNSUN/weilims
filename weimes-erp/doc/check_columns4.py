import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

for table in ['lims_test_template_item', 'lims_micro_test_record', 'lims_specification_line']:
    try:
        cur.execute(f"SHOW COLUMNS FROM {table}")
        columns = [r[0] for r in cur.fetchall()]
        print(f"{table} ({len(columns)}列):")
        print(f"  {columns}")
    except Exception as e:
        print(f"{table}: ERROR - {e}")

conn.close()
