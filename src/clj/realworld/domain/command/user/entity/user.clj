(ns realworld.domain.command.user.entity.user
  (:require [clojure.spec.alpha :as s]))

(s/def ::user (s/keys :req-un [::id
                               ::username
                               ::email
                               ::hashed-password
                               ::bio
                               ::image
                               ::created-at
                               ::updated-at]))

(defrecord User [id
                 username
                 email
                 hashed-password
                 bio
                 image
                 created-at
                 updated-at])

(defn make-user [{:keys [id username email hashed-password created-at]}]
  (map->User {:id id
              :username username
              :email email
              :hashed-password hashed-password
              :bio ""
              :image nil
              :created-at created-at
              :updated-at nil}))

(defn update-user [user data]
  (merge user (select-keys data [:username :email :hashed-password :bio :image])))

