"""
Utility functions for the Spotify and Youtube API.
"""

import requests


class RateLimitError(Exception):
    """Raised when the rate limit is exceeded"""

    pass


def check_response(response) -> None:
    """Raise an exception if the response status code is not 200"""
    if response.status_code != 200:
        if response.status_code == 429:
            raise RateLimitError(f"{response.headers.get('Retry-After', 1)}")
        else:
            raise requests.exceptions.RequestException(
                f"Api error : {response.status_code} - {response.text}"
            )
