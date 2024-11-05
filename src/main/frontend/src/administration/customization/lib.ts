type Ordered = {
  sortOrder: number;
};

export function getAffectedItems<T extends Ordered>(
  items: T[],
  source: number,
  destination: number,
): T[] {
  const lesser = Math.min(source, destination);

  const greater = Math.max(source, destination);

  const movedUpwards = lesser === destination;

  const sortOrderDelta = movedUpwards ? 1 : -1;

  const sliceBoundsDelta = movedUpwards ? 0 : 1;

  const displaced = items
    .slice(lesser + sliceBoundsDelta, greater + sliceBoundsDelta)
    .map((item: T) => ({
      ...item,
      sortOrder: item.sortOrder + sortOrderDelta,
    }));

  const movedItem = {
    ...items[source],
    sortOrder: destination,
  };

  return movedUpwards ? [movedItem, ...displaced] : [...displaced, movedItem];
}
