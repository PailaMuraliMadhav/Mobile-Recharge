# Eureka Service Discovery Server

The **Eureka Server** is the heart of the OmniRecharge microservices ecosystem. It acts as a service registry where all other microservices register themselves at startup, allowing them to find each other without hardcoded IP addresses.

## ⚙️ Core Configuration

- **Port**: `8761`
- **Annotation**: `@EnableEurekaServer`
- **Dashboard**: Accessible at `http://localhost:8761`

## 🛠️ Key Features

- **Dynamic Service Registration**: Automatically detects new service instances.
- **Health Monitoring**: Services send heartbeats to Eureka; if a service stops responding, it is removed from the registry.
- **Client-Side Load Balancing**: Used by OpenFeign and API Gateway to distribute traffic among multiple instances of a service.

## 📁 Connection Details
All microservices include the following dependency to connect to Eureka:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

---
*Developed by Paila Murali Madhav*
