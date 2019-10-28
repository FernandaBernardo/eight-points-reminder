(ns eight-points-reminder.core-test
  (:require [clojure.test :refer :all]
            [eight-points-reminder.core :refer :all]))

(require '[clojure.test.check.generators :as gen])
(require '[clojure.test.check.properties :as prop])
(require '[clojure.test.check.clojure-test :refer [defspec]])
(import '(java.time LocalDate))

(def gen-date
  (gen/fmap (fn [[year day]]
              (.withDayOfYear (LocalDate/of year 1 1) day))
            (gen/tuple gen/nat (gen/large-integer* {:min 1 :max 364}))))

(defspec week-of-year>0-prop
  (prop/for-all [date gen-date]
    (< 0 (week-of-year date))))

(defspec week-of-year<max-of-weeks
  (prop/for-all [date gen-date]
    (<= (week-of-year date) (/ 364 7))))

(defspec always-generate-a-working-day-of-the-week-prop
  (prop/for-all [seed gen/nat]
    (#{1 2 3 4 5} (choose-day seed))))

(defspec always-generate-a-deterministic-positive-integer-prop
  (prop/for-all [seed  gen/int
                 seed2 gen/int]
    (and (pos? (pseudo-random seed))
         (= (pseudo-random seed) (pseudo-random seed))
         (or (= seed seed2)
             (not= (pseudo-random seed) (pseudo-random seed2))))))

(defspec always-works-prop
  (prop/for-all [date  gen-date
                 hour  gen/nat]
    (send-reminder? date hour)))
