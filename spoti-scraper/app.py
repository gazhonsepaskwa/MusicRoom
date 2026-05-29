if __name__ == "__main__":
    # files
    import api.spotify as sp_api
    import database as db
    # external libs
    from bottle import Bottle, request, response, run
    import json

    app = Bottle()

# utils
    def get_querry(error: str):
        data = request.json
        if data is None:
            error = 'Request body is required'
            return None

        query = data.get('q')
        if not query:
            error = 'Query string is required'
            return None

        return query

    def cleanup_image(images_table):
        """get rid of the width and height from the image urls by rewriting the structure"""
        return [
            images_table[0].get('url') if images_table else None,
            images_table[1].get('url') if images_table else None,
            images_table[2].get('url') if images_table else None
        ]


# routes
    @app.route('/add_artist', method='POST')
    def handle_add_artist_post():
        error = None
        query = get_querry(error)
        if query is None:
            response.status = 400
            return json.dumps({'error': error})

        # get artist info from spotify
        result = sp_api.search_artist(query)

        # create artist record in db
        artist_id = db.insert_artist(
            result.get('uri'),
            result.get('name'),
            cleanup_image(result.get('images'))
        )

        # get artist album from spotify
        albums = sp_api.get_artist_albums(result.get('uri'))

        # create each album of the artist in the db
        for album in albums:
            album_id = db.insert_album(
                album.get('uri'),
                album.get('name'),
                album.get('release_date'),
                cleanup_image(album.get('images'))
            )
            db.link_album_to_artist(album_id, artist_id)

            # create every track of the album in the db
            album_tracks = sp_api.get_album_tracks(album.get('uri'))
            for track in album_tracks:
                track_id = db.insert_track(
                    track.get('uri'),
                    track.get('name'),
                    track.get('duration_ms'),
                    track.get('track_number'),
                    album_id
                )
                # create artist that are featuring some songs
                for song_specific_artist in track.get('artists'):
                    if song_specific_artist.get('name') != result.get('name'): # check if one of the artist is the different than the album artist. If yes, create it
                        song_specific_artist_id = db.insert_artist(
                            song_specific_artist.get('uri'),
                            song_specific_artist.get('name'),
                            cleanup_image(song_specific_artist.get('images'))
                        )
                        db.link_track_to_artist(track_id, song_specific_artist_id)
                    else:
                        db.link_track_to_artist(track_id, artist_id)

        db.commit()

        response.content_type = 'application/json'
        return json.dumps({"msg": f"{result.get('name')} added successfully to MusicRoom database"})

    @app.route('/add_album', method='POST')
    def handle_add_album_post():
        error = None
        query = get_querry(error)
        if query is None:
            response.status = 400
            return json.dumps({'error': error})

        # get album info from spotify
        result = sp_api.search_album(query)
        print(result);

        # create album record in db
        album_id = db.insert_album(
            result.get('uri'),
            result.get('name'),
            result.get('release_date'),
            cleanup_image(result.get('images'))
        )

        # create artists record in db
        artist_ids = []
        for artist in result.get('artists'):
            # in the artist in the album there isn't the images
            artist_data = sp_api.get_artist(artist.get('uri'))
            artist_id = db.insert_artist(
                artist.get('uri'),
                artist.get('name'),
                cleanup_image(artist_data.get('images'))
            )
            db.link_album_to_artist(album_id, artist_id)
            artist_ids.append(artist_id)


        # create every track of the album in the db
        album_tracks = sp_api.get_album_tracks(result.get('uri'))
        for track in album_tracks:
            track_id = db.insert_track(
                track.get('uri'),
                track.get('name'),
                track.get('duration_ms'),
                track.get('track_number'),
                album_id
            )
            for artist_id in artist_ids:
                db.link_track_to_artist(track_id, artist_id)


        db.commit()

        response.content_type = 'application/json'
        return json.dumps({"msg": f"The Album '{result.get('name')}' was added successfully to MusicRoom database"})

    @app.route('/add_track', method='POST')
    def handle_add_track_post():
        # check query
        error = None
        query = get_querry(error)
        if query is None:
            response.status = 400
            return json.dumps({'error': error})

        # get track info from spotify api
        result = sp_api.search_track(query)
        print(result);

        # create music record in db
        track_id = db.insert_track(
            result.get('uri'),
            result.get('name'),
            result.get('duration_ms'),
            result.get('track_number'),
            album_id
        )

        # create album record in db
        album_id = db.insert_album(
            result.get('album').get('uri'),
            result.get('album').get('name'),
            result.get('album').get('release_date'),
            cleanup_image(result.get('album').get('images'))
        )

        # create artist record in db
        for artist in result.get('artists'):
            artist_id = db.insert_artist(
                artist.get('uri'),
                artist.get('name'),
                cleanup_image(result.get('album').get('images'))
            )
            db.link_album_to_artist(album_id, artist_id)
            db.link_track_to_artist(track_id, artist_id)

        db.commit()

        response.content_type = 'application/json'
        return json.dumps({"msg":f"The track '{result.get('name')}' was added successfully to MusicRoom database"})


# run server
    run(app, host='0.0.0.0', port=4242)
