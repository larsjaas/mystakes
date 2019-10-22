# mystakes

For tracking your stakes.

# setting up

1. copy `frontend/src/typescript/mapbox.ts.sample` to `frontend/src/typescript/mapbox.ts` and
   update it to use your own Mapbox API key.
2. edit `frontend/src/typescript/map.tsx` to use your latitude, longitude coordinate of choice.
3. edit `backend/resoures/application.conf` to set the web service port to use, or keep the default 8000.
4. build and start the backend with `./mill backend.run`
5. run `npm i` to populate `node_modules/` for the frontend build
6. run `npx webpack --watch` to package the frontend code
7. launch a browser at http://localhost:8000/ (or whichever port you chose)

# structure

backend/ - server implemented in scala using akka
frontend/ - app in typescript using react

build.sc - mill build config for the backend

