# Database schema

The Family Tree Flyway migrations now live with the service that owns them:

```
backend/family-tree-service/src/main/resources/db/migration/
```

They were moved out of this directory because Flyway is configured with
`locations: classpath:db/migration`, so anything outside the service's
classpath is never applied. Tests passed locally only because the developer
database already had the schema applied by hand; CI caught it.

Apply order is `V1` … `V9`, followed by the repeatable `R__views.sql`.

`multi_store_migration.sql` stays here — it is a one-off, cross-database
script and is not part of the Family Tree migration chain.
