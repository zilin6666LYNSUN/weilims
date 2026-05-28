import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

alter_statements = [
    # 1. lims_report_signature: add missing audit fields
    "ALTER TABLE lims_report_signature ADD COLUMN create_by VARCHAR(64) DEFAULT '' COMMENT '创建者'",
    "ALTER TABLE lims_report_signature ADD COLUMN update_by VARCHAR(64) DEFAULT '' COMMENT '更新者'",
    "ALTER TABLE lims_report_signature ADD COLUMN update_time DATETIME DEFAULT NULL COMMENT '更新时间'",
    "ALTER TABLE lims_report_signature ADD COLUMN remark VARCHAR(500) DEFAULT NULL COMMENT '备注'",
    "ALTER TABLE lims_report_signature ADD INDEX idx_signature_type (signature_type)",

    # 2. lims_data_validation_log: add missing audit fields
    "ALTER TABLE lims_data_validation_log ADD COLUMN create_by VARCHAR(64) DEFAULT '' COMMENT '创建者'",
    "ALTER TABLE lims_data_validation_log ADD COLUMN create_time DATETIME DEFAULT NULL COMMENT '创建时间'",
    "ALTER TABLE lims_data_validation_log ADD COLUMN update_by VARCHAR(64) DEFAULT '' COMMENT '更新者'",
    "ALTER TABLE lims_data_validation_log ADD COLUMN update_time DATETIME DEFAULT NULL COMMENT '更新时间'",
    "ALTER TABLE lims_data_validation_log ADD COLUMN remark VARCHAR(500) DEFAULT NULL COMMENT '备注'",
    "ALTER TABLE lims_data_validation_log ADD INDEX idx_rule_id (rule_id)",
    "ALTER TABLE lims_data_validation_log ADD INDEX idx_validation_status (validation_status)",

    # 3. lims_report: add missing indexes
    "ALTER TABLE lims_report ADD UNIQUE INDEX uk_report_code (report_code)",
    "ALTER TABLE lims_report ADD INDEX idx_report_type (report_type)",
    "ALTER TABLE lims_report ADD INDEX idx_report_status (status)",
    "ALTER TABLE lims_report ADD INDEX idx_business (business_type, business_id)",
    "ALTER TABLE lims_report ADD INDEX idx_template_id (template_id)",

    # 4. lims_data_validation_rule: add missing index
    "ALTER TABLE lims_data_validation_rule ADD INDEX idx_rule_type (rule_type)",

    # 5. lims_report_template: add missing indexes
    "ALTER TABLE lims_report_template ADD UNIQUE INDEX uk_template_code (template_code)",
    "ALTER TABLE lims_report_template ADD INDEX idx_template_type (template_type)",
]

for sql in alter_statements:
    try:
        cur.execute(sql)
        # Extract table and action from SQL
        parts = sql.split()
        action = parts[2] if len(parts) > 2 else "?"
        table = parts[2] if action == "TABLE" else "?"
        print(f"  [OK] {sql[:80]}...")
    except Exception as e:
        err_str = str(e)
        if 'Duplicate' in err_str:
            print(f"  [SKIP] Already exists: {sql[:60]}...")
        else:
            print(f"  [FAIL] {sql[:60]}... : {err_str[:80]}")

conn.commit()
cur.close()
conn.close()
print("\nAll ALTER statements executed!")
