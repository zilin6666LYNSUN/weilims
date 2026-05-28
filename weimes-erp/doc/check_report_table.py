import pymysql
conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()
cur.execute("DESCRIBE lims_report")
for row in cur.fetchall():
    print(f"  {row[0]:30s} {row[1]:20s} {row[2]:5s} {row[3]:5s}")
cur.close()
conn.close()
