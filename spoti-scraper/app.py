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


# routes
    @app.route('/add_artist', method='POST')
    def handle_add_artist_post():
        data = request.json
        if data is None:
            response.status = 400
            return {'error': 'Request body is required'}

        query = data.get('q')
        if not query:
            response.status = 400
            return {'error': 'Query string is required'}

        result = sp_api.search_artist(query)
        print(result);

        # extract image URLs from result
        image_table = [
            result.get('images')[0].get('url') if result.get('images') else None,
            result.get('images')[1].get('url') if result.get('images') else None,
            result.get('images')[2].get('url') if result.get('images') else None
        ]

        cursor.execute('INSERT IGNORE INTO "artist" (spotifyId, title, images) VALUES ($1, $2, $3)', result.get('uri'), result.get('name'), image_table)

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

        # get rid of the width and height from the image urls by rewriting the structure
        album_image_table = [
            result.get('images')[0].get('url') if result.get('images') else None,
            result.get('images')[1].get('url') if result.get('images') else None,
            result.get('images')[2].get('url') if result.get('images') else None
        ]

        # create album record in db
        album_id = db.insert_album(
            result.get('uri'),
            result.get('name'),
            result.get('release_date'),
            album_image_table
        )

        # create artists record in db
        for artist in result.get('artists'):
            # in the artist in the album there isn't the images
            artist_data = sp_api.get_artist(artist.get('uri'))
            # get artist images
            artist_image_table = [
                artist_data.get('images')[0].get('url') if artist_data.get('images') else None,
                artist_data.get('images')[1].get('url') if artist_data.get('images') else None,
                artist_data.get('images')[2].get('url') if artist_data.get('images') else None
            ]

            artist_id = db.insert_artist(
                artist.get('uri'),
                artist.get('name'),
                artist_image_table
            )

            db.link_album_to_artist(album_id, artist_id)


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

        # get rid of the width and height from the image urls by rewriting the structure
        album_image_table = [
            result.get('album').get('images')[0].get('url') if result.get('album').get('images') else None,
            result.get('album').get('images')[1].get('url') if result.get('album').get('images') else None,
            result.get('album').get('images')[2].get('url') if result.get('album').get('images') else None
        ]

        # create album record in db
        album_id = db.insert_album(
            result.get('album').get('uri'),
            result.get('album').get('name'),
            result.get('album').get('release_date'),
            album_image_table
        )

        # create artist record in db
        for artist in result.get('artists'):
            # get artist images
            artist_image_table = [
                result.get('album').get('images')[0].get('url') if result.get('album').get('images') else None,
                result.get('album').get('images')[1].get('url') if result.get('album').get('images') else None,
                result.get('album').get('images')[2].get('url') if result.get('album').get('images') else None
            ]

            artist_id = db.insert_artist(
                artist.get('uri'),
                artist.get('name'),
                artist_image_table
            )

            db.link_album_to_artist(album_id, artist_id)

        # create music record in db
        track_id = db.insert_track(
            result.get('uri'),
            result.get('name'),
            result.get('duration_ms'),
            result.get('track_number'),
            album_id
        )

        db.commit()

        response.content_type = 'application/json'
        return json.dumps({"msg":f"The track '{result.get('name')}' was added successfully to MusicRoom database"})


# run server
    run(app, host='0.0.0.0', port=4242)
