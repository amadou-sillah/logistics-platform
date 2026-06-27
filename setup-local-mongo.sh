#!/bin/bash
set -e

echo "🐳 Setting up local MongoDB for development..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first:"
    echo "   sudo apt update && sudo apt install docker.io"
    echo "   sudo systemctl start docker"
    echo "   sudo systemctl enable docker"
    echo "   sudo usermod -aG docker $USER  # then log out and back in"
    exit 1
fi

# Check if the container already exists
if docker ps -a --format '{{.Names}}' | grep -q mongodb-local; then
    echo "✅ MongoDB container already exists. Starting it..."
    docker start mongodb-local
else
    echo "🚀 Creating and starting MongoDB container..."
    docker run -d --name mongodb-local -p 27017:27017 mongo:latest
    echo "⏳ Waiting for MongoDB to be ready..."
    sleep 5
fi

# Seed the database
echo "🌱 Seeding initial data..."
docker exec -i mongodb-local mongosh LogisticsManagement --eval '
db.users.insertMany([
  {
    email: "admin@logistics.com",
    password: "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E",
    name: "Admin",
    role: "ADMIN",
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    email: "agent@logistics.com",
    password: "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E",
    name: "Agent",
    role: "AGENT",
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

db.shipments.insertMany([
  {
    trackingNumber: "S-1001",
    customerId: "admin",
    origin: "New York, NY",
    destination: "Los Angeles, CA",
    status: "PENDING",
    eta: new Date(Date.now() + 86400000),
    amount: 340.5,
    priority: "STANDARD",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    trackingNumber: "S-1002",
    customerId: "admin",
    origin: "Chicago, IL",
    destination: "Miami, FL",
    status: "PENDING",
    eta: new Date(Date.now() + 172800000),
    amount: 215.0,
    priority: "STANDARD",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    trackingNumber: "S-1003",
    customerId: "admin",
    origin: "Seattle, WA",
    destination: "Austin, TX",
    status: "PENDING",
    eta: new Date(Date.now() + 259200000),
    amount: 480.75,
    priority: "STANDARD",
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);
' && echo "✅ Data seeded successfully."

# Build the JAR if missing
if [ ! -f target/logistics-backend-1.0.0.jar ]; then
    echo "🔨 Building JAR..."
    mvn clean package -DskipTests
fi

# Start the backend with local MongoDB
echo "🚀 Starting Java backend with local MongoDB..."
export SPRING_DATA_MONGODB_URI="mongodb://localhost:27017/LogisticsManagement"
export SPRING_CACHE_TYPE="none"
java -jar target/logistics-backend-1.0.0.jar
