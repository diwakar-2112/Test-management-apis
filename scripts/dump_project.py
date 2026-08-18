#!/usr/bin/env python3
"""Create a single text dump of the project's current code files.

The script reads files from the current working tree, so modified and
untracked files are included as long as they are not excluded. It streams
file contents into the dump so the output can grow as large as needed without
loading the whole project into memory at once.
"""

from __future__ import annotations

import argparse
import os
import subprocess
from datetime import datetime, timezone
from pathlib import Path


DEFAULT_OUTPUT = "scripts/project_code_dump.txt"

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


def should_include(path: Path, root: Path, output_path: Path) -> bool:
    if path.resolve() == output_path.resolve():
        return False
    if not path.is_file():
        return False
    if path.name in EXCLUDED_FILE_NAMES:
        return False

    relative = path.relative_to(root)
    if any(part in EXCLUDED_DIRS for part in relative.parts[:-1]):
        return False

    return not is_probably_binary(path)


def iter_files(root: Path, output_path: Path) -> list[Path]:
    files: list[Path] = []
    for current_root, dir_names, file_names in os.walk(root):
        current_path = Path(current_root)
        dir_names[:] = sorted(name for name in dir_names if name not in EXCLUDED_DIRS)

        for file_name in sorted(file_names):
            path = current_path / file_name
            if should_include(path, root, output_path):
                files.append(path)

    return sorted(files, key=lambda item: item.relative_to(root).as_posix().lower())


def append_file_text(dump, path: Path) -> None:
    with path.open("r", encoding="utf-8-sig", errors="replace", newline="") as source:
        for chunk in iter(lambda: source.read(1024 * 1024), ""):
            dump.write(chunk)


def write_dump(root: Path, output_path: Path) -> int:
    files = iter_files(root, output_path)
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
            append_file_text(dump, path)
            dump.write("\n```\n\n")

    return len(files)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Dump all current non-binary project text into one file."
    )
    parser.add_argument(
        "-o",
        "--output",
        default=DEFAULT_OUTPUT,
        help=f"Output dump file path. Default: {DEFAULT_OUTPUT}",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    root = Path.cwd().resolve()
    output_path = (root / args.output).resolve()
    output_path.parent.mkdir(parents=True, exist_ok=True)

    count = write_dump(root, output_path)
    print(f"Wrote {count} files to {output_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
