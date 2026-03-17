# Run on the VM 2
git pull
docker compose -f docker-compose.vm2.yml down
docker compose -f docker-compose.vm2.yml up --build -d
docker compose -f docker-compose.vm2.yml ps
docker compose -f docker-compose.vm2.yml logs --tail=100 app