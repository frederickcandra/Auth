# Tahap build menggunakan Maven 3.9.9 dan JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory di dalam container
WORKDIR /app

# Salin file pom.xml terlebih dahulu untuk caching dependencies
COPY pom.xml ./

    # Download dependencies tanpa melakukan build
RUN mvn dependency:go-offline -B

# Salin source code ke dalam container
COPY src ./src

# Build project menggunakan Maven
RUN mvn clean package -DskipTests

# Tahap runtime: gunakan image runtime yang ringan
FROM eclipse-temurin:21-jre-alpine

# Set working directory di dalam container
WORKDIR /app

# Salin file JAR dari stage build sebelumnya
COPY --from=build /app/target/auth-0.0.1-SNAPSHOT.jar /app/auth-0.0.1-SNAPSHOT.jar

# Tentukan port di mana aplikasi akan dijalankan
EXPOSE 8080

# Jalankan aplikasi
CMD ["java", "-jar", "/app/auth-0.0.1-SNAPSHOT.jar"]
