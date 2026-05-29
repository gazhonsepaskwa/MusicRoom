import psycopg2
import os

# database connection
connection = psycopg2.connect(
    database=os.environ.get("DB_NAME"),
    user=os.environ.get("DB_USER"),
    password=os.environ.get("DB_PASSWORD"),
    host=os.environ.get("DB_HOST"),
    port=os.environ.get("DB_PORT")
)
cursor = connection.cursor()

# utils

def fix_date(date: str) -> str:
    # sometime spotify give partial date format, postgress can't handle so I set first month and first day when missing
    if len(date) == 0:
        return "0001-01-01"
    elif len(date) == 4:
        return date + "-01-01"
    elif len(date) == 7:
        return date + "-01"
    return date

# commit
def commit():
    connection.commit()

# querries
def insert_track(uri: str, name: str, duration_ms: str, track_number: str, album_id: str):
    cursor.execute(
        """
        INSERT INTO "music" ("spotifyId", title, duration, "albumIndex", "albumId")
        VALUES (%s, %s, %s, %s, %s)
        ON CONFLICT ("spotifyId") DO UPDATE SET "spotifyId" = EXCLUDED."spotifyId"
        RETURNING id
        """,
        (
            uri,
            name,
            duration_ms,
            track_number,
            album_id,
        )
    )
    track_id = cursor.fetchone()[0]
    return track_id

def insert_album(spotifyId: str, title: str, date: str, images: str):
    date = fix_date(date)
    cursor.execute(
        """
        INSERT INTO "album" ("spotifyId", title, date, images)
        VALUES (%s, %s, %s, %s)
        ON CONFLICT ("spotifyId") DO UPDATE SET "spotifyId" = EXCLUDED."spotifyId"
        RETURNING id
        """,
        (
            spotifyId,
            title,
            date,
            images,
        )
    )
    album_id = cursor.fetchone()[0]
    return album_id

def insert_artist(spotifyId: str, name: str, images: str):
    cursor.execute(
        """
        INSERT INTO "artist" ("spotifyId", title, images)
        VALUES (%s, %s, %s)
        ON CONFLICT ("spotifyId") DO UPDATE SET "spotifyId" = EXCLUDED."spotifyId"
        RETURNING id
        """,
        (
            spotifyId,
            name,
            images,
        )
    )
    artist_id = cursor.fetchone()[0]
    return artist_id

def link_album_to_artist(album_id: str, artist_id: str):
    cursor.execute(
        """
        INSERT INTO "_albumToartist" ("A", "B")
        VALUES (%s, %s)
        ON CONFLICT ("A", "B") DO NOTHING
        """,
        (
            album_id,
            artist_id,
        )
    )

def link_track_to_artist(track_id: str, artist_id: str):
    cursor.execute(
        """
        INSERT INTO "_artistTomusic" ("A", "B")
        VALUES (%s, %s)
        ON CONFLICT ("A", "B") DO NOTHING
        """,
        (
            artist_id,
            track_id
        )
    )
