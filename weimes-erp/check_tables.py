import pymysql
import re

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

# Get existing tables
cursor.execute("SHOW TABLES LIKE 'lims_%'")
existing = set(t[0] for t in cursor.fetchall())

# Parse SQL file to find expected table names
with open(r'c:\Work\mes\mes\weimes-erp\doc\lims-init.sql', 'r', encoding='utf-8') as f:
    sql_content = f.read()

expected = set(re.findall(r'CREATE\s+TABLE\s+(\w+)', sql_content, re.IGNORECASE))

missing = expected - existing
extra = existing - expected

print(f'Expected tables: {len(expected)}')
print(f'Existing tables: {len(existing)}')
print(f'Missing tables ({len(missing)}):')
for t in sorted(missing):
    print(f'  {t}')
print(f'\nExtra tables ({len(extra)}):')
for t in sorted(extra):
    print(f'  {t}')

cursor.close()
conn.close()
