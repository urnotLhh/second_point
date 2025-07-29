import os
import json
import re

SRC_DIR = os.path.join(os.path.dirname(__file__), 'banners0708')
DST_DIR = os.path.join(os.path.dirname(__file__), 'cleand_banners')

os.makedirs(DST_DIR, exist_ok=True)

HTTP_STATUS_TEXT = {
    200: 'OK',
    301: 'Moved Permanently',
    302: 'Found',
    400: 'Bad Request',
    401: 'Unauthorized',
    403: 'Forbidden',
    404: 'Not Found',
    500: 'Internal Server Error',
    502: 'Bad Gateway',
    503: 'Service Unavailable',
    504: 'Gateway Timeout',
    # 可补充常见状态码
}

def parse_filename(filename):
    base = os.path.basename(filename)
    m = re.match(r'([a-zA-Z0-9]+)_([a-zA-Z0-9]+)_http_banner\.json$', base)
    if m:
        device_type, vendor = m.group(1), m.group(2)
        return device_type, vendor
    m = re.match(r'([a-zA-Z0-9]+)_http_banner\.json$', base)
    if m:
        device_type = m.group(1)
        return device_type, None
    return None, None

def headers_to_str(headers):
    if isinstance(headers, dict):
        return '\n'.join(f"{k}: {v}" for k, v in headers.items())
    return str(headers)

def get_status_text(status_code):
    try:
        code = int(status_code)
        return HTTP_STATUS_TEXT.get(code, str(code))
    except Exception:
        return str(status_code)

def process_file(src_path, dst_path):
    device_type, vendor = parse_filename(src_path)
    with open(src_path, 'r', encoding='utf-8') as fin, open(dst_path, 'w', encoding='utf-8') as fout:
        try:
            data = json.load(fin)
        except Exception as e:
            print(f"[ERROR] 解析JSON失败: {src_path} {e}")
            return
        for ip, ports in data.items():
            for port_key, port_info in ports.items():
                if not isinstance(port_info, dict):
                    continue
                if not port_info.get('success', False):
                    continue
                status_code = port_info.get('status_code')
                if not status_code:
                    continue
                headers = port_info.get('headers', {})
                if not headers:
                    continue
                # 跳过Content-Length: 0的
                if isinstance(headers, dict):
                    cl = headers.get('Content-Length')
                    if cl is not None and str(cl).strip() == '0':
                        continue
                content = port_info.get('content', '')
                status_text = get_status_text(status_code)
                status_line = f"HTTP/1.1 {status_code} {status_text}"
                headers_str = headers_to_str(headers)
                banner = status_line
                if headers_str:
                    banner += f"\n{headers_str}"
                if content:
                    banner += f"\n{content}"
                port_match = re.match(r'port_(\d+)', port_key)
                port = port_match.group(1) if port_match else ''
                protocol = 'https' if port == '443' else 'http'
                out_obj = {
                    'banner': banner,
                    'port': port,
                    'protocol': protocol,
                    'ip': ip
                }
                if device_type:
                    out_obj['device_type'] = device_type
                if vendor:
                    out_obj['vendor'] = vendor
                fout.write(json.dumps(out_obj, ensure_ascii=False) + '\n')
    print(f"[OK] {src_path} -> {dst_path}")

def main():
    for fname in os.listdir(SRC_DIR):
        if fname.endswith('http_banner.json'):
            src_path = os.path.join(SRC_DIR, fname)
            dst_path = os.path.join(DST_DIR, fname)
            process_file(src_path, dst_path)

if __name__ == '__main__':
    main()
