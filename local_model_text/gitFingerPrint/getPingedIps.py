import os
import time
import subprocess

MANUFACTURERS_DIR = '../manufacturers_ip'
OUTPUT_DIR = '../canPing0708'
DELAY_SECONDS = 0.5  # 每次ping之间的延迟，防止被ban

# 确保输出目录存在
os.makedirs(OUTPUT_DIR, exist_ok=True)

def is_ip_reachable(ip):
    # Windows下ping一次，timeout 1秒
    try:
        result = subprocess.run(
            ['ping', '-n', '1', '-w', '1000', ip],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL
        )
        return result.returncode == 0
    except Exception:
        return False

def process_file(input_path, output_path):
    reachable_ips = []
    with open(input_path, 'r', encoding='utf-8', errors='ignore') as f:
        for line in f:
            line = line.strip()
            if not line or ',' not in line:
                continue
            ip = line.split(',')[-1].strip()
            if not ip:
                continue
            # 跳过IPv6
            if ':' in ip:
                continue
            if is_ip_reachable(ip):
                reachable_ips.append(ip)
            time.sleep(DELAY_SECONDS)
    if reachable_ips:
        with open(output_path, 'w', encoding='utf-8') as out:
            for ip in reachable_ips:
                out.write(ip + '\n')

def main():
    for filename in os.listdir(MANUFACTURERS_DIR):
        if not filename.endswith('.txt'):
            continue
        input_path = os.path.join(MANUFACTURERS_DIR, filename)
        output_path = os.path.join(OUTPUT_DIR, filename)
        print(f'Processing {filename}...')
        process_file(input_path, output_path)
        print(f'Finished {filename}')

if __name__ == '__main__':
    main()
