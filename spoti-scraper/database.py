"""
Database
"""

import os

import psycopg2

# check for env var existance
if not os.environ.get("DB_NAME"):
    raise Exception("DB_NAME environment variable not set")
if not os.environ.get("DB_USER"):
    raise Exception("DB_USER environment variable not set")
if not os.environ.get("DB_HOST"):
    raise Exception("DB_HOST environment variable not set")
if not os.environ.get("DB_PORT"):
    raise Exception("DB_PORT environment variable not set")

# get secret
with open("/run/secrets/db_password", "r") as file:
    password_secret = file.read().replace("\n", "")

# database connection
connection = psycopg2.connect(
    database=os.environ.get("DB_NAME"),
    user=os.environ.get("DB_USER"),
    password=password_secret,
    host=os.environ.get("DB_HOST"),
    port=os.environ.get("DB_PORT"),
)
cursor = connection.cursor()


# utils
def fix_date(date: str) -> str:
    """
    fix spotify date format.
    Sometime spotify give partial date format.
    Postgress can't handle it so I set first month and first day when missing
    ex :
        2025-03-19 -> 2025-03-19
        2025-03    -> 2025-03-01
        2025       -> 2025-01-01
    """

    if len(date) == 0:
        return "0001-01-01"
    elif len(date) == 4:
        return date + "-01-01"
    elif len(date) == 7:
        return date + "-01"
    return date


# commit


def commit():
    """commit changes to the database"""
    connection.commit()


# querries
def insert_track(
    uri: str, name: str, duration_ms: str, track_number: str, album_id: str
) -> int:
    """Insert a track into the database and return the track_id"""
    cursor.execute(
        """
        INSERT INTO "music" ("spotifyId", title, duration, "albumIndex", "albumId")
        VALUES (%s, %s, %s, %s, %s)
        ON CONFLICT ("spotifyId") DO UPDATE SET
            title = EXCLUDED.title,
            duration = EXCLUDED.duration,
            "albumIndex" = EXCLUDED."albumIndex",
            "albumId" = EXCLUDED."albumId"
        RETURNING id
        """,
        (
            uri,
            name,
            duration_ms,
            track_number,
            album_id,
        ),
    )
    track_id = cursor.fetchone()[0]
    return track_id


def insert_album(spotifyId: str, title: str, date: str, images: str):
    date = fix_date(date)
    cursor.execute(
        """
        INSERT INTO "album" ("spotifyId", title, date, images)
        VALUES (%s, %s, %s, %s)
        ON CONFLICT ("spotifyId") DO UPDATE SET
            title = EXCLUDED.title,
            date = EXCLUDED.date,
            images = EXCLUDED.images
        RETURNING id
        """,
        (
            spotifyId,
            title,
            date,
            images,
        ),
    )
    album_id = cursor.fetchone()[0]
    return album_id


def insert_artist(spotifyId: str, name: str, images: str):
    cursor.execute(
        """
        INSERT INTO "artist" ("spotifyId", title, images)
        VALUES (%s, %s, %s)
        ON CONFLICT ("spotifyId") DO UPDATE SET
            title = EXCLUDED.title,
            images = EXCLUDED.images
        RETURNING id
        """,
        (
            spotifyId,
            name,
            images,
        ),
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
        ),
    )


def link_track_to_artist(track_id: str, artist_id: str):
    cursor.execute(
        """
        INSERT INTO "_artistTomusic" ("A", "B")
        VALUES (%s, %s)
        ON CONFLICT ("A", "B") DO NOTHING
        """,
        (artist_id, track_id),
    )
