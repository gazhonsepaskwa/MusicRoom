"""
General utility functions
"""
from bottle import request

def get_querry() -> str:
    """
    Extract the query string from the request body and return it.
    Raise an error if there is no query string.
    """
    data = request.json
    if data is None:
        raise ValueError('Request body is required')

    query = data.get('q')
    if not query:
        raise ValueError('Query string is required')

    return query

def cleanup_image(images_table) -> list:
    """
    Get rid of the width and height from the image urls by rewriting the structure.
    """
    if not images_table or len(images_table) < 3:
        return [None, None, None]

    return [
        images_table[0].get('url'),
        images_table[1].get('url'),
        images_table[2].get('url')
    ]
