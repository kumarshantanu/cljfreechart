# cljfreechart

Clojure wrapper for the [JFreeChart](http://www.jfree.org/jfreechart/) library.


## Installation

Leiningen coordinates: `[cljfreechart "0.2.0"]`


## Usage

```clojure
(require '[cljfreechart.core :as c])
```

### Bar chart

```clojure
(def bar-data [{:brand "fiat" :speed 1.0 :userrating 3.0 :mileage  5.0 :safety 5.0}
               {:brand "audi" :speed 5.0 :userrating 6.0 :mileage 10.0 :safety 4.0}
               {:brand "ford" :speed 4.0 :userrating 2.0 :mileage  3.0 :safety 6.0}])

(def bar-chart (-> bar-data
                 (c/make-category-dataset {:group-key :brand})
                 (c/make-bar-chart "Car usage statistics" {:category-title "Category"
                                                           :value-title "Score"})))

(c/save-chart-as-file
  bar-chart "bar-chart.png" {})
```

### Pie chart

```clojure
(def pie-data [{"iPhone 5s" 20}
               {"Samsung Grand" 20}
               {"MotoG" 40}
               {"Nokia Lumia" 10}])

(def pie-chart (-> pie-data
                 (c/make-pie-dataset)
                 (c/make-pie-chart "Mobile sales" {})))

(c/save-chart-as-file
  pie-chart "pie-chart.png" {})
```

### Time-series chart

```clojure
(def time-series-data [{1295384773401 45}  ; data spaced out by 1000ms so that it can fit in as :second
                       {1295384774401 61}
                       {1295384775401 39}
                       {1295384776401 48}
                       {1295384777401 53}])

(def time-series-chart (-> time-series-data
                         (c/make-time-series-dataset {})
                         (c/make-time-series-chart "Time-series plot" {})))

(c/save-chart-as-file
  time-series-chart "time-series-chart.png" {})
```

## License

Copyright © 2015 Shantanu Kumar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
