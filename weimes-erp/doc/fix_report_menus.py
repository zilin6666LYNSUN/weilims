import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# 1. 先删除之前错误插入的菜单（parent_id在3101-3104下的报告相关按钮）
cur.execute("DELETE FROM sys_role_menu WHERE menu_id IN (3112,3113,3114,3115,3122,3123,3124,3125,3126,3127,3128,3132,3133,3134,3135,3142,3143,3144)")
conn.commit()
print("OK: 删除旧的role_menu关联")

cur.execute("DELETE FROM sys_menu WHERE menu_id IN (3112,3113,3114,3115,3122,3123,3124,3125,3126,3127,3128,3132,3133,3134,3135,3142,3143,3144)")
conn.commit()
print("OK: 删除旧的菜单")

# 2. 创建报告管理目录 (parent_id=3000, order_num=7)
cur.execute("""INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (3700, '报告管理', 3000, 7, 'report', NULL, 1, 0, 'M', '0', '0', '', 'documentation', 'admin', now())""")
conn.commit()
print("OK: 报告管理目录 3700")

# 3. 报告模板菜单
cur.execute("""INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (3701, '报告模板', 3700, 1, 'template', 'lims/report/template', 1, 0, 'C', '0', '0', 'lims:report:template:list', 'form', 'admin', now())""")
conn.commit()
print("OK: 报告模板菜单 3701")

# 4. 检验报告菜单
cur.execute("""INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (3702, '检验报告', 3700, 2, 'testReport', 'lims/report/testReport', 1, 0, 'C', '0', '0', 'lims:report:test:list', 'documentation', 'admin', now())""")
conn.commit()
print("OK: 检验报告菜单 3702")

# 5. 数据校验菜单
cur.execute("""INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (3703, '数据校验', 3700, 3, 'validation', 'lims/report/validation', 1, 0, 'C', '0', '0', 'lims:report:validation:list', 'checkbox', 'admin', now())""")
conn.commit()
print("OK: 数据校验菜单 3703")

# 6. 仪器采集配置菜单 (放在仪器管理下)
cur.execute("""INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (3704, '采集配置', 3300, 5, 'acquisition', 'lims/instrument/acquisition', 1, 0, 'C', '0', '0', 'lims:instrument:acquisition:list', 'upload', 'admin', now())""")
conn.commit()
print("OK: 采集配置菜单 3704")

# 7. 报告模板按钮权限
template_buttons = [
    (3711, '报告模板查询', 3701, 'lims:report:template:query'),
    (3712, '报告模板新增', 3701, 'lims:report:template:add'),
    (3713, '报告模板修改', 3701, 'lims:report:template:edit'),
    (3714, '报告模板删除', 3701, 'lims:report:template:remove'),
    (3715, '报告模板导出', 3701, 'lims:report:template:export'),
]
for bid, bname, bparent, bperm in template_buttons:
    cur.execute(f"""INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
    VALUES ({bid}, '{bname}', {bparent}, 1, '', '', 1, 0, 'F', '0', '0', '{bperm}', '#', 'admin', now())""")
    conn.commit()
print("OK: 报告模板按钮权限")

# 8. 检验报告按钮权限
report_buttons = [
    (3721, '检验报告查询', 3702, 'lims:report:test:query'),
    (3722, '检验报告新增', 3702, 'lims:report:test:add'),
    (3723, '检验报告修改', 3702, 'lims:report:test:edit'),
    (3724, '检验报告删除', 3702, 'lims:report:test:remove'),
    (3725, '报告复核', 3702, 'lims:report:test:review'),
    (3726, '报告批准', 3702, 'lims:report:test:approve'),
    (3727, '报告放行', 3702, 'lims:report:test:release'),
    (3728, '报告生成', 3702, 'lims:report:test:generate'),
]
for bid, bname, bparent, bperm in report_buttons:
    cur.execute(f"""INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
    VALUES ({bid}, '{bname}', {bparent}, 1, '', '', 1, 0, 'F', '0', '0', '{bperm}', '#', 'admin', now())""")
    conn.commit()
print("OK: 检验报告按钮权限")

# 9. 数据校验按钮权限
validation_buttons = [
    (3731, '校验规则查询', 3703, 'lims:report:validation:query'),
    (3732, '校验规则新增', 3703, 'lims:report:validation:add'),
    (3733, '校验规则修改', 3703, 'lims:report:validation:edit'),
    (3734, '校验规则删除', 3703, 'lims:report:validation:remove'),
    (3735, '执行校验', 3703, 'lims:report:validation:execute'),
]
for bid, bname, bparent, bperm in validation_buttons:
    cur.execute(f"""INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
    VALUES ({bid}, '{bname}', {bparent}, 1, '', '', 1, 0, 'F', '0', '0', '{bperm}', '#', 'admin', now())""")
    conn.commit()
print("OK: 数据校验按钮权限")

# 10. 采集配置按钮权限
acquisition_buttons = [
    (3741, '采集配置查询', 3704, 'lims:instrument:acquisition:query'),
    (3742, '采集配置新增', 3704, 'lims:instrument:acquisition:add'),
    (3743, '采集配置修改', 3704, 'lims:instrument:acquisition:edit'),
    (3744, '采集配置删除', 3704, 'lims:instrument:acquisition:remove'),
]
for bid, bname, bparent, bperm in acquisition_buttons:
    cur.execute(f"""INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
    VALUES ({bid}, '{bname}', {bparent}, 1, '', '', 1, 0, 'F', '0', '0', '{bperm}', '#', 'admin', now())""")
    conn.commit()
print("OK: 采集配置按钮权限")

# 11. 为管理员角色授权所有新菜单
all_menu_ids = [3700, 3701, 3702, 3703, 3704,
    3711, 3712, 3713, 3714, 3715,
    3721, 3722, 3723, 3724, 3725, 3726, 3727, 3728,
    3731, 3732, 3733, 3734, 3735,
    3741, 3742, 3743, 3744]

for mid in all_menu_ids:
    cur.execute(f"INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, {mid})")
    conn.commit()
print("OK: 管理员角色授权完成")

cur.close()
conn.close()
print("\n菜单修复完成！")
