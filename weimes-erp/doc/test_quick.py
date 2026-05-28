import requests, json
BASE_URL = "http://localhost:8088"
r = requests.post(f"{BASE_URL}/login", json={"username": "admin", "password": "admin123"}, timeout=10)
token = r.json()["token"]
h = {"Authorization": f"Bearer {token}"}

# Test template list
r = requests.get(f"{BASE_URL}/lims/report/template/list", headers=h, timeout=10)
print(f"Template list: code={r.json().get('code')}, msg={r.json().get('msg','')[:80]}")

# Test validation rule list
r = requests.get(f"{BASE_URL}/lims/report/validation/list", headers=h, timeout=10)
print(f"Validation list: code={r.json().get('code')}, msg={r.json().get('msg','')[:80]}")

# Test acquisition list
r = requests.get(f"{BASE_URL}/lims/instrument/acquisition/list", headers=h, timeout=10)
print(f"Acquisition list: code={r.json().get('code')}, msg={r.json().get('msg','')[:80]}")

# Test report list
r = requests.get(f"{BASE_URL}/lims/report/test/list", headers=h, timeout=10)
print(f"Report list: code={r.json().get('code')}, msg={r.json().get('msg','')[:80]}")
