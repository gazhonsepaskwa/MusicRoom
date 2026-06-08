import requests

# chose the request to do
request_type = input("Search content to download from Spotify :\n 1: track\n 2: album\n 3: artist\n-> ")

CLIENT_ID = "f9998302eaf64b50bc581c00b291173a"
CLIENT_SECRET = "f97ac01d01244f4582e2a0c8c9426fba"

# get access_token
data = {
    "grant_type": "client_credentials",
}
response = requests.post(
    "https://accounts.spotify.com/api/token",
    data=data,
    auth=(CLIENT_ID, CLIENT_SECRET),
)
access_token = response.json()["access_token"]


# general headers
headers = {
    "Authorization": f"Bearer {access_token}",
}

case = request_type
if case == "1": # search track
    params = {
        "q": input("Enter search query: "),
        "type": "track",
    }
    response = requests.get(
        "https://api.spotify.com/v1/search",
        params=params,
        headers=headers,
    )
    res = response.json().get("tracks").get("items")
    print(res)
    for item in res:
        artist_string = ", ".join([artist.get('uri') for artist in item.get('artists')])
        print(f"Name: {item.get('name')}, URI: {item.get('uri')}, artist: [{artist_string}], Album: {item.get('album').get('uri')}, Duration: {item.get('duration_ms')}")

elif case == "2": # search album
    params = {
        "q": input("Enter search query: "),
        "type": "album",
    }
    response = requests.get(
        "https://api.spotify.com/v1/search",
        params=params,
        headers=headers,
    )
    res = response.json().get("albums").get("items")
    print(res)
    for item in res:
        artist_string = ", ".join([artist.get('uri') for artist in item.get('artists')])
        images_string = ", ".join([image.get('url') for image in item.get('images')])
        print(f"Name: {item.get('name')}, URI: {item.get('uri')}, Artists: [{artist_string}], Image: {images_string}, Release Date: {item.get('release_date')}")

elif case == "3": # search artist
    params = {
        "q": input("Enter search query: "),
        "type": "artist",
    }
    response = requests.get(
        "https://api.spotify.com/v1/search",
        params=params,
        headers=headers,
    )
    res = response.json().get("artists").get("items")
    for item in res:
        images_string = ", ".join([image.get('url') for image in item.get('images')])
        print(f"Name: {item.get('name')}, URI: {item.get('uri')}, Image: {images_string}")

else:
    print("Invalid request type")
