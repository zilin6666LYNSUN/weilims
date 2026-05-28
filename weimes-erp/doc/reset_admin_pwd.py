import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# BCrypt hash for "admin123" - standard Ruooyi default
# $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
cur.execute("UPDATE sys_user SET password='$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2' WHERE user_name='admin'")
conn.commit()
print("Password reset to admin123")

# Also check if there's a login lock
cur.execute("SELECT user_id, user_name, status, login_date FROM sys_user WHERE user_name='admin'")
row = cur.fetchone()
print(f"Admin status: id={row[0]}, name={row[1]}, status={row[2]}, last_login={row[3]}")

cur.close()
conn.close()
