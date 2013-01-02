(defproject com.floatbackwards/multipart "0.0.7-SNAPSHOT"
  :description "Ring middleware for parsing multipart/mixed requests"

  :url "https://github.com/whostolebenfrog/compojure-multipart"

  :dependencies [[org.clojure/clojure "1.4.0"]
                 [midje "1.4.0"]
                 [org.apache.commons/commons-email "1.2"]
                 [commons-fileupload/commons-fileupload "1.2.2"]]
  :profiles {:dev {:plugins [[lein-midje "2.0.0-SNAPSHOT"]]}}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins [[lein-release "1.0.0"]]

  :lein-release {:release-tasks [:clean :uberjar :pom]
                 :clojars-url "clojars@clojars.org:"}

  :uberjar-name "multipart.jar"

  :aot [ring.TooMuchContent])
