import pymysql
conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# Add missing columns to lims_report
alter_sqls = [
    "ALTER TABLE lims_report ADD COLUMN remark varchar(500) DEFAULT NULL COMMENT '备注'",
    "ALTER TABLE lims_report ADD COLUMN update_by varchar(64) DEFAULT NULL COMMENT '更新者'",
    "ALTER TABLE lims_report ADD COLUMN update_time datetime DEFAULT NULL COMMENT '更新时间'",
]

for sql in alter_sqls:
    try:
        cur.execute(sql)
        print(f"[OK] {sql[:60]}...")
    except Exception as e:
        if 'Duplicate column' in str(e):
            print(f"[SKIP] Column already exists: {sql[:60]}...")
        else:
            print(f"[FAIL] {sql[:60]}... Error: {e}")

conn.commit()
cur.close()
conn.close()
print("\nDone!")
