(ns ring.multipart-mixed-params
  (:import [javax.mail.internet MimeMultipart]
           [org.apache.commons.mail ByteArrayDataSource]))

(defn- mixed-multipart?
  "Is this a multipart/mixed request?"
  [request]
  (if-let [content-type (:content-type request)]
    (.startsWith content-type "multipart/mixed")))

(defn- parts-sequence
  "Returns a lazy sequence of the parts from a MimeMultipart request"
  ([multipart] (parts-sequence multipart 0))
  ([multipart n]
     (if (< n (.getCount multipart))
       (let [part (.getBodyPart multipart n)]
         (lazy-seq (cons {(.getContentType part) (.getInputStream part)}
                   (parts-sequence multipart (inc n))))))))

(defn- merge-two [a [k v]]
  (update-in a [k] conj v))

(defn- merge-matches [list]
  (reduce #(merge-two % (flatten (seq %2))) {} list))

(defn- parse-request
  "Parse a mutlipart/mixed request"
  [request]
  (let [multipart  (MimeMultipart.
                    (ByteArrayDataSource.
                     (:body request) "multipart/mixed"))
        seq        (parts-sequence multipart)
        merged     (merge-matches seq)]
    merged))

(defn- parse-multipart-mixed
  "Parse multipart/mixed if in the correct format"
  [request]
  (if (mixed-multipart? request)
    (parse-request request)
    {:info "Request not multipart/mixed"}))

(defn wrap-multipart-mixed
  "Places an additional key of :multiparts into the request map.
   Multiparts contains a map of content type to seq of part. Each
   part is a stream of the data from that part.

   Map also contains a key of :count with the total number of parts."
  [handler]
  (fn [req]
    (let [parts    (parse-multipart-mixed req)
          mult-req (merge req {:multiparts parts})]
      (handler mult-req))))
