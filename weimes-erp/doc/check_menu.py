import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# 查询menu_id=3000的顶级菜单
cur.execute('SELECT menu_id, menu_name, parent_id, path, component, menu_type, icon, order_num FROM sys_menu WHERE menu_id = 3000')
r = cur.fetchone()
print(f'顶级菜单: id={r[0]}, name={r[1]}, parent={r[2]}, path={r[3]}, component={r[4]}, type={r[5]}, icon={r[6]}, order={r[7]}')

# 查看顶级目录菜单 (parent_id=0, type=M)
cur.execute("SELECT menu_id, menu_name, path, icon, order_num FROM sys_menu WHERE parent_id = 0 AND menu_type = 'M' ORDER BY order_num")
print('\n=== 所有顶级目录 ===')
for r in cur.fetchall():
    print(f'  id={r[0]}, name={r[1]}, path={r[2]}, icon={r[3]}, order={r[4]}')

# 查看包含打印的菜单
cur.execute("SELECT menu_id, menu_name, parent_id, path, component FROM sys_menu WHERE menu_name LIKE '%打印%' ORDER BY menu_id")
print('\n=== 包含打印的菜单 ===')
for r in cur.fetchall():
    print(f'  id={r[0]}, name={r[1]}, parent={r[2]}, path={r[3]}, component={r[4]}')

# 查看角色菜单关联
cur.execute("SELECT role_id, COUNT(*) as cnt FROM sys_role_menu WHERE menu_id >= 3000 GROUP BY role_id")
print('\n=== 角色LIMS菜单授权 ===')
for r in cur.fetchall():
    print(f'  role_id={r[0]}, count={r[1]}')

# 查看当前登录用户的角色
cur.execute("SELECT user_id, user_name, nick_name FROM sys_user WHERE deleted = 0 LIMIT 5")
print('\n=== 系统用户 ===')
for r in cur.fetchall():
    print(f'  id={r[0]}, username={r[1]}, nick={r[2]}')

cur.execute("SELECT u.user_id, u.user_name, r.role_id, r.role_name FROM sys_user u JOIN sys_user_role ur ON u.user_id = ur.user_id JOIN sys_role r ON ur.role_id = r.role_id WHERE u.deleted = 0")
print('\n=== 用户-角色关系 ===')
for r in cur.fetchall():
    print(f'  user_id={r[0]}, username={r[1]}, role_id={r[2]}, role_name={r[3]}')

conn.close()
