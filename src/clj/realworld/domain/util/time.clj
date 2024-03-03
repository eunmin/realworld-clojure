(ns realworld.domain.util.time
  (:import [java.time Instant]))

(defn now []
  (Instant/now))
