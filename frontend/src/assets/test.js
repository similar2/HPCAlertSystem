function mergeSort(arr) {
  if (arr.length <= 1) {
    return arr
  }
  const middle = Math.floor(arr.length / 2)
  const left = arr.slice(0, middle)
  const right = arr.slice(middle)
  return merge(mergeSort(left), mergeSort(right))
}

function merge(left, right) {
  let resultArray = [],
    leftIndex = 0,
    rightIndex = 0
  while (leftIndex < left.length && rightIndex < right.length) {
    if (left[leftIndex] < right[rightIndex]) {
      resultArray.push(left[leftIndex])
      leftIndex++
    } else {
      resultArray.push(right[rightIndex])
      rightIndex++
    }
  }
  return resultArray
    .concat(left.slice(leftIndex))
    .concat(right.slice(rightIndex))
}

console.log(mergeSort([1, 5, 3, 8, 104, 6, 4, 7])) // [1, 2, 3, 4, 5, 6, 7, 8]
