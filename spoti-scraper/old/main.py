import requests
import api.spotify as sp_api

# chose the request to do
request_type = input("Search content to download from Spotify(metadata) and Youtube(mp3) :\n 1: track\n 2: album\n 3: artist\n-> ")
request_content = input("Enter search query: ")

case = request_type
if case == "1": # search track
    track = sp_api.search_track(request_content, user_mode=True)
    album = track.get('album')
    artists = track.get('artists')

    # print metadata
    print("track:", track.get('name'))
    print("artists:", ", ".join([artist.get('name') for artist in artists]))
    print("album:", album.get('name'))




elif case == "2": # search album
    album = sp_api.search_album(request_content, user_mode=True)
    artists = album.get('artists')
    album_tracks = sp_api.get_album_tracks(album.get('uri'))

    # print metadata
    print("album:", album.get('name'))
    print("artists:", ", ".join([artist.get('name') for artist in artists]))
    print("tracks:")
    for track in album_tracks:
        print("  -", track.get('name'))


elif case == "3": # search artist
    artist = sp_api.search_artist(request_content, user_mode=True)
    albums = sp_api.get_artist_albums(artist.get('uri'))

    print("artist:", artist.get('name'))
    print("albums:")
    for album in albums:
        print("  -", album.get('name'))
        #for track in sp_api.get_album_tracks(album.get('uri')):
        #    print("    -", track.get('name'))


else:
    print("Invalid request type")
