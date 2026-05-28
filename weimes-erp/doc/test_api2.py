import urllib.request, json

# 测试登录
for pwd in ['admin123', '123456', 'admin', 'Admin123', 'ry123456']:
    login_data = json.dumps({'username':'admin','password':pwd}).encode()
    req = urllib.request.Request('http://localhost:8088/login', data=login_data, headers={'Content-Type':'application/json'})
    try:
        resp = urllib.request.urlopen(req)
        result = json.loads(resp.read())
        if result.get('code') == 200:
            print(f'登录成功! password={pwd}')
            token = result.get('token')

            # 测试LIMS API
            for api in ['/lims/sample/list', '/lims/oos/investigation/list', '/lims/instrument/list', '/lims/reagent/list', '/lims/test/order/list']:
                req2 = urllib.request.Request('http://localhost:8088' + api, headers={'Authorization':'Bearer ' + token})
                try:
                    resp2 = urllib.request.urlopen(req2)
                    data = resp2.read().decode()[:200]
                    print(f'  {api}: OK - {data}')
                except urllib.error.HTTPError as e2:
                    body = e2.read().decode()[:200]
                    print(f'  {api}: HTTP {e2.code} - {body}')
                except Exception as e2:
                    print(f'  {api}: ERROR - {e2}')
            break
        else:
            print(f'password={pwd}: {result.get("msg")}')
    except Exception as e:
        print(f'password={pwd}: {e}')
