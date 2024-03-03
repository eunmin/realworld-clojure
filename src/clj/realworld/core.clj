(ns realworld.core
  (:require
   [clojure.tools.logging :as log]
   [integrant.core :as ig]
   [realworld.infra.config :as config]
   [realworld.infra.env :refer [defaults]]

    ;; Edges       
   [kit.edge.server.undertow]
   [realworld.infra.web.handler]

    ;; Routes
   [realworld.infra.web.routes.api]

   [kit.edge.db.sql.conman]
   [kit.edge.db.sql.migratus])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (log/error {:what :uncaught-exception
                 :exception ex
                 :where (str "Uncaught exception on" (.getName thread))}))))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!))
  (shutdown-agents))

(defn start-app [& [params]]
  ((or (:start params) (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (ig/prep)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& _]
  (start-app))

;; (defmethod ig/init-key :use-case/user [_ {:keys [tx user-repo token-gateway password-service]}]
;;   (->UserUseCaseImpl tx user-repo token-gateway password-service))

;; (defmethod ig/init-key :repo/user [_ {:keys [query-fn]}]
;;   (->PGUserRepository query-fn))

;; (defmethod ig/init-key :gateway/token [_ {:keys [secret]}]
;;   (->JWTTokenGateway secret))

;; (defmethod ig/init-key :service/password [_ _]
;;   (->BCryptPasswordService))