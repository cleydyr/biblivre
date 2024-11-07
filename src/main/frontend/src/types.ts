export type Digit = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9;

export type Singleton<T> = {
  [K in keyof T]: { [P in K]: T[K] };
}[keyof T];
