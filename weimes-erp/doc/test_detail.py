import requests
import json

BASE_URL = "http://localhost:8088"

# Login
r = requests.post(f"{BASE_URL}/login", json={"username": "admin", "password": "admin123"}, timeout=10)
token = r.json()["token"]
h = {"Authorization": f"Bearer {token}"}

# Test template list with detailed response
r = requests.get(f"{BASE_URL}/lims/report/template/list", headers=h, timeout=10)
print("Template list response:")
print(f"  Status: {r.status_code}")
print(f"  Body: {json.dumps(r.json(), indent=2, ensure_ascii=False)[:500]}")

# Test existing working endpoint
r = requests.get(f"{BASE_URL}/lims/sample/list", headers=h, timeout=10)
print(f"\nSample list: code={r.json().get('code')}, total={r.json().get('total', 'N/A')}")

# Test report list
r = requests.get(f"{BASE_URL}/lims/report/test/list", headers=h, timeout=10)
print(f"\nReport list response:")
print(f"  Body: {json.dumps(r.json(), indent=2, ensure_ascii=False)[:500]}")
