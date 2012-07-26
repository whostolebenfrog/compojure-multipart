# Ring / compojure middleware providing support for parsing multipart/mixed mime types into the request map #

A clojure multipart/mixed parser

# Add to your project #

Add `[com.floatbackwards/multipart "0.0.1"]` to your project.clj

Available on clojars: [multipart](https://clojars.org/com.floatbackwards/multipart)

# Use with #

    (:require (ring [multipart-mixed-params :as mm])))

then

    (defroutes main-routes
      (mm/wrap-multipart-mixed
       (POST "/data" req "OK")))

or 

    (def app
      (handler/site
       mm/wrap-multipart-mixed
       main-routes))


# Building #

`lein jar`

# Author #

Benjamin Griffiths (whostolebenfrog)

Inspiration from [multipart-params](https://github.com/mmcgrana/ring/blob/master/ring-core/src/ring/middleware/multipart_params.clj)
