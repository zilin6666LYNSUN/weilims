import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

cur.execute("SELECT user_id, user_name, password, status FROM sys_user WHERE user_name='admin'")
row = cur.fetchone()
if row:
    print(f"admin user: id={row[0]}, name={row[1]}, password_hash={row[2][:50]}..., status={row[3]}")
else:
    print("admin user not found")

cur.close()
conn.close()
