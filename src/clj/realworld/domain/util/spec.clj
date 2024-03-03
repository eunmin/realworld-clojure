(ns realworld.domain.util.spec
  (:require [clojure.spec.alpha :as s]))

(defn spec-validate [spec data]
  (-> (s/explain-data spec data)
      ::s/problems
      first
      :via))