import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# Get admin's password hash
cur.execute("SELECT password FROM sys_user WHERE user_name='admin'")
admin_hash = cur.fetchone()[0]
print(f"Admin password hash: {admin_hash[:30]}...")

# Update test users with same password as admin (admin123)
for username in ['lims_prep', 'lims_rev', 'lims_appr', 'lims_rel']:
    cur.execute("UPDATE sys_user SET password=%s WHERE user_name=%s", (admin_hash, username))
    print(f"[OK] Updated password for {username}")

conn.commit()
cur.close()
conn.close()
print("\nAll test users now use password: admin123")
