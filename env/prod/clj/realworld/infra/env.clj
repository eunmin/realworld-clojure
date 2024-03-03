(ns realworld.infra.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[realworld starting]=-"))
   :start      (fn []
                 (log/info "\n-=[realworld started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[realworld has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})
