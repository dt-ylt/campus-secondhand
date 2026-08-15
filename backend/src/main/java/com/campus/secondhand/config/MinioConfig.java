package com.campus.secondhand.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * MinIO 配置类
 *
 * 1. 创建 MinioClient（连接 MinIO 服务的客户端），注册成 Bean 供上传服务使用
 * 2. 项目启动时自动检查/创建 bucket（存图片的"桶"），并设为公开可读
 *    （公开可读 = 拿到图片URL的人都能直接看，这样前端才能显示图片）
 */
@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.bucket}")
    private String bucket;

    /**
     * MinioClient：操作 MinIO 的客户端对象，上传文件全靠它
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * @PostConstruct：这个 Bean 创建完后自动执行一次
     * 检查 bucket 是否存在，不存在就创建，然后设为公开可读
     */
    @PostConstruct
    public void initBucket() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("MinIO bucket [{}] 已创建", bucket);
            }

            // 设为公开可读：任何人拿到 URL 都能 GET 这里的文件（开发环境用，生产要改签名URL）
            String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\","
                    + "\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],"
                    + "\"Resource\":[\"arn:aws:s3:::" + bucket + "/*\"]}]}";
            client.setBucketPolicy(
                    SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
            log.info("MinIO bucket [{}] 已设为公开可读", bucket);
        } catch (Exception e) {
            log.error("MinIO bucket 初始化失败：{}", e.getMessage());
        }
    }
}
