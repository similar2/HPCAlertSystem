// Custom method to dynamically fetch suggestions
import { getBaseURL } from '@/utils/request.js'

export const fetchDynamicSuggestions = async (callback) => {
  try {
    // Replace this with your own method for fetching dynamic data
    const response = await fetch(
      `${getBaseURL()}/9090/api/v1/label/__name__/values`
    )

    const data = await response.json()

    // Process and format the response data for the autocomplete
    const formattedSuggestions = data.map((item) => ({ value: item }))

    // Update suggestions and call the callback with the results
    callback(formattedSuggestions)
  } catch (error) {
    console.error('Error fetching suggestions:', error)
    callback([]) // Return an empty list in case of an error
  }
}
