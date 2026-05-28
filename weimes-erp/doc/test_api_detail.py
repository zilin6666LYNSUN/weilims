import urllib.request, json

# 登录
login_data = json.dumps({'username':'admin','password':'123456'}).encode()
req = urllib.request.Request('http://localhost:8088/login', data=login_data, headers={'Content-Type':'application/json'})
resp = urllib.request.urlopen(req)
result = json.loads(resp.read())
token = result.get('token')

# 详细测试失败API
fail_apis = [
    '/lims/test/method/list',
    '/lims/test/template/list',
    '/lims/instrument/type/list',
    '/lims/instrument/maintenance/list',
    '/lims/instrument/reservation/list',
    '/lims/reagent/standard/list',
    '/lims/reagent/culture/list',
    '/lims/sample/retain/list',
    '/lims/sample/dispose/list',
    '/lims/sample/testitem/list/1',
    '/lims/test/spec/list',
    '/lims/test/result/order/1',
    '/lims/oos/investigation/list',
    '/lims/change/control/list',
    '/lims/stability/list',
    '/lims/audit-trail/list',
]

for api in fail_apis:
    req2 = urllib.request.Request('http://localhost:8088' + api, headers={'Authorization':'Bearer ' + token})
    try:
        resp2 = urllib.request.urlopen(req2)
        raw = resp2.read().decode()
        print(f'\n{api}:')
        print(f'  响应长度: {len(raw)}')
        print(f'  内容: {raw[:300]}')
    except urllib.error.HTTPError as e2:
        body = e2.read().decode()[:300]
        print(f'\n{api}: HTTP {e2.code}')
        print(f'  内容: {body}')
    except Exception as e2:
        print(f'\n{api}: ERROR {e2}')
