package com.golf.app.golfapp.config;

import org.flywaydb.core.api.output.RepairResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway の起動時動作をカスタマイズする。
 *
 * <p>V1/V2 は一度DBに適用された後にファイル側を直接書き換えてしまったため、
 * flyway_schema_history に記録されたチェックサムと一致しなくなっている。
 * この状態では migrate() の前段の validate が失敗し、Spring の
 * flywayInitializer Bean の生成に失敗してアプリが起動できない
 * (= コンテナが再起動を繰り返し、Elastic Beanstalk のポート80が閉じたまま
 * になり、CloudFront が 504 を返す)。
 *
 * <p>repair() は flyway_schema_history のチェックサムをファイル側の内容に
 * 合わせ直し、失敗記録を取り除く。これで validate を通せるようにしてから
 * migrate() を実行する。チェックサムが揃うだけでスキーマ自体のズレは
 * 解消しないため、実体のカラム差分は V3 で前進させている。
 */
@Configuration
public class FlywayConfig {

    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);

    /**
     * migrate() の前に repair() を実行する。
     *
     * <p>常時有効にすると「適用済みマイグレーションを書き換えてしまった」こと自体を
     * Flyway が検知できなくなる。移行が落ち着いた後に無効化できるよう、
     * {@code app.flyway.repair-before-migrate=false} で切れるようにしている。
     */
    @Bean
    @ConditionalOnProperty(name = "app.flyway.repair-before-migrate",
            havingValue = "true", matchIfMissing = true)
    public FlywayMigrationStrategy repairBeforeMigrate() {
        return flyway -> {
            RepairResult repair = flyway.repair();
            log.info("Flyway repair completed: aligned={}, removed={}, deleted={}",
                    repair.migrationsAligned.size(),
                    repair.migrationsRemoved.size(),
                    repair.migrationsDeleted.size());
            flyway.migrate();
        };
    }
}
