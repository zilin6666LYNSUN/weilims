"""
WEIMES LIMS 数据库初始化脚本
执行顺序: 1. 20250415-init.sql  2. lims-init.sql  3. 角色授权
"""
import pymysql
import os
import sys
import time

DB_CONFIG = {
    'host': 'sh-cdb-l0c3gbtu.sql.tencentcdb.com',
    'port': 22700,
    'user': 'root',
    'password': 'Stl123456789',
    'database': 'weilims',
    'charset': 'utf8mb4',
}

SQL_DIR = r'c:\Work\mes\mes\weimes-erp\doc'

def execute_sql_file(cursor, filepath, desc):
    """执行SQL文件，按分号分割逐条执行"""
    print(f"\n{'='*60}")
    print(f"正在执行: {desc}")
    print(f"文件: {filepath}")
    print(f"{'='*60}")

    if not os.path.exists(filepath):
        print(f"  [错误] 文件不存在: {filepath}")
        return False

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # 按分号分割，过滤空语句和注释
    statements = []
    current = []
    for line in content.split('\n'):
        stripped = line.strip()
        # 跳过纯注释行
        if stripped.startswith('--') or stripped == '':
            continue
        current.append(line)
        if stripped.endswith(';'):
            stmt = '\n'.join(current).strip()
            if stmt and stmt != ';':
                statements.append(stmt)
            current = []

    success = 0
    failed = 0
    for i, stmt in enumerate(statements):
        try:
            cursor.execute(stmt)
            success += 1
            if (i + 1) % 50 == 0:
                print(f"  已执行 {i+1}/{len(statements)} 条...")
        except Exception as e:
            failed += 1
            err_msg = str(e)[:200]
            # 只打印关键错误
            if 'already exists' not in err_msg.lower() and 'duplicate' not in err_msg.lower():
                print(f"  [警告] 第{i+1}条执行失败: {err_msg}")

    print(f"  执行完成: 成功 {success}, 失败 {failed}, 总计 {len(statements)}")
    return True

def check_tables(cursor):
    """检查数据库中的表"""
    cursor.execute('SHOW TABLES')
    tables = cursor.fetchall()
    print(f"\n当前数据库表数量: {len(tables)}")

    lims_tables = [t[0] for t in tables if t[0].startswith('lims_')]
    sys_tables = [t[0] for t in tables if t[0].startswith('sys_')]
    print(f"  LIMS表: {len(lims_tables)} 张")
    print(f"  系统表: {len(sys_tables)} 张")

    # 检查LIMS菜单
    try:
        cursor.execute('SELECT COUNT(*) FROM sys_menu WHERE menu_id >= 3000')
        menu_count = cursor.fetchone()[0]
        print(f"  LIMS菜单数: {menu_count}")
    except:
        print(f"  LIMS菜单: sys_menu表不存在")

    return len(tables)

def grant_role_menus(cursor):
    """给角色授权LIMS菜单"""
    print(f"\n{'='*60}")
    print("正在为角色授权LIMS菜单...")
    print(f"{'='*60}")

    # 查看现有角色
    cursor.execute('SELECT role_id, role_name, role_key FROM sys_role WHERE deleted = 0')
    roles = cursor.fetchall()
    print(f"现有角色:")
    for r in roles:
        print(f"  role_id={r[0]}, name={r[1]}, key={r[2]}")

    # 给所有非超级管理员角色授权LIMS菜单
    for role in roles:
        role_id = role[0]
        if role_id == 1:
            print(f"  跳过超级管理员(role_id=1)，自动拥有所有权限")
            continue

        # 检查是否已有授权
        cursor.execute('SELECT COUNT(*) FROM sys_role_menu WHERE role_id = %s AND menu_id >= 3000', (role_id,))
        existing = cursor.fetchone()[0]
        if existing > 0:
            print(f"  role_id={role_id} 已有 {existing} 条LIMS菜单授权，跳过")
            continue

        # 插入授权
        cursor.execute("""
            INSERT INTO sys_role_menu (role_id, menu_id, deleted, deleteAt, version)
            SELECT %s, menu_id, 0, '1000-01-01 00:00:00', 1
            FROM sys_menu WHERE menu_id >= 3000 AND menu_id <= 3699
        """, (role_id,))
        count = cursor.rowcount
        print(f"  role_id={role_id} 授权 {count} 条LIMS菜单")

def main():
    print("WEIMES LIMS 数据库初始化")
    print(f"数据库: {DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}")

    start_time = time.time()

    try:
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("数据库连接成功!")
    except Exception as e:
        print(f"数据库连接失败: {e}")
        sys.exit(1)

    # 检查当前状态
    table_count = check_tables(cursor)

    if table_count == 0:
        # 第1步: 执行基础初始化
        sql_file = os.path.join(SQL_DIR, '20250415-init.sql')
        execute_sql_file(cursor, sql_file, "基础系统表初始化")
        conn.commit()
        print("  [已提交]")

        # 检查结果
        cursor.execute('SHOW TABLES')
        new_count = len(cursor.fetchall())
        print(f"  基础表创建完成，当前表数量: {new_count}")
    else:
        print(f"数据库已有 {table_count} 张表，跳过基础初始化")

    # 第2步: 执行LIMS初始化
    sql_file = os.path.join(SQL_DIR, 'lims-init.sql')
    execute_sql_file(cursor, sql_file, "LIMS表和菜单初始化")
    conn.commit()
    print("  [已提交]")

    # 第3步: 角色授权
    try:
        grant_role_menus(cursor)
        conn.commit()
        print("  [已提交]")
    except Exception as e:
        print(f"  角色授权失败: {e}")

    # 最终检查
    print(f"\n{'='*60}")
    print("最终检查:")
    print(f"{'='*60}")
    check_tables(cursor)

    elapsed = time.time() - start_time
    print(f"\n初始化完成! 耗时: {elapsed:.1f}秒")

    cursor.close()
    conn.close()

if __name__ == '__main__':
    main()
