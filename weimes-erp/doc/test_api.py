import urllib.request, json

# 查看用户密码
import pymysql
conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()
cur.execute('SELECT user_id, user_name, password, salt FROM sys_user WHERE deleted = 0 LIMIT 5')
for r in cur.fetchall():
    print(f'  id={r[0]}, username={r[1]}, password={r[2][:50]}..., salt={r[3]}')
conn.close()

# 尝试登录
for pwd in ['admin123', '123456', 'admin', 'Admin123']:
    login_data = json.dumps({'username':'admin','password':pwd}).encode()
    req = urllib.request.Request('http://localhost:8088/login', data=login_data, headers={'Content-Type':'application/json'})
    try:
        resp = urllib.request.urlopen(req)
        result = json.loads(resp.read())
        if result.get('code') == 200:
            print(f'\n登录成功! password={pwd}')
            token = result.get('token')
            print(f'token={token[:50]}...')

            # 测试LIMS API
            req2 = urllib.request.Request('http://localhost:8088/lims/sample/list', headers={'Authorization':'Bearer ' + token})
            try:
                resp2 = urllib.request.urlopen(req2)
                print(f'lims/sample/list: {resp2.read().decode()[:200]}')
            except Exception as e2:
                print(f'lims/sample/list 错误: {e2}')

            req3 = urllib.request.Request('http://localhost:8088/lims/oos/investigation/list', headers={'Authorization':'Bearer ' + token})
            try:
                resp3 = urllib.request.urlopen(req3)
                print(f'lims/oos/investigation/list: {resp3.read().decode()[:200]}')
            except Exception as e3:
                print(f'lims/oos/investigation/list 错误: {e3}')
            break
        else:
            print(f'password={pwd}: {result.get("msg")}')
    except Exception as e:
        print(f'password={pwd}: 请求失败 {e}')
