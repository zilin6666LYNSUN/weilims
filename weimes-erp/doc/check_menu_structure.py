import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# 查看LIMS顶级菜单结构
cur.execute("SELECT menu_id, menu_name, parent_id, order_num, path, component, menu_type FROM sys_menu WHERE parent_id = 3000 ORDER BY order_num")
print('=== LIMS子菜单 (parent_id=3000) ===')
for row in cur.fetchall():
    print(row)

print()

# 查看最大menu_id
cur.execute("SELECT MAX(menu_id) FROM sys_menu")
print('Max menu_id:', cur.fetchone()[0])

print()

# 查看已有的报告相关菜单
cur.execute("SELECT menu_id, menu_name, parent_id, path, component, menu_type FROM sys_menu WHERE menu_name LIKE '%报告%' OR menu_name LIKE '%校验%' OR menu_name LIKE '%采集%'")
print('=== 报告相关菜单 ===')
for row in cur.fetchall():
    print(row)

cur.close()
conn.close()
