(defproject cljfreechart "0.1.0"
  :description "Clojure wrapper for the JFreeChart library"
  :url "https://github.com/kumarshantanu/cljfreechart"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.jfree/jfreechart "1.0.19"]]
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]]}})
