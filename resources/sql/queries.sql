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
SELECT id as user_id, * FROM users WHERE id = :id

-- :name find-user-by-username :? :1
SELECT id as user_id, * FROM users WHERE username = :username

-- :name find-user-by-email :? :1
SELECT id as user_id, * FROM users WHERE email = :email

-- :name find-profile-with-following :? :1 
SELECT username, bio, image, 
  CASE WHEN f.created_at IS null THEN false ELSE true END following 
FROM users u 
LEFT JOIN followings f ON u.id = f.following_id AND f.user_id = :actor-id
WHERE username = :username

-- :name find-profile :? :1
SELECT username, bio, image , false as following FROM users WHERE username = :username

-- :name follow :! :n
INSERT INTO followings (user_id, following_id) VALUES (:follower-id, :followee-id)

-- :name unfollow :! :n
DELETE FROM followings WHERE user_id = :follower-id AND following_id = :followee-id

-- :name find-following :? :1
SELECT COUNT(*) FROM followings WHERE user_id = :follower-id AND following_id = :followee-id