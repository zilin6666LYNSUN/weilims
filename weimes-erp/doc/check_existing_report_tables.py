import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# Check existing report template table structure
cur.execute("DESCRIBE lims_report_template")
print('=== lims_report_template ===')
for row in cur.fetchall():
    print(row)

print()

cur.execute("DESCRIBE lims_report_template_field")
print('=== lims_report_template_field ===')
for row in cur.fetchall():
    print(row)

print()

cur.execute("DESCRIBE lims_report_template_section")
print('=== lims_report_template_section ===')
for row in cur.fetchall():
    print(row)

print()

cur.execute("DESCRIBE lims_report")
print('=== lims_report ===')
for row in cur.fetchall():
    print(row)

print()

cur.execute("DESCRIBE lims_report_template_style")
print('=== lims_report_template_style ===')
for row in cur.fetchall():
    print(row)

print()

cur.execute("DESCRIBE lims_report_template_product")
print('=== lims_report_template_product ===')
for row in cur.fetchall():
    print(row)

cur.close()
conn.close()
