import { useMemo } from 'react'
import { useLocation } from "react-router-dom"

import CONFIG from '../CONFIG.json'

export const API_VERSION_QUERY_PARAM_KEY = 'api-version'

export function useApiVersion() {
  return useQueryParams(API_VERSION_QUERY_PARAM_KEY) || null
}

export function setApiVersion(version) {
  const actualVersionNumber = version || '1'

  const searchParams = new URLSearchParams(window.location.search)
  searchParams.set(API_VERSION_QUERY_PARAM_KEY, actualVersionNumber)

  const l = window.location
  const urlWithVersion = `${l.origin}${l.pathname}?${searchParams.toString()}`

  window.location.assign(urlWithVersion)
}

export function useBaseUrl() {
  const versionNumber = useApiVersion()

  if (versionNumber === null) {
    setApiVersion(1)
  } else {
    return CONFIG.api[`v${versionNumber}`]['base-url']
  }
}

function useQueryParams(values) {
  const location = useLocation()
  const queryParams = useMemo(() => new URLSearchParams(location.search), [ location.search ])

  const result = useMemo(() => {
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
  }, [queryParams, values])

  return result
}