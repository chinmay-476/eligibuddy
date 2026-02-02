# Authentication System Documentation

## Overview
This Spring Boot application now includes a complete authentication system with user registration, login, and role-based access control.

## Features

### 🔐 Authentication Features
- **User Registration**: New users can create accounts
- **User Login**: Secure login with username/password
- **Role-Based Access**: Two user roles (USER and ADMIN)
- **Secure Logout**: Proper session termination
- **Password Encryption**: BCrypt password hashing

### 🛡️ Security Features
- **Protected Routes**: Certain pages require authentication
- **Admin-Only Access**: View Contacts page is restricted to admin users only
- **CSRF Protection**: Built-in CSRF protection (disabled for simplicity)
- **Session Management**: Proper session handling

## Default Users

### Admin User
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: ADMIN
- **Access**: All features including View Contacts

### Regular User
- **Username**: `user`
- **Password**: `user123`
- **Role**: USER
- **Access**: Basic features (no access to View Contacts)

## How to Use

### 1. Starting the Application
```bash
mvn spring-boot:run
```

### 2. Accessing the Application
- Open your browser and go to `http://localhost:8080`
- You'll see the main page with navigation options

### 3. Registration
- Click "Register" in the navigation
- Fill in your details (username, email, password)
- Click "Create Account"
- You'll be redirected to login

### 4. Login
- Click "Login" in the navigation
- Enter your username and password
- Click "Sign In"
- You'll be redirected to the home page

### 5. Accessing Admin Features
- Login with admin credentials (admin/admin123)
- You'll see "View Contacts" in the navigation
- Regular users won't see this option

## Page Access Control

### Public Pages (No Login Required)
- `/` - Home page
- `/contact` - Contact form
- `/register` - User registration
- `/login` - User login

### Protected Pages
- `/view-contacts` - **ADMIN ONLY** - View all contact submissions

## Database Tables

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
);
```

### Contacts Table (Existing)
```sql
CREATE TABLE contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    message TEXT NOT NULL
);
```

## Security Configuration

The application uses Spring Security with the following configuration:

- **Password Encoding**: BCrypt
- **Session Management**: Default Spring Security
- **CSRF**: Disabled for simplicity
- **Authentication**: Form-based login
- **Authorization**: Role-based access control

## Customization

### Adding New Roles
1. Add new role to `User.Role` enum
2. Update security configuration in `SecurityConfig.java`
3. Add role checks in controllers

### Changing Default Passwords
Edit the `DataInitializer.java` file to change default passwords.

### Adding New Protected Routes
Update the `SecurityConfig.java` file to add new protected endpoints.

## Troubleshooting

### Common Issues

1. **Can't access View Contacts**
   - Make sure you're logged in as admin
   - Check console for admin user creation messages

2. **Login not working**
   - Verify username/password
   - Check database connection
   - Look for error messages in console

3. **Registration fails**
   - Username or email might already exist
   - Check password requirements (minimum 6 characters)

### Database Issues
- Ensure MySQL is running
- Check `application.yml` configuration
- Verify database exists and is accessible

## Development Notes

- The application automatically creates admin and user accounts on startup
- Passwords are encrypted using BCrypt
- Session data is stored in memory (not persistent across restarts)
- For production, consider using persistent sessions and HTTPS 