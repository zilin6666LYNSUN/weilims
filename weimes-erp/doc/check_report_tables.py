import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# Check existing tables
cur.execute("SHOW TABLES LIKE 'lims_report%'")
print('Report tables:', cur.fetchall())

cur.execute("SHOW TABLES LIKE 'lims_data_validation%'")
print('Validation tables:', cur.fetchall())

cur.execute("SHOW TABLES LIKE 'lims_instrument_data%'")
print('Acquisition tables:', cur.fetchall())

cur.execute("SHOW TABLES LIKE 'lims_test_report%'")
print('Test report tables:', cur.fetchall())

cur.close()
conn.close()
