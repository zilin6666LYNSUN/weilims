import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

tables = ['lims_sample', 'lims_test_order', 'lims_instrument', 'lims_instrument_calibration',
          'lims_instrument_maintenance', 'lims_reagent', 'lims_reagent_stock',
          'lims_oos_investigation', 'lims_oos_phase_record',
          'lims_deviation', 'lims_capa', 'lims_change_control',
          'lims_stability_program', 'lims_stability_sample', 'lims_stability_result']
for t in tables:
    try:
        cursor.execute(f"DESCRIBE {t}")
        print(f"\n=== {t} ===")
        for row in cursor.fetchall():
            print(f"  {row[0]:30s} {row[1]:20s} {row[2]:5s} {row[3]:5s} {row[4] if row[4] else ''}")
    except Exception as e:
        print(f"\n=== {t} === ERROR: {e}")

cursor.close()
conn.close()
