(ns example.conduit.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[conduit starting]=-"))
   :start      (fn []
                 (log/info "\n-=[conduit started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[conduit has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})
