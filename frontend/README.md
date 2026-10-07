# GameSense frontend

React and Vite UI for the Spring Boot GameSense API in `../Backend`.

## Run locally

1. Start the backend on port `8080`.
2. Copy `.env.example` to `.env` if you need to change the API URL.
3. Install the frontend dependencies with `npm install`.
4. Start Vite with `npm run dev`.

The API base URL defaults to `http://localhost:8080` and can be set with `VITE_API_BASE_URL`.

## Structure

- `src/pages` contains the top level screens and page state.
- `src/components` contains reusable UI components.
- `src/services/api.js` maps requests and response pages to the backend contract.
- `src/data` and `src/utils` hold shared presentation data and helpers.
- `src/css` is the only location for stylesheets.

Player login and review based recommendations use the existing user ID in the backend. Saved games and the player ID are stored in this browser's local storage.

The UI calls catalog, game metadata, user login, review based recommendations, and custom game search routes. It does not call ingestion or evaluation routes.
