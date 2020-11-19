import { useEffect, useState } from 'react'
import { usePivo } from './use-pivo'
import { vocabulary } from './vocabulary'

export function useMealPreference () {
  const pivo = usePivo()

  const [ getPollMealPreferences, setGetPollMealPreferences ] = useState()

  useEffect(() => {
    if (pivo !== undefined) {
      const maybeOperation = pivo.get(
        vocabulary.types.MealPreferences,
        [ vocabulary.terms.slug ]
      ).getOrUndefined()

      setGetPollMealPreferences(maybeOperation)
    }
  }, [pivo])

  return {
    isMealPrefFeatureAvailable: getPollMealPreferences !== undefined,
    getPollMealPreferences
  }
}