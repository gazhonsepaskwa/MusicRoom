"""
Utility functions for the Spotify and Youtube API.
"""

def check_response(response) -> None:
    """Raise an exception if the response status code is not 200"""
    if response.status_code != 200:
        if response.status_code == 429:
            raise requests.exceptions.RequestException(f"Rate limited. Retry after {response.headers.get("Retry-After", 1)} seconds.")
        else:
            raise requests.exceptions.RequestException(f"Api error : {response.status_code} - {response.text}")
