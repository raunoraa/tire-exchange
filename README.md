# Tire Exchange Application


## Table of Contents

- [Project Overview](#project-overview)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Setup Instructions](#setup-instructions)
  - [General Setup](#general-setup)
  - [Back-End Setup](#back-end-setup)
  - [Front-End Setup](#front-end-setup)
- [Configuration](#configuration)
  - [Adding or Modifying Sites](#adding-or-modifying-sites)
  - [Modifying Back-End and Front-End Ports](#modifying-back-end-and-front-end-ports)


## Project Overview

The Tire Exchange Application provides a platform for users to book tire exchange services at various sites. Each site offers tire exchange service for different types of vehicles (e.g., for cars or trucks), and users can filter available time slots by site, date range, and vehicle types. The application is structured to be easily extendable, allowing for new sites to be added with minimal changes.


## Technologies Used

- **Back-End:**
  - Java Spring Boot

- **Front-End:**
  - Vue 3

- **Configuration:**
  - YAML for application settings


## Features

- **View Available Time Slots:** Users can view available time slots for different tire exchange sites.

- **Filter Options:** Users can filter time slots by site, date range, and vehicle type.

- **Book Time Slots:** Users can book a time slot and view their booked time slots, although this data is not persisted due to the lack of a database.
  
- **Responsive Design:** The front-end is designed to be responsive and user-friendly.


## Setup Instructions

### General Setup

**1. Clone this repository.**

**2. Navigate to the cloned repository's folder.**

**3. Download and run the external API simulators.** 
- Follow the inctructions provided on the following GitHub repository: https://github.com/Surmus/tire-change-workshop/tree/master

### Back-End Setup

**1. Navigate to the back-end folder.**

**2. Run the Spring Boot application:**
- Use your preferred IDE or run the following command to start the application: `mvnw spring-boot:run`
- Back-End will run by default on http://localhost:8080/tire-exchange.

### Front-End Setup

**1. Navigate to the front-end folder.**

**2. Install dependencies:**
- Use the following command: `npm install`

**3. Run the Front-End:**
- Use the following command: `npm run serve`
- Front-End will run by default on http://localhost:8081.


## Configuration

### Adding or Modifying Sites

- **Adding Sites:** To add a new site, provide the necessary site information in the application.yml file under the tire-exchange-sites section. Ensure that each siteId is unique. For each new site, create a corresponding API client class that implements the TireExchangeClient interface.
- **Modifying Sites:** You can safely modify the site information (like name, address, vehicle types) in the application.yml file, but do not change the siteId as it must remain unique and consistent.

### Modifying Back-End and Front-End Ports

- **Modifying Back-End port:** To modify the port, where the back-end will run, change the server.port parameter in application.yml file. After that, the back-end port should also be changed in the front-end configuration file src/config/config.js (modify the port in the apiBaseUrl property's URL).
- **Modifying Front-End port:** To modify the port, where the front-end will run, change the devServer.port property in the other front-end configuration file vue.config.js. After that, the port number needs to be changed in the back-end's WebConfig.java file as well (modify the port in the URL).
