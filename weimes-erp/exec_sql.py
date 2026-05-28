import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

# Check existing LIMS tables
cursor.execute("SHOW TABLES LIKE 'lims_%'")
tables = cursor.fetchall()
print(f'Found {len(tables)} LIMS tables:')
for t in tables:
    print(f'  {t[0]}')

if len(tables) == 0:
    print('\nNo LIMS tables found. Executing lims-init.sql...')
    with open(r'c:\Work\mes\mes\weimes-erp\doc\lims-init.sql', 'r', encoding='utf-8') as f:
        sql_content = f.read()

    # Remove comments and split by semicolons properly
    import re
    # Remove single-line comments
    sql_content = re.sub(r'--.*$', '', sql_content, flags=re.MULTILINE)
    # Remove multi-line comments
    sql_content = re.sub(r'/\*.*?\*/', '', sql_content, flags=re.DOTALL)

    statements = []
    current = ''
    for line in sql_content.split('\n'):
        line = line.strip()
        if not line:
            continue
        current += ' ' + line
        if current.strip().endswith(';'):
            statements.append(current.strip().rstrip(';'))
            current = ''

    success = 0
    errors = 0
    for stmt in statements:
        stmt = stmt.strip()
        if stmt:
            try:
                cursor.execute(stmt)
                success += 1
            except Exception as e:
                errors += 1
                print(f'ERROR: {str(e)[:120]}')
                print(f'SQL: {stmt[:100]}...')

    conn.commit()
    print(f'\nExecuted: {success} success, {errors} errors')

    # Verify tables created
    cursor.execute("SHOW TABLES LIKE 'lims_%'")
    tables = cursor.fetchall()
    print(f'\nNow found {len(tables)} LIMS tables')

cursor.close()
conn.close()
