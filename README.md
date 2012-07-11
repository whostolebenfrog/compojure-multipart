Example of uploading a multipart message using compojure / ring middleware.

Run with lein ring server

Requires lein 2.x

Makes use of ring wrap-multipart-params middleware to bind multipart params into a map for us.

Still very rough - just got it working

TODO: decompose better onto the request map,
      can we autobind the maps values to keywords?
