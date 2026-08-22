import { getStoreColor, getStoreInitials } from './store-avatar';

describe('getStoreInitials', () => {
  it('returns the single uppercase initial for a one-word name', () => {
    expect(getStoreInitials('SuperMart')).toBe('S');
  });

  it('returns the first letter of the first two words, uppercased', () => {
    expect(getStoreInitials('Super Mart')).toBe('SM');
  });

  it('ignores extra/leading/trailing whitespace between words', () => {
    expect(getStoreInitials('  Super   Mart  ')).toBe('SM');
  });

  it('uses only the first two words when there are more than two', () => {
    expect(getStoreInitials('Super Fresh Mart')).toBe('SF');
  });

  it('throws for an empty or whitespace-only name', () => {
    const name1 = '';
    const name2 = '   ';
    expect(() => getStoreInitials(name1)).toThrowError('Name is either empty or whitespace-only');
    expect(() => getStoreInitials(name2)).toThrowError('Name is either empty or whitespace-only');
  });
});

describe('getStoreColor', () => {
  const PALETTE = ['#5B6EF5', '#00A896', '#E0797A', '#8A6FDF', '#D9A441', '#4C8577'];

  it('returns a color from the fixed palette', () => {
    expect(PALETTE).toContain(getStoreColor('store-123'));
  });

  it('is deterministic: the same id always returns the same color', () => {
    const color1 = getStoreColor('store-abc');
    const color2 = getStoreColor('store-abc');
    expect(color1).toBe(color2);
  });

  it('handles an empty id without throwing', () => {
    expect(() => getStoreColor('')).toThrowError('Empty store id');
  });
});
