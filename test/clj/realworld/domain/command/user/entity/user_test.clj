(ns realworld.domain.command.user.entity.user-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :refer [for-all]]
            [realworld.domain.command.user.entity.user :as user]
            [realworld.domain.command.user.value :as value]))

(defspec update-user-should-pass 100
  (for-all [user (s/gen ::user/user)
            username (s/gen ::value/username)
            email (s/gen ::value/email)
            hashed-password (s/gen ::value/hashed-password)
            bio (s/gen ::value/bio)
            image (s/gen ::value/image)]
           (not= (user/update-user user {:username username
                                         :email email
                                         :hashed-password hashed-password
                                         :bio bio
                                         :image image}) user)))

(defspec update-user-change-nothing 100
  (for-all [user (s/gen ::user/user)]
           (= (user/update-user user {}) user)))

(defspec update-user-idempotence 100
  (for-all [user (s/gen ::user/user)
            username (s/gen ::value/username)
            email (s/gen ::value/email)
            hashed-password (s/gen ::value/hashed-password)
            bio (s/gen ::value/bio)
            image (s/gen ::value/image)]
           (let [params {:username username
                         :email email
                         :hashed-password hashed-password
                         :bio bio
                         :image image}
                 updated-user (user/update-user user params)]
             (= (user/update-user user params) updated-user))))


