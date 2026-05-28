import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

cur.execute("DESCRIBE lims_report_template")
print('=== lims_report_template ===')
for row in cur.fetchall():
    print(row)

print()
cur.execute("SELECT * FROM lims_report_template LIMIT 3")
print('=== lims_report_template data ===')
for row in cur.fetchall():
    print(row)

print()
cur.execute("SELECT menu_id, menu_name, parent_id, path, component FROM sys_menu WHERE menu_id BETWEEN 3100 AND 3150")
print('=== menus ===')
for row in cur.fetchall():
    print(row)

cur.close()
conn.close()
