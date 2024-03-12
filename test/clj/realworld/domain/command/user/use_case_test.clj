(ns realworld.domain.command.user.use-case-test
  (:require [clojure.test :refer :all]
            [failjure.core :as f]
            [realworld.domain.command.user.use-case :as user-use-case :refer [->UserUseCaseImpl]]
            [realworld.fixture.password-gateway :as password-gateway-fixture :refer [->FixturePasswordGateway]]
            [realworld.fixture.token-gateway :as token-gateway-fixture :refer [->FixtureTokenGateway]]
            [realworld.fixture.user-repository :as user-repository-fixture :refer [->FixtureUserRepository]]))

(deftest registration
  (let [with-tx #(apply %2 %1)
        password-gateway (->FixturePasswordGateway)
        token-gateway (->FixtureTokenGateway)
        user-repository (->FixtureUserRepository)
        user-use-case (->UserUseCaseImpl with-tx password-gateway token-gateway user-repository)]

    (testing "should pass"
      (with-redefs [password-gateway-fixture/hash-password (fn [_ _] "hashed")
                    user-repository-fixture/find-by-username (fn [_ _] nil)
                    user-repository-fixture/find-by-email (fn [_ _] nil)
                    user-repository-fixture/save (fn [_ _] true)
                    token-gateway-fixture/generate (fn [_ _ _] "token")]
        (let [command {:username "username"
                       :email "username@example.com"
                       :password "abcd1234!@#$"}
              result (user-use-case/registration user-use-case command)]
          (is (f/ok? result))
          (is (= result {:token "token"})))))

    (testing "should fail when username is already taken"
      (with-redefs [password-gateway-fixture/hash-password (fn [_ _] "hashed")
                    user-repository-fixture/find-by-username (fn [_ _] true)]
        (let [command {:username "username"
                       :email "username@example.com"
                       :password "abcd1234!@#$"}
              result (user-use-case/registration user-use-case command)]
          (is (f/failed? result))
          (is (:message result) :registration-error/username-already-exists))))

    (testing "should fail when email is already taken"
      (with-redefs [password-gateway-fixture/hash-password (fn [_ _] "hashed")
                    user-repository-fixture/find-by-username (fn [_ _] nil)
                    user-repository-fixture/find-by-email (fn [_ _] true)]
        (let [command {:username "username"
                       :email "username@example.com"
                       :password "abcd1234!@#$"}
              result (user-use-case/registration user-use-case command)]
          (is (f/failed? result))
          (is (:message result) :registration-error/email-already-exists))))))