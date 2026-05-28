import requests
import json

BASE_URL = "http://localhost:8088"

# Login
r = requests.post(f"{BASE_URL}/login", json={"username": "admin", "password": "admin123"}, timeout=10)
print("Login:", r.status_code, r.json().get("code"))
token = r.json().get("token")
h = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

# Test various paths
paths = [
    "/lims/report/template/list",
    "/lims/report/test/list",
    "/lims/report/validation/list",
    "/lims/instrument/acquisition/list",
    "/lims/sample/list",
    "/system/user/list",
]

for p in paths:
    r = requests.get(f"{BASE_URL}{p}", headers=h, timeout=10)
    data = r.json() if r.status_code == 200 else {}
    code = data.get("code", "N/A")
    msg = data.get("msg", "")[:60] if data.get("msg") else ""
    print(f"  {p}: status={r.status_code}, code={code}, msg={msg}")
