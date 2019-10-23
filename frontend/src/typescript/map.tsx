import React, { useReducer } from 'react';
import ReactMapGL, { ViewportProps } from 'react-map-gl';

import { token } from './mapbox';
import { normalizeViewportProps } from 'viewport-mercator-project';

const Actions = {
  SET_VIEWPORT: 'set-viewport'
};

const reducer = (state: any, action: Action) => {
  switch (action.type) {
    case Actions.SET_VIEWPORT: {
      return {...state, viewport: action.viewport};
    }
  }
};

class Action {
  type: string;
  viewport: ViewportProps;
}

class State {
  viewport: ViewportProps;
}

const initialState: State = {
  viewport: {
    width: 900,
    height: 600,
    // Trolla
    latitude: 63.4500,  // north
    longitude: 10.3000, // east
    zoom: 14,
    bearing: 0,
    pitch: 0,
    altitude: 0,
    maxZoom: 16,
    minZoom: 1,
    maxPitch: 0,
    minPitch: 0

  }
};

const Map = (style: any) => {
  const [state, dispatch] = useReducer(reducer, initialState);

  return (
    <ReactMapGL
      {...state.viewport}
      style={style}
      mapStyle='mapbox://styles/mapbox/outdoors-v11'
      onViewportChange={(viewport: ViewportProps) => dispatch({type: Actions.SET_VIEWPORT, viewport: viewport})}
      mapboxApiAccessToken={token}
    />
  );
};

export { Map };