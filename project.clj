(defproject cljfreechart "0.2.0"
  :description "Clojure wrapper for the JFreeChart library"
  :url "https://github.com/kumarshantanu/cljfreechart"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.jfree/jfreechart "1.5.0"]]
  :target-path "target/%s"
  :global-vars {*assert* true
                *warn-on-reflection* true
                *unchecked-math* :warn-on-boxed}
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.10.0"]]}
             :c07 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :c08 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :c09 {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :c10 {:dependencies [[org.clojure/clojure "1.10.0"]]}})
