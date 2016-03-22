(defproject com.floatbackwards/multipart "0.0.8"
  :description "Ring middleware for parsing multipart/mixed requests"

  :url "https://github.com/whostolebenfrog/compojure-multipart"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.apache.commons/commons-email "1.3.3"]
                 [commons-fileupload/commons-fileupload "1.3.1"]]

  :profiles {:dev {:plugins [[lein-midje "3.1.3"]
                             [lein-release "1.0.5"]]
                   :dependencies [[midje "1.6.3"]]}}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins [[lein-release "1.1.3"]]

  :lein-release {:release-tasks [:clean :uberjar :pom]
                 :clojars-url "clojars@clojars.org:"}

  :uberjar-name "multipart.jar"

  :aot [ring.TooMuchContent])
