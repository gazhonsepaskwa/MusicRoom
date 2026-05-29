"""
Main application module for the Spotify/YouTube scraper.
"""

if __name__ == "__main__":
    # files
    import api.spotify as sp_api
    import database as db
    import utils
    # external libs
    from bottle import Bottle, request, response, run
    import json

    app = Bottle()

    # routes
    @app.route('/add_artist', method='POST')
    def handle_add_artist_post():
        try:
            query = utils.get_querry()
        except ValueError as e:
            error = str(e)
            response.status = 400
            return json.dumps({'message' : 'Invalid request body', 'error': error})

        # get artist info from spotify
        try :
            result = sp_api.search_artist(query)
        except Exception as e:
            error = str(e)
            response.status = 500
            return json.dumps({'message' : 'Failed to search artist from Spotify', 'error': error})

        # create artist record in db
        artist_id = db.insert_artist(
            result.get('uri'),
            result.get('name'),
            utils.cleanup_image(result.get('images'))
        )

        # get artist album from spotify
        try :
            albums = sp_api.get_artist_albums(result.get('uri'))
        except Exception as e:
            error = str(e)
            response.status = 500
            return json.dumps({'message' : 'Failed to get artist albums from Spotify', 'error': error})

        # create each album of the artist in the db
        for album in albums:
            album_id = db.insert_album(
                album.get('uri'),
                album.get('name'),
                album.get('release_date'),
                utils.cleanup_image(album.get('images'))
            )
            db.link_album_to_artist(album_id, artist_id)

            # create every track of the album in the db
            try :
                album_tracks = sp_api.get_album_tracks(album.get('uri'))
            except Exception as e:
                error = str(e)
                response.status = 500
                return json.dumps({'message' : 'Failed to get album tracks from Spotify', 'error': error})
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
                            utils.cleanup_image(song_specific_artist.get('images'))
                        )
                        db.link_track_to_artist(track_id, song_specific_artist_id)
                    else:
                        db.link_track_to_artist(track_id, artist_id)

        db.commit()

        response.content_type = 'application/json'
        return json.dumps({"msg": f"{result.get('name')} added successfully to MusicRoom database"})

    @app.route('/add_album', method='POST')
    def handle_add_album_post():
        try:
            query = utils.get_querry()
        except ValueError as e:
            error = str(e)
            response.status = 400
            return json.dumps({'message' : 'Invalid request body', 'error': error})

        # get album info from spotify
        try:
            result = sp_api.search_album(query)
        except Exception as e:
            error = str(e)
            response.status = 500
            return json.dumps({'message' : 'Failed to get album info from Spotify', 'error': error})

        # create album record in db
        album_id = db.insert_album(
            result.get('uri'),
            result.get('name'),
            result.get('release_date'),
            utils.cleanup_image(result.get('images'))
        )

        # create artists record in db
        artist_ids = []
        for artist in result.get('artists'):
            # in the artist in the album there isn't the images
            try:
                artist_data = sp_api.get_artist(artist.get('uri'))
            except Exception as e:
                error = str(e)
                response.status = 500
                return json.dumps({'message' : 'Failed to get artist info from Spotify', 'error': error})

            artist_id = db.insert_artist(
                artist.get('uri'),
                artist.get('name'),
                utils.cleanup_image(artist_data.get('images'))
            )
            db.link_album_to_artist(album_id, artist_id)
            artist_ids.append(artist_id)


        # create every track of the album in the db
        try:
            album_tracks = sp_api.get_album_tracks(result.get('uri'))
        except Exception as e:
            error = str(e)
            response.status = 500
            return json.dumps({'message' : 'Failed to get album tracks from Spotify', 'error': error})
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
        try:
            query = utils.get_querry()
        except ValueError as e:
            error = str(e)
            response.status = 400
            return json.dumps({'message' : 'Invalid request body', 'error': error})

        # get track info from spotify api
        try:
            result = sp_api.search_track(query)
        except Exception as e:
            error = str(e)
            response.status = 500
            return json.dumps({'message' : 'Failed to get track info from Spotify', 'error': error})

        # create album record in db
        album_id = db.insert_album(
            result.get('album').get('uri'),
            result.get('album').get('name'),
            result.get('album').get('release_date'),
            utils.cleanup_image(result.get('album').get('images'))
        )

        # create music record in db
        track_id = db.insert_track(
            result.get('uri'),
            result.get('name'),
            result.get('duration_ms'),
            result.get('track_number'),
            album_id
        )

        # create artist record in db
        for artist in result.get('artists'):
            artist_id = db.insert_artist(
                artist.get('uri'),
                artist.get('name'),
                utils.cleanup_image(result.get('album').get('images'))
            )
            db.link_album_to_artist(album_id, artist_id)
            db.link_track_to_artist(track_id, artist_id)

        db.commit()

        response.content_type = 'application/json'
        return json.dumps({"msg":f"The track '{result.get('name')}' was added successfully to MusicRoom database"})

# run server
    run(app, host='0.0.0.0', port=4242)
