export const formatToDateTimeLocal = (timestamp) => {
  return new Date(timestamp * 1000).toISOString().slice(0, 16)
}
