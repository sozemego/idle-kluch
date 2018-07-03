export const isEqual = (a, b, epsilon) => {
  return Math.abs(a - b) < epsilon;
};