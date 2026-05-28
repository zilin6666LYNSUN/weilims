"""
修复数据库初始化 - 只执行缺失的 sys_ 系统表和菜单数据
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

def execute_statements(cursor, statements, desc):
    """执行SQL语句列表"""
    success = 0
    failed = 0
    for i, stmt in enumerate(statements):
        stmt = stmt.strip()
        if not stmt or stmt == ';':
            continue
        try:
            cursor.execute(stmt)
            success += 1
        except Exception as e:
            err_msg = str(e)[:300]
            if 'already exists' in err_msg.lower():
                pass  # 表已存在，跳过
            elif 'duplicate' in err_msg.lower():
                pass  # 数据已存在，跳过
            else:
                # 打印前100字符用于定位
                preview = stmt[:100].replace('\n', ' ')
                print(f"  [错误] {preview}... => {err_msg[:150]}")
                failed += 1
    print(f"  {desc}: 成功={success}, 失败={failed}")
    return success, failed

def split_sql_file(filepath):
    """按分号分割SQL文件，处理多行语句"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # 简单按 ;\n 分割
    parts = content.split(';\n')
    statements = []
    for p in parts:
        p = p.strip()
        # 过滤纯注释和空行
        lines = [l for l in p.split('\n') if l.strip() and not l.strip().startswith('--')]
        if lines:
            stmt = '\n'.join(lines)
            if stmt.strip():
                statements.append(stmt)
    return statements

def main():
    print("修复数据库初始化 - 补充 sys_ 系统表和菜单数据")
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    # 检查缺失的表
    cursor.execute('SHOW TABLES')
    existing = set(t[0] for t in cursor.fetchall())
    print(f"当前表数量: {len(existing)}")

    # 需要的系统表
    required_sys_tables = [
        'sys_config', 'sys_config_jasypt', 'sys_dept', 'sys_dict_data', 'sys_dict_type',
        'sys_job', 'sys_job_log', 'sys_logininfor', 'sys_menu', 'sys_notice',
        'sys_oper_log', 'sys_post', 'sys_role', 'sys_role_dept', 'sys_role_menu',
        'sys_user', 'sys_user_post', 'sys_user_role', 'sys_attachment',
        'sys_auto_code_part', 'sys_auto_code_result', 'sys_auto_code_rule', 'sys_message'
    ]

    missing = [t for t in required_sys_tables if t not in existing]
    print(f"缺失的系统表: {len(missing)} 张 - {missing}")

    if missing:
        # 读取并执行 20250415-init.sql 中缺失的表
        print("\n正在执行 20250415-init.sql 补充系统表...")
        with open(r'c:\Work\mes\mes\weimes-erp\doc\20250415-init.sql', 'r', encoding='utf-8') as f:
            full_sql = f.read()

        # 提取 CREATE TABLE 语句
        for table_name in missing:
            pattern = rf"CREATE TABLE\s+`?{table_name}`?\s*\("
            match = re.search(pattern, full_sql, re.IGNORECASE)
            if match:
                # 找到建表语句的结束位置（找到 ENGINE=...;）
                start = match.start()
                # 从start位置找 ENGINE= 后面的 ;
                end_search = full_sql[start:]
                engine_match = re.search(r"ENGINE=\w+[^;]*;", end_search)
                if engine_match:
                    stmt = full_sql[start:start + engine_match.end()]
                    try:
                        cursor.execute(stmt)
                        print(f"  创建表 {table_name} 成功")
                    except Exception as e:
                        if 'already exists' in str(e).lower():
                            print(f"  表 {table_name} 已存在，跳过")
                        else:
                            print(f"  创建表 {table_name} 失败: {str(e)[:200]}")
                else:
                    print(f"  未找到 {table_name} 建表语句的结束位置")
            else:
                print(f"  未找到 {table_name} 的建表语句")

        conn.commit()

        # 执行 INSERT 语句（系统菜单、角色、用户等基础数据）
        print("\n正在插入系统基础数据...")

        # 提取 sys_menu 的 INSERT 语句
        insert_pattern = re.findall(r"INSERT INTO `sys_menu`[^;]+;", full_sql, re.IGNORECASE)
        s, f = 0, 0
        for stmt in insert_pattern:
            try:
                cursor.execute(stmt)
                s += 1
            except Exception as e:
                f += 1
        print(f"  sys_menu INSERT: 成功={s}, 失败={f}")

        # 提取其他系统表的 INSERT
        for table in ['sys_config', 'sys_dept', 'sys_dict_type', 'sys_dict_data', 'sys_role', 'sys_user',
                       'sys_user_role', 'sys_role_menu', 'sys_post', 'sys_job', 'sys_notice', 'sys_menu']:
            inserts = re.findall(rf"INSERT INTO `{table}`[^;]+;", full_sql, re.IGNORECASE)
            if inserts and table != 'sys_menu':  # sys_menu 已处理
                s, f = 0, 0
                for stmt in inserts:
                    try:
                        cursor.execute(stmt)
                        s += 1
                    except Exception as e:
                        f += 1
                if s > 0:
                    print(f"  {table} INSERT: 成功={s}, 失败={f}")

        conn.commit()

    # 检查 LIMS 菜单是否存在
    try:
        cursor.execute('SELECT COUNT(*) FROM sys_menu WHERE menu_id >= 3000')
        lims_menu_count = cursor.fetchone()[0]
        print(f"\nLIMS菜单数: {lims_menu_count}")

        if lims_menu_count == 0:
            print("正在插入LIMS菜单数据...")
            with open(r'c:\Work\mes\mes\weimes-erp\doc\lims-init.sql', 'r', encoding='utf-8') as f:
                lims_sql = f.read()

            # 提取 sys_menu INSERT
            inserts = re.findall(r"INSERT INTO `sys_menu`[^;]+;", lims_sql, re.IGNORECASE)
            s, f = 0, 0
            for stmt in inserts:
                try:
                    cursor.execute(stmt)
                    s += 1
                except Exception as e:
                    f += 1
            print(f"  LIMS菜单 INSERT: 成功={s}, 失败={f}")
            conn.commit()
    except Exception as e:
        print(f"  检查LIMS菜单失败: {e}")

    # 角色授权
    try:
        cursor.execute('SELECT role_id, role_name FROM sys_role WHERE deleted = 0')
        roles = cursor.fetchall()
        print(f"\n角色列表: {roles}")

        for role in roles:
            role_id = role[0]
            if role_id == 1:
                continue
            cursor.execute('SELECT COUNT(*) FROM sys_role_menu WHERE role_id = %s AND menu_id >= 3000', (role_id,))
            existing_count = cursor.fetchone()[0]
            if existing_count > 0:
                print(f"  role_id={role_id} 已有LIMS菜单授权，跳过")
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
    cursor.execute('SHOW TABLES')
    tables = cursor.fetchall()
    sys_t = len([t for t in tables if t[0].startswith('sys_')])
    lims_t = len([t for t in tables if t[0].startswith('lims_')])
    print(f"\n最终结果: 总表数={len(tables)}, 系统表={sys_t}, LIMS表={lims_t}")

    try:
        cursor.execute('SELECT COUNT(*) FROM sys_menu')
        print(f"sys_menu 总记录: {cursor.fetchone()[0]}")
        cursor.execute('SELECT COUNT(*) FROM sys_menu WHERE menu_id >= 3000')
        print(f"LIMS菜单记录: {cursor.fetchone()[0]}")
        cursor.execute('SELECT COUNT(*) FROM sys_user')
        print(f"sys_user 记录: {cursor.fetchone()[0]}")
        cursor.execute('SELECT COUNT(*) FROM sys_role')
        print(f"sys_role 记录: {cursor.fetchone()[0]}")
    except Exception as e:
        print(f"检查失败: {e}")

    cursor.close()
    conn.close()
    print("\n修复完成!")

if __name__ == '__main__':
    main()
