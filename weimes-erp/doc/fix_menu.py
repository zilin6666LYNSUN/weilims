"""
修复LIMS菜单数据
1. 删除旧的打印管理菜单(menu_id 3000-3002)
2. 重新插入LIMS菜单
3. 重新授权角色菜单
"""
import pymysql
import re

DB_CONFIG = {
    'host': 'sh-cdb-l0c3gbtu.sql.tencentcdb.com',
    'port': 22700,
    'user': 'root',
    'password': 'Stl123456789',
    'database': 'weilims',
    'charset': 'utf8mb4',
}

def main():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    # 1. 删除旧的冲突菜单 (3000-3002 是基础脚本中的打印管理)
    print("1. 删除旧的冲突菜单...")
    cursor.execute("DELETE FROM sys_role_menu WHERE menu_id IN (3000, 3001, 3002)")
    print(f"  删除 sys_role_menu: {cursor.rowcount} 条")
    cursor.execute("DELETE FROM sys_menu WHERE menu_id IN (3000, 3001, 3002)")
    print(f"  删除 sys_menu: {cursor.rowcount} 条")
    conn.commit()

    # 2. 删除所有LIMS菜单(menu_id >= 3000)，重新插入
    print("\n2. 清理所有LIMS菜单...")
    cursor.execute("DELETE FROM sys_role_menu WHERE menu_id >= 3000")
    print(f"  删除 sys_role_menu: {cursor.rowcount} 条")
    cursor.execute("DELETE FROM sys_menu WHERE menu_id >= 3000")
    print(f"  删除 sys_menu: {cursor.rowcount} 条")
    conn.commit()

    # 3. 重新插入LIMS菜单
    print("\n3. 重新插入LIMS菜单...")
    with open(r'c:\Work\mes\mes\weimes-erp\doc\lims-init.sql', 'r', encoding='utf-8') as f:
        lims_sql = f.read()

    # 提取 sys_menu INSERT 语句
    inserts = re.findall(r"INSERT INTO `sys_menu`[^;]+;", lims_sql, re.IGNORECASE | re.DOTALL)
    s, f = 0, 0
    for stmt in inserts:
        try:
            cursor.execute(stmt)
            s += 1
        except Exception as e:
            f += 1
            print(f"  INSERT 错误: {str(e)[:150]}")
    print(f"  LIMS菜单插入: 成功={s}, 失败={f}")
    conn.commit()

    # 4. 验证
    cursor.execute("SELECT menu_id, menu_name, parent_id, path, component, menu_type FROM sys_menu WHERE menu_id = 3000")
    r = cursor.fetchone()
    print(f"\n4. 验证顶级菜单: id={r[0]}, name={r[1]}, parent={r[2]}, path={r[3]}, component={r[4]}, type={r[5]}")

    # 5. 角色授权
    print("\n5. 角色授权...")
    cursor.execute('SELECT role_id, role_name FROM sys_role WHERE deleted = 0')
    roles = cursor.fetchall()
    for role in roles:
        role_id = role[0]
        if role_id == 1:
            print(f"  role_id=1 (超级管理员) 自动拥有所有权限")
            continue
        cursor.execute("""
            INSERT INTO sys_role_menu (role_id, menu_id, deleted, deleteAt, version)
            SELECT %s, menu_id, 0, '1000-01-01 00:00:00', 1
            FROM sys_menu WHERE menu_id >= 3000 AND menu_id <= 3699
        """, (role_id,))
        print(f"  role_id={role_id} ({role[1]}) 授权 {cursor.rowcount} 条LIMS菜单")
    conn.commit()

    # 6. 最终验证
    cursor.execute("SELECT COUNT(*) FROM sys_menu WHERE menu_id >= 3000")
    print(f"\n6. 最终: LIMS菜单总数={cursor.fetchone()[0]}")

    cursor.execute("SELECT menu_id, menu_name, path FROM sys_menu WHERE menu_id >= 3000 AND menu_type = 'M' ORDER BY menu_id")
    print("  目录菜单:")
    for r in cursor.fetchall():
        print(f"    id={r[0]}, name={r[1]}, path={r[2]}")

    cursor.execute("SELECT menu_id, menu_name, path, component FROM sys_menu WHERE menu_id >= 3000 AND menu_type = 'C' ORDER BY menu_id")
    print("  页面菜单:")
    for r in cursor.fetchall():
        print(f"    id={r[0]}, name={r[1]}, path={r[2]}, component={r[3]}")

    cursor.close()
    conn.close()
    print("\n菜单修复完成!")

if __name__ == '__main__':
    main()
