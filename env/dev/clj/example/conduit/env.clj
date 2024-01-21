(ns example.conduit.env
  (:require
    [clojure.tools.logging :as log]
    [example.conduit.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[conduit starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[conduit started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[conduit has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev
                :persist-data? true}})
