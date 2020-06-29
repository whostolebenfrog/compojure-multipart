(ns multipart.unit
  (:require [clojure.java.io :as io]
            [ring.multipart-mixed-params :refer :all]
            [midje.sweet :refer :all]))

(defn multipart
  [path]
  (let [f (io/file (format "test/multipart/resources/%s" path))]
    {:body (io/input-stream f)
     :content-type (-> (re-seq #"^(?i)content-type: (.+)" (slurp f))
                     first
                     second)}))

(defn get-part [n type req]
  (slurp (nth (req type) n)))

(defn get-plain [n req]
  (get-part n "text/plain" req))

(defn gif?
  [xs]
  (.startsWith xs "GIF"))

(fact-group
 :unit

 (fact "Can parse multipart with single part"
       (get-plain 0 (parse-multipart-mixed (multipart "single.part")))
       => "single part")

 (fact "Many parts gives sequences of parts in map"
       (let [parts (parse-multipart-mixed (multipart "multi.part"))]
         (get-plain 0 parts) => "single part 1"
         (get-plain 1 parts) => "single part 2"
         (gif? (get-part 0 "image/gif" parts)) => truthy))

 (fact "Non-multipart shows info message"
       (parse-multipart-mixed {:content-type "none"})
       => {})

 (fact "Input size is limited"
       (let [function (wrap-multipart-mixed (fn [req] nil) 100)]
         (function (multipart "single.part"))
         => (contains {:status 413})))

 (fact "Final boundary is required"
       (parse-multipart-mixed (multipart "multi-missing-final-boundary.part")) => (throws javax.mail.MessagingException))

 (fact "Trailing dash in boundary should parse correctly"
       (get-part 0
                 "application/json; charset=UTF-8"
                 (parse-multipart-mixed
                     (multipart "single-with-trailing-boundary-dash.part")))
       => "{}\n"))
