-- ===========================================================
-- V1: テーブル作成
-- ゴルフレッスンアプリの基本テーブルを作成する
-- ===========================================================

-- アカウント（role: 1=一般ユーザー / 2=プロ / 9=管理者）
CREATE TABLE accounts (
    id       BIGSERIAL    PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     INTEGER      NOT NULL DEFAULT 1,
    image    VARCHAR(500)
);

-- レッスン
CREATE TABLE lessons (
    id          BIGSERIAL    PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    content     TEXT,
    image       VARCHAR(500),
    cause       TEXT,
    improvement TEXT,
    practice    TEXT,
    category    VARCHAR(50)
);

-- 投稿（プロによる改善記事）
CREATE TABLE posts (
    id          BIGSERIAL    PRIMARY KEY,
    pro_id      BIGINT       NOT NULL REFERENCES accounts (id) ON DELETE CASCADE,
    title       VARCHAR(200) NOT NULL,
    content     TEXT,
    image       VARCHAR(500),
    cause       TEXT,
    improvement TEXT,
    practice    TEXT,
    category    VARCHAR(50)
);

-- 予約（レッスン申込。status: 1=申込済み / 2=承認済み / 3=キャンセル）
CREATE TABLE reservations (
    id        BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT    NOT NULL REFERENCES lessons (id)  ON DELETE CASCADE,
    user_id   BIGINT    NOT NULL REFERENCES accounts (id) ON DELETE CASCADE,
    status    INTEGER   NOT NULL DEFAULT 1
);

-- 動画提出（status: 1=提出済み / 2=添削中 / 3=添削完了）
CREATE TABLE lesson_submissions (
    id        BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT    NOT NULL REFERENCES lessons (id)  ON DELETE CASCADE,
    user_id   BIGINT    NOT NULL REFERENCES accounts (id) ON DELETE CASCADE,
    comment   TEXT,
    video_url VARCHAR(500),
    feedback  TEXT,
    status    INTEGER   NOT NULL DEFAULT 1
);

-- 検索でよく使うカラムにインデックスを付与
CREATE INDEX idx_lessons_category        ON lessons (category);
CREATE INDEX idx_posts_pro_id            ON posts (pro_id);
CREATE INDEX idx_reservations_lesson_id  ON reservations (lesson_id);
CREATE INDEX idx_reservations_user_id    ON reservations (user_id);
CREATE INDEX idx_submissions_user_id     ON lesson_submissions (user_id);
CREATE INDEX idx_submissions_lesson_id   ON lesson_submissions (lesson_id);
