(ns ring.multipart-mixed-params
  (:import [javax.mail.internet MimeMultipart]
           [java.io IOException]
           [org.apache.commons.fileupload.util LimitedInputStream]
           [org.apache.commons.mail ByteArrayDataSource]))


(defn mixed-multipart?
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

(defn- input-stream [request & [limit]]
  "Returns either the input stream of a size limited input stream if limit is set"
  (if limit
    (proxy [LimitedInputStream] [(:body request) limit]
      (raiseError [max-size count]
        (throw (IOException.
                (format "The body exceeds its maximum permitted size of %s bytes" max-size)))))
    (:body request)))

(defn- parse-request
  "Parse a mutlipart/mixed request"
  [request & [limit]]
  (let [multipart  (MimeMultipart. (ByteArrayDataSource. (input-stream request limit)
                                                         "multipart/mixed"))]
    (if (.isComplete multipart)
      (merge-matches (parts-sequence multipart))
      (throw (javax.mail.MessagingException. "Incomplete request received")))))

(defn parse-multipart-mixed
  "Parse multipart/mixed if in the correct format"
  [request & [limit]]
  (if (mixed-multipart? request)
    (parse-request request limit) {}))

(defn wrap-multipart-mixed
  "Places an additional key of :multiparts into the request map.
   Multiparts contains a map of content type to seq of part. Each
   part is a stream of the data from that part.

   Map also contains a key of :count with the total number of parts."
  [handler & [limit]]
  (fn [req]
    (try
      (let [parts    (parse-multipart-mixed req limit)
            mult-req (merge req {:multiparts parts})]
        (handler mult-req))
      (catch IOException e
        {:status 413 :body (.getMessage e)}))))
