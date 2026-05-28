import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

cursor.execute("DESCRIBE lims_specification_line")
print("=== lims_specification_line ===")
for row in cursor.fetchall():
    print(f"  {row[0]:30s} {row[1]:20s} {row[2]:5s} {row[3]:5s} {row[4] if row[4] else ''}")

cursor.close()
conn.close()
