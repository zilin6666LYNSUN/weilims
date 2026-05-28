import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# BCrypt hash for "Test1234" - same for all test users
password_hash = "$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2"

# Create 4 test users for GMP four-eyes principle testing
users = [
    (200, "lims_prep",  "LIMS编制员", "报告编制角色"),
    (201, "lims_rev",   "LIMS复核员", "报告复核角色"),
    (202, "lims_appr",  "LIMS批准员", "报告批准角色"),
    (203, "lims_rel",   "LIMS放行员", "报告放行角色"),
]

for user_id, username, nick_name, remark in users:
    try:
        cur.execute(
            "INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, password, status, create_by, create_time, remark) "
            "VALUES (%s, 100, %s, %s, %s, '0', 'admin', NOW(), %s)",
            (user_id, username, nick_name, password_hash, remark)
        )
        print(f"[OK] Created user: {username} ({nick_name})")
    except Exception as e:
        if 'Duplicate entry' in str(e):
            # Update password in case it was created before
            cur.execute("UPDATE sys_user SET password=%s WHERE user_name=%s", (password_hash, username))
            print(f"[SKIP/UPDATE] User exists, password reset: {username}")
        else:
            print(f"[FAIL] {username}: {e}")

conn.commit()

# Assign all LIMS report permissions to these users via role
# First check if a LIMS test role exists
cur.execute("SELECT role_id, role_key FROM sys_role WHERE role_key='lims_test'")
role = cur.fetchone()
if role:
    role_id = role[0]
    print(f"\nUsing existing role: {role_id} ({role[1]})")
else:
    # Create a LIMS test role with all report permissions
    cur.execute(
        "INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, status, create_by, create_time) "
        "VALUES (200, 'LIMS测试角色', 'lims_test', 99, '1', '0', 'admin', NOW())"
    )
    role_id = 200
    print(f"\n[OK] Created role: lims_test (id={role_id})")

    # Assign all report-related menu permissions to this role
    menu_ids = [
        3100, 3101, 3102, 3103, 3104,  # menus
        3111, 3112, 3113, 3114, 3115,  # template buttons
        3121, 3122, 3123, 3124, 3125, 3126, 3127, 3128,  # report buttons
        3131, 3132, 3133, 3134, 3135,  # validation buttons
        3141,  # acquisition button
    ]
    for mid in menu_ids:
        try:
            cur.execute("INSERT INTO sys_role_menu (role_id, menu_id) VALUES (%s, %s)", (role_id, mid))
        except:
            pass
    print(f"[OK] Assigned {len(menu_ids)} menu permissions")

# Assign role to all 4 test users
for user_id, username, _, _ in users:
    try:
        cur.execute("INSERT INTO sys_user_role (user_id, role_id) VALUES (%s, %s)", (user_id, role_id))
        print(f"[OK] Assigned role to {username}")
    except:
        print(f"[SKIP] Role already assigned to {username}")

conn.commit()
cur.close()
conn.close()
print("\nTest users ready!")
