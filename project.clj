(defproject com.floatbackwards/multipart "0.0.1"
  :description "Ring middleware for parsing multipart/mixed requests"
  :url "https://github.com/whostolebenfrog/compojure-multipart"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [midje "1.4.0"]
                 [org.apache.commons/commons-email "1.2"]
                 [commons-fileupload/commons-fileupload "1.2.2"]]
  :profiles {:dev {:plugins [[lein-midje "2.0.0-SNAPSHOT"]]}})
