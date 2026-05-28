import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

tables = ['lims_instrument_calibration', 'lims_reagent', 'lims_oos_investigation', 'lims_deviation', 'lims_capa', 'lims_change_control']

for table in tables:
    cursor.execute(f'DESCRIBE {table}')
    columns = [r[0] for r in cursor.fetchall()]
    print(f'\n{table}:')
    print(', '.join(columns))

cursor.close()
conn.close()
