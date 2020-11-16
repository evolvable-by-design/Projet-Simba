import { useEffect, useState } from 'react'
import Pivo from '@evolvable-by-design/pivo'
import { useApiVersion } from '../utils/apiVersionManager'

import CONFIG from '../CONFIG.json'

export function usePivo() {
  const apiVersion = useApiVersion()
  const [ pivo, setPivo ] = useState()

  useEffect(() => {
    const apiConfig = CONFIG.api[`v${apiVersion}`]
    Pivo.fetchDocumentationAndCreate(
      apiConfig['documentation-url'] || apiConfig['base-url'],
      apiConfig['documentation-verb'] || 'OPTIONS'
    )
    .then(newPivoInstance => {
      setPivo(newPivoInstance)
    })
    .catch(e => {
      alert(`An error occured while fetching the documentation at OPTIONS ${apiConfig['base-url']}. See console for the error detail.`)
      console.error(e)
    })
  }, [apiVersion])

  return pivo
}
