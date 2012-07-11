(ns multipart.core
  (:use [net.cgrand.enlive-html :only [deftemplate]]
        [compojure.core]
        [clojure.java.io :only [copy file]]
        [ring.middleware.params])
  (:require (compojure [route :as route])
            (compojure [handler :as handler])
            (ring.middleware [multipart-params :as mp])))

(defn render [template]
  (apply str template))

(deftemplate upload-page "pages/add.html" [])
(deftemplate added "pages/added.html" [])

(defn upload-file [img name]
  (copy (file (:tempfile img)) (file name))
  (render (added)))

(defroutes main-routes
  (GET "/" [] (render (upload-page)))
  (mp/wrap-multipart-params
   (POST "/data" req
     (upload-file
      (get (:multipart-params req) "img")
      (get (:multipart-params req) "name")))))

(def app
  (handler/site main-routes))
