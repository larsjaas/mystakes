import React, {Component} from 'react';
import ReactMapGL from 'react-map-gl';

import { token } from './mapbox';

export class Map extends Component {
  constructor(props) {
    super(props);
    this.state = {
      viewport: {
        width: 900,
        height: 600,
        // Trolla
        latitude: 63.4500,  // north
        longitude: 10.3000, // east
        zoom: 14
      }
    };
  }

  render() {
    return (
      <ReactMapGL
        {...this.state.viewport}
        onViewportChange={(viewport) => this.setState({viewport})}
        mapboxApiAccessToken={token}
      />
    );
  }
};