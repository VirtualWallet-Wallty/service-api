<div align=center>

# This is the backend API for [VirtualWallet-Wallty](https://github.com/VirtualWallet-Wallty)

</div>

> Requires Java 17+, Gradle, and a running MySQL instance.

### 1. Clone the repository

```bash
git clone https://github.com/VirtualWallet-Wallty/service-api
cd service-api
```

<br/>

### 2. Set up the database

Run the scripts from the [`db/`](https://github.com/VirtualWallet-Wallty/service-api/tree/main/db) directory in order:

```sql
-- Create the schema and tables
source db/create.sql

-- Insert required seed data (currencies, exchange rates, roles, etc.)
source db/inserts.sql
```

Or run them directly from the links:

- [`db/create.sql`](https://github.com/VirtualWallet-Wallty/service-api/blob/main/db/create.sql) - creates the database and all tables
- [`db/inserts.sql`](https://github.com/VirtualWallet-Wallty/service-api/blob/main/db/inserts.sql) - inserts seed data for testing

<br/>

### 3. Configure `application.properties`

In [`src/main/resources/application.properties`](https://github.com/VirtualWallet-Wallty/service-api/blob/main/src/main/resources/application.properties), fill in the commented-out values:

```properties
spring.application.name=virtual-wallet
spring.datasource.url=jdbc:mysql://localhost:3306/virtual_wallet
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=        # ← your MySQL username
spring.datasource.password=        # ← your MySQL password

security.jwt.secret=               # ← any long random secret string
security.jwt.expiration=3600000

exchange.api.base-url=https://v6.exchangerate-api.com/v6
exchange.api.key=                  # ← your key from exchangerate-api.com

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.web.resources.add-mappings=false
```

> Get a free ExchangeRate API key at [exchangerate-api.com](https://www.exchangerate-api.com).

<br/>

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

<br/>

<div align=center>

## 👤 Contact

🐙 **_[GitHub Profile](https://github.com/todorkrushkov)_**

💼 **_[LinkedIn Profile](https://www.linkedin.com/in/todor-krushkov-64991433a/)_**

✉️ _**<todorkrushkov.1304@gmail.com>**_

<br/>

**Developed as a course project @ Technical University of Sofia**

*Faculty of Computer Systems and Technologies - Computer & Software Engineering*

</div>
