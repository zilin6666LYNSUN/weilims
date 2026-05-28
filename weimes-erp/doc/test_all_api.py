import urllib.request, json

# 登录
login_data = json.dumps({'username':'admin','password':'123456'}).encode()
req = urllib.request.Request('http://localhost:8088/login', data=login_data, headers={'Content-Type':'application/json'})
resp = urllib.request.urlopen(req)
result = json.loads(resp.read())
token = result.get('token')
print(f'登录成功, token={token[:30]}...')

# 测试所有LIMS API
apis = [
    '/lims/sample/list',
    '/lims/test/method/list',
    '/lims/test/spec/list',
    '/lims/test/template/list',
    '/lims/test/order/list',
    '/lims/test/result/order/1',
    '/lims/instrument/list',
    '/lims/instrument/type/list',
    '/lims/instrument/calibration/list',
    '/lims/instrument/maintenance/list',
    '/lims/instrument/reservation/list',
    '/lims/reagent/list',
    '/lims/reagent/standard/list',
    '/lims/reagent/culture/list',
    '/lims/reagent/stocklog/list',
    '/lims/oos/investigation/list',
    '/lims/oos/phase/list/1',
    '/lims/deviation/list',
    '/lims/capa/list',
    '/lims/change/control/list',
    '/lims/stability/list',
    '/lims/audit-trail/list',
    '/lims/esign/list',
    '/lims/sample/retain/list',
    '/lims/sample/dispose/list',
    '/lims/sample/testitem/list/1',
]

ok_apis = []
fail_apis = []

for api in apis:
    req2 = urllib.request.Request('http://localhost:8088' + api, headers={'Authorization':'Bearer ' + token})
    try:
        resp2 = urllib.request.urlopen(req2)
        data = resp2.read().decode()[:150]
        result2 = json.loads(data) if data else {}
        code = result2.get('code', '?')
        ok_apis.append(api)
        print(f'  OK  {api} (code={code})')
    except urllib.error.HTTPError as e2:
        body = e2.read().decode()[:100]
        fail_apis.append(api)
        print(f'  FAIL {api} (HTTP {e2.code}: {body})')
    except Exception as e2:
        fail_apis.append(api)
        print(f'  ERR {api} ({e2})')

print(f'\n总计: OK={len(ok_apis)}, FAIL={len(fail_apis)}')
if fail_apis:
    print(f'失败API: {fail_apis}')
