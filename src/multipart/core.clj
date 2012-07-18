(ns multipart.core
  (:use [net.cgrand.enlive-html :only [deftemplate]]
        [compojure.core]
        [clojure.java.io :only [copy file]]
        [ring.middleware.params])
  (:require (compojure [route :as route])
            (compojure [handler :as handler])
            (ring.middleware [multipart-params :as mp])
            (ring [multipart-mixed-params :as mm])))

(defn render [template]
  (apply str template))

(deftemplate upload-page "pages/add.html" [])
(deftemplate added "pages/added.html" [])

(defn upload-file [stream]
  (prn stream)
  (spit "test.jpg" stream))

(defroutes main-routes
  (GET "/" [] (render (upload-page)))
  (mm/wrap-multipart-mixed
   (POST "/data" req
         (prn req)
         (prn (first (:multiparts req)))
         (prn (get (first (:multiparts req)) "image/jpeg"))
         (upload-file (first (get (first (:multiparts req)) "image/jpeg")))
         (render (added)))))

(def app
  (handler/site main-routes))
