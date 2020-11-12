import CONFIG from '../CONFIG.json'

export const API_VERSION_QUERY_PARAM_KEY = 'api-version'

export function getApiVersion() {
  return getQueryParams(API_VERSION_QUERY_PARAM_KEY) || null
}

export function setApiVersion(version) {
  const actualVersionNumber = version || '1'

  const searchParams = new URLSearchParams(window.location.search)
  searchParams.set(API_VERSION_QUERY_PARAM_KEY, actualVersionNumber)

  const l = window.location
  const urlWithVersion = `${l.origin}${l.pathname}?${searchParams.toString()}`

  window.location.assign(urlWithVersion)
}

export function getBaseUrl() {
  const versionNumber = getApiVersion()

  if (versionNumber === null) {
    setApiVersion(1)
  } else {
    return CONFIG.api[`v${versionNumber}`]['base-url']
  }
}

function getQueryParams(values) {
  const queryParams = new URLSearchParams(window.location.search)

  if (typeof values === 'string') {
    return queryParams.get(values)
  } else if (values instanceof Array) {
    return values.reduce((acc, value) => {
      acc[value] = queryParams.get(value)
      return acc
    }, {})
  } else {
    return queryParams
  }
}