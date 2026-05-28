import pymysql
import re
import os
import glob

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

cursor.execute("SHOW TABLES LIKE 'lims_%'")
tables = [row[0] for row in cursor.fetchall()]

db_columns = {}
for t in tables:
    cursor.execute(f"DESCRIBE {t}")
    db_columns[t] = set(row[0] for row in cursor.fetchall())

mapper_dir = r'c:\Work\mes\mes\weimes-erp\wei-lims\src\main\resources\mapper'
errors = []

for xml_file in glob.glob(os.path.join(mapper_dir, '**', '*.xml'), recursive=True):
    with open(xml_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find table name
    table_match = re.search(r'from\s+(\w+)', content, re.IGNORECASE)
    if not table_match:
        continue
    table_name = table_match.group(1)

    if table_name not in db_columns:
        errors.append(f"\n{os.path.basename(xml_file)}: Table '{table_name}' not found!")
        continue

    actual_cols = db_columns[table_name]

    # Check resultMap column mappings
    result_map_matches = re.findall(r'column="(\w+)"', content)
    for col in result_map_matches:
        if col not in actual_cols:
            errors.append(f"\n{os.path.basename(xml_file)}: Column '{col}' in resultMap not found in {table_name}")

cursor.close()
conn.close()

if errors:
    print("COLUMN MISMATCH ERRORS (resultMap only):")
    for e in errors:
        print(e)
else:
    print("ALL COLUMNS MATCH!")
