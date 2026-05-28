import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

# Check micro test record table
cursor.execute("SHOW TABLES LIKE 'lims_micro%'")
for row in cursor.fetchall():
    print(f"Table: {row[0]}")
    cursor.execute(f"DESCRIBE {row[0]}")
    for r in cursor.fetchall():
        print(f"  {r[0]:30s} {r[1]:20s}")

# Check stability tables
cursor.execute("SHOW TABLES LIKE 'lims_stab%'")
for row in cursor.fetchall():
    print(f"Table: {row[0]}")

# Check all lims tables
cursor.execute("SHOW TABLES LIKE 'lims_%'")
print("\nAll LIMS tables:")
for row in cursor.fetchall():
    print(f"  {row[0]}")

cursor.close()
conn.close()
