import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# 1. 先检查是否有重复数据
cur.execute("""
    SELECT report_id, signature_type, COUNT(*) as cnt
    FROM lims_report_signature
    WHERE is_valid = 1
    GROUP BY report_id, signature_type
    HAVING cnt > 1
""")
duplicates = cur.fetchall()
if duplicates:
    print(f"[WARN] Found {len(duplicates)} duplicate groups, cleaning up...")
    for d in duplicates:
        print(f"  report_id={d[0]}, type={d[1]}, count={d[2]}")
        # Keep only the latest one, invalidate older ones
        cur.execute("""
            UPDATE lims_report_signature SET is_valid = 0
            WHERE report_id = %s AND signature_type = %s AND is_valid = 1
            ORDER BY signature_time ASC
            LIMIT %s
        """, (d[0], d[1], d[2] - 1))
    conn.commit()
    print("  Cleaned up duplicates")
else:
    print("[OK] No duplicate valid signatures found")

# 2. Add unique constraint
try:
    cur.execute("""
        ALTER TABLE lims_report_signature
        ADD UNIQUE INDEX uk_report_signature_type (report_id, signature_type)
    """)
    conn.commit()
    print("[OK] Unique constraint uk_report_signature_type added")
except pymysql.IntegrityError as e:
    if 'Duplicate' in str(e):
        print(f"[SKIP] Constraint already exists: {e}")
    else:
        print(f"[FAIL] {e}")

# 3. Verify
cur.execute("SHOW INDEX FROM lims_report_signature WHERE Non_unique = 0 AND Key_name != 'PRIMARY'")
for row in cur.fetchall():
    print(f"  Unique index: {row[2]} on column(s): {row[4]}")

cur.close()
conn.close()
