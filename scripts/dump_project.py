#!/usr/bin/env python3
"""Create a single text dump of the project's code files.

The script reads files from the current working tree, so modified and
untracked files are included as long as they are not excluded.
"""

from __future__ import annotations

import argparse
import os
import subprocess
from datetime import datetime, timezone
from pathlib import Path


DEFAULT_OUTPUT = "project_code_dump.txt"

EXCLUDED_DIRS = {
    ".git",
    ".idea",
    ".mvn",
    ".vscode",
    "build",
    "dist",
    "nbbuild",
    "nbdist",
    "target",
    "__pycache__",
}

EXCLUDED_FILE_NAMES = {
    ".DS_Store",
    "Thumbs.db",
    "maven-wrapper.jar",
}

INCLUDED_EXTENSIONS = {
    ".bat",
    ".cmd",
    ".css",
    ".csv",
    ".dockerignore",
    ".env",
    ".example",
    ".gitignore",
    ".gradle",
    ".html",
    ".http",
    ".java",
    ".js",
    ".json",
    ".jsx",
    ".kt",
    ".kts",
    ".md",
    ".properties",
    ".py",
    ".sh",
    ".sql",
    ".ts",
    ".tsx",
    ".txt",
    ".xml",
    ".yaml",
    ".yml",
}

INCLUDED_FILE_NAMES = {
    "Dockerfile",
    "Makefile",
    "mvnw",
}


def run_git(args: list[str], root: Path) -> str:
    try:
        result = subprocess.run(
            ["git", *args],
            cwd=root,
            check=False,
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
        )
    except FileNotFoundError:
        return "git command not available"

    output = (result.stdout + result.stderr).strip()
    return output or "(no output)"


def is_probably_binary(path: Path) -> bool:
    try:
        chunk = path.read_bytes()[:4096]
    except OSError:
        return True
    return b"\0" in chunk


def should_include(path: Path, root: Path, output_path: Path, include_all_text: bool) -> bool:
    if path.resolve() == output_path.resolve():
        return False
    if not path.is_file():
        return False
    if path.name in EXCLUDED_FILE_NAMES:
        return False

    relative = path.relative_to(root)
    if any(part in EXCLUDED_DIRS for part in relative.parts[:-1]):
        return False

    if include_all_text:
        return not is_probably_binary(path)

    return path.name in INCLUDED_FILE_NAMES or path.suffix.lower() in INCLUDED_EXTENSIONS


def iter_files(root: Path, output_path: Path, include_all_text: bool) -> list[Path]:
    files: list[Path] = []
    for current_root, dir_names, file_names in os.walk(root):
        current_path = Path(current_root)
        dir_names[:] = sorted(name for name in dir_names if name not in EXCLUDED_DIRS)

        for file_name in sorted(file_names):
            path = current_path / file_name
            if should_include(path, root, output_path, include_all_text):
                files.append(path)

    return sorted(files, key=lambda item: item.relative_to(root).as_posix().lower())


def read_text(path: Path) -> str:
    raw = path.read_bytes()
    for encoding in ("utf-8", "utf-8-sig", "cp1252"):
        try:
            return raw.decode(encoding)
        except UnicodeDecodeError:
            continue
    return raw.decode("utf-8", errors="replace")


def write_dump(root: Path, output_path: Path, include_all_text: bool) -> int:
    files = iter_files(root, output_path, include_all_text)
    timestamp = datetime.now(timezone.utc).isoformat(timespec="seconds")

    with output_path.open("w", encoding="utf-8", newline="\n") as dump:
        dump.write("# Project Code Dump\n\n")
        dump.write(f"Generated at: {timestamp}\n")
        dump.write(f"Project root: {root}\n")
        dump.write(f"File count: {len(files)}\n\n")

        dump.write("## Git Status\n\n")
        dump.write("```text\n")
        dump.write(run_git(["status", "--short"], root))
        dump.write("\n```\n\n")

        dump.write("## Files\n\n")
        for path in files:
            relative = path.relative_to(root).as_posix()
            dump.write(f"===== FILE: {relative} =====\n")
            dump.write("```")
            suffix = path.suffix.lstrip(".")
            if suffix:
                dump.write(suffix)
            dump.write("\n")
            dump.write(read_text(path).rstrip())
            dump.write("\n```\n\n")

    return len(files)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Dump project code into one text file from the current working tree."
    )
    parser.add_argument(
        "-o",
        "--output",
        default=DEFAULT_OUTPUT,
        help=f"Output dump file path. Default: {DEFAULT_OUTPUT}",
    )
    parser.add_argument(
        "--all-text",
        action="store_true",
        help="Include every non-binary text file except excluded folders/files.",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    root = Path.cwd().resolve()
    output_path = (root / args.output).resolve()
    output_path.parent.mkdir(parents=True, exist_ok=True)

    count = write_dump(root, output_path, args.all_text)
    print(f"Wrote {count} files to {output_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
