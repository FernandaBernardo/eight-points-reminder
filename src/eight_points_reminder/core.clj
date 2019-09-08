(ns eight-points-reminder.core
  (:gen-class))

(require '[org.httpkit.client :as http])
(import '(java.util Calendar))
(import '(java.util Date))
(import '(java.time LocalDate))
(import '(java.time LocalDateTime))
(import '(java.time ZoneId))

(defn pseudo-random
  [seed]
  (Math/abs (.nextInt (java.util.Random. seed))))

(defn choose-day [seed]
  (+ (mod seed 5) 2))

(defn week-of-year
  [date]
  (let [cal (Calendar/getInstance)]
    (.setTime cal (Date/from (.toInstant (.atStartOfDay date (ZoneId/systemDefault)))))
    (.get cal Calendar/WEEK_OF_YEAR)))

(def reminder-hours #{10 14 22})
(def midnight-hour 3)

(defn send-reminder? [date hour]
  (let [day-choosen (-> date week-of-year pseudo-random choose-day)
        today-day   (-> date .getDayOfWeek .getValue)
        next-day (inc day-choosen)]
    (or
     (and (= today-day day-choosen)
          (reminder-hours hour))
     (and (= next-day today-day)
          (= midnight-hour hour)))))

(def webhook "https://maker.ifttt.com/trigger/eight_points_reminder/with/key/fq2eLYq92Lzwo32iilhV5JgXvZYrgk8ct-9SsrGSL_6")

(defn -main [& args]
  (let [time  (LocalDateTime/now)
        today (LocalDate/now)
        hour  (.getHour time)]
    (when (send-reminder? today hour)
      (do
       (println "Sending reminder!")
       (http/post webhook)))))

-main
