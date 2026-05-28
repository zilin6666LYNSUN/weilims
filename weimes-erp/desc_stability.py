import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

for t in ['lims_stability_protocol', 'lims_stability_test']:
    cursor.execute(f"DESCRIBE {t}")
    print(f"\n=== {t} ===")
    for row in cursor.fetchall():
        print(f"  {row[0]:30s} {row[1]:20s} {row[2]:5s} {row[3]:5s} {row[4] if row[4] else ''}")

cursor.close()
conn.close()
