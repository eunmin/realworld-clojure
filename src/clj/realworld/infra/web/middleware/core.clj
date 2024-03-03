(ns realworld.infra.web.middleware.core
  (:require [clojure.string :as str]
            [realworld.infra.env :as env]
            [ring.middleware.defaults :as defaults]
            [ring.middleware.session.cookie :as cookie]
            [ring.util.http-response :refer [forbidden]]))

(defn wrap-base
  [{:keys [metrics site-defaults-config cookie-secret] :as opts}]
  (let [cookie-store (cookie/cookie-store {:key (.getBytes ^String cookie-secret)})]
    (fn [handler]
      (cond-> ((:middleware env/defaults) handler opts)
        true (defaults/wrap-defaults
              (assoc-in site-defaults-config [:session :store] cookie-store))))))

(defn- get-token [req]
  (let [[_ token] (str/split (or (get-in req [:headers "authorization"]) "") #" ")]
    token))

(defn wrap-optional-token [handler]
  (fn [req]
    (handler (assoc req :token (get-token req)))))

(defn wrap-required-token [handler]
  (fn [req]
    (if-let [token (get-token req)]
      (handler (assoc req :token token))
      (forbidden {:errors {:body ["Token is required"]}}))))
