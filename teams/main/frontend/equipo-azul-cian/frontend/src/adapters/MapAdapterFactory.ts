import { OpenStreetMapAdapter } from './OpenStreetMapAdapter';
import { MapboxAdapter } from './MapboxAdapter';

export function createMapAdapter() {
  const provider = import.meta.env.VITE_MAP_PROVIDER;

  switch (provider) {
    case 'mapbox':
      return new MapboxAdapter();

    case 'openstreet':
    default:
      return new OpenStreetMapAdapter();
  }
}