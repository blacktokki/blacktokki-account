package com.example.account.core.migration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumSet;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.hibernate.boot.Metadata;
import org.hibernate.tool.schema.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

@Service
public class SchemaUpdateService implements InitializingBean {


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private static final Logger log = LoggerFactory.getLogger(SchemaUpdateService.class);

    private static final String tempFileName =  Paths.get(System.getProperty("java.io.tmpdir"), "migrate.sql").toString();

    private static final String userDir = System.getProperty("user.dir") + "/";

    private static final String fileNamePrefix = "V1_0_";

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private DataSource dataSource;

    @Value("${spring.jpa.properties.application-name}")
    private String applicationName;

    @Override
    public void afterPropertiesSet() throws Exception {
        Metadata metadata = HibernateInfoHolder.getMetadata();
        SessionFactoryServiceRegistry serviceRegistry = HibernateInfoHolder.getServiceRegistry();
        org.hibernate.tool.hbm2ddl.SchemaUpdate schemaUpdate = new org.hibernate.tool.hbm2ddl.SchemaUpdate();
        schemaUpdate.setDelimiter(";");
        schemaUpdate.setOutputFile(tempFileName);
        schemaUpdate.setFormat(true);

        log.warn("Starting SCHEMA MIGRATION lookup------------------------------------------------------------------");
        // log.warn("please add the following SQL code (if any) to a flyway migration");
        String schemaName = "db1_" + applicationName;
        log.warn("Working on schema: " +  schemaName);
        schemaUpdate.execute(EnumSet.of(TargetType.SCRIPT, TargetType.STDOUT), metadata, serviceRegistry);
        File tempFile = new File(tempFileName);
        String directoryPath = (userDir.endsWith("account-service/")?userDir:userDir + "account-service/") + "src/main/resources/db/migration/";
        String path =  directoryPath + applicationName + "/";
        File[] fileList = new File(path).listFiles();
        Flyway flyway = Flyway.configure().dataSource(dataSource).schemas(schemaName).load();
        int sqlFileCount = fileList.length;
        int migrationCount = flyway.info().applied().length;
        log.warn("Number of migrations: " + sqlFileCount + " files / " + migrationCount+ " applied");
        String fileName = null;
        if (sqlFileCount <= migrationCount){
            fileName = path + fileNamePrefix + formatter.format(LocalDateTime.now()) + "__auto.sql";
        }
        else{
            Arrays.sort(fileList);
            fileName = fileList[migrationCount].getAbsolutePath();
        }
        File file = new File(fileName);
        if (tempFile.exists() && tempFile.length() != 0) {  // migrations present.
            log.warn("Migrations also written to: " + fileName);
            Files.copy(tempFile.toPath(),  file.toPath(), StandardCopyOption.REPLACE_EXISTING); 
        } else if (tempFile.exists()) {  // delete empty files
            log.warn("No migrations");
            if(file.exists()){
                Files.copy(tempFile.toPath(),  file.toPath(), StandardCopyOption.REPLACE_EXISTING); 
            }
        }
        tempFile.delete();
        log.warn("END OF SCHEMA MIGRATION lookup--------------------------------------------------------------------");
    }
}