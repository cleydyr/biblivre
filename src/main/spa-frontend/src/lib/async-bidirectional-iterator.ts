export interface AsyncBidirectionalIterator<T> {
  next(): Promise<T>
  previous(): Promise<T>
  hasNext(): Promise<boolean>
  hasPrevious(): Promise<boolean>
}
