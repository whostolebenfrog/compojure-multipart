(defproject multipart "0.0.1-SNAPSHOT"
  :description "Testing out multipart data in compojure"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.0"]
                 [enlive "1.0.1"]]
  :plugins [[lein-ring "0.7.1"]
             [jonase/kibit "0.0.4"]
             [lein-swank "1.4.4"]]
  :ring {:handler multipart.core/app})
