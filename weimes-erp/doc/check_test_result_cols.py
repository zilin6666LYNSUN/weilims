import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

cur.execute("DESCRIBE lims_test_result")
for row in cur.fetchall():
    print(row[0], row[1])

cur.close()
conn.close()
