# Car Rental Application

A comprehensive car rental management system built with Spring Boot, featuring separate dashboards for users and administrators.

## Features

### User Features
- User registration and login
- Browse available cars
- Book cars for specific dates
- View booking history
- Modern and responsive UI

### Admin Features
- Admin dashboard with full management capabilities
- Add, edit, and delete cars
- Manage user bookings
- View all users and their activities
- Approve/reject booking requests

## Technology Stack

- **Backend**: Spring Boot 3.x
- **Database**: MySQL
- **Security**: Spring Security
- **Frontend**: HTML, CSS, JavaScript
- **Build Tool**: Maven

## Prerequisites

- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Installation & Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/car-rental-application.git
   cd car-rental-application
   ```

2. Configure MySQL database:
   - Create a database named `carrentalapplication`
   - Update `src/main/resources/application.properties` with your database credentials

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access the application:
   - Main page: http://localhost:8081
   - The application will automatically create sample data on first run

## Default Login Credentials

### Admin Access
- Email: admin@gmail.com
- Password: admin123

### Sample User Access
- Email: user@gmail.com
- Password: user123

## Project Structure

```
src/
├── main/
│   ├── java/com/example/carRentalApplication/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── model/          # Entity classes
│   │   ├── repository/     # Data repositories
│   │   └── security/       # Security configuration
│   └── resources/
│       ├── static/         # HTML, CSS, JS files
│       └── application.properties
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Cars
- `GET /api/cars` - Get all cars
- `POST /api/cars` - Add new car (Admin only)
- `PUT /api/cars/{id}` - Update car (Admin only)
- `DELETE /api/cars/{id}` - Delete car (Admin only)

### Bookings
- `GET /api/bookings` - Get user bookings
- `POST /api/bookings` - Create new booking
- `PUT /api/bookings/{id}` - Update booking status (Admin only)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
