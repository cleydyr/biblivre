import { getAffectedItems } from "./lib";

describe("getAffectedItems", () => {
  test("should return the correct length", () => {
    expect(
      getAffectedItems(
        [
          {
            id: 0,
            sortOrder: 0,
          },
          {
            id: 1,
            sortOrder: 1,
          },
          {
            id: 2,
            sortOrder: 2,
          },
        ],
        0,
        2,
      ).length,
    ).toBe(3);
  });

  test("should return the affected items", () => {
    expect(
      getAffectedItems(
        [
          {
            id: 0,
            sortOrder: 0,
          },
          {
            id: 1,
            sortOrder: 1,
          },
          {
            id: 2,
            sortOrder: 2,
          },
        ],
        0,
        2,
      ),
    ).toEqual([
      {
        id: 1,
        sortOrder: 0,
      },
      {
        id: 2,
        sortOrder: 1,
      },
      {
        id: 0,
        sortOrder: 2,
      },
    ]);
  });

  test("should return the affected items when source is greater than destination", () => {
    expect(
      getAffectedItems(
        [
          {
            id: 0,
            sortOrder: 0,
          },
          {
            id: 1,
            sortOrder: 1,
          },
          {
            id: 2,
            sortOrder: 2,
          },
        ],
        2,
        0,
      ),
    ).toEqual([
      {
        id: 2,
        sortOrder: 0,
      },
      {
        id: 0,
        sortOrder: 1,
      },
      {
        id: 1,
        sortOrder: 2,
      },
    ]);
  });
});
