(defproject sudoku "1.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [iloveponies.tests/sudoku "0.1.0-SNAPSHOT"]]
  :profiles {:dev {:plugins [[lein-midje "3.2-RC4"]
                             [lein-gorilla "0.4.0"]]}})
