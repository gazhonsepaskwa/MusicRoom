import requests

CLIENT_ID = "f9998302eaf64b50bc581c00b291173a"
CLIENT_SECRET = "f97ac01d01244f4582e2a0c8c9426fba"

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
    """search for the albums of an artist on Spotify"""
    params = {
        "artist_uri": artist_uri,
    }
    response = requests.get(
        "https://api.spotify.com/v1/artists/{}/albums".format(artist_uri[artist_uri.rfind(":") + 1:]),
        params=params,
        headers=headers,
    )
    albums = response.json().get("items")

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
