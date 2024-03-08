(ns realworld.infra.web.controllers.article)

(defn list-articles [{:keys [token] :as req}])

(defn feed-articles [{:keys [token] :as req}])

(defn get-article [{:keys [token] :as req}])

(defn create-article [{:keys [token] :as req}])

(defn update-article [{:keys [token] :as req}])

(defn delete-article [{:keys [token] :as req}])

(defn add-comment [{:keys [token] :as req}])

(defn get-comments [{:keys [token] :as req}])

(defn delete-comment [{:keys [token] :as req}])

(defn favorite [{:keys [token] :as req}])

(defn unfavorite [{:keys [token] :as req}])

(defn get-tags [{:keys [token] :as req}])
