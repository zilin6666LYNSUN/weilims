import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# Check current columns
cur.execute("DESCRIBE lims_report")
existing = {row[0] for row in cur.fetchall()}
print("Current columns:", sorted(existing))

# Expected columns from updated lims-init.sql
expected = {
    'report_id', 'report_code', 'report_type', 'template_id', 'template_version',
    'business_type', 'business_id', 'title', 'product_name', 'batch_no',
    'report_data', 'pdf_path', 'pdf_hash',
    'generated_by', 'generated_time', 'reviewed_by', 'reviewed_time', 'review_comment',
    'approved_by', 'approved_time', 'approve_comment',
    'released_by', 'released_time',
    'printed_count', 'last_printed_time', 'status', 'proc_inst_id',
    'conclusion', 'void_reason', 'version', 'parent_report_id',
    'create_by', 'create_time', 'update_by', 'update_time', 'remark'
}

missing = expected - existing
print(f"\nMissing columns ({len(missing)}):", sorted(missing))

# Generate ALTER TABLE statements
if missing:
    column_defs = {
        'review_comment': "VARCHAR(500) DEFAULT NULL COMMENT '复核意见'",
        'approve_comment': "VARCHAR(500) DEFAULT NULL COMMENT '批准意见'",
        'released_by': "BIGINT DEFAULT NULL COMMENT '放行人'",
        'released_time': "DATETIME DEFAULT NULL COMMENT '放行时间'",
        'conclusion': "VARCHAR(500) DEFAULT NULL COMMENT '报告结论'",
        'void_reason': "VARCHAR(500) DEFAULT NULL COMMENT '作废原因'",
        'version': "INT DEFAULT NULL COMMENT '版本号'",
        'parent_report_id': "BIGINT DEFAULT NULL COMMENT '原报告ID(重新生成时关联)'",
        'update_by': "VARCHAR(64) DEFAULT NULL COMMENT '更新者'",
        'update_time': "DATETIME DEFAULT NULL COMMENT '更新时间'",
        'remark': "VARCHAR(500) DEFAULT NULL COMMENT '备注'",
    }

    for col in sorted(missing):
        if col in column_defs:
            sql = f"ALTER TABLE lims_report ADD COLUMN {col} {column_defs[col]}"
            try:
                cur.execute(sql)
                print(f"  [OK] Added: {col}")
            except Exception as e:
                if 'Duplicate column' in str(e):
                    print(f"  [SKIP] Already exists: {col}")
                else:
                    print(f"  [FAIL] {col}: {e}")
        else:
            print(f"  [WARN] No definition for: {col}")

    conn.commit()
    print("\nAll missing columns added!")
else:
    print("\nNo missing columns - table is already up to date!")

# Verify
cur.execute("DESCRIBE lims_report")
final = {row[0] for row in cur.fetchall()}
still_missing = expected - final
if still_missing:
    print(f"Still missing: {still_missing}")
else:
    print("Verification: All expected columns present!")

cur.close()
conn.close()
