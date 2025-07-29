import os
import time
import subprocess
import requests
import json
import platform
import urllib3
from datetime import datetime
import re

# 禁用SSL警告
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# 配置参数
MANUFACTURERS_DIR = '../manufacturers_ip'
OUTPUT_DIR = '../getHttpBanner0708'
UNREACHABLE_DIR = '../getHttpBanner0708/unreachable'
DELAY_SECONDS = 0.5  # 每次请求之间的延迟
HTTP_TIMEOUT = 10     # HTTP请求超时时间
PING_TIMEOUT = 5     # Ping超时时间

# 确保输出目录存在
os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(UNREACHABLE_DIR, exist_ok=True)

def is_ip_reachable(ip):
    """测试IP是否可达 (Linux兼容)"""
    try:
        cmd = ['ping', '-c', '1', '-W', str(PING_TIMEOUT), ip]

        result = subprocess.run(
            cmd,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            timeout=PING_TIMEOUT + 0.5  # 添加额外超时缓冲
        )
        return result.returncode == 0
    except (subprocess.TimeoutExpired, subprocess.SubprocessError) as e:
        print(f"    Ping error for {ip}: {str(e)}")
        return False
    except Exception as e:
        print(f"    Unexpected ping error for {ip}: {str(e)}")
        return False

def test_http_port(ip, port, host_header=None):
    """增强版HTTP端口测试"""
    try:
        protocol = 'https' if port == 443 else 'http'
        url = f"{protocol}://{ip}:{port}"

        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
            'Accept': '*/*',  # 接受所有内容类型
            'Connection': 'close'  # 每次请求后关闭连接
        }

        # 设置Host头（如果提供）
        if host_header:
            headers['Host'] = host_header

        # 分别设置连接和读取超时
        timeout = (3, HTTP_TIMEOUT)  # 连接超时0.3秒，读取超时HTTP_TIMEOUT秒

        response = requests.get(
            url,
            headers=headers,
            timeout=timeout,
            verify=False,
            allow_redirects=False  # 禁用自动重定向
        )

        print(response)

        # 收集重定向信息（即使禁用了自动重定向）
        redirect_history = [
            {
                'url': resp.url,
                'status_code': resp.status_code,
                'headers': dict(resp.headers)
            } for resp in response.history
        ]

        return {
            'status_code': response.status_code,
            'headers': dict(response.headers),
            'content': response.text[:2000],
            'url': url,
            'redirect_history': redirect_history,
            'success': True
        }

    except requests.exceptions.Timeout as e:
        error_type = '连接超时' if isinstance(e, requests.exceptions.ConnectTimeout) else '读取超时'
        return {'error': error_type, 'url': url, 'success': False}

    except requests.exceptions.SSLError as e:
        return {'error': f'SSL错误: {str(e)}', 'url': url, 'success': False}

    except requests.exceptions.ConnectionError as e:
        error_class = type(e).__name__
        return {'error': f'连接失败 ({error_class})', 'url': url, 'success': False}

    except requests.exceptions.RequestException as e:
        return {'error': f'请求异常: {str(e)}', 'url': url, 'success': False}
def is_valid_ip(ip):
    """验证IP地址格式"""
    pattern = r'^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$'
    return re.match(pattern, ip) is not None

def process_file(input_path, output_dir, unreachable_dir):
    """处理单个IP文件 (优化内存和性能)"""
    filename = os.path.basename(input_path)
    base_name = os.path.splitext(filename)[0]

    reachable_ips = []
    unreachable_ips = []
    http_results = {}

    print(f"处理文件: {filename}")

    try:
        with open(input_path, 'r', encoding='utf-8', errors='ignore') as f:
            lines = f.readlines()
            total_lines = len(lines)

            for line_num, line in enumerate(lines, 1):
                line = line.strip()
                if not line:
                    continue

                # 提取IP地址 (更健壮的方法)
                parts = line.split(',')
                if not parts:
                    continue

                ip_candidate = parts[-1].strip()
                if not ip_candidate or not is_valid_ip(ip_candidate):
                    print(f"  跳过无效IP: {line}")
                    continue

                ip = ip_candidate
                print(f"  测试IP {line_num}/{total_lines}: {ip}")

                if is_ip_reachable(ip):
                    reachable_ips.append(ip)
                    http_results[ip] = {}

                    # 测试80端口
                    print(f"    测试端口 80...")
                    http_results[ip]['port_80'] = test_http_port(ip, 80)
                    time.sleep(DELAY_SECONDS)

                    # 测试443端口
                    print(f"    测试端口 443...")
                    http_results[ip]['port_443'] = test_http_port(ip, 443)
                    time.sleep(DELAY_SECONDS)
                else:
                    unreachable_ips.append(ip)
                    print(f"    IP不可达: {ip}")

                time.sleep(DELAY_SECONDS)

    except Exception as e:
        print(f"  处理文件时出错: {str(e)}")
        # 即使出错也尝试保存已收集的结果

    # 保存结果
    save_results(base_name, http_results, reachable_ips, unreachable_ips, output_dir, unreachable_dir)

    return len(reachable_ips), len(unreachable_ips)

def save_results(base_name, http_results, reachable_ips, unreachable_ips, output_dir, unreachable_dir):
    """保存所有结果 (原子操作)"""
    # 保存HTTP响应结果
    if http_results:
        output_file = os.path.join(output_dir, f"{base_name}_http_banner.json")
        try:
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(http_results, f, indent=2, ensure_ascii=False)
            print(f"  HTTP结果保存到 {output_file}")
        except Exception as e:
            print(f"  保存HTTP结果失败: {str(e)}")

    # 保存可达IP列表
    if reachable_ips:
        reachable_file = os.path.join(output_dir, f"{base_name}_reachable.txt")
        try:
            with open(reachable_file, 'w', encoding='utf-8') as f:
                for ip in reachable_ips:
                    f.write(f"{ip}\n")
            print(f"  可达IP保存到 {reachable_file}")
        except Exception as e:
            print(f"  保存可达IP失败: {str(e)}")

    # 保存不可达IP列表
    if unreachable_ips:
        unreachable_file = os.path.join(unreachable_dir, f"{base_name}_unreachable.txt")
        try:
            with open(unreachable_file, 'w', encoding='utf-8') as f:
                for ip in unreachable_ips:
                    f.write(f"{ip}\n")
            print(f"  不可达IP保存到 {unreachable_file}")
        except Exception as e:
            print(f"  保存不可达IP失败: {str(e)}")

    print(f"  统计: {len(reachable_ips)} 可达, {len(unreachable_ips)} 不可达")

def main():
    """主函数 (添加详细日志)"""
    print("=" * 50)
    print("开始HTTP Banner抓取")
    print(f"时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"操作系统: {platform.system()} {platform.release()}")
    print(f"制造商目录: {MANUFACTURERS_DIR}")
    print(f"输出目录: {OUTPUT_DIR}")
    print(f"请求间隔: {DELAY_SECONDS}秒")
    print(f"HTTP超时: {HTTP_TIMEOUT}秒")
    print(f"Ping超时: {PING_TIMEOUT}秒")
    print("=" * 50)

    processed_count = 0
    total_reachable = 0
    total_unreachable = 0

    try:
        files = [f for f in os.listdir(MANUFACTURERS_DIR) if f.endswith('.txt')]
        total_files = len(files)
        print(f"发现 {total_files} 个待处理文件")

        for i, filename in enumerate(files, 1):
            input_path = os.path.join(MANUFACTURERS_DIR, filename)
            print(f"\n处理文件 ({i}/{total_files}): {filename}")

            try:
                reachable, unreachable = process_file(input_path, OUTPUT_DIR, UNREACHABLE_DIR)
                total_reachable += reachable
                total_unreachable += unreachable
                processed_count += 1
            except Exception as e:
                print(f"  处理文件 {filename} 时出错: {str(e)}")

            print(f"  已完成 {i}/{total_files} 个文件")
            print("-" * 50)
    except Exception as e:
        print(f"获取文件列表失败: {str(e)}")
        return

    # 最终统计
    print("\n" + "=" * 50)
    print("任务完成!")
    print(f"处理文件总数: {processed_count}/{total_files}")
    print(f"检测IP总数: {total_reachable + total_unreachable}")
    print(f"可达IP总数: {total_reachable}")
    print(f"不可达IP总数: {total_unreachable}")
    if total_reachable + total_unreachable > 0:
        success_rate = total_reachable / (total_reachable + total_unreachable) * 100
        print(f"成功率: {success_rate:.2f}%")
    print(f"完成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 50)

if __name__ == '__main__':
    main()