import requests
import json

BASE_URL = "http://localhost:8088"

# Login
r = requests.post(f"{BASE_URL}/login", json={"username": "admin", "password": "admin123"}, timeout=10)
print("Login response:", json.dumps(r.json(), indent=2, ensure_ascii=False))

# Try with captcha bypass
r2 = requests.post(f"{BASE_URL}/login", json={"username": "admin", "password": "admin123", "code": "", "uuid": ""}, timeout=10)
print("\nLogin with empty captcha:", json.dumps(r2.json(), indent=2, ensure_ascii=False))
