(ns realworld.infra.database.pg-util)

(defn to-string-pgarray [conn coll]
  (.createArrayOf conn "varchar" (into-array String coll)))

(defn from-pgarray [arr]
  (into [] (.getArray arr)))