import requests
import time
import os

# check for env var existance
if not os.environ.get("SPOTIFY_API_USER_ID"):
    raise Exception("SPOTIFY_API_USER_ID environment variable not set")
if not os.environ.get("SPOTIFY_API_SECRET"):
    raise Exception("SPOTIFY_API_SECRET environment variable not set")

# get secrets from env
CLIENT_ID = os.environ.get("SPOTIFY_API_USER_ID")
CLIENT_SECRET = os.environ.get("SPOTIFY_API_SECRET")

# utils
def check_response(response) -> None:
    """raise an exception if the response status code is not 200"""
    if response.status_code != 200:
        if response.status_code == 429:
            raise requests.exceptions.RequestException(f"Rate limited. Retry after {response.headers.get("Retry-After", 1)} seconds.")
        else:
            raise requests.exceptions.RequestException(f"Api error : {response.status_code} - {response.text}")

# API INIT : request access token
data = { "grant_type": "client_credentials" }
response = requests.post(
    "https://accounts.spotify.com/api/token",
    data=data,
    auth=(CLIENT_ID, CLIENT_SECRET),
)
check_response(response)

# Global variables
access_token = response.json()["access_token"]
headers = { "Authorization": f"Bearer {access_token}" }

# search
def search_track(track_string: str, user_mode: bool = False) -> list:
    """
    Search for a track on Spotify and return the track list.
    Raises a RequestException if the response status code is not 200.
    """

    params = {
        "q": track_string,
        "type": "track",
    }

    response = requests.get(
        "https://api.spotify.com/v1/search",
        params=params,
        headers=headers,
    )
    # extract track list from request response
    tracks = response.json().get("tracks").get("items")

    # let user chose the track to extract if user mode is enabled
    if user_mode:
        track_count = 1
        for track in tracks:
            artist_string = ", ".join([artist.get('name') for artist in track.get('artists')])
            print(f"{track_count} {track.get('name')} - {artist_string}, Album: {track.get('album').get('name')}, Duration: {track.get('duration_ms')}")
            track_count += 1
        choice = input("chose track -> ")
    else:
        # if no user mode, choose the first track by default
        choice = 1

    return (tracks[int(choice) - 1])

def search_artist(artist_string: str, user_mode: bool = False) -> list:
    """
    Search for an artist on Spotify and return the artist list
    Raises a RequestException if the response status code is not 200.
    """
    params = {
        "q": artist_string,
        "type": "artist",
    }

    response = requests.get(
        "https://api.spotify.com/v1/search",
        params=params,
        headers=headers,
    )
    check_response(response)

    # extract artist list from request response
    artists = response.json().get("artists").get("items")

    # let user chose the artist to extract if user mode is enabled
    if user_mode:
        artist_count = 1
        for artist in artists:
            print(f"{artist_count} {artist.get('name')}")
            artist_count += 1
        choice = input("chose artist -> ")
    else:
        # if no user mode, choose the first artist by default
        choice = 1

    return (artists[int(choice) - 1])

def search_album(album_string: str, user_mode: bool = False) -> list:
    """
    Search for an album on Spotify and return the album list
    Raises a RequestException if the response status code is not 200.
    """
    params = {
        "q": album_string,
        "type": "album",
    }
    response = requests.get(
        "https://api.spotify.com/v1/search",
        params=params,
        headers=headers,
    )
    check_response(response)

    # extract album list from request response
    albums = response.json().get("albums").get("items")

    # let user chose the album to extract if user mode is enabled
    if user_mode:
        album_count = 1
        for album in albums:
            print(f"{album_count} {album.get('name')}")
            album_count += 1
        choice = input("chose album -> ")
    else:
        # if no user mode, choose the first album by default
        choice = 1

    return (albums[int(choice) - 1])

# other
def get_album_tracks(album_uri: str) -> list:
    """
    Get the tracks of an album on Spotify and return the track list
    Raises a RequestException if the response status code is not 200.
    """
    params = {
        "album_uri": album_uri,
    }
    response = requests.get(
        "https://api.spotify.com/v1/albums/{}/tracks".format(album_uri[album_uri.rfind(":") + 1:]),
        params=params,
        headers=headers,
    )
    check_response(response)
    tracks = response.json().get("items")

    return tracks

def get_artist_albums(artist_uri: str) -> list:
    """
    Get all albums of an artist on Spotify and return the album list
    Raises a RequestException if the response status code is not 200.
    """
    # while because the limit can't be more than 10 but an artist can have more than 10 albums (ex : Queen)
    offset = 0
    already_fetched = 0
    albums = []
    while True: # do-while
        params = {
            "artist_uri": artist_uri,
            "limit": 10,
            "offset": offset
        }
        response = requests.get(
            "https://api.spotify.com/v1/artists/{}/albums".format(artist_uri[artist_uri.rfind(":") + 1:]),
            params=params,
            headers=headers,
        )
        check_response(response)

        albums += response.json().get("items")

        if already_fetched + len(albums) >= response.json().get("total"): # exit condition
            break

        already_fetched += len(albums)
        offset += 10

    # fix the year error time format

    return albums

def get_artist(artist_uri) -> dict:
    """
    Search for an artist on Spotify by URI
    Raises a RequestException if the response status code is not 200.
    """
    params = {
        "artist_uri": artist_uri,
    }
    response = requests.get(
        "https://api.spotify.com/v1/artists/{}".format(artist_uri[artist_uri.rfind(":") + 1:]),
        params=params,
        headers=headers,
    )
    check_response(response)
    artist = response.json()

    return artist
