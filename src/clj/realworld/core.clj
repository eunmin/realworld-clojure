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
   [kit.edge.db.sql.migratus]

   ;; Use Cases
   [realworld.domain.command.user.use-case :refer [->UserUseCaseImpl]]

   ;; Repositories
   [realworld.infra.repository.pg-user-repository :refer [->PgUserRepository]]
   [realworld.infra.repository.pg-query-service :refer [->PgQueryService]]

   ;; Gateways
   [realworld.infra.gateway.bcrypt-password-gateway :refer [->BcryptPasswordGateway]]
   [realworld.infra.gateway.jwt-token-gateway :refer [->JwtTokenGateway]])
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

(defmethod ig/init-key :use-case/user [_ {:keys [with-tx
                                                 password-gateway
                                                 token-gateway
                                                 user-repository]}]
  (->UserUseCaseImpl with-tx password-gateway token-gateway user-repository))

(defmethod ig/init-key :repository/pg-user-repository [_ {:keys [query-fn]}]
  (->PgUserRepository query-fn))

(defmethod ig/init-key :gateway/bcrypt-password-gateway [_ _]
  (->BcryptPasswordGateway))

(defmethod ig/init-key :gateway/jwt-token-gateway [_ {:keys [secret]}]
  (->JwtTokenGateway secret))

(defmethod ig/init-key :service/query-service [_ {:keys [query-fn]}]
  (->PgQueryService query-fn))