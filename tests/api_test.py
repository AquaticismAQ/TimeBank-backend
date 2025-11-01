"""Simple script to exercise TimeBank HTTP APIs."""

from __future__ import annotations

import argparse
import json
import sys
import time
from typing import Any, Dict

import requests

DEFAULT_BASE_URL = "http://localhost:8080"


def call_health(base_url: str) -> Dict[str, Any]:
    """Invoke the /health endpoint and return the parsed JSON payload."""
    url = f"{base_url.rstrip('/')}/health"
    response = requests.post(url, timeout=5)
    response.raise_for_status()
    try:
        payload = response.json()
    except json.JSONDecodeError as exc:
        raise ValueError(f"Response from {url} was not valid JSON: {response.text}") from exc
    return payload


def run_tests(base_url: str) -> int:
    """Execute API calls and print results. Returns process exit code."""
    try:
        health = call_health(base_url)
    except Exception as exc:
        print(f"[FAIL] /health call failed: {exc}", file=sys.stderr)
        return 1

    status = health.get("status")
    message = health.get("message")
    data = health.get("data")

    if status != "success" or data != "OK":
        print(
            "[FAIL] Unexpected /health payload: "
            f"status={status!r}, message={message!r}, data={data!r}",
            file=sys.stderr,
        )
        return 1

    print("[PASS] /health endpoint returned expected payload.")
    print(json.dumps(health, indent=2))
    return 0


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Test TimeBank APIs.")
    parser.add_argument(
        "--base-url",
        default=DEFAULT_BASE_URL,
        help=f"Base URL for the API (default: {DEFAULT_BASE_URL}).",
    )
    return parser.parse_args()


if __name__ == "__main__":
    print("Waiting 10 seconds for server")
    time.sleep(10)
    print("Starting test")
    arguments = parse_args()
    sys.exit(run_tests(arguments.base_url))
