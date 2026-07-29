-- ===========================================================
-- V3: 稼働中DBのスキーマを V1/V2 の現行定義に合わせる
--
-- 背景:
--   V1/V2 は一度DBに適用された後、複数のコミットで直接書き換えられた。
--   Flyway は適用済みバージョンを再実行しないため、稼働中DB
--   (Elastic Beanstalk 上の postgres コンテナのボリューム)には
--   最初に適用された時点の古いスキーマが残っている。
--   具体的には accounts のプロフィール系カラム、lessons.pro_id / video、
--   lesson_submissions.reservation_id / feedback_video_url が存在せず、
--   逆に廃止済みの posts テーブルが残っている。
--
--   このマイグレーションは、その古いスキーマを現行定義まで前進させる。
--   現行 V1/V2 から作られた新規DB(ローカル開発環境など)では対象が
--   すべて既に存在するため、何も変更しない。
--   そのため全ての操作を冪等(IF NOT EXISTS 等)にしている。
-- ===========================================================

-- accounts: プロフィール系カラム -------------------------------
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS profile       TEXT;
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS favorite_club VARCHAR(255);
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS best_score    INTEGER;
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS sns           VARCHAR(255);

-- lessons: 担当プロ・動画 --------------------------------------
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS pro_id BIGINT;
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS video  VARCHAR(500);

-- 旧スキーマのレッスンには担当プロの情報が無いため、プロ(role=2)を割り当てる。
-- プロが居ない場合は最小IDのアカウントで代替する。
-- どちらも取得できない(=アカウントが1件も無い)状態でレッスンが存在する場合は、
-- 後段の SET NOT NULL が失敗してマイグレーションが中断する。
-- 不整合を黙って通さないため、あえてそのままエラーにしている。
UPDATE lessons
SET pro_id = COALESCE(
        (SELECT MIN(id) FROM accounts WHERE role = 2),
        (SELECT MIN(id) FROM accounts)
             )
WHERE pro_id IS NULL;

-- 外部キーは IF NOT EXISTS が使えないため、対象カラムに既にFKが
-- 付いているかを pg_constraint で確認してから追加する。
-- (新規DBでは V1 が自動生成名 lessons_pro_id_fkey で作成済み)
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM pg_constraint c
                                JOIN pg_attribute a
                                     ON a.attrelid = c.conrelid
                                         AND a.attnum = ANY (c.conkey)
                       WHERE c.conrelid = 'lessons'::regclass
                         AND c.contype = 'f'
                         AND a.attname = 'pro_id') THEN
            ALTER TABLE lessons
                ADD CONSTRAINT lessons_pro_id_fkey
                    FOREIGN KEY (pro_id) REFERENCES accounts (id) ON DELETE CASCADE;
        END IF;
    END
$$;

ALTER TABLE lessons ALTER COLUMN pro_id SET NOT NULL;

-- lesson_submissions: 予約への紐付け・添削動画 ------------------
ALTER TABLE lesson_submissions ADD COLUMN IF NOT EXISTS reservation_id     BIGINT;
ALTER TABLE lesson_submissions ADD COLUMN IF NOT EXISTS feedback_video_url VARCHAR(500);

-- 動画提出は「予約に対する提出」なので、対応する予約が無い旧データには
-- 予約(status=2:承認済み)を作ってから紐付ける。
INSERT INTO reservations (lesson_id, user_id, status)
SELECT DISTINCT s.lesson_id, s.user_id, 2
FROM lesson_submissions s
WHERE s.reservation_id IS NULL
  AND NOT EXISTS (SELECT 1
                  FROM reservations r
                  WHERE r.lesson_id = s.lesson_id
                    AND r.user_id = s.user_id);

UPDATE lesson_submissions s
SET reservation_id = (SELECT MIN(r.id)
                      FROM reservations r
                      WHERE r.lesson_id = s.lesson_id
                        AND r.user_id = s.user_id)
WHERE s.reservation_id IS NULL;

DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM pg_constraint c
                                JOIN pg_attribute a
                                     ON a.attrelid = c.conrelid
                                         AND a.attnum = ANY (c.conkey)
                       WHERE c.conrelid = 'lesson_submissions'::regclass
                         AND c.contype = 'f'
                         AND a.attname = 'reservation_id') THEN
            ALTER TABLE lesson_submissions
                ADD CONSTRAINT lesson_submissions_reservation_id_fkey
                    FOREIGN KEY (reservation_id) REFERENCES reservations (id) ON DELETE CASCADE;
        END IF;
    END
$$;

ALTER TABLE lesson_submissions ALTER COLUMN reservation_id SET NOT NULL;

-- 現行 V1 で追加されたインデックス ------------------------------
CREATE INDEX IF NOT EXISTS idx_lessons_pro_id             ON lessons (pro_id);
CREATE INDEX IF NOT EXISTS idx_submissions_reservation_id ON lesson_submissions (reservation_id);

-- 廃止テーブルの削除 ------------------------------------------
-- posts は現行の V1 から削除済みで、アプリのコード(mapper/model/template)からも
-- 参照されていない。旧スキーマのDBにのみ残っているため削除して定義を揃える。
DROP TABLE IF EXISTS posts;

-- シーケンスを最新IDに合わせる(上の INSERT で予約を追加した場合に備える)------
-- 第3引数(is_called)を予約の有無で切り替えることで、予約が0件のときは
-- 次の nextval が 1 を返すようにする。
SELECT setval('reservations_id_seq',
              COALESCE((SELECT MAX(id) FROM reservations), 1),
              (SELECT COUNT(*) > 0 FROM reservations));
