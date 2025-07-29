import os
import argparse
import math
import re
import shutil
from pathlib import Path

def clean_filename(filename):
    """清理文件名，移除特殊字符"""
    # 移除无效字符
    clean = re.sub(r'[\\/*?:"<>|]', "", filename)
    # 替换空格为下划线
    clean = clean.replace(" ", "_")
    return clean

def split_file(input_file, output_dir, lines_per_file=100, encoding="utf-8"):
    """将单个文件分割成多个小文件，使用_编号.txt格式"""
    # 确保输出目录存在
    os.makedirs(output_dir, exist_ok=True)

    # 获取文件名和扩展名
    input_path = Path(input_file)
    base_name = clean_filename(input_path.stem)
    orig_ext = input_path.suffix.lower()

    # 读取文件内容
    try:
        with open(input_file, 'r', encoding=encoding) as f:
            lines = f.readlines()
    except UnicodeDecodeError:
        # 尝试其他常见编码
        for enc in ['latin1', 'cp1252', 'gbk', 'gb2312']:
            try:
                with open(input_file, 'r', encoding=enc) as f:
                    lines = f.readlines()
                break
            except:
                continue

    # 计算需要分割的文件数量
    num_files = math.ceil(len(lines) / lines_per_file) if lines else 0

    # 创建原始文件的副本（使用原始名称）
    backup_path = os.path.join(output_dir, f"{base_name}{orig_ext}")
    shutil.copy2(input_file, backup_path)

    # 分割文件
    for i in range(num_files):
        # 计算当前分片的起始和结束位置
        start = i * lines_per_file
        end = min((i + 1) * lines_per_file, len(lines))

        # 创建新文件名 (统一使用.txt扩展名)
        output_filename = f"{base_name}_{i+1}.txt"
        output_path = os.path.join(output_dir, output_filename)

        # 写入新文件
        with open(output_path, 'w', encoding='utf-8') as f_out:
            f_out.writelines(lines[start:end])

    return num_files

def process_current_directory(lines_per_file=100, output_subdir="split_files"):
    """处理当前目录中的所有文件"""
    # 获取当前工作目录
    current_dir = os.getcwd()

    # 创建输出子目录
    output_dir = os.path.join(current_dir, output_subdir)
    os.makedirs(output_dir, exist_ok=True)

    # 处理每个文件
    processed_files = 0
    for filename in os.listdir(current_dir):
        input_path = os.path.join(current_dir, filename)

        # 跳过目录和脚本自身
        if not os.path.isfile(input_path) or filename == os.path.basename(__file__):
            continue

        # 处理文件
        num_files = split_file(input_path, output_dir, lines_per_file)
        print(f"已分割文件: {filename} -> {num_files} 个分片")
        processed_files += 1

    return processed_files, output_dir

def main():
    # 设置命令行参数
    parser = argparse.ArgumentParser(description='按行分割当前目录下的文件，输出为_编号.txt格式')
    parser.add_argument('-l', '--lines', type=int, default=100,
                        help='每个分片文件的行数 (默认: 100)')
    parser.add_argument('-o', '--output', default="split_files",
                        help='输出子目录名称 (默认: split_files)')

    args = parser.parse_args()

    # 执行分割
    print(f"开始处理当前目录: {os.getcwd()}")
    print(f"每个分片行数: {args.lines}")
    print(f"输出目录: {args.output}")
    print("-" * 50)

    processed_count, output_dir = process_current_directory(
        lines_per_file=args.lines,
        output_subdir=args.output
    )

    print("\n" + "=" * 50)
    print(f"处理完成！共处理了 {processed_count} 个文件")
    print(f"输出文件格式: 文件名_编号.txt")
    print(f"文件保存在: {output_dir}")

if __name__ == "__main__":
    main()