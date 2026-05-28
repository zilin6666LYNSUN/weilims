import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

for t in ['lims_instrument_maintenance', 'lims_instrument_type', 'lims_instrument_reservation',
          'lims_culture_collection', 'lims_reagent_lot', 'lims_reagent_stock_log']:
    cursor.execute(f"DESCRIBE {t}")
    print(f"\n=== {t} ===")
    for row in cursor.fetchall():
        print(f"  {row[0]:30s} {row[1]:20s}")

cursor.close()
conn.close()
