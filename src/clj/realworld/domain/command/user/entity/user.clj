(ns realworld.domain.command.user.entity.user
  (:require [clojure.spec.alpha :as s]
            [realworld.domain.command.user.value :as value]))

(s/def ::created-at inst?)

(s/def ::updated-at (s/nilable inst?))

(s/def ::user (s/keys :req-un [::value/user-id
                               ::value/username
                               ::value/email
                               ::value/hashed-password
                               ::value/bio
                               ::value/image
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

(defn make-user [{:keys [user-id username email hashed-password created-at]}]
  (let [user (map->User {:user-id user-id
                         :username username
                         :email email
                         :hashed-password hashed-password
                         :bio ""
                         :image nil
                         :created-at created-at
                         :updated-at nil})]
    (when (s/valid? ::user user)
      user)))

(defn update-user [user data]
  (merge user (select-keys data [:username :email :hashed-password :bio :image])))