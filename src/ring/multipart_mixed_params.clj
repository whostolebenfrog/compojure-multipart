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
         (lazy-seq (cons part (parts-sequence multipart (inc n))))))))

(defn- file-part?
  "Is this part a file part"
  [part]
  (if-let [content-type (.getContentType part)]
    (not (or
          (.startsWith content-type "application/json")
          (.startsWith content-type "application/xml")
          (.startsWith content-type "text/plain")))))

(defn- parse-request
  "Parse a mutlipart/mixed request"
  [request encoding]
  (let [multipart  (MimeMultipart. (ByteArrayDataSource. (:body request) "multipart/mixed"))
        parts      (group-by file-part? (parts-sequence multipart))]
    {:parts (map (fn [part]
                    {:content-type (.getContentType part)
                     :stream       (.getInputStream part)}) (get parts true))
     :meta  (map #(slurp (.getInputStream %)) (get parts false))
     :count (.getCount multipart)}))

(defn- parse-multipart-mixed
  "Parse multipart/mixed if in the correct format"
  [request encoding]
  (if (mixed-multipart? request)
    (parse-request request encoding)
    {:info "Request not multipart/mixed"}))

(defn wrap-multipart-mixed
  "Places an additional key of :multiparts into the request map that contains the following maps:
     :binary - seq binary parts {:content-type 'image/jpeg' :stream 'inputstream'}
     :meta   - seq of parts in format application/json, application/xml or text/plain
     :count  - total number of parts found
  TODO - not actually using encoding atm..."
  [handler & [opts]]
  (fn [request]
    (let [encoding (or (:encoding opts)
                       (:character-encoding request)
                       "UTF-8")
          parts    (parse-multipart-mixed request encoding)
          request  (merge request {:multiparts parts})]
      (handler request))))
