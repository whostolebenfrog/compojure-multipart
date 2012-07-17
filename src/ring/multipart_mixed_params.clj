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
    (.startsWith content-type "image/")))

(defn- parse-request
  "Parse a mutlipart/mixed request"
  [request encoding]
  (let [multipart  (MimeMultipart. (ByteArrayDataSource. (:body request) "multipart/mixed"))
        parts      (group-by file-part? (parts-sequence multipart))]
    {:parts multipart :files (:true parts) :others (:false parts) :count (.getCount multipart)}))

(defn- parse-multipart-mixed
  "Parse multipart/mixed if in the correct format"
  [request encoding]
  (if (mixed-multipart? request)
    (parse-request request encoding)
    {:parts "Request not multipart/mixed"}))

(defn wrap-multipart-mixed
  [handler & [opts]]
  (fn [request]
    (let [encoding (or (:encoding opts)
                       (:character-encoding request)
                       "UTF-8")
          parts    (parse-multipart-mixed request encoding)
          request  (merge-with merge request
                              {:parts parts})]
      (handler request))))
