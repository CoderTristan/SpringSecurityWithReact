Video Creator Toolkit

A streamlined full‑stack workspace for fast video transcoding, proxy generation, and high‑quality audio extraction. Built on a secure Spring Boot backend, a native FFmpeg processing pipeline, and automated AWS S3 delivery.

Core Features

Lightweight Editing Proxies — Generates smooth 480p H.264 proxy files designed for instant scrubbing and timeline playback.

Smart Compression Profiles — Multi‑pass libx264 encoding with tuned CRF settings (CRF=28, slow preset) to reduce file size while keeping full browser compatibility.

Audio‑Only Extraction — Pulls audio tracks from large video containers and outputs clean 192kbps MP3 files.

Secure Cloud Delivery — Uses AWS S3 presigned URLs to provide temporary, private access to processed media.

Workspace History Tracking — Stores job history and metadata through a PostgreSQL‑backed logging system.

Technical Pipeline Overview

[React 18 / Vite Frontend]
        │
        │  (JWT Auth Header)
        ▼
[Spring Boot REST API]
   ├──► Local Worker + FFmpeg
   │       (Proxies / Compression / MP3)
   │
   └──► AWS S3 Storage
           (Presigned Playback Links)

Technology Stack

Frontend: React 18, TypeScript, Tailwind CSS, Vite

Backend: Java 17, Spring Boot 3, Spring Security (JWT)

Database: PostgreSQL, Spring Data JPA / Hibernate

Processing & Cloud: FFmpeg (system binary), AWS SDK v2 (S3), Lombok

Setup & Usage

System Requirements

JDK 17+

Node.js 18+

A running PostgreSQL instance

FFmpeg installed and available in your system path (ffmpeg -version)

1. Backend Setup

Edit your configuration file:

src/main/resources/application.properties

spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_system_username
spring.datasource.password=your_secure_password

spring.servlet.multipart.max-file-size=500MB
spring.servlet.multipart.max-request-size=500MB

aws.s3.bucket-name=your-production-bucket-name
aws.region=us-east-2

Start the backend:

./mvnw spring-boot:run

2. Frontend Setup

Install dependencies and start the dev server:

npm install
npm run dev

Open your browser at:

http://localhost:5173

API Guide

All routes require a valid JWT:

Authorization: Bearer <TOKEN>

Process a Media File

POST /api/v1/video/processBody: multipart/form-dataQuery:

action = proxy | compress | audio (default: proxy)

Response:A presigned S3 URL pointing to the processed file.

Retrieve Your Processed Videos

GET /api/v1/video/my-videosReturns an array of presigned URLs for your processed assets.

Notes on Compatibility & Stability

Browser Playback:Some formats (like HEVC/H.265) fail on certain browsers or hardware.This toolkit standardizes output to H.264 with yuv420p to guarantee HTML5 playback.

Frontend Auth Handling:The client checks local storage tokens before making requests, preventing 403 errors during hard reloads.
