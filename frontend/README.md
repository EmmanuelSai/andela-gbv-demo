# GBV Case Portal

Angular staff portal for the GBV reporting bot.

## Run locally

From the `frontend` directory:

```bash
npm start
```

The dev server runs at [http://localhost:4200](http://localhost:4200) and proxies `/api` to the Spring Boot app on port 8080.

Staff usernames and passwords come from the backend environment (`PORTAL_ADMIN_*` and `PORTAL_MANAGER_*`). Do not commit them.
