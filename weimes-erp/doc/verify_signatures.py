import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

print("=" * 80)
print("GMP Compliance - Electronic Signature & Approval Timestamp Verification")
print("=" * 80)

# 1. Find the latest report (from our test)
print("\n[1] Latest lims_report records:")
cur.execute("""
    SELECT report_id, report_code, status,
           generated_by, generated_time,
           reviewed_by, reviewed_time, review_comment,
           approved_by, approved_time, approve_comment,
           released_by, released_time,
           conclusion, version, create_by, create_time, update_by, update_time, remark
    FROM lims_report
    ORDER BY report_id DESC LIMIT 3
""")
reports = cur.fetchall()
for r in reports:
    print(f"\n  Report ID: {r[0]}, Code: {r[1]}, Status: {r[2]}")
    print(f"  Generated:  by={r[3]}, time={r[4]}")
    print(f"  Reviewed:   by={r[5]}, time={r[6]}, comment={r[7]}")
    print(f"  Approved:   by={r[8]}, time={r[9]}, comment={r[10]}")
    print(f"  Released:   by={r[11]}, time={r[12]}")
    print(f"  Conclusion: {r[13]}, Version: {r[14]}")
    print(f"  Create: by={r[15]}, time={r[16]}")
    print(f"  Update: by={r[17]}, time={r[18]}")
    print(f"  Remark: {r[19]}")

# 2. Check electronic signatures for the latest report
if reports:
    latest_id = reports[0][0]
    print(f"\n[2] Electronic signatures for report_id={latest_id}:")
    cur.execute("""
        SELECT signature_id, report_id, signature_type, signer_id, signer_name,
               signature_time, signature_meaning, is_valid, ip_address, reason
        FROM lims_report_signature
        WHERE report_id = %s
        ORDER BY signature_time
    """, (latest_id,))
    sigs = cur.fetchall()
    if sigs:
        for s in sigs:
            print(f"\n  Signature ID: {s[0]}")
            print(f"    Type: {s[2]}, Signer: {s[3]} ({s[4]})")
            print(f"    Time: {s[5]}")
            print(f"    Meaning: {s[6]}")
            print(f"    Valid: {s[7]}, IP: {s[8]}, Reason: {s[9]}")
    else:
        print("  WARNING: No signatures found!")

    # 3. Check audit trail for the latest report
    print(f"\n[3] Audit trail for report_id={latest_id}:")
    cur.execute("""
        SELECT audit_id, table_name, record_id, field_name, old_value, new_value,
               operation_type, operator_id, operator_name, operation_time, reason, ip_address
        FROM lims_audit_trail
        WHERE table_name = 'lims_report' AND record_id = %s
        ORDER BY operation_time
    """, (latest_id,))
    trails = cur.fetchall()
    if trails:
        for t in trails:
            print(f"\n  Audit ID: {t[0]}")
            print(f"    Op: {t[6]}, Field: {t[3]}")
            print(f"    Old: {str(t[4])[:60] if t[4] else 'NULL'}")
            print(f"    New: {str(t[5])[:60] if t[5] else 'NULL'}")
            print(f"    Operator: {t[7]} ({t[8]}), Time: {t[9]}")
            print(f"    Reason: {t[10]}, IP: {t[11]}")
    else:
        print("  WARNING: No audit trail found!")

# 4. Verify user IDs match test users
print("\n[4] Test user verification:")
cur.execute("""
    SELECT user_id, user_name, nick_name
    FROM sys_user
    WHERE user_name IN ('lims_prep', 'lims_rev', 'lims_appr', 'lims_rel')
    ORDER BY user_id
""")
for u in cur.fetchall():
    print(f"  user_id={u[0]}, username={u[1]}, nick={u[2]}")

# 5. Summary check
print("\n" + "=" * 80)
print("COMPLIANCE VERIFICATION SUMMARY")
print("=" * 80)

if reports:
    r = reports[0]
    checks = {
        "generated_by recorded": r[3] is not None,
        "generated_time recorded": r[4] is not None,
        "reviewed_by recorded": r[5] is not None,
        "reviewed_time recorded": r[6] is not None,
        "review_comment recorded": r[7] is not None,
        "approved_by recorded": r[8] is not None,
        "approved_time recorded": r[9] is not None,
        "approve_comment recorded": r[10] is not None,
        "released_by recorded": r[11] is not None,
        "released_time recorded": r[12] is not None,
        "conclusion recorded": r[13] is not None,
        "version recorded": r[14] is not None,
        "create_by recorded": r[15] is not None,
        "create_time recorded": r[16] is not None,
        "update_by recorded": r[17] is not None,
        "update_time recorded": r[18] is not None,
        "4 roles are different users": len({r[3], r[5], r[8], r[11]}) == 4 if all([r[3], r[5], r[8], r[11]]) else False,
    }

    all_pass = True
    for check, result in checks.items():
        icon = "[OK]" if result else "[FAIL]"
        if not result:
            all_pass = False
        print(f"  {icon} {check}")

    # Signature count check
    if sigs:
        sig_types = {s[2] for s in sigs}
        expected_types = {"PREPARE", "REVIEW", "APPROVE", "RELEASE"}
        sig_complete = expected_types.issubset(sig_types)
        icon = "[OK]" if sig_complete else "[FAIL]"
        if not sig_complete:
            all_pass = False
        print(f"  {icon} All 4 signature types present (found: {sig_types})")
        print(f"  {'[OK]' if len(sigs) >= 4 else '[FAIL]'} Signature count: {len(sigs)} (expected >= 4)")
    else:
        print("  [FAIL] No electronic signatures found")
        all_pass = False

    # Audit trail count
    print(f"  {'[OK]' if len(trails) > 0 else '[FAIL]'} Audit trail entries: {len(trails)}")

    print(f"\n  Overall: {'ALL CHECKS PASSED' if all_pass else 'SOME CHECKS FAILED'}")

cur.close()
conn.close()
