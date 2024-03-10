(ns realworld.domain.command.user.entity.user
  (:require [clojure.spec.alpha :as s]
            [realworld.domain.command.user.value :as value]))

(s/def ::user (s/keys :req-un [::value/user-id
                               ::value/username
                               ::value/email
                               ::value/hashed-password
                               ::value/bio
                               ::value/image
                               ::value/created-at
                               ::value/updated-at]))

(defrecord User [user-id
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

(defn update-user [user {:keys [username email hashed-password bio image]}]
  (let [user' (merge user
                     {:username (or username (:username user))
                      :email (or email (:email user))
                      :hashed-password (or hashed-password (:hashed-password user))
                      :bio (or bio (:bio user))
                      :image (if image
                               (:value image)
                               (:image user))})]
    (when (s/valid? ::user user')
      user')))
