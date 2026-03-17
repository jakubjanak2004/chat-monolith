# Run on the VM 1
git pull
docker compose -f docker-compose.vm1.yml down
docker compose -f docker-compose.vm1.yml up --build -d
docker compose -f docker-compose.vm1.yml ps