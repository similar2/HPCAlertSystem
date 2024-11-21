import { getServerURL } from '@/utils/request.js'

export const fetchDynamicSuggestions = async (callback) => {
  try {
    // Fetch the data from the API
    const response = await fetch(
      `${getServerURL()}:9090/api/v1/label/__name__/values`
    )
    const result = await response.json()

    // Log result for debugging
    console.log('Fetched result:', result)

    // Check if the response has the expected structure and is an array
    if (result && Array.isArray(result.data)) {
      // Map over the data array
      const formattedSuggestions = result.data.map((item) => ({ value: item }))
      callback(formattedSuggestions)
    } else {
      console.error('Unexpected data structure:', result)
      callback([]) // Return an empty array if the structure is unexpected
    }
  } catch (error) {
    console.error('Error fetching suggestions:', error)
    callback([]) // Return an empty list in case of an error
  }
}
