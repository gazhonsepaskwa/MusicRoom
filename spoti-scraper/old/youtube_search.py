import os
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request

SCOPES = ['https://www.googleapis.com/auth/youtube.force-ssl']
PORT = 63486
REDIRECT_URI = f'http://localhost:{PORT}/'

os.environ['OAUTHLIB_REDIRECT_URI'] = REDIRECT_URI


def authenticate():
    flow = InstalledAppFlow.from_client_secrets_file('credential.json', SCOPES)
    credentials = flow.run_local_server(port=PORT)

    return credentials

credentials = authenticate()

youtube_service = build('youtube', 'v3', credentials=credentials, static_discovery=False)


def search_videos(query):
    request = youtube_service.search().list(
        part='id,snippet',
        q=query,
        type='video'
    )
    response = request.execute()

    return response['items']

results = search_videos(input("Enter YouTube Music keyword: "))
print(f"https://www.youtube.com/watch?v={results[0]['id']['videoId']}")
