#!/bin/bash

cd ~/workplace/logistics-platform/backend-java || { echo "❌ Directory not found"; read -p "Press Enter to exit"; exit 1; }

echo "🔄 Switching from PostgreSQL to MongoDB Atlas..."
echo "⏳ This will overwrite: pom.xml, application.yml, entities, repositories, main class."

# Create a backup folder
mkdir -p backup
cp pom.xml backup/ 2>/dev/null || true
cp src/main/resources/application.yml backup/ 2>/dev/null || true

# 1. Overwrite pom.xml
echo "📦 Writing pom.xml..."
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.2</version>
        <relativePath/>
    </parent>
    <groupId>com.logistics</groupId>
    <artifactId>logistics-backend</artifactId>
    <version>1.0.0</version>
    <properties>
        <java.version>21</java.version>
        <jjwt.version>0.12.5</jjwt.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# 2. Overwrite application.yml
echo "⚙️ Writing application.yml..."
mkdir -p src/main/resources
cat > src/main/resources/application.yml << 'EOF'
spring:
  application:
    name: logistics-backend
  data:
    mongodb:
      uri: ${SPRING_DATA_MONGODB_URI}
  cache:
    type: none
  security:
    jwt:
      secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
      expiration: 86400000

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    com.logistics: DEBUG
    org.springframework.security: INFO

app:
  concurrency:
    core-pool-size: 10
    max-pool-size: 50
    queue-capacity: 1000
EOF

# 3. Entities (MongoDB versions)
echo "📄 Writing entity classes..."
# BaseEntity
cat > src/main/java/com/logistics/model/BaseEntity.java << 'EOF'
package com.logistics.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {
    @Id
    private String id;
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
EOF

# Role
cat > src/main/java/com/logistics/model/Role.java << 'EOF'
package com.logistics.model;

public enum Role {
    ADMIN, CUSTOMER, AGENT
}
EOF

# User
cat > src/main/java/com/logistics/model/User.java << 'EOF'
package com.logistics.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
public class User extends BaseEntity {
    @NotBlank
    private String name;
    @Email @NotBlank
    @Indexed(unique = true)
    private String email;
    @NotBlank
    private String password;
    private Role role = Role.CUSTOMER;
    private boolean active = true;
}
EOF

# Shipment
cat > src/main/java/com/logistics/model/Shipment.java << 'EOF'
package com.logistics.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Document(collection = "shipments")
public class Shipment extends BaseEntity {
    @NotBlank
    @Indexed(unique = true)
    private String trackingNumber;
    private String customerId;
    private String origin;
    private String destination;
    private String status;
    private LocalDateTime eta;
    @Positive
    private Double amount;
    private String priority;
}
EOF

# TrackingEvent
cat > src/main/java/com/logistics/model/TrackingEvent.java << 'EOF'
package com.logistics.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "tracking_events")
public class TrackingEvent extends BaseEntity {
    @NotBlank
    private String shipmentId;
    private String eventType;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime occurredAt = LocalDateTime.now();
}
EOF

# Warehouse, DeliveryAgent, Notification, AuditLog
for model in Warehouse DeliveryAgent Notification AuditLog; do
    cat > src/main/java/com/logistics/model/${model}.java << EOF
package com.logistics.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "${model}s".toLowerCase())
public class ${model} extends BaseEntity {
    @NotBlank
    private String name; // adjust as needed
}
EOF
done

# 4. Repositories (MongoDB)
echo "📁 Writing repositories..."
cat > src/main/java/com/logistics/repository/BaseRepository.java << 'EOF'
package com.logistics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends MongoRepository<T, ID> {
}
EOF

cat > src/main/java/com/logistics/repository/UserRepository.java << 'EOF'
package com.logistics.repository;

import com.logistics.model.User;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, String> {
    Optional<User> findByEmail(String email);
}
EOF

cat > src/main/java/com/logistics/repository/ShipmentRepository.java << 'EOF'
package com.logistics.repository;

import com.logistics.model.Shipment;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends BaseRepository<Shipment, String> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    List<Shipment> findByCustomerId(String customerId);
    List<Shipment> findByStatus(String status);
}
EOF

cat > src/main/java/com/logistics/repository/TrackingEventRepository.java << 'EOF'
package com.logistics.repository;

import com.logistics.model.TrackingEvent;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrackingEventRepository extends BaseRepository<TrackingEvent, String> {
    List<TrackingEvent> findByShipmentIdOrderByOccurredAtDesc(String shipmentId);
}
EOF

for repo in Warehouse DeliveryAgent Notification AuditLog; do
    cat > src/main/java/com/logistics/repository/${repo}Repository.java << EOF
package com.logistics.repository;

import com.logistics.model.${repo};
import org.springframework.stereotype.Repository;

@Repository
public interface ${repo}Repository extends BaseRepository<${repo}, String> {
}
EOF
done

# 5. Main application class
echo "🚀 Updating main application class..."
cat > src/main/java/com/logistics/LogisticsApplication.java << 'EOF'
package com.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class LogisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogisticsApplication.class, args);
    }
}
EOF

# 6. Dockerfile with JVM flags for MongoDB SSL
echo "🐳 Updating Dockerfile..."
cat > Dockerfile << 'EOF'
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", \
    "-Dcom.mongodb.ssl.allowInvalidCertificates=true", \
    "-Dcom.mongodb.ssl.invalidHostNameAllowed=true", \
    "-Dhttps.protocols=TLSv1.2,TLSv1.3", \
    "-Djdk.tls.client.protocols=TLSv1.2,TLSv1.3", \
    "-Djsse.enableSNIExtension=false", \
    "-jar", "app.jar"]
EOF

echo "✅ All files updated to MongoDB."

# 7. Commit and push
echo "📦 Committing and pushing to GitHub..."
git add -A
git commit -m "Switch from PostgreSQL to MongoDB Atlas"
git push origin main

echo ""
echo "🚀 Done! Now set the environment variable in Render:"
echo ""
echo "SPRING_DATA_MONGODB_URI=mongodb+srv://amadousillah112_db_user:Underline123456@cluster0.zkxrybr.mongodb.net/LogisticsManagement?retryWrites=true&w=majority"
echo "JWT_SECRET=your-secret-key (any random string)"
echo ""
echo "Delete any SPRING_DATASOURCE_* variables."
echo ""
echo "Press Enter to exit this script."
read