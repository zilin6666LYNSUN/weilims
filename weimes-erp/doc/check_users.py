import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# Check existing users
cur.execute("SELECT user_id, user_name, nick_name, status FROM sys_user ORDER BY user_id LIMIT 20")
print("Existing users:")
for row in cur.fetchall():
    print(f"  id={row[0]}, username={row[1]}, nick={row[2]}, status={row[3]}")

cur.close()
conn.close()
