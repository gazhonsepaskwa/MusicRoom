"""
www.kworb.net scraper to get top 27500 artist list
"""

import requests
from bs4 import BeautifulSoup


def get_artist_list(page: str):
    """
    Get one page from the top 27000 artist on spotify
    take the page as arg,
    return the list of artist on this page
    """
    if page == "1":
        url = "https://www.kworb.net/spotify/listeners.html"
    else:
        url = f"https://www.kworb.net/spotify/listeners{page}.html"

    response = requests.get(url, verify=False)

    soup = BeautifulSoup(response.text, "html.parser")

    links = soup.select('a[href^="artist/"]')

    res = []
    for link in links:
        res.append(link.text)

    return res
