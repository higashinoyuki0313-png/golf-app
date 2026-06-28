-- ===========================================================
-- V2: 初期データ投入
-- 動作確認用のアカウント・レッスン・投稿・予約・提出を登録する
-- パスワードは全て「123」（学習用のため平文）
-- ===========================================================

-- アカウント -------------------------------------------------
INSERT INTO accounts (id, name, email, password, role, image) VALUES
    (1, '管理者',       'admin@gmail.com', '123', 9, NULL),
    (2, '一般ユーザー', 'user@gmail.com',  '123', 1, NULL),
    (3, '東野プロ',     'pro@gmail.com',   '123', 2, 'https://placehold.co/300x300');

-- レッスン ---------------------------------------------------
INSERT INTO lessons (id, title, content, image, cause, improvement, practice, category) VALUES
    (1, '飛距離アップレッスン', '飛距離を伸ばしたい方向けのレッスンです。', NULL,
        '体の回転が使えず腕だけで振っている', '下半身リードでの体重移動を習得する', '連続素振りで回転の感覚を身につける', 'distance'),
    (2, 'スライス改善レッスン', '右に曲がるミスを改善するレッスンです。', NULL,
        'アウトサイドインの軌道になっている', 'インサイドからクラブを下ろす', 'ハーフスイングで軌道を確認する', 'slice'),
    (3, 'フック改善レッスン', '左に曲がるミスを改善するレッスンです。', NULL,
        '手首の返しが強すぎる', 'フェースの開閉を抑える', 'グリップを確認しながらゆっくり振る', 'hook'),
    (4, 'ミート率アップレッスン', '芯でボールを捉えるためのレッスンです。', NULL,
        'スイング軌道が安定していない', '一定のリズムで振る', 'ティーアップして芯で捉える練習をする', 'contact'),
    (5, 'ショートゲームレッスン', 'アプローチとパターを改善するレッスンです。', NULL,
        '距離感が合っていない', '振り幅で距離をコントロールする', '時計の文字盤をイメージした振り幅練習', 'shortgame');

-- 投稿 -------------------------------------------------------
INSERT INTO posts (id, pro_id, title, content, image, cause, improvement, practice, category) VALUES
    (1, 3, 'スライスを直す3つのポイント', 'アマチュアに多いスライスの原因と直し方を解説します。', NULL,
        'アウトサイドインの軌道', 'インサイドアウトの軌道へ修正', '右手一本での素振り', 'slice'),
    (2, 3, '飛距離を伸ばす体の使い方', '効率よく力を伝えて飛距離を伸ばすコツを紹介します。', NULL,
        '手打ちになっている', '下半身からの始動を意識する', 'メディシンボール投げで連動を覚える', 'distance');

-- 予約（一般ユーザーが飛距離アップレッスンを申込済み）-----------
INSERT INTO reservations (id, lesson_id, user_id, status) VALUES
    (1, 1, 2, 1);

-- 動画提出（一般ユーザーがスライス改善レッスンへ提出済み）-------
INSERT INTO lesson_submissions (id, lesson_id, user_id, comment, video_url, feedback, status) VALUES
    (1, 2, 2, 'スライスが直りません。確認お願いします。', 'https://example.com/swing.mp4', NULL, 1);

-- シーケンスを最新IDに合わせる（以降のINSERTでID重複を防ぐ）------
SELECT setval('accounts_id_seq',           (SELECT MAX(id) FROM accounts));
SELECT setval('lessons_id_seq',            (SELECT MAX(id) FROM lessons));
SELECT setval('posts_id_seq',              (SELECT MAX(id) FROM posts));
SELECT setval('reservations_id_seq',       (SELECT MAX(id) FROM reservations));
SELECT setval('lesson_submissions_id_seq', (SELECT MAX(id) FROM lesson_submissions));
