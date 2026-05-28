"""
LIMS 电子签名唯一约束冲突测试
测试场景：
  1. 同一用户对同一报告同一环节重复签名 → 应被阻止
  2. 同一报告不同环节同一用户签名 → 应允许（但四眼原则会阻止）
  3. 不同用户对同一报告同一环节签名 → 应被阻止（一个环节只能签一次）
  4. 作废签名后重新签名 → 应允许
"""
import pymysql
import sys
from datetime import datetime

DB_CONFIG = {
    'host': 'sh-cdb-l0c3gbtu.sql.tencentcdb.com',
    'port': 22700,
    'user': 'root',
    'password': 'Stl123456789',
    'database': 'weilims',
    'charset': 'utf8mb4'
}

BASE_URL = "http://localhost:8088"

results = []

def record(test_id, desc, passed, detail=""):
    status = "PASS" if passed else "FAIL"
    results.append((test_id, desc, passed, detail))
    icon = "[OK]" if passed else "[FAIL]"
    print(f"  {icon} [{test_id}] {desc}")
    if detail:
        print(f"        Detail: {detail}")


def check_unique_constraint_exists(cur):
    """检查 lims_report_signature 表是否有唯一约束"""
    cur.execute("SHOW INDEX FROM lims_report_signature WHERE Non_unique = 0")
    unique_indexes = cur.fetchall()
    # 排除 PRIMARY KEY
    non_pk_unique = [idx for idx in unique_indexes if idx[2] != 'PRIMARY']
    return non_pk_unique


def test_duplicate_signature_db(cur, conn):
    """测试1: 直接通过SQL插入重复签名"""
    print("\n[1] 数据库层: 同一用户对同一报告同一环节重复签名")
    print("-" * 60)

    # 使用一个不存在的report_id来避免与现有数据冲突
    test_report_id = 99999

    # 清理测试数据
    cur.execute("DELETE FROM lims_report_signature WHERE report_id = %s", (test_report_id,))
    conn.commit()

    # 第一次插入 - 应成功
    try:
        cur.execute("""
            INSERT INTO lims_report_signature
            (report_id, signature_type, signer_id, signer_name, signature_time, signature_meaning, is_valid, reason, create_time)
            VALUES (%s, 'PREPARE', 99999, 'test_user', NOW(), '报告编制', 1, '第一次签名', NOW())
        """, (test_report_id,))
        conn.commit()
        record("UC-001", "首次签名插入成功", True)
    except pymysql.IntegrityError as e:
        record("UC-001", "首次签名插入成功", False, f"IntegrityError: {e}")
        return

    # 第二次插入相同签名 - 应失败（唯一约束生效）
    try:
        cur.execute("""
            INSERT INTO lims_report_signature
            (report_id, signature_type, signer_id, signer_name, signature_time, signature_meaning, is_valid, reason, create_time)
            VALUES (%s, 'PREPARE', 99999, 'test_user', NOW(), '报告编制', 1, '重复签名', NOW())
        """, (test_report_id,))
        conn.commit()
        record("UC-002", "重复签名应被阻止", False,
               "唯一约束未生效，重复签名插入成功！")
    except pymysql.IntegrityError as e:
        record("UC-002", "重复签名应被阻止", True, f"唯一约束生效: {e}")

    # 清理测试数据
    cur.execute("DELETE FROM lims_report_signature WHERE report_id = %s", (test_report_id,))
    conn.commit()


def test_different_type_same_user(cur, conn):
    """测试2: 同一用户对不同环节签名"""
    print("\n[2] 数据库层: 同一用户对同一报告不同环节签名")
    print("-" * 60)

    test_report_id = 99998

    # 清理
    cur.execute("DELETE FROM lims_report_signature WHERE report_id = %s", (test_report_id,))
    conn.commit()

    # 插入 PREPARE 签名
    try:
        cur.execute("""
            INSERT INTO lims_report_signature
            (report_id, signature_type, signer_id, signer_name, signature_time, signature_meaning, is_valid, reason, create_time)
            VALUES (%s, 'PREPARE', 99998, 'test_user2', NOW(), '报告编制', 1, NULL, NOW())
        """, (test_report_id,))
        conn.commit()
        record("UC-003", "PREPARE签名插入成功", True)
    except pymysql.IntegrityError as e:
        record("UC-003", "PREPARE签名插入成功", False, f"IntegrityError: {e}")
        return

    # 同一用户插入 REVIEW 签名（不同环节）
    try:
        cur.execute("""
            INSERT INTO lims_report_signature
            (report_id, signature_type, signer_id, signer_name, signature_time, signature_meaning, is_valid, reason, create_time)
            VALUES (%s, 'REVIEW', 99998, 'test_user2', NOW(), '报告复核', 1, NULL, NOW())
        """, (test_report_id,))
        conn.commit()
        # 数据库层允许（业务层由四眼原则阻止）
        record("UC-004", "不同环节签名在DB层允许（业务层由四眼原则阻止）", True)
    except pymysql.IntegrityError as e:
        record("UC-004", "不同环节签名在DB层允许", False, f"不应阻止: {e}")

    # 清理
    cur.execute("DELETE FROM lims_report_signature WHERE report_id = %s", (test_report_id,))
    conn.commit()


def test_different_user_same_type(cur, conn):
    """测试3: 不同用户对同一环节签名"""
    print("\n[3] 数据库层: 不同用户对同一报告同一环节签名")
    print("-" * 60)

    test_report_id = 99997

    # 清理
    cur.execute("DELETE FROM lims_report_signature WHERE report_id = %s", (test_report_id,))
    conn.commit()

    # 用户A签PREPARE
    try:
        cur.execute("""
            INSERT INTO lims_report_signature
            (report_id, signature_type, signer_id, signer_name, signature_time, signature_meaning, is_valid, reason, create_time)
            VALUES (%s, 'PREPARE', 99997, 'user_A', NOW(), '报告编制', 1, NULL, NOW())
        """, (test_report_id,))
        conn.commit()
        record("UC-005", "用户A PREPARE签名插入成功", True)
    except pymysql.IntegrityError as e:
        record("UC-005", "用户A PREPARE签名插入成功", False, f"IntegrityError: {e}")
        return

    # 用户B也签PREPARE（同一环节不同人）- 唯一约束阻止
    try:
        cur.execute("""
            INSERT INTO lims_report_signature
            (report_id, signature_type, signer_id, signer_name, signature_time, signature_meaning, is_valid, reason, create_time)
            VALUES (%s, 'PREPARE', 99996, 'user_B', NOW(), '报告编制', 1, NULL, NOW())
        """, (test_report_id,))
        conn.commit()
        record("UC-006", "同一环节不同用户签名应被阻止", False,
               "唯一约束未生效，同一环节可被多人签名！")
    except pymysql.IntegrityError as e:
        record("UC-006", "同一环节不同用户签名应被阻止", True, f"唯一约束生效: {e}")

    # 清理
    cur.execute("DELETE FROM lims_report_signature WHERE report_id = %s", (test_report_id,))
    conn.commit()


def test_invalidate_and_resign(cur, conn):
    """测试4: 作废签名后重新签名（先删后插）"""
    print("\n[4] 数据库层: 作废签名后重新签名（先删后插）")
    print("-" * 60)

    test_report_id = 99996

    # 清理
    cur.execute("DELETE FROM lims_report_signature WHERE report_id = %s", (test_report_id,))
    conn.commit()

    # 插入签名
    cur.execute("""
        INSERT INTO lims_report_signature
        (report_id, signature_type, signer_id, signer_name, signature_time, signature_meaning, is_valid, reason, create_time)
        VALUES (%s, 'REVIEW', 99995, 'reviewer', NOW(), '报告复核', 1, NULL, NOW())
    """, (test_report_id,))
    conn.commit()
    sig_id = cur.lastrowid
    record("UC-007", "签名插入成功", True)

    # 作废签名（软删除 - UPDATE is_valid=0）
    cur.execute("UPDATE lims_report_signature SET is_valid = 0 WHERE signature_id = %s", (sig_id,))
    conn.commit()
    record("UC-008", "签名作废成功（软删除）", True)

    # 直接重新INSERT会因唯一约束失败（旧记录仍存在）
    try:
        cur.execute("""
            INSERT INTO lims_report_signature
            (report_id, signature_type, signer_id, signer_name, signature_time, signature_meaning, is_valid, reason, create_time)
            VALUES (%s, 'REVIEW', 99995, 'reviewer', NOW(), '报告复核（重新签名）', 1, '退回后重新签名', NOW())
        """, (test_report_id,))
        conn.commit()
        record("UC-009a", "软删除后直接INSERT应失败", False,
               "唯一约束未生效，软删除后可重复插入")
    except pymysql.IntegrityError as e:
        record("UC-009a", "软删除后直接INSERT被唯一约束阻止", True,
               f"符合预期: {e}")

    # 正确做法：先DELETE旧记录，再INSERT新记录（Service层先删后插逻辑）
    cur.execute("DELETE FROM lims_report_signature WHERE report_id = %s AND signature_type = 'REVIEW'", (test_report_id,))
    conn.commit()
    try:
        cur.execute("""
            INSERT INTO lims_report_signature
            (report_id, signature_type, signer_id, signer_name, signature_time, signature_meaning, is_valid, reason, create_time)
            VALUES (%s, 'REVIEW', 99995, 'reviewer', NOW(), '报告复核（重新签名）', 1, '退回后重新签名', NOW())
        """, (test_report_id,))
        conn.commit()
        record("UC-009b", "先删后插重新签名成功", True)
    except pymysql.IntegrityError as e:
        record("UC-009b", "先删后插重新签名应允许", False, f"不应阻止: {e}")

    # 清理
    cur.execute("DELETE FROM lims_report_signature WHERE report_id = %s", (test_report_id,))
    conn.commit()


def test_api_duplicate_signature():
    """测试5: 通过API重复签名"""
    print("\n[5] API层: 同一用户重复执行同一审批操作")
    print("-" * 60)

    try:
        import requests
    except ImportError:
        record("UC-010", "API测试", False, "requests库不可用")
        return

    # 登录
    try:
        r = requests.post(f"{BASE_URL}/login",
            json={"username": "lims_prep", "password": "admin123"}, timeout=10)
        if r.status_code != 200 or r.json().get("code") != 200:
            record("UC-010", "登录lims_prep", False, f"登录失败: {r.text[:100]}")
            return
        token = r.json()["token"]
        headers = {"Authorization": f"Bearer {token}"}
    except Exception as e:
        record("UC-010", "登录lims_prep", False, str(e)[:100])
        return

    # 创建一个新报告
    try:
        r = requests.post(f"{BASE_URL}/lims/report/test/createFromOrder",
            headers=headers,
            params={"testOrderId": 15, "templateId": 1, "reportType": "COA"}, timeout=10)
        if r.status_code != 200 or r.json().get("code") != 200:
            record("UC-011", "创建测试报告", False, f"创建失败: {r.json().get('msg', '')[:80]}")
            return
        report_id = r.json().get("data")
        record("UC-011", "创建测试报告", True, f"report_id={report_id}")
    except Exception as e:
        record("UC-011", "创建测试报告", False, str(e)[:100])
        return

    # 生成报告（第一次PREPARE签名）
    try:
        r = requests.put(f"{BASE_URL}/lims/report/test/generate/{report_id}",
            headers=headers, timeout=10)
        if r.status_code == 200 and r.json().get("code") == 200:
            record("UC-012", "首次生成报告（PREPARE签名）", True)
        else:
            record("UC-012", "首次生成报告", False, r.json().get("msg", "")[:80])
            return
    except Exception as e:
        record("UC-012", "首次生成报告", False, str(e)[:100])
        return

    # 再次生成（重复PREPARE签名） - 应被阻止
    try:
        r = requests.put(f"{BASE_URL}/lims/report/test/generate/{report_id}",
            headers=headers, timeout=10)
        resp = r.json() if r.status_code == 200 else {}
        # 期望：业务层应阻止重复生成
        blocked = resp.get("code") != 200
        record("UC-013", "重复生成报告应被阻止", blocked,
               f"msg={resp.get('msg', '')[:80]}" if not blocked else "业务层已阻止")
    except Exception as e:
        record("UC-013", "重复生成报告测试", False, str(e)[:100])


def add_unique_constraint_if_missing(cur, conn):
    """如果缺少唯一约束，添加它"""
    print("\n[6] 唯一约束检查与修复")
    print("-" * 60)

    non_pk = check_unique_constraint_exists(cur)
    if non_pk:
        record("UC-014", "唯一约束已存在", True, f"约束: {[idx[2] for idx in non_pk]}")
        return

    record("UC-014", "唯一约束不存在", False, "lims_report_signature 表无唯一约束，存在合规风险")

    # 添加唯一约束：同一报告同一环节只能有一条有效签名
    # 使用条件唯一索引（MySQL 8.0+ 支持 functional key parts，但更安全的方式是添加普通唯一索引+业务逻辑）
    # 方案：对 (report_id, signature_type) 添加唯一索引，作废时需要先删除旧记录或使用软删除
    # 但由于作废后需要重新签名，我们使用 (report_id, signature_type, is_valid) 组合
    # 注意：MySQL不支持条件唯一索引，所以用 (report_id, signature_type) 唯一索引
    # 作废签名时需要 DELETE 而非 UPDATE is_valid=0

    print("\n  分析唯一约束方案：")
    print("  方案A: UNIQUE(report_id, signature_type) - 每个环节只能一条签名记录")
    print("         作废时需DELETE而非软删除，重新签名时重新INSERT")
    print("  方案B: UNIQUE(report_id, signature_type, signer_id) - 同一人同环节只能签一次")
    print("         允许同一环节换人签名（如退回后换人复核）")
    print("  方案C: 业务层校验（不添加DB约束）- 灵活但依赖代码正确性")

    # 检查当前是否有同一报告同一环节多条有效签名
    cur.execute("""
        SELECT report_id, signature_type, COUNT(*) as cnt
        FROM lims_report_signature
        WHERE is_valid = 1
        GROUP BY report_id, signature_type
        HAVING cnt > 1
    """)
    duplicates = cur.fetchall()
    if duplicates:
        print(f"\n  [WARN] 发现 {len(duplicates)} 组重复有效签名：")
        for d in duplicates:
            print(f"    report_id={d[0]}, type={d[1]}, count={d[2]}")
    else:
        print("\n  当前数据无重复有效签名，可以安全添加唯一约束")


def main():
    print("=" * 70)
    print("LIMS 电子签名唯一约束冲突测试")
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 70)

    conn = pymysql.connect(**DB_CONFIG)
    cur = conn.cursor()

    # 先检查当前约束状态
    print("\n[0] 当前约束状态检查")
    print("-" * 60)
    non_pk = check_unique_constraint_exists(cur)
    if non_pk:
        print(f"  唯一约束: {[idx[2] for idx in non_pk]}")
    else:
        print("  唯一约束: 无（仅PRIMARY KEY）")

    # 查看当前签名数据概况
    cur.execute("""
        SELECT signature_type, COUNT(*) as cnt
        FROM lims_report_signature
        WHERE is_valid = 1
        GROUP BY signature_type
        ORDER BY signature_type
    """)
    print("  当前有效签名分布:")
    for row in cur.fetchall():
        print(f"    {row[0]}: {row[1]}条")

    # 执行测试
    test_duplicate_signature_db(cur, conn)
    test_different_type_same_user(cur, conn)
    test_different_user_same_type(cur, conn)
    test_invalidate_and_resign(cur, conn)
    test_api_duplicate_signature()
    add_unique_constraint_if_missing(cur, conn)

    # 汇总
    print("\n" + "=" * 70)
    print("TEST SUMMARY")
    print("=" * 70)
    total = len(results)
    passed = sum(1 for r in results if r[2])
    failed = total - passed
    print(f"  Total: {total}, Passed: {passed}, Failed: {failed}")

    if failed > 0:
        print("\n  FAILED TESTS:")
        for test_id, desc, ok, detail in results:
            if not ok:
                print(f"    [{test_id}] {desc}")
                if detail:
                    print(f"           {detail}")

    # 合规建议
    print("\n" + "=" * 70)
    print("GMP合规建议")
    print("=" * 70)
    has_unique = any(r[0] == "UC-014" and r[2] for r in results)
    if not has_unique:
        print("""
  [风险] lims_report_signature 表缺少唯一约束
  [影响] 21 CFR Part 11 / GMP合规要求电子签名不可被重复创建
  [建议] 添加唯一约束，推荐方案：

    ALTER TABLE lims_report_signature
      ADD UNIQUE INDEX uk_report_signature_type (report_id, signature_type);

  [注意] 添加前需确认：
    1. 同一报告同一环节不存在多条有效签名
    2. 作废签名逻辑改为DELETE而非UPDATE is_valid=0
       或改用 (report_id, signature_type) 唯一索引 + 作废时DELETE旧记录再INSERT新记录
    3. Service层在插入签名前先检查是否已存在有效签名
""")

    cur.close()
    conn.close()


if __name__ == "__main__":
    main()
