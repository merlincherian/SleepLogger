# Sleep API

This project is a Kotlin-based API for managing sleep data. It uses PostgreSQL as the database and Flyway for database migrations.

## Prerequisites

- Docker
- Docker Compose
- Java 11 or higher

## Getting Started

### Clone the Repository

First, clone the repository to your local machine:

```sh
git clone <repository-url>
cd <repository-directory>
```

### Building the Project


To build the project, run the following command:


```sh
docker-compose up --build
```

This will start the PostgreSQL database, apply the Flyway migrations, and run the Sleep API.


### Stopping the Project


To stop the project, use the following command:


```sh
docker-compose down
```

### Database Schema


The database schema is managed by Flyway and includes the following tables:

sleep_logs: Stores sleep data with columns for user ID, start time, end time, and duration.

### SQL Table Design

#### sleep_logs Table

The `sleep_logs` table is designed to store information about users' sleep logs. Below is the design of the table:

| Column            | Type         | Description                              |
|-------------------|--------------|------------------------------------------|
| id                | BIGINT       | Primary key, auto-incremented            |
| user_id           | BIGINT       | Foreign key referencing the users table  |
| sleep_date        | DATE         | The date of the sleep log                |
| start_time        | TIME         | The start time of the sleep              |
| end_time          | TIME         | The end time of the sleep                |
| total_time_in_bed | INTERVAL     | The total time spent in bed              |
| feeling           | VARCHAR(255) | The user's feeling after the sleep       |


#### users Table

The `users` table is designed to store information about users. Below is the design of the table:

| Column    | Type         | Description                   |
|-----------|--------------|-------------------------------|
| id        | BIGINT       | Primary key, auto-incremented |
| name      | VARCHAR(255) | The name of the user          |
| email     | VARCHAR(255) | The email of the user         |

### Seed Data

The migration files include seed data to populate the database with initial values. This helps in setting up the database with some default data for testing and development purposes.


### Postman Collection
A Postman collection is included in this repository and can be used to test the API. You can import the collection into Postman and use it to send requests to the API endpoints. The collection file is named ```SleepLogger API.postman_collection.json```

### API Endpoints


#### POST /api/sleep-logs

Create a new sleep log entry.

**URL:** `http://localhost:8080/api/sleep-logs`

**Method:** `POST`

**Headers:**
- `Content-Type: application/json`

**Request Body:**
```json
{
  "userId": 1,
  "sleepDate": "2024-10-22",
  "startTime": "07:30 PM",
  "endTime": "06:00 AM",
  "feeling": "OK"
}
```

#### POST /api/users

Create a new user.

**URL:** `http://localhost:8080/api/users`

**Method:** `POST`

**Headers:**
- `Content-Type: application/json`

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john.doe@example.com"
}

```
### API Endpoints

#### GET /api/sleep-logs/last-night

Retrieve the sleep log for the last night.

**URL:** `http://localhost:8080/api/sleep-logs/last-night?userId=?`

**Method:** `GET`

**Description:**
This endpoint retrieves the sleep log entry for the last night for the queried user.


#### GET /api/sleep-logs/last-30-days

Retrieve the sleep log report for the last 30 days.

**URL:** `http://localhost:8080/api/sleep-logs/last-30-days?userId=?`

**Method:** `GET`

**Description:**
This endpoint retrieves the sleep log summary for the last 30 days for the queried user.

