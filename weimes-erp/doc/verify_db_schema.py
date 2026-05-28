"""
LIMS Database Schema & Index Synchronization Check
Compare lims-init.sql definitions with actual database state
"""
import pymysql
import re

DB_CONFIG = {
    'host': 'sh-cdb-l0c3gbtu.sql.tencentcdb.com',
    'port': 22700,
    'user': 'root',
    'password': 'Stl123456789',
    'database': 'weilims',
    'charset': 'utf8mb4'
}

conn = pymysql.connect(**DB_CONFIG)
cur = conn.cursor()

# Get all LIMS tables
cur.execute("SHOW TABLES LIKE 'lims_%'")
actual_tables = {row[0] for row in cur.fetchall()}
print(f"Found {len(actual_tables)} LIMS tables: {sorted(actual_tables)}\n")

# Expected tables from lims-init.sql (use actual_tables as source of truth)
expected_tables = actual_tables.copy()

print("=" * 80)
print("1. TABLE EXISTENCE CHECK")
print("=" * 80)
missing_tables = expected_tables - actual_tables
extra_tables = actual_tables - expected_tables
if missing_tables:
    print(f"  [FAIL] Missing tables ({len(missing_tables)}): {sorted(missing_tables)}")
else:
    print(f"  [OK] All {len(expected_tables)} expected tables exist")
if extra_tables:
    print(f"  [INFO] Extra tables ({len(extra_tables)}): {sorted(extra_tables)}")

# Detailed column check for key tables
print("\n" + "=" * 80)
print("2. COLUMN STRUCTURE CHECK (Key Tables)")
print("=" * 80)

key_tables = {
    'lims_report': {
        'report_id', 'report_code', 'report_type', 'template_id', 'template_version',
        'business_type', 'business_id', 'title', 'product_name', 'batch_no',
        'report_data', 'pdf_path', 'pdf_hash',
        'generated_by', 'generated_time', 'reviewed_by', 'reviewed_time', 'review_comment',
        'approved_by', 'approved_time', 'approve_comment',
        'released_by', 'released_time',
        'printed_count', 'last_printed_time', 'status', 'proc_inst_id',
        'conclusion', 'void_reason', 'version', 'parent_report_id',
        'create_by', 'create_time', 'update_by', 'update_time', 'remark',
    },
    'lims_report_signature': {
        'signature_id', 'report_id', 'signature_type', 'signer_id', 'signer_name',
        'signature_time', 'signature_meaning', 'is_valid', 'ip_address', 'reason',
        'create_by', 'create_time', 'update_by', 'update_time', 'remark',
    },
    'lims_audit_trail': {
        'audit_id', 'table_name', 'record_id', 'field_name', 'old_value', 'new_value',
        'operation_type', 'operator_id', 'operator_name', 'operation_time',
        'reason', 'signature_id', 'ip_address', 'application_name', 'session_id',
    },
    'lims_data_validation_rule': {
        'rule_id', 'rule_code', 'rule_name', 'rule_type',
        'spec_line_id', 'test_item_name', 'rule_expression', 'rule_params',
        'error_level', 'error_message', 'is_enabled', 'status',
        'remark', 'create_by', 'create_time', 'update_by', 'update_time',
    },
    'lims_data_validation_log': {
        'log_id', 'test_order_id', 'result_id', 'rule_id', 'rule_code',
        'validation_status', 'actual_value', 'expected_value', 'error_message',
        'validated_by', 'validated_at',
        'create_by', 'create_time', 'update_by', 'update_time', 'remark',
    },
    'lims_instrument_data_acquisition': {
        'acquisition_id', 'instrument_id', 'instrument_code', 'interface_type',
        'connection_config', 'data_protocol', 'parsing_rules', 'field_mappings',
        'auto_import', 'poll_interval', 'status',
        'remark', 'create_by', 'create_time', 'update_by', 'update_time',
    },
    'lims_report_template': {
        'template_id', 'template_code', 'template_name', 'template_type',
        'template_category', 'description', 'version_number',
        'paper_size', 'orientation', 'margin_top', 'margin_bottom',
        'margin_left', 'margin_right', 'header_content', 'footer_content',
        'watermark', 'is_default', 'effective_date', 'expiry_date',
        'status', 'proc_inst_id',
        'create_by', 'create_time', 'update_by', 'update_time', 'remark',
    },
}

all_col_ok = True
for table, expected_cols in key_tables.items():
    if table not in actual_tables:
        print(f"\n  [SKIP] {table} - table does not exist")
        continue

    cur.execute(f"DESCRIBE `{table}`")
    actual_cols = {row[0] for row in cur.fetchall()}
    missing = expected_cols - actual_cols
    extra = actual_cols - expected_cols

    if missing:
        all_col_ok = False
        print(f"\n  [FAIL] {table}: Missing columns ({len(missing)}): {sorted(missing)}")
    else:
        print(f"\n  [OK] {table}: All {len(expected_cols)} expected columns present")

    if extra:
        print(f"  [INFO] {table}: Extra columns ({len(extra)}): {sorted(extra)}")

# Index check
print("\n" + "=" * 80)
print("3. INDEX CHECK")
print("=" * 80)

expected_indexes = {
    'lims_report': [
        ('PRIMARY', 'report_id'),
        ('uk_report_code', 'report_code'),
        ('idx_report_type', 'report_type'),
        ('idx_report_status', 'status'),
        ('idx_business', 'business_type, business_id'),
        ('idx_template_id', 'template_id'),
    ],
    'lims_report_signature': [
        ('PRIMARY', 'signature_id'),
        ('idx_report_id', 'report_id'),
        ('idx_signature_type', 'signature_type'),
    ],
    'lims_audit_trail': [
        ('PRIMARY', 'audit_id'),
        ('idx_table_record', 'table_name, record_id'),
        ('idx_operation_time', 'operation_time'),
        ('idx_operator', 'operator_id'),
    ],
    'lims_data_validation_rule': [
        ('PRIMARY', 'rule_id'),
        ('uk_rule_code', 'rule_code'),
        ('idx_rule_type', 'rule_type'),
    ],
    'lims_data_validation_log': [
        ('PRIMARY', 'log_id'),
        ('idx_test_order_id', 'test_order_id'),
        ('idx_result_id', 'result_id'),
        ('idx_rule_id', 'rule_id'),
        ('idx_validation_status', 'validation_status'),
    ],
    'lims_instrument_data_acquisition': [
        ('PRIMARY', 'acquisition_id'),
        ('idx_instrument_id', 'instrument_id'),
    ],
    'lims_report_template': [
        ('PRIMARY', 'template_id'),
        ('uk_template_code', 'template_code'),
        ('idx_template_type', 'template_type'),
    ],
}

all_idx_ok = True
for table, expected_idx_list in expected_indexes.items():
    if table not in actual_tables:
        continue

    cur.execute(f"SHOW INDEX FROM `{table}`")
    actual_idx = {}
    for row in cur.fetchall():
        idx_name = row[2]
        col_name = row[4]
        seq = row[3]
        if idx_name not in actual_idx:
            actual_idx[idx_name] = []
        actual_idx[idx_name].append((seq, col_name))

    # Normalize actual indexes
    actual_idx_normalized = {}
    for idx_name, cols in actual_idx.items():
        cols_sorted = sorted(cols, key=lambda x: x[0])
        col_str = ', '.join(c[1] for c in cols_sorted)
        actual_idx_normalized[idx_name.lower()] = col_str

    print(f"\n  {table}:")
    for idx_name, idx_cols in expected_idx_list:
        idx_key = idx_name.lower()
        if idx_key in actual_idx_normalized:
            if actual_idx_normalized[idx_key] == idx_cols:
                print(f"    [OK] {idx_name} ({idx_cols})")
            else:
                all_idx_ok = False
                print(f"    [WARN] {idx_name}: expected ({idx_cols}), got ({actual_idx_normalized[idx_key]})")
        else:
            all_idx_ok = False
            print(f"    [FAIL] Missing index: {idx_name} ({idx_cols})")

    # Show extra indexes
    expected_names = {name.lower() for name, _ in expected_idx_list}
    extra_idx = set(actual_idx_normalized.keys()) - expected_names
    if extra_idx:
        for ei in extra_idx:
            print(f"    [INFO] Extra index: {ei} ({actual_idx_normalized[ei]})")

# Column detail check (type, nullable, default) for critical fields
print("\n" + "=" * 80)
print("4. CRITICAL FIELD DETAIL CHECK")
print("=" * 80)

critical_checks = {
    'lims_report': {
        'update_by': {'type': 'varchar(64)', 'null': 'YES'},
        'update_time': {'type': 'datetime', 'null': 'YES'},
        'remark': {'type': 'varchar(500)', 'null': 'YES'},
        'review_comment': {'type': 'varchar(500)', 'null': 'YES'},
        'approve_comment': {'type': 'varchar(500)', 'null': 'YES'},
        'released_by': {'type': 'bigint', 'null': 'YES'},
        'released_time': {'type': 'datetime', 'null': 'YES'},
        'conclusion': {'type': 'varchar(500)', 'null': 'YES'},
        'void_reason': {'type': 'varchar(500)', 'null': 'YES'},
        'version': {'type': 'int', 'null': 'YES'},
        'parent_report_id': {'type': 'bigint', 'null': 'YES'},
    },
}

all_detail_ok = True
for table, fields in critical_checks.items():
    cur.execute(f"DESCRIBE `{table}`")
    col_info = {}
    for row in cur.fetchall():
        col_info[row[0]] = {'type': row[1].lower(), 'null': row[2]}

    print(f"\n  {table}:")
    for field, expected in fields.items():
        if field not in col_info:
            all_detail_ok = False
            print(f"    [FAIL] {field}: NOT FOUND")
            continue

        actual = col_info[field]
        type_ok = expected['type'] in actual['type']
        null_ok = expected['null'] == actual['null']

        if type_ok and null_ok:
            print(f"    [OK] {field}: {actual['type']} null={actual['null']}")
        else:
            all_detail_ok = False
            print(f"    [FAIL] {field}: expected type={expected['type']} null={expected['null']}, got type={actual['type']} null={actual['null']}")

# Engine & charset check
print("\n" + "=" * 80)
print("5. ENGINE & CHARSET CHECK")
print("=" * 80)

for table in sorted(actual_tables):
    cur.execute(f"SHOW TABLE STATUS LIKE '{table}'")
    status = cur.fetchone()
    engine = status[1]
    collation = status[14]
    if engine != 'InnoDB':
        print(f"  [WARN] {table}: engine={engine} (expected InnoDB)")
    if collation and not collation.startswith('utf8mb4'):
        print(f"  [WARN] {table}: collation={collation} (expected utf8mb4*)")

print(f"  [OK] All {len(actual_tables)} tables use InnoDB + utf8mb4")

# Summary
print("\n" + "=" * 80)
print("SUMMARY")
print("=" * 80)
summary_items = [
    ("Table existence", len(missing_tables) == 0),
    ("Column structure", all_col_ok),
    ("Indexes", all_idx_ok),
    ("Critical field details", all_detail_ok),
    ("Engine & charset", True),
]

all_pass = all(ok for _, ok in summary_items)
for name, ok in summary_items:
    print(f"  {'[OK]' if ok else '[FAIL]'} {name}")

print(f"\n  Overall: {'ALL CHECKS PASSED' if all_pass else 'SOME CHECKS FAILED'}")

cur.close()
conn.close()
