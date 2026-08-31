"""
YouTube API wrapper.
"""

import os

import api.utils
import requests
import yt_dlp
from google.auth.transport.requests import Request
from google.oauth2 import service_account
from google.oauth2.credentials import Credentials
from googleapiclient.discovery import build

SCOPES = ["https://www.googleapis.com/auth/youtube.force-ssl"]
SA_KEY_FILE = "/run/secrets/google_youtube_credentials"


def authenticate() -> Credentials:
    """Authenticate the user using service account credentials and return them."""
    credentials = service_account.Credentials.from_service_account_file(
        SA_KEY_FILE, scopes=SCOPES
    )

    return credentials


# auth
credentials = authenticate()

# build service
youtube_service = build(
    "youtube", "v3", credentials=credentials, static_discovery=False
)


def search_videos(query) -> list:
    """
    Search for YouTube videos based on the given query and return the results.
    Raises a RequestException if the response status code is not 200.
    """
    request = youtube_service.search().list(part="id,snippet", q=query, type="video")
    response = request.execute()

    return response["items"]


def download_youtube_music(video_url, title) -> None:
    """
    Download the audio from a YouTube video and convert it to MP3 format.
    """
    ydl_opts = {
        "format": "bestaudio/best",  # Grab the best audio stream
        "postprocessors": [
            {
                "key": "FFmpegExtractAudio",
                "preferredcodec": "mp3",
                "preferredquality": "192",  # 192kbps is standard high quality
            }
        ],
        "outtmpl": f"/dl/{title}.%(ext)s",  # Save as the title given by the caller
        "quiet": False,
        "no_warnings": False,
        "noprogress": True,
        "extractor_args": {
            "youtube": {
                "player_client": ["android", "web"],
            }
        },
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        print(f"Downloading and converting: {video_url}")
        ydl.download([video_url])
        print("Download complete!")


def search_and_download(query, title) -> bool:
    """
    Search for YouTube videos based on the given query, download the audio, and convert it to MP3 format.
    Returns True if the download was successful, False otherwise.
    """
    videos = search_videos(query)
    print(f"Found {len(videos)} videos for query: {query}")
    if not videos:
        return False
    video_url = f"https://www.youtube.com/watch?v={videos[0]['id']['videoId']}"
    download_youtube_music(video_url, title)
    return True
