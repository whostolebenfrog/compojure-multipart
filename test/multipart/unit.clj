(ns multipart.unit
  (:use [clojure.java.io :only [copy file]])
  (:use [ring.multipart-mixed-params])
  (:use [midje.sweet])
  (:import [javax.mail.internet MimeMultipart]
           [org.apache.commons.mail ByteArrayDataSource]))

;; helper methods

(defn multipart
  [path]
  {:body         (slurp (format "test/multipart/resources/%s" path))
   :content-type "multipart/mixed"})

(defn get-part [n type req]
  (slurp (nth (req type) n)))

(defn get-plain [n req]
  (get-part n "text/plain" req))

(defn gif?
  [xs]
  (.startsWith xs "GIF"))

;; tests

(fact "Can parse multipart with single part"
      (get-plain 0 (parse-multipart-mixed (multipart "single.part")))
      => "single part")


(fact "Many parts gives sequences of parts in map"
      (let [parts (parse-multipart-mixed (multipart "multi.part"))]
        (get-plain 0 parts) => "single part 2"
        (get-plain 1 parts) => "single part 1"
        (gif? (get-part 0 "image/gif" parts)) => truthy))

(fact "Non-multipart shows info message"
      (:info (parse-multipart-mixed {:content-type "none"}))
      => "Request not multipart/mixed")
