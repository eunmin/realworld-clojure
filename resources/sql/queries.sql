-- :name insert-user :! :n
INSERT INTO users (id, username, email, hashed_password, created_at) 
VALUES (:id, :username, :email, :hashed-password, :created-at)

-- :name find-user-by-id :? :1
SELECT * FROM users WHERE id = :id

-- :name find-user-by-username :? :1
SELECT * FROM users WHERE username = :username

-- :name find-user-by-email :? :1
SELECT * FROM users WHERE email = :email