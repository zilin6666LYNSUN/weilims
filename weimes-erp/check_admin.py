import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

cursor.execute("SELECT user_name, password, status FROM sys_user WHERE user_name = 'admin'")
row = cursor.fetchone()
if row:
    print(f"User: {row[0]}, Password: {row[1][:30]}..., Status: {row[2]}")
else:
    print("Admin user not found")

cursor.close()
conn.close()
