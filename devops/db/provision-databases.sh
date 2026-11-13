for database in \
  authdb \
  restaurantdb \
  guesthousedb \
  marketplacedb \
  familytreedb
do
  if ! psql -tAc \
    "SELECT 1 FROM pg_database WHERE datname='$database'" \
    | grep -q 1; then

    createdb "$database"
  fi
done