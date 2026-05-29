import requests
import time
import os

CLIENT_ID = os.environ.get("SPOTIFY_API_USER_ID")
CLIENT_SECRET = os.environ.get("SPOTIFY_API_SECRET")

# API INIT : request access token
data = { "grant_type": "client_credentials" }
response = requests.post(
    "https://accounts.spotify.com/api/token",
    data=data,
    auth=(CLIENT_ID, CLIENT_SECRET),
)

# Global variables
access_token = response.json()["access_token"]
headers = {
    "Authorization": f"Bearer {access_token}",
}

def search_track(track_string, user_mode: bool = False):
    """search for a track on Spotify"""
    params = {
        "q": track_string,
        "type": "track",
    }
    response = requests.get(
        "https://api.spotify.com/v1/search",
        params=params,
        headers=headers,
    )
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
        choice = 1

    return (tracks[int(choice) - 1])

def search_artist(artist_string, user_mode: bool = False):
    """search for an artist on Spotify"""
    params = {
        "q": artist_string,
        "type": "artist",
    }
    response = requests.get(
        "https://api.spotify.com/v1/search",
        params=params,
        headers=headers,
    )
    artists = response.json().get("artists").get("items")

    # let user chose the artist to extract if user mode is enabled
    if user_mode:
        artist_count = 1
        for artist in artists:
            print(f"{artist_count} {artist.get('name')}")
            artist_count += 1
        choice = input("chose artist -> ")
    else:
        choice = 1

    return (artists[int(choice) - 1])

def search_album(album_string, user_mode: bool = False):
    """search for an album on Spotify"""
    params = {
        "q": album_string,
        "type": "album",
    }
    response = requests.get(
        "https://api.spotify.com/v1/search",
        params=params,
        headers=headers,
    )
    albums = response.json().get("albums").get("items")

    # let user chose the album to extract if user mode is enabled
    if user_mode:
        album_count = 1
        for album in albums:
            print(f"{album_count} {album.get('name')}")
            album_count += 1
        choice = input("chose album -> ")
    else:
        choice = 1

    return (albums[int(choice) - 1])

# to delete
def get_album_from_uri(album_uri):
    """search for an album on Spotify by URI"""
    params = {
        "album_uri": album_uri,
    }
    response = requests.get(
        "https://api.spotify.com/v1/albums/{}".format(album_uri),
        params=params,
        headers=headers,
    )
    album = response.json()

    return album

def get_album_tracks(album_uri):
    """search for the tracks of an album on Spotify"""
    params = {
        "album_uri": album_uri,
    }
    response = requests.get(
        "https://api.spotify.com/v1/albums/{}/tracks".format(album_uri[album_uri.rfind(":") + 1:]),
        params=params,
        headers=headers,
    )
    tracks = response.json().get("items")

    return tracks

def get_artist_albums(artist_uri):
    """Search for the albums of an artist on Spotify"""
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
        print(response.status_code)
        if response.status_code != 200:
            if response.status_code == 429:
                print(f"Rate limited. Retry after {response.headers.get("Retry-After", 1)} seconds.")
                break
            else:
                print(f"Api error : {response.status_code}")
                break

        albums += response.json().get("items")

        if already_fetched + len(albums) >= response.json().get("total"): # exit condition
            break

        already_fetched += len(albums)
        offset += 10

    # fix the year error time format

    return albums

def get_artist(artist_uri):
    """search for an artist on Spotify by URI"""
    params = {
        "artist_uri": artist_uri,
    }
    response = requests.get(
        "https://api.spotify.com/v1/artists/{}".format(artist_uri[artist_uri.rfind(":") + 1:]),
        params=params,
        headers=headers,
    )
    artist = response.json()

    return artist
