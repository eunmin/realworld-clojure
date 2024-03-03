-- :name save :! :n
INSERT INTO users (id, username, email, hashed_password, bio, image, created_at) 
VALUES (:user-id, :username, :email, :hashed-password, :bio, :image, :created-at)
ON CONFLICT (id) DO UPDATE SET
  username = :username,
  email = :email,
  hashed_password = :hashed-password,
  bio = :bio,
  image = :image,
  updated_at = now()

-- :name find-user-by-id :? :1
SELECT * FROM users WHERE id = :id

-- :name find-user-by-username :? :1
SELECT * FROM users WHERE username = :username

-- :name find-user-by-email :? :1
SELECT * FROM users WHERE email = :email