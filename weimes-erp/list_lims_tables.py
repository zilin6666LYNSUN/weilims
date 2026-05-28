import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

cursor.execute("SHOW TABLES LIKE 'lims_%'")
tables = [row[0] for row in cursor.fetchall()]
print(f"Total LIMS tables: {len(tables)}")
for t in sorted(tables):
    print(t)

cursor.close()
conn.close()
