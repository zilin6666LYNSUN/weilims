"""
检查数据库表实际列名 vs Mapper XML中引用的列名
"""
import pymysql

DB_CONFIG = {
    'host': 'sh-cdb-l0c3gbtu.sql.tencentcdb.com',
    'port': 22700,
    'user': 'root',
    'password': 'Stl123456789',
    'database': 'weilims',
    'charset': 'utf8mb4',
}

# 需要检查的表和错误列名
TABLES_TO_CHECK = {
    'lims_test_method': ['standard_reference'],
    'lims_test_template': ['template_type'],
    'lims_instrument_type': ['instrument_type_id'],
    'lims_instrument_maintenance': ['maintenance_code'],
    'lims_instrument_reservation': ['user_id'],
    'lims_reference_standard': ['batch_no'],
    'lims_culture_collection': ['strain_no'],
    'lims_retain_sample': ['retain_location'],
    'lims_sample_dispose': ['dispose_reason'],
    'lims_sample_test_item': ['method_name'],
    # No static resource 的表
    'lims_specification': [],
    'lims_test_result': [],
    'lims_oos_investigation': [],
    'lims_change_control': [],
    'lims_stability': [],
    'lims_audit_trail': [],
}

conn = pymysql.connect(**DB_CONFIG)
cur = conn.cursor()

for table, wrong_cols in TABLES_TO_CHECK.items():
    cur.execute(f"SHOW COLUMNS FROM {table}")
    columns = [r[0] for r in cur.fetchall()]
    print(f"\n{table} ({len(columns)}列):")
    print(f"  列名: {columns}")
    for wc in wrong_cols:
        if wc in columns:
            print(f"  [OK] {wc} 存在")
        else:
            # 找相似的列名
            similar = [c for c in columns if any(part in c for part in wc.split('_'))]
            print(f"  [缺失] {wc} 不存在! 相似列: {similar}")

conn.close()
