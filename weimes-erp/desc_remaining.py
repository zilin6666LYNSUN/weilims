import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

for t in ['lims_sample', 'lims_sample_status_log', 'lims_sample_test_item',
          'lims_retain_sample', 'lims_sample_dispose',
          'lims_instrument', 'lims_instrument_maintenance', 'lims_instrument_type',
          'lims_instrument_reservation',
          'lims_reagent', 'lims_reagent_stock_log', 'lims_reagent_lot',
          'lims_reference_standard', 'lims_culture_collection',
          'lims_electronic_signature', 'lims_audit_trail']:
    try:
        cursor.execute(f"DESCRIBE {t}")
        print(f"\n=== {t} ===")
        for row in cursor.fetchall():
            print(f"  {row[0]:30s} {row[1]:20s}")
    except Exception as e:
        print(f"\n=== {t} === ERROR: {e}")

cursor.close()
conn.close()
