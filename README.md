# 💱 Exchange Rate Service

A production-ready, testable, and extensible **Java Spring Boot application** that fetches and manages foreign exchange rates from ECB. It provides REST APIs for currency conversion, currency pair lookup, and usage analytics — all powered by a caching layer, background scheduling, and robust XML parsing.

---

## 🌟 Features

- ✅ Fetch the latest currency exchange rates from an external XML API
- 🔁 Convert currency amounts between any supported pairs
- 📊 Track request counts for each currency (analytics)
- 🧠 Caching to reduce API calls and improve performance
- 📅 Auto-refresh exchange rates using scheduled jobs
- 💡 Dependency Injection & Clean Architecture principles
- ✅ Input validation using `javax.validation`

---

## 🔧 Tech Stack

- **Java 17+**
- **Spring Boot**
- **Spring Scheduler** (`@Scheduled`)
- **Jackson XML (`XmlMapper`)**
- **Custom Caching Layer**
- **SLF4J** for logging
- **Maven** as build tool


