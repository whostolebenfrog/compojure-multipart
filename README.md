# Ring middleware for multipart/mixed #

Clojure ring / compojure middleware that knows how to parse a multipart/mixed mime type message into the request map.

# Add to your project #

Add `[com.floatbackwards/multipart "0.0.1"]` to your project.clj

Available on clojars: [multipart](https://clojars.org/com.floatbackwards/multipart)

# Use with #

    (:use [ring.multipart-mixed-params])

then

    (defroutes main-routes
      (wrap-multipart-mixed
       (POST "/data" req "OK")))

or 

    (def app
      (handler/site
       wrap-multipart-mixed
       main-routes))

If the request body contains a message with the content type of `multipart/mixed` This will put a key of `:multiparts` into the request map. Otherwise does nothing.

This key will contain a map of content type to seq of streams for each part of the message with that type.

For example a message containing three parts: 2 jpeg images and 1 text/plan would give us the following request map.

    {... 
      :multiparts {
        "image/jpeg"       : (<stream1>, <stream2>),
        "application/json" : (<stream1>)
      }
    ...}

Each of the parsed elements is available as a stream that can be split, slurped or whatever else you fancy.

# Building #

`lein jar`

# Author #

Benjamin Griffiths (whostolebenfrog)

Inspiration from [multipart-params](https://github.com/mmcgrana/ring/blob/master/ring-core/src/ring/middleware/multipart_params.clj)
