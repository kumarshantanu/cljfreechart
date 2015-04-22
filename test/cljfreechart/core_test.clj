(ns cljfreechart.core-test
  (:require [clojure.test :refer :all]
            [cljfreechart.core :as c])
  (:import
    [org.jfree.chart JFreeChart]))


(def bar-data [{:brand "fiat" :speed 1.0 :userrating 3.0 :mileage  5.0 :safety 5.0}
               {:brand "audi" :speed 5.0 :userrating 6.0 :mileage 10.0 :safety 4.0}
               {:brand "ford" :speed 4.0 :userrating 2.0 :mileage  3.0 :safety 6.0}])


(def line-data [{:group "schools" "1970" 15}
                {:group "schools" "1980" 30}
                {:group "schools" "1990" 60}
                {:group "schools" "2000" 120}
                {:group "schools" "2010" 240}
                {:group "schools" "2014" 300}])


(deftest test-category-chart
  (testing "Bar chart"
    (is (instance? JFreeChart (-> bar-data
                                (c/make-category-dataset {:group-key :brand})
                                (c/make-bar-chart "Car usage statistics" {:category-title "Category"
                                                                          :value-title "Score"})))))
  (testing "Bar chart 3D"
    (is (instance? JFreeChart (-> bar-data
                                (c/make-category-dataset {:group-key :brand})
                                (c/make-bar-chart-3d "Car usage statistics" {:category-title "Category"
                                                                             :value-title "Score"})))))
  (testing "Line chart"
    (is (instance? JFreeChart (-> line-data
                                (c/make-category-dataset {:group-key :group})
                                (c/make-line-chart "Schools vs Years" {:category-title "Year"
                                                                       :value-title "Schools Count"}))))))


(def pie-data [{"iPhone 5s" 20}
               {"Samsung Grand" 20}
               {"MotoG" 40}
               {"Nokia Lumia" 10}])


(deftest test-pie-chart
  (testing "Pie chart"
    (is (instance? JFreeChart (-> pie-data
                                (c/make-pie-dataset)
                                (c/make-pie-chart "Mobile sales" {})))))
  (testing "Pie chart 3D"
    (is (instance? JFreeChart (-> pie-data
                                (c/make-pie-dataset)
                                (c/make-pie-chart-3d "Mobile sales" {}))))))


(def time-series-data [{1295384773401 45}  ; data spaced out by 1000ms so that it can fit in as :second
                       {1295384774401 61}
                       {1295384775401 39}
                       {1295384776401 48}
                       {1295384777401 53}])


(deftest test-time-series-chart
  (testing "Time-series chart"
    (is (instance? JFreeChart (-> time-series-data
                                (c/make-time-series-dataset {})
                                (c/make-time-series-chart "Time-series plot" {}))))))
