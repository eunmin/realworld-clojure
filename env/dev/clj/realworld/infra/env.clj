(ns realworld.infra.env
  (:require
   [clojure.tools.logging :as log]
   [realworld.infra.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[realworld starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[realworld started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[realworld has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev
                :persist-data? true}})
