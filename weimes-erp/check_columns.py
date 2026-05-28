import pymysql
import re

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

# Tables that had errors
problem_tables = {
    'lims_test_order': ['lims/test/LimsTestOrderMapper.xml'],
    'lims_instrument_calibration': ['lims/instrument/LimsInstrumentCalibrationMapper.xml'],
    'lims_reagent': ['lims/reagent/LimsReagentMapper.xml'],
    'lims_oos_investigation': ['lims/oos/LimsOosInvestigationMapper.xml'],
    'lims_deviation': ['lims/deviation/LimsDeviationMapper.xml'],
    'lims_capa': ['lims/capa/LimsCapaMapper.xml'],
    'lims_change_control': ['lims/change/LimsChangeControlMapper.xml'],
}

for table, mapper_files in problem_tables.items():
    cursor.execute(f'DESCRIBE {table}')
    db_columns = set(r[0] for r in cursor.fetchall())

    for mf in mapper_files:
        path = rf'c:\Work\mes\mes\weimes-erp\wei-lims\src\main\resources\mapper\{mf}'
        try:
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
        except:
            print(f'Cannot read {mf}')
            continue

        # Extract column names from SQL (select and insert statements)
        # Look for column lists in SQL
        sql_columns = set()
        # Find select columns
        for match in re.finditer(r'select\s+(.*?)\s+from', content, re.IGNORECASE | re.DOTALL):
            cols_text = match.group(1)
            for col in re.findall(r'(\w+)', cols_text):
                if col.lower() not in ('select', 'from', 'where', 'and', 'or', 'order', 'by', 'desc', 'asc', 'null', 'not', 'as', 'if', 'test', 'into', 'values', 'set', 'update', 'insert', 'delete', 'create', 'table', 'index', 'key'):
                    sql_columns.add(col.lower())

        # Find columns in insert statements
        for match in re.finditer(r'insert\s+into\s+\w+\s*\((.*?)\)', content, re.IGNORECASE | re.DOTALL):
            cols_text = match.group(1)
            for col in re.findall(r'(\w+)', cols_text):
                sql_columns.add(col.lower())

        # Compare
        db_lower = set(c.lower() for c in db_columns)
        in_sql_not_db = sql_columns - db_lower - {'deleted', 'deleteat', 'version', 'attr1', 'attr2', 'attr3', 'attr4'}
        in_db_not_sql = db_lower - sql_columns

        if in_sql_not_db or in_db_not_sql:
            print(f'\n=== {table} ({mf}) ===')
            if in_sql_not_db:
                print(f'  In Mapper but NOT in DB: {sorted(in_sql_not_db)}')
            if in_db_not_sql:
                print(f'  In DB but NOT in Mapper: {sorted(in_db_not_sql)}')

cursor.close()
conn.close()
