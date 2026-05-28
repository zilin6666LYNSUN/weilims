import pymysql
conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()
cur.execute("SELECT * FROM lims_test_order ORDER BY test_order_id DESC LIMIT 1")
for row in cur.fetchall():
    print(f"  id={row[0]}, code={row[1]}, status={row[2]}")
cur.close()
conn.close()
