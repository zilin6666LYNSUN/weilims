import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# Tables that need sync: get their full CREATE TABLE statements
tables_to_check = [
    'lims_report_signature',
    'lims_data_validation_rule',
    'lims_data_validation_log',
    'lims_instrument_data_acquisition',
]

for t in tables_to_check:
    cur.execute(f"SHOW CREATE TABLE `{t}`")
    row = cur.fetchone()
    if row:
        print(f"\n{'='*80}")
        print(f"-- {t}")
        print(f"{'='*80}")
        print(row[1] + ";")

# Also check lims_report for current indexes
print(f"\n{'='*80}")
print("-- lims_report INDEXES")
print(f"{'='*80}")
cur.execute("SHOW INDEX FROM lims_report")
for row in cur.fetchall():
    print(f"  {row[2]} -> {row[4]} (seq={row[3]}, unique={row[1]})")

cur.close()
conn.close()
