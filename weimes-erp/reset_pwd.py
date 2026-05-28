import pymysql
from bcrypt import hashpw, gensalt

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

# BCrypt hash for 'admin123'
new_hash = hashpw(b'admin123', gensalt()).decode('utf-8')
print(f"New hash: {new_hash}")

cursor.execute("UPDATE sys_user SET password = %s WHERE user_name = 'admin'", (new_hash,))
conn.commit()
print(f"Password reset. Rows affected: {cursor.rowcount}")

cursor.close()
conn.close()
