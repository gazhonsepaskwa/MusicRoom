import psycopg2

# database connection
connection = psycopg2.connect(
    database="music_room_db",
    user="music_room_user",
    password="oEhb7utCFpaspJVzIT9FrZgPbTAp65e2rV6P8uW2v7l2OeXmuDV2G14UQSgJgchd",
    host="db",
    port=5432
)
cursor = connection.cursor()

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
