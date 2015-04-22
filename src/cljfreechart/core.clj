(ns cljfreechart.core
  (:require
    [clojure.java.io :as io]
    [clojure.string  :as str]
    [cljfreechart.internal :as i])
  (:import
    [java.util Date]
    [org.jfree.chart ChartFactory ChartUtilities JFreeChart]
    [org.jfree.chart.plot    PlotOrientation]
    [org.jfree.data.category CategoryDataset DefaultCategoryDataset]
    [org.jfree.data.general  PieDataset DefaultPieDataset]
    [org.jfree.data.time     RegularTimePeriod Millisecond Second Minute Hour Day Week Month Quarter Year
                             TimeSeries TimeSeriesCollection]
    [org.jfree.data.xy       XYDataset]))


;; ===== data preparation =====


(defn make-category-dataset
  "Make category dataset (useful for line chart, bar chart etc.) from given arguments.
  Chart data must be a list of maps. Options include:
  :group-key - the key used for not value, but for group name"
  ^DefaultCategoryDataset [chart-data options]
  (let [^DefaultCategoryDataset dataset (DefaultCategoryDataset.)
        {:keys [group-key]} options
        find-group-name (if group-key
                          #(get % group-key)
                          (let [a (atom 0)]
                            (fn [_] (swap! a inc))))]
    (doseq [each-map chart-data]
      (let [group-name (find-group-name each-map)]
        (doseq [[k v] (seq each-map)]
          (when-not (= group-key k)
            (.addValue dataset ^Number v ^Comparable group-name ^Comparable k)))))
    dataset))


(defn make-pie-dataset
  "Make pie dataset from specified arguments. Chart data must be a list of maps."
  ^PieDataset [chart-data]
  (let [^DefaultPieDataset dataset (DefaultPieDataset.)]
    (doseq [each-pair chart-data]
      (let [[label value] (if (map? each-pair)
                            (first each-pair)
                            each-pair)]
        (when (nil? label)
          (i/illegal-arg "Nil encountered for label"))
        (when (nil? value)
          (i/illegal-arg "Nil encountered for value"))
        (.setValue dataset ^Comparable label ^Number value)))
    dataset))


(defn make-time-series-dataset
  "Make time-series dataset (useful for graphing continuous events, e.g. metrics events) from given arguments. Chart
  data must be a list of maps. Options include:
  :name  - string, keyword or any Comparable type (required)
  :unit  - any of :millis, :second (default), :minute, :hour, :day, :week, :month, :year"
  [chart-data options]
  (let [ts-name (get options :name "Time-series data")
        ts-unit (get options :unit :second)
        all-ts-units #{:millis, :second, :minute, :hour, :day, :week, :month, :year}]
    (when-not (instance? Comparable ts-name)
      (i/illegal-arg "Expected :name to be an instance of Comparable but found" (pr-str ts-name)))
    (when-not (all-ts-units ts-unit)
      (i/illegal-arg
        "Expected timeseries unit to be either of :millis, :second (default), :minute, :hour, :day, :week, :month, :year but found"
        (pr-str ts-unit)))
    (let [mktu (fn ^RegularTimePeriod [x]
                 (cond
                   (instance? RegularTimePeriod x) x
                   (integer? x)                    (let [^Date d (Date. x)]
                                                     (condp = ts-unit
                                                       :millis (Millisecond. d)
                                                       :second (Second. d)
                                                       :minute (Minute. d)
                                                       :hour   (Hour. d)
                                                       :day    (Day. d)
                                                       :week   (Week. d)
                                                       :month  (Month. d)
                                                       :year   (Year. d)))
                   :otherwise                      (i/illegal-arg "Expected epochal timestamp (long) but found"
                                                     x)))
          ^TimeSeries series (TimeSeries. ts-name)]
      (doseq [each-map chart-data
              [k v] (seq each-map)]
        (.add series (mktu k) (double v)))
      (TimeSeriesCollection. series))))


;; ===== chart preparation =====

(defn make-category-chart
  "Make line chart from specified arguments. Option keys (with meaning) are below:
  :chart-type      :line-chart or :bar-chart (default) or :bar-chart-3d
  :category-title  category axis label (default \"Categories\")
  :value-title     value axis label (default \"Values\")
  :orientation     :horizontal or :vertical (default)
  :legend?         true (default) or false
  :tooltips?       true (default) or false"
  ^JFreeChart [^CategoryDataset dataset title options]
  (when-not (instance? CategoryDataset dataset)
    (i/illegal-arg "Expected CategoryDataset instance but found" dataset))
  (let [{:keys [chart-type category-title value-title orientation legend? tooltips?]
         :or {chart-type :bar-chart
              orientation :vertical
              category-title "Categories"
              value-title    "Values"
              legend? true
              tooltips? true}} options]
    (when-not (string? category-title)
      (i/illegal-arg "Expected :category-title option to be a string, but found" (pr-str category-title)))
    (when-not (string? value-title)
      (i/illegal-arg "Expected :value-title option to be a string, but found" (pr-str value-title)))
    (condp = chart-type
      :bar-chart    (ChartFactory/createBarChart title category-title value-title dataset
                      (if (= :horizontal orientation)
                        PlotOrientation/HORIZONTAL
                        PlotOrientation/VERTICAL)
                      legend? tooltips? false)
      :bar-chart-3d (ChartFactory/createBarChart3D title category-title value-title dataset
                      (if (= :horizontal orientation)
                        PlotOrientation/HORIZONTAL
                        PlotOrientation/VERTICAL)
                      legend? tooltips? false)
      :line-chart (ChartFactory/createLineChart title category-title value-title dataset
                    (if (= :horizontal orientation)
                      PlotOrientation/HORIZONTAL
                      PlotOrientation/VERTICAL)
                    legend? tooltips? false)
      (i/illegal-arg "Expected :chart-type option to be :bar-chart, :bar-chart-3d or :line-chart but found"
        (pr-str chart-type)))))


(defn make-bar-chart
  "Shortcut to make-category-chart (with {:chart-type :bar-chart} in options.)"
  ^JFreeChart [^CategoryDataset dataset title options]
  (make-category-chart dataset title (assoc options :chart-type :bar-chart)))


(defn make-bar-chart-3d
  "Shortcut to make-category-chart (with {:chart-type :bar-chart-3d} in options.)"
  ^JFreeChart [^CategoryDataset dataset title options]
  (make-category-chart dataset title (assoc options :chart-type :bar-chart-3d)))


(defn make-line-chart
  "Shortcut to make-category-chart (with {:chart-type :line-chart} in options.)"
  ^JFreeChart [^CategoryDataset dataset title options]
  (make-category-chart dataset title (assoc options :chart-type :line-chart)))


(defn make-pie-chart
  "Make pie chart from specified arguments. Chart data must be a list of pairs (either a list of single-pair maps,
  or list of two-element vectors.) Options may include the following:
  :chart-type :pie-chart (default) or :pie-chart-3d
  :legend?    true (default) or false
  :tooltips?  true (default) or false"
  ^JFreeChart [^PieDataset dataset title options]
  (when-not (instance? PieDataset dataset)
    (i/illegal-arg "Expected PieDataset instance but found" dataset))
  (let [{:keys [chart-type legend? tooltips?]
         :or {chart-type :pie-chart
              legend? true
              tooltips? true}} options]
    (condp = chart-type
      :pie-chart    (ChartFactory/createPieChart   ^String title ^PieDataset dataset ^boolean legend? ^boolean tooltips? false)
      :pie-chart-3d (ChartFactory/createPieChart3D ^String title ^PieDataset dataset ^boolean legend? ^boolean tooltips? false)
      (i/illegal-arg "Expected :chart-type option to be :pie-chart or :pie-chart-3d but found"
        (pr-str chart-type)))))


(defn make-pie-chart-3d
  "Short cut to make-pie-chart with {:chart-type :pie-chart-3d} in options."
  [dataset title options]
  (make-pie-chart dataset title (assoc options :chart-type :pie-chart-3d)))


(defn make-time-series-chart
  "Make line chart from specified arguments. Option keys (with meaning) are below:
  :time-title  time axis label (required)
  :value-title value axis label (required)
  :legend?     true (default) or false
  :tooltips?   true (default) or false"
  ^JFreeChart [^XYDataset dataset ^String title options]
  (when-not (instance? XYDataset dataset)
    (i/illegal-arg "Expected XYDataset instance but found" dataset))
  (let [{:keys [time-title value-title legend? tooltips?]
         :or {time-title  "Time"
              value-title "Values"
              legend? true
              tooltips? true}} options]
    (when-not (string? time-title)
      (i/illegal-arg "Expected :time-title option to be a string, but found" (pr-str time-title)))
    (when-not (string? value-title)
      (i/illegal-arg "Expected :value-title option to be a string, but found" (pr-str value-title)))
    (ChartFactory/createTimeSeriesChart title time-title value-title dataset legend? tooltips? false)))


;; ===== saving chart as file =====


(defn save-chart-as-file
  "Save the specified chart as file. Options can include the following:
  :width   +ve integer (default 640)
  :height  +ve integer (default 480)
  :image-format :png or :jpeg (autodiscovers from filename by default)"
  [^JFreeChart chart file-or-filename options]
  (let [{:keys [width height image-format]
         :or {width 640
              height 480
              image-format (if (string? file-or-filename)
                             (let [lower-name (str/lower-case file-or-filename)]
                               (cond
                                 (.endsWith lower-name ".png")  :png
                                 (.endsWith lower-name ".jpg")  :jpg
                                 (.endsWith lower-name ".jpeg") :jpeg
                                 :otherwise (i/illegal-arg "Expected PNG or JPEG file type but found"
                                              (pr-str file-or-filename))))
                             (i/illegal-arg "Expected :image-format option to be :png or :jpeg but found"
                               (pr-str (:image-format options))))}} options
        chart-file (io/as-file file-or-filename)]
    (condp = image-format
      :jpeg (ChartUtilities/saveChartAsJPEG chart-file chart width height)
      :jpg  (ChartUtilities/saveChartAsJPEG chart-file chart width height)
      :png  (ChartUtilities/saveChartAsPNG  chart-file chart width height)
      (i/illegal-arg "Expected image-format to be :png or :jpeg but found" (pr-str image-format)))))
