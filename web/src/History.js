import queryString from 'query-string'
import {createBrowserHistory} from 'history'

import { API_VERSION_QUERY_PARAM_KEY } from './utils/apiVersionManager'

function preserveQueryParameters(history, preserve, location) {
  const currentQuery = queryString.parse(history.location.search);
  if (currentQuery) {
      const preservedQuery = {};
      for (let p of preserve) {
          const v = currentQuery[p];
          if (v) {
              preservedQuery[p] = v;
          }
      }
      const search = location.search || location.pathname.split('?')[1]
      if (search) {
          Object.assign(preservedQuery, queryString.parse('?' + search));
      }
      location.search = queryString.stringify(preservedQuery);
      location.pathname = location.pathname.split('?')[0]
  }
  return location;
}

function createLocationDescriptorObject(location, state) {
  return typeof location === 'string' ? { pathname: location, state } : location;
}

function createPreserveQueryHistory(createHistory, queryParameters) {
  return (options) => {
      const history = createHistory(options);
      const oldPush = history.push, oldReplace = history.replace;
      history.push = (path, state) => oldPush.apply(history, [preserveQueryParameters(history, queryParameters, createLocationDescriptorObject(path, state))]);
      history.replace = (path, state) => oldReplace.apply(history, [preserveQueryParameters(history, queryParameters, createLocationDescriptorObject(path, state))]);
      return history;
  };
}

export const history = createPreserveQueryHistory(createBrowserHistory, [API_VERSION_QUERY_PARAM_KEY])();