import yt_dlp
import requests

def download_youtube_music(video_url):
    ydl_opts = {
        'format': 'bestaudio',  # Grab the best audio stream
        'postprocessors': [{
            'key': 'FFmpegExtractAudio',
            'preferredcodec': 'mp3',
            'preferredquality': '192',  # 192kbps is standard high quality
        }],
        'outtmpl': '%(title)s.%(ext)s',  # Save as the video's original title
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        print(f"Downloading and converting: {video_url}")
        ydl.download([video_url])
        print("Download complete!")

keyword = input("Enter YouTube Music keyword: ")
access_token = "AIzaSyCTihli_9IHu50g2ZyUMPlQM1M1XGKi2nA"

headers = {
    "Authorization": f"Bearer {access_token}",
}

response = requests.get(f"https://www.googleapis.com/youtube/v3/search?part=snippet&q={keyword}&type=video", headers=headers)
print(response.json())



#download_youtube_music(target_url)
