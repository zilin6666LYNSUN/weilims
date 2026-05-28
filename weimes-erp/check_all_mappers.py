import pymysql
import re
import os
import glob

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

# Get all LIMS tables and their columns
cursor.execute("SHOW TABLES LIKE 'lims_%'")
tables = [row[0] for row in cursor.fetchall()]

db_columns = {}
for t in tables:
    cursor.execute(f"DESCRIBE {t}")
    db_columns[t] = set(row[0] for row in cursor.fetchall())

# Parse mapper XML files
mapper_dir = r'c:\Work\mes\mes\weimes-erp\wei-lims\src\main\resources\mapper'
errors = []

for xml_file in glob.glob(os.path.join(mapper_dir, '**', '*.xml'), recursive=True):
    with open(xml_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find table name from SQL
    table_match = re.search(r'from\s+(\w+)', content, re.IGNORECASE)
    if not table_match:
        continue
    table_name = table_match.group(1)

    if table_name not in db_columns:
        errors.append(f"\n{os.path.basename(xml_file)}: Table '{table_name}' not found in database!")
        continue

    actual_cols = db_columns[table_name]

    # Find all column references in select/insert/update statements
    # Select columns
    select_matches = re.findall(r'select\s+(.*?)\s+from', content, re.IGNORECASE | re.DOTALL)
    for sm in select_matches:
        if '*' in sm:
            continue
        cols = [c.strip().split()[-1] for c in sm.split(',') if c.strip() and not c.strip().startswith('(')]
        for col in cols:
            col = col.strip()
            if col and col not in actual_cols and not col.startswith('#') and col != '':
                errors.append(f"\n{os.path.basename(xml_file)}: Column '{col}' in SELECT not found in {table_name}")

    # Insert columns
    insert_col_match = re.search(r'insert\s+into\s+\w+\s*\((.*?)\)', content, re.IGNORECASE | re.DOTALL)
    if insert_col_match:
        cols_text = insert_col_match.group(1)
        cols = [c.strip() for c in cols_text.split(',') if c.strip()]
        for col in cols:
            if col not in actual_cols:
                errors.append(f"\n{os.path.basename(xml_file)}: Column '{col}' in INSERT not found in {table_name}")

    # Update set columns
    update_set_matches = re.findall(r'(\w+)\s*=\s*#\{', content)
    for col in update_set_matches:
        if col in ('update_time',):
            continue
        if col not in actual_cols:
            errors.append(f"\n{os.path.basename(xml_file)}: Column '{col}' in UPDATE SET not found in {table_name}")

    # Where clause columns
    where_matches = re.findall(r'and\s+(\w+)\s*[=<>]|where\s+(\w+)\s*[=<>]', content, re.IGNORECASE)
    for match in where_matches:
        col = match[0] or match[1]
        if col not in actual_cols:
            errors.append(f"\n{os.path.basename(xml_file)}: Column '{col}' in WHERE not found in {table_name}")

cursor.close()
conn.close()

if errors:
    print("COLUMN MISMATCH ERRORS FOUND:")
    for e in errors:
        print(e)
else:
    print("ALL COLUMNS MATCH!")
