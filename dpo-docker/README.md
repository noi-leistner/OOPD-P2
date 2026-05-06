# LSParking — Docker Setup Guide

This project can be easily run using Docker Compose.

## 🚀 Getting Started

Make sure you have the following installed:

* Docker
* Docker Compose

> ⚠️ **Important:** Do **not modify the `.env` file**. The project is preconfigured and changing it may break the setup.

## ▶️ Start the Application

To build and start all services in detached mode, run:

```bash
docker compose up -d
```

## 🛑 Stop and Clean Up

To stop the containers and remove associated volumes:

```bash
docker compose down -v
```

## 🗄️ Access phpMyAdmin

Once the containers are running, you can access phpMyAdmin in your browser:

```
http://localhost:8080
```

## 📌 Notes

* Ensure port `8080` is not already in use on your machine.
* Use `docker compose ps` to check running containers.
* Logs can be viewed with:

```bash
docker compose logs -f
```

---

You're now ready to work with LSParking locally using Docker 🎉
