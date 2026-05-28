"""
修复数据库初始化 - 替换排序规则后创建系统表
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
    print("修复数据库 - 替换排序规则后创建系统表")
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    # 读取SQL文件并替换排序规则
    with open(r'c:\Work\mes\mes\weimes-erp\doc\20250415-init.sql', 'r', encoding='utf-8') as f:
        full_sql = f.read()

    # 替换排序规则
    full_sql = full_sql.replace('utf8mb4_0900_ai_ci', 'utf8mb4_general_ci')

    # 提取所有 CREATE TABLE 语句
    create_pattern = re.finditer(
        r"CREATE TABLE\s+`(\w+)`\s*\([^;]*?ENGINE=\w+[^;]*;",
        full_sql, re.IGNORECASE | re.DOTALL
    )

    created = 0
    skipped = 0
    failed = 0

    for match in create_pattern:
        table_name = match.group(1)
        stmt = match.group(0)

        # 检查表是否已存在
        cursor.execute(f"SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=%s AND table_name=%s", ('weilims', table_name))
        if cursor.fetchone()[0] > 0:
            skipped += 1
            continue

        try:
            cursor.execute(stmt)
            created += 1
            print(f"  创建 {table_name} 成功")
        except Exception as e:
            failed += 1
            print(f"  创建 {table_name} 失败: {str(e)[:200]}")

    conn.commit()
    print(f"\n建表结果: 创建={created}, 跳过={skipped}, 失败={failed}")

    # 插入基础数据
    print("\n正在插入系统基础数据...")

    # 需要插入数据的表（按依赖顺序）
    data_tables = [
        'sys_config', 'sys_config_jasypt', 'sys_dept', 'sys_dict_type', 'sys_dict_data',
        'sys_user', 'sys_role', 'sys_post', 'sys_user_role', 'sys_role_menu',
        'sys_role_dept', 'sys_user_post', 'sys_menu', 'sys_job', 'sys_notice',
        'sys_attachment', 'sys_auto_code_part', 'sys_auto_code_result', 'sys_auto_code_rule',
        'sys_message', 'sys_oper_log', 'sys_logininfor', 'sys_job_log'
    ]

    for table in data_tables:
        inserts = re.findall(rf"INSERT INTO `{table}`[^;]+;", full_sql, re.IGNORECASE | re.DOTALL)
        if not inserts:
            continue

        s, f = 0, 0
        for stmt in inserts:
            try:
                cursor.execute(stmt)
                s += 1
            except Exception as e:
                f += 1
                if f <= 2:  # 只打印前2个错误
                    print(f"  {table} INSERT 错误: {str(e)[:150]}")
        if s > 0 or f > 0:
            print(f"  {table}: 成功={s}, 失败={f}")

    conn.commit()

    # 插入 LIMS 菜单
    print("\n正在插入LIMS菜单数据...")
    with open(r'c:\Work\mes\mes\weimes-erp\doc\lims-init.sql', 'r', encoding='utf-8') as f:
        lims_sql = f.read()

    lims_sql = lims_sql.replace('utf8mb4_0900_ai_ci', 'utf8mb4_general_ci')

    # LIMS菜单 INSERT
    inserts = re.findall(r"INSERT INTO `sys_menu`[^;]+;", lims_sql, re.IGNORECASE | re.DOTALL)
    s, f = 0, 0
    for stmt in inserts:
        try:
            cursor.execute(stmt)
            s += 1
        except Exception as e:
            f += 1
            if f <= 3:
                print(f"  LIMS菜单 INSERT 错误: {str(e)[:150]}")
    print(f"  LIMS菜单: 成功={s}, 失败={f}")
    conn.commit()

    # 角色授权
    print("\n正在授权角色菜单...")
    try:
        cursor.execute('SELECT role_id, role_name FROM sys_role WHERE deleted = 0')
        roles = cursor.fetchall()
        for role in roles:
            role_id = role[0]
            if role_id == 1:
                print(f"  role_id=1 (超级管理员) 自动拥有所有权限")
                continue
            cursor.execute('SELECT COUNT(*) FROM sys_role_menu WHERE role_id = %s AND menu_id >= 3000', (role_id,))
            if cursor.fetchone()[0] > 0:
                print(f"  role_id={role_id} 已有LIMS菜单授权")
                continue
            cursor.execute("""
                INSERT INTO sys_role_menu (role_id, menu_id, deleted, deleteAt, version)
                SELECT %s, menu_id, 0, '1000-01-01 00:00:00', 1
                FROM sys_menu WHERE menu_id >= 3000 AND menu_id <= 3699
            """, (role_id,))
            print(f"  role_id={role_id} 授权 {cursor.rowcount} 条LIMS菜单")
        conn.commit()
    except Exception as e:
        print(f"  角色授权失败: {e}")

    # 最终检查
    print(f"\n{'='*60}")
    print("最终检查:")
    cursor.execute('SHOW TABLES')
    tables = cursor.fetchall()
    sys_t = len([t for t in tables if t[0].startswith('sys_')])
    lims_t = len([t for t in tables if t[0].startswith('lims_')])
    print(f"  总表数: {len(tables)}, 系统表: {sys_t}, LIMS表: {lims_t}")

    cursor.execute('SELECT COUNT(*) FROM sys_menu')
    print(f"  sys_menu 总记录: {cursor.fetchone()[0]}")
    cursor.execute('SELECT COUNT(*) FROM sys_menu WHERE menu_id >= 3000')
    print(f"  LIMS菜单记录: {cursor.fetchone()[0]}")
    cursor.execute('SELECT COUNT(*) FROM sys_user')
    print(f"  sys_user 记录: {cursor.fetchone()[0]}")
    cursor.execute('SELECT COUNT(*) FROM sys_role')
    print(f"  sys_role 记录: {cursor.fetchone()[0]}")

    cursor.close()
    conn.close()
    print("\n修复完成!")

if __name__ == '__main__':
    main()
